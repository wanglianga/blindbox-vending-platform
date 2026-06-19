package com.blindbox.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blindbox.common.BusinessException;
import com.blindbox.common.ResultCode;
import com.blindbox.common.UserContext;
import com.blindbox.dto.RepairerConfirmDTO;
import com.blindbox.dto.StuckHandleDTO;
import com.blindbox.entity.*;
import com.blindbox.mapper.*;
import com.blindbox.service.StuckHandleService;
import com.blindbox.vo.InventoryCorrectionVO;
import com.blindbox.vo.StuckHandleDetailVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StuckHandleServiceImpl extends ServiceImpl<StuckHandleRecordMapper, StuckHandleRecord> implements StuckHandleService {

    private final CsTicketMapper csTicketMapper;
    private final PayOrderMapper payOrderMapper;
    private final MachinePointMapper machinePointMapper;
    private final MachineGridMapper machineGridMapper;
    private final BlindboxSkuMapper blindboxSkuMapper;
    private final SysUserMapper sysUserMapper;
    private final InventoryCorrectionMapper inventoryCorrectionMapper;
    private final RevenueShareMapper revenueShareMapper;
    private final RefundOrderMapper refundOrderMapper;

    @Override
    public IPage<StuckHandleDetailVO> getStuckRecordPage(String status, String machineCode, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<StuckHandleRecord> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq(StuckHandleRecord::getStatus, status);
        }
        if (machineCode != null && !machineCode.isEmpty()) {
            wrapper.eq(StuckHandleRecord::getMachineCode, machineCode);
        }
        wrapper.orderByDesc(StuckHandleRecord::getCreatedTime);

        Page<StuckHandleRecord> page = new Page<>(pageNum, pageSize);
        IPage<StuckHandleRecord> recordPage = baseMapper.selectPage(page, wrapper);

        return recordPage.convert(this::convertToDetailVO);
    }

    @Override
    public StuckHandleDetailVO getStuckRecordDetail(Long id) {
        StuckHandleRecord record = baseMapper.selectById(id);
        if (record == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return convertToDetailVO(record);
    }

    @Override
    public StuckHandleDetailVO getStuckRecordByTicketId(Long ticketId) {
        LambdaQueryWrapper<StuckHandleRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StuckHandleRecord::getTicketId, ticketId);
        StuckHandleRecord record = baseMapper.selectOne(wrapper);
        if (record == null) {
            return null;
        }
        return convertToDetailVO(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleStuck(StuckHandleDTO dto) {
        CsTicket ticket = csTicketMapper.selectById(dto.getTicketId());
        if (ticket == null) {
            throw new BusinessException("工单不存在");
        }
        if (!"OPEN".equals(ticket.getStatus()) && !"PROCESSING".equals(ticket.getStatus())) {
            throw new BusinessException("工单已处理，无法重复处理");
        }

        PayOrder order = null;
        MachinePoint machine = null;
        MachineGrid grid = null;
        BlindboxSku sku = null;

        if (ticket.getOrderId() != null) {
            order = payOrderMapper.selectById(ticket.getOrderId());
            if (order != null) {
                machine = machinePointMapper.selectById(order.getMachineId());
                if (order.getGridNo() != null) {
                    LambdaQueryWrapper<MachineGrid> gridWrapper = new LambdaQueryWrapper<>();
                    gridWrapper.eq(MachineGrid::getMachineId, order.getMachineId())
                            .eq(MachineGrid::getGridNo, order.getGridNo());
                    grid = machineGridMapper.selectOne(gridWrapper);
                }
                if (order.getDrawSkuId() != null) {
                    sku = blindboxSkuMapper.selectById(order.getDrawSkuId());
                }
            }
        }

        StuckHandleRecord record = new StuckHandleRecord();
        record.setRecordNo("SH" + IdUtil.getSnowflakeNextIdStr());
        record.setTicketId(ticket.getId());
        record.setTicketNo(ticket.getTicketNo());
        if (order != null) {
            record.setOrderId(order.getId());
            record.setOrderNo(order.getOrderNo());
            record.setMachineId(order.getMachineId());
            record.setGridNo(order.getGridNo());
            record.setSkuId(order.getDrawSkuId());
            if (machine != null) {
                record.setMachineCode(machine.getMachineCode());
            }
            if (sku != null) {
                record.setSkuName(sku.getSkuName());
            }
        }

        record.setPayFlowChecked(dto.getPayFlowChecked());
        record.setMotorStatusChecked(dto.getMotorStatusChecked());
        record.setSensorStatusChecked(dto.getSensorStatusChecked());
        record.setInventoryChangeChecked(dto.getInventoryChangeChecked());
        record.setMonitorPhotoChecked(dto.getMonitorPhotoChecked());
        record.setCheckRemark(dto.getCheckRemark());
        record.setHandleDecision(dto.getHandleDecision());
        record.setRefundAmount(dto.getRefundAmount());
        record.setReissueSkuId(dto.getReissueSkuId());
        record.setReissueGridNo(dto.getReissueGridNo());
        record.setRepairContent(dto.getRepairContent());

        record.setStatus("PENDING_REPAIR");
        if ("REFUND".equals(dto.getHandleDecision())) {
            record.setStatus("COMPLETED");
        } else if ("REISSUE".equals(dto.getHandleDecision())) {
            record.setStatus("COMPLETED");
        } else if ("REPAIR".equals(dto.getHandleDecision())) {
            record.setStatus("PENDING_REPAIR");
        }

        record.setCsHandlerId(UserContext.getUserId());
        record.setHandleTime(LocalDateTime.now());
        record.setCreatedTime(LocalDateTime.now());
        record.setUpdatedTime(LocalDateTime.now());
        baseMapper.insert(record);

        if ("REFUND".equals(dto.getHandleDecision()) && order != null) {
            RefundOrder refund = new RefundOrder();
            refund.setRefundNo("RF" + IdUtil.getSnowflakeNextIdStr());
            refund.setOrderId(order.getId());
            refund.setOrderNo(order.getOrderNo());
            refund.setRefundAmount(dto.getRefundAmount() != null ? dto.getRefundAmount() : order.getPayAmount());
            refund.setRefundType("NO_SHIP");
            refund.setRefundReason("卡货退款: " + dto.getCheckRemark());
            refund.setApplicant("客服处理");
            refund.setStatus("APPROVED");
            refund.setAuditorId(UserContext.getUserId());
            refund.setAuditTime(LocalDateTime.now());
            refund.setAuditRemark("卡货处理，已审核通过");
            refund.setRefundTime(LocalDateTime.now());
            refundOrderMapper.insert(refund);

            order.setPayStatus("REFUNDED");
            payOrderMapper.updateById(order);

            LambdaQueryWrapper<RevenueShare> revenueWrapper = new LambdaQueryWrapper<>();
            revenueWrapper.eq(RevenueShare::getOrderId, order.getId());
            RevenueShare revenue = revenueShareMapper.selectOne(revenueWrapper);
            if (revenue != null) {
                revenue.setSettleStatus("CANCELLED");
                revenueShareMapper.updateById(revenue);
            }
        }

        if ("REISSUE".equals(dto.getHandleDecision()) && dto.getReissueSkuId() != null && order != null) {
            if (dto.getReissueGridNo() != null) {
                LambdaQueryWrapper<MachineGrid> gridWrapper = new LambdaQueryWrapper<>();
                gridWrapper.eq(MachineGrid::getMachineId, order.getMachineId())
                        .eq(MachineGrid::getGridNo, Integer.parseInt(dto.getReissueGridNo()));
                MachineGrid reissueGrid = machineGridMapper.selectOne(gridWrapper);
                if (reissueGrid != null) {
                    reissueGrid.setStatus("EMPTY");
                    reissueGrid.setSkuId(null);
                    machineGridMapper.updateById(reissueGrid);
                }
            }
        }

        ticket.setStatus("CLOSED");
        ticket.setHandlerId(UserContext.getUserId());
        ticket.setHandleResult("处理决定: " + dto.getHandleDecision() + ", 备注: " + dto.getCheckRemark());
        ticket.setHandleTime(LocalDateTime.now());
        csTicketMapper.updateById(ticket);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void repairerConfirm(RepairerConfirmDTO dto) {
        StuckHandleRecord record = baseMapper.selectById(dto.getStuckRecordId());
        if (record == null) {
            throw new BusinessException("卡货处理记录不存在");
        }
        if (!"PENDING_REPAIR".equals(record.getStatus())) {
            throw new BusinessException("该记录无需维修确认");
        }

        record.setRepairerId(UserContext.getUserId());
        record.setRepairerConfirmResult(dto.getConfirmResult());
        record.setRepairerConfirmTime(LocalDateTime.now());

        if ("ITEM_STILL_IN_GRID".equals(dto.getConfirmResult())) {
            record.setStatus("COMPLETED");

            createInventoryCorrection(record);
        } else if ("REPAIRED".equals(dto.getConfirmResult())) {
            record.setStatus("COMPLETED");
        } else if ("ITEM_LOST".equals(dto.getConfirmResult())) {
            record.setStatus("COMPLETED");
        }

        record.setUpdatedTime(LocalDateTime.now());
        baseMapper.updateById(record);
    }

    @Transactional(rollbackFor = Exception.class)
    private void createInventoryCorrection(StuckHandleRecord record) {
        LambdaQueryWrapper<MachineGrid> gridWrapper = new LambdaQueryWrapper<>();
        gridWrapper.eq(MachineGrid::getMachineId, record.getMachineId())
                .eq(MachineGrid::getGridNo, record.getGridNo());
        MachineGrid grid = machineGridMapper.selectOne(gridWrapper);

        BlindboxSku sku = null;
        if (record.getSkuId() != null) {
            sku = blindboxSkuMapper.selectById(record.getSkuId());
        }

        LambdaQueryWrapper<MachineGrid> countWrapper = new LambdaQueryWrapper<>();
        countWrapper.eq(MachineGrid::getMachineId, record.getMachineId())
                .eq(MachineGrid::getSkuId, record.getSkuId())
                .eq(MachineGrid::getStatus, "FILLED");
        Integer currentInventory = Math.toIntExact(machineGridMapper.selectCount(countWrapper));

        BigDecimal beforeRevenue = BigDecimal.ZERO;
        if (record.getOrderId() != null) {
            LambdaQueryWrapper<RevenueShare> revenueWrapper = new LambdaQueryWrapper<>();
            revenueWrapper.eq(RevenueShare::getOrderId, record.getOrderId());
            RevenueShare revenue = revenueShareMapper.selectOne(revenueWrapper);
            if (revenue != null) {
                beforeRevenue = revenue.getTotalAmount();
            }
        }

        InventoryCorrection correction = new InventoryCorrection();
        correction.setCorrectionNo("IC" + IdUtil.getSnowflakeNextIdStr());
        correction.setStuckRecordId(record.getId());
        correction.setStuckRecordNo(record.getRecordNo());
        correction.setMachineId(record.getMachineId());
        correction.setMachineCode(record.getMachineCode());
        correction.setGridNo(record.getGridNo());
        correction.setSkuId(record.getSkuId());
        correction.setSkuName(sku != null ? sku.getSkuName() : "");
        correction.setBeforeInventory(currentInventory);
        correction.setAfterInventory(currentInventory + 1);
        correction.setCorrectionQty(1);
        correction.setBeforeRevenue(beforeRevenue);
        correction.setAfterRevenue(BigDecimal.ZERO);
        correction.setCorrectionRevenue(beforeRevenue.negate());
        correction.setCorrectionType("REVERSE_CORRECTION");
        correction.setReason("维修人员确认格口实物仍在，反向修正库存与收益");
        correction.setOperatorId(UserContext.getUserId());
        correction.setOperateTime(LocalDateTime.now());
        correction.setStatus("COMPLETED");
        correction.setCreatedTime(LocalDateTime.now());
        correction.setUpdatedTime(LocalDateTime.now());
        inventoryCorrectionMapper.insert(correction);

        if (grid != null) {
            grid.setStatus("FILLED");
            grid.setSkuId(record.getSkuId());
            grid.setLastCheckTime(LocalDateTime.now());
            machineGridMapper.updateById(grid);
        }

        if (record.getOrderId() != null) {
            LambdaQueryWrapper<RevenueShare> revenueWrapper = new LambdaQueryWrapper<>();
            revenueWrapper.eq(RevenueShare::getOrderId, record.getOrderId());
            RevenueShare revenue = revenueShareMapper.selectOne(revenueWrapper);
            if (revenue != null) {
                revenue.setSettleStatus("REVERSED");
                revenueShareMapper.updateById(revenue);
            }
        }

        record.setInventoryCorrectionId(correction.getId());
        baseMapper.updateById(record);
    }

    @Override
    public IPage<InventoryCorrectionVO> getInventoryCorrectionPage(String status, Long machineId, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<InventoryCorrection> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq(InventoryCorrection::getStatus, status);
        }
        if (machineId != null) {
            wrapper.eq(InventoryCorrection::getMachineId, machineId);
        }
        wrapper.orderByDesc(InventoryCorrection::getCreatedTime);

        Page<InventoryCorrection> page = new Page<>(pageNum, pageSize);
        IPage<InventoryCorrection> correctionPage = inventoryCorrectionMapper.selectPage(page, wrapper);

        return correctionPage.convert(this::convertToCorrectionVO);
    }

    @Override
    public InventoryCorrectionVO getInventoryCorrectionDetail(Long id) {
        InventoryCorrection correction = inventoryCorrectionMapper.selectById(id);
        if (correction == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return convertToCorrectionVO(correction);
    }

    private StuckHandleDetailVO convertToDetailVO(StuckHandleRecord record) {
        StuckHandleDetailVO vo = new StuckHandleDetailVO();
        BeanUtils.copyProperties(record, vo);

        if (record.getMachineId() != null) {
            MachinePoint machine = machinePointMapper.selectById(record.getMachineId());
            if (machine != null) {
                vo.setMachineName(machine.getMachineName());
            }
        }

        if (record.getSkuId() != null) {
            BlindboxSku sku = blindboxSkuMapper.selectById(record.getSkuId());
            if (sku != null) {
                vo.setSkuImage(sku.getImageUrl());
            }
        }

        if (record.getOrderId() != null) {
            PayOrder order = payOrderMapper.selectById(record.getOrderId());
            if (order != null) {
                vo.setPayAmount(order.getPayAmount());
                vo.setPayStatus(order.getPayStatus());
                vo.setShipStatus(order.getShipStatus());
                vo.setMotorStatus(order.getMotorStatus());
            }
        }

        if (record.getCsHandlerId() != null) {
            SysUser user = sysUserMapper.selectById(record.getCsHandlerId());
            if (user != null) {
                vo.setCsHandlerName(user.getNickname());
            }
        }

        if (record.getRepairerId() != null) {
            SysUser user = sysUserMapper.selectById(record.getRepairerId());
            if (user != null) {
                vo.setRepairerName(user.getNickname());
            }
        }

        if (record.getReissueSkuId() != null) {
            BlindboxSku sku = blindboxSkuMapper.selectById(record.getReissueSkuId());
            if (sku != null) {
                vo.setReissueSkuName(sku.getSkuName());
            }
        }

        return vo;
    }

    private InventoryCorrectionVO convertToCorrectionVO(InventoryCorrection correction) {
        InventoryCorrectionVO vo = new InventoryCorrectionVO();
        BeanUtils.copyProperties(correction, vo);

        if (correction.getMachineId() != null) {
            MachinePoint machine = machinePointMapper.selectById(correction.getMachineId());
            if (machine != null) {
                vo.setMachineName(machine.getMachineName());
            }
        }

        if (correction.getSkuId() != null) {
            BlindboxSku sku = blindboxSkuMapper.selectById(correction.getSkuId());
            if (sku != null) {
                vo.setSkuImage(sku.getImageUrl());
            }
        }

        if (correction.getOperatorId() != null) {
            SysUser user = sysUserMapper.selectById(correction.getOperatorId());
            if (user != null) {
                vo.setOperatorName(user.getNickname());
            }
        }

        return vo;
    }
}
