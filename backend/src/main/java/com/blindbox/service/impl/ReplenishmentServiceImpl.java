package com.blindbox.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blindbox.common.BusinessException;
import com.blindbox.common.ResultCode;
import com.blindbox.common.UserContext;
import com.blindbox.entity.*;
import com.blindbox.mapper.*;
import com.blindbox.service.ReplenishmentService;
import com.blindbox.vo.ReplenishmentPlanVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReplenishmentServiceImpl extends ServiceImpl<ReplenishmentPlanMapper, ReplenishmentPlan> implements ReplenishmentService {

    private final MachinePointMapper machinePointMapper;
    private final MachineGridMapper machineGridMapper;
    private final ReplenishmentDetailMapper replenishmentDetailMapper;
    private final SysUserMapper sysUserMapper;
    private final PayOrderMapper payOrderMapper;
    private final ProbabilityAuditLogMapper probabilityAuditLogMapper;
    private final BlindboxSkuMapper blindboxSkuMapper;
    private final ObjectMapper objectMapper;

    @Override
    public IPage<ReplenishmentPlanVO> getMyTaskList(String status, Integer pageNum, Integer pageSize) {
        Long replenisherId = UserContext.getUserId();

        LambdaQueryWrapper<ReplenishmentPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReplenishmentPlan::getReplenisherId, replenisherId);
        if (status != null && !status.isEmpty()) {
            wrapper.eq(ReplenishmentPlan::getStatus, status);
        }
        wrapper.orderByDesc(ReplenishmentPlan::getCreatedTime);

        Page<ReplenishmentPlan> page = new Page<>(pageNum, pageSize);
        IPage<ReplenishmentPlan> planPage = baseMapper.selectPage(page, wrapper);

        IPage<ReplenishmentPlanVO> result = new Page<>();
        BeanUtils.copyProperties(planPage, result);

        List<ReplenishmentPlanVO> voList = new ArrayList<>();
        for (ReplenishmentPlan plan : planPage.getRecords()) {
            ReplenishmentPlanVO vo = convertToVO(plan);
            voList.add(vo);
        }
        result.setRecords(voList);

        return result;
    }

    @Override
    public ReplenishmentPlanVO getTaskDetail(Long planId) {
        ReplenishmentPlan plan = baseMapper.selectById(planId);
        if (plan == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return convertToVO(plan);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void startReplenishment(Long planId) {
        ReplenishmentPlan plan = baseMapper.selectById(planId);
        if (plan == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        if (!"PENDING".equals(plan.getStatus())) {
            throw new BusinessException("补货计划状态不允许开始");
        }

        plan.setStatus("IN_PROGRESS");
        plan.setStartTime(LocalDateTime.now());
        baseMapper.updateById(plan);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeReplenishment(Long planId, List<ReplenishmentDetail> details) {
        ReplenishmentPlan plan = baseMapper.selectById(planId);
        if (plan == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        if (!"IN_PROGRESS".equals(plan.getStatus())) {
            throw new BusinessException("补货计划状态不允许完成");
        }

        for (ReplenishmentDetail detail : details) {
            detail.setPlanId(planId);
            replenishmentDetailMapper.insert(detail);

            if (detail.getGridNo() != null && detail.getSkuId() != null) {
                MachineGrid grid = machineGridMapper.selectOne(
                        new LambdaQueryWrapper<MachineGrid>()
                                .eq(MachineGrid::getMachineId, plan.getMachineId())
                                .eq(MachineGrid::getGridNo, detail.getGridNo())
                );
                if (grid != null) {
                    grid.setSkuId(detail.getSkuId());
                    grid.setStatus("FILLED");
                    grid.setLastCheckTime(LocalDateTime.now());
                    machineGridMapper.updateById(grid);
                }
            }
        }

        plan.setStatus("COMPLETED");
        plan.setEndTime(LocalDateTime.now());
        baseMapper.updateById(plan);

        auditProbabilityAfterReplenishment(plan.getMachineId(), plan.getId());
    }

    @Override
    public List<MachineGrid> getMachineGridInventory(Long machineId) {
        return machineGridMapper.selectList(
                new LambdaQueryWrapper<MachineGrid>()
                        .eq(MachineGrid::getMachineId, machineId)
                        .orderByAsc(MachineGrid::getGridNo)
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkGridInventory(Long machineId, List<MachineGrid> grids) {
        for (MachineGrid grid : grids) {
            MachineGrid exist = machineGridMapper.selectOne(
                    new LambdaQueryWrapper<MachineGrid>()
                            .eq(MachineGrid::getMachineId, machineId)
                            .eq(MachineGrid::getGridNo, grid.getGridNo())
            );
            if (exist != null) {
                exist.setSkuId(grid.getSkuId());
                exist.setStatus(grid.getStatus());
                exist.setLastCheckTime(LocalDateTime.now());
                machineGridMapper.updateById(exist);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleJam(Long orderId, String handleResult) {
        PayOrder order = payOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_EXIST);
        }
        if (!"JAMMED".equals(order.getShipStatus())) {
            throw new BusinessException("订单状态不是卡货，无需处理");
        }

        if ("RETRY_SUCCESS".equals(handleResult)) {
            order.setShipStatus("SUCCESS");
            order.setShipTime(LocalDateTime.now());
            order.setMotorStatus("NORMAL");
        } else if ("REFUND".equals(handleResult)) {
            order.setShipStatus("FAILED");
            order.setMotorStatus("FAULT");
        }
        payOrderMapper.updateById(order);
    }

    @Override
    public IPage<ReplenishmentPlan> getReplenishmentRecord(Integer pageNum, Integer pageSize) {
        Long replenisherId = UserContext.getUserId();

        LambdaQueryWrapper<ReplenishmentPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReplenishmentPlan::getReplenisherId, replenisherId);
        wrapper.eq(ReplenishmentPlan::getStatus, "COMPLETED");
        wrapper.orderByDesc(ReplenishmentPlan::getEndTime);

        Page<ReplenishmentPlan> page = new Page<>(pageNum, pageSize);
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public List<ReplenishmentDetail> getReplenishmentDetails(Long planId) {
        return replenishmentDetailMapper.selectList(
                new LambdaQueryWrapper<ReplenishmentDetail>()
                        .eq(ReplenishmentDetail::getPlanId, planId)
        );
    }

    private ReplenishmentPlanVO convertToVO(ReplenishmentPlan plan) {
        ReplenishmentPlanVO vo = new ReplenishmentPlanVO();
        BeanUtils.copyProperties(plan, vo);

        MachinePoint machine = machinePointMapper.selectById(plan.getMachineId());
        if (machine != null) {
            vo.setMachineName(machine.getMachineName());
            vo.setMachineCode(machine.getMachineCode());
        }

        SysUser user = sysUserMapper.selectById(plan.getReplenisherId());
        if (user != null) {
            vo.setReplenisherName(user.getNickname());
        }

        return vo;
    }

    private void auditProbabilityAfterReplenishment(Long machineId, Long operatorId) {
        MachinePoint machine = machinePointMapper.selectById(machineId);
        if (machine == null || machine.getCurrentSeriesId() == null) {
            return;
        }

        Long seriesId = machine.getCurrentSeriesId();

        List<MachineGrid> grids = machineGridMapper.selectList(
                new LambdaQueryWrapper<MachineGrid>()
                        .eq(MachineGrid::getMachineId, machineId)
                        .eq(MachineGrid::getStatus, "FILLED")
        );

        Map<Long, Long> skuCountMap = grids.stream()
                .filter(g -> g.getSkuId() != null)
                .collect(Collectors.groupingBy(MachineGrid::getSkuId, Collectors.counting()));

        int totalCount = grids.size();
        List<BlindboxSku> skuList = blindboxSkuMapper.selectList(
                new LambdaQueryWrapper<BlindboxSku>()
                        .eq(BlindboxSku::getSeriesId, seriesId)
        );

        boolean isConsistent = true;
        StringBuilder beforeAudit = new StringBuilder();
        for (BlindboxSku sku : skuList) {
            long actualCount = skuCountMap.getOrDefault(sku.getId(), 0L);
            double actualProbability = totalCount > 0 ? (double) actualCount / totalCount : 0;
            double expectedProbability = sku.getProbability() != null ? sku.getProbability().doubleValue() : 0;

            beforeAudit.append(String.format("SKU[%s]: 预期概率=%.4f, 实际数量=%d, 实际概率=%.4f; ",
                    sku.getSkuName(), expectedProbability, actualCount, actualProbability));

            if (Math.abs(actualProbability - expectedProbability) > 0.05) {
                isConsistent = false;
            }
        }

        ProbabilityAuditLog auditLog = new ProbabilityAuditLog();
        auditLog.setMachineId(machineId);
        auditLog.setSeriesId(seriesId);
        auditLog.setAuditType("REPLENISH");
        auditLog.setBeforeAudit(beforeAudit.toString());
        auditLog.setAfterAudit("补货完成，已记录实际库存");
        auditLog.setIsConsistent(isConsistent ? 1 : 0);
        auditLog.setOperatorId(operatorId);
        auditLog.setCreatedTime(LocalDateTime.now());
        probabilityAuditLogMapper.insert(auditLog);
    }
}
