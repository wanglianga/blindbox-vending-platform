package com.blindbox.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blindbox.common.BusinessException;
import com.blindbox.common.ResultCode;
import com.blindbox.common.UserContext;
import com.blindbox.dto.MachineMigrationDTO;
import com.blindbox.dto.MigrationInventoryItemDTO;
import com.blindbox.entity.*;
import com.blindbox.mapper.*;
import com.blindbox.service.MachineMigrationService;
import com.blindbox.vo.MachineMigrationDetailVO;
import com.blindbox.vo.MigrationInventoryDetailVO;
import com.blindbox.vo.SegmentRevenueVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MachineMigrationServiceImpl extends ServiceImpl<MachineMigrationMapper, MachineMigration> implements MachineMigrationService {

    private final MachinePointMapper machinePointMapper;
    private final MallMapper mallMapper;
    private final MachineGridMapper machineGridMapper;
    private final BlindboxSkuMapper blindboxSkuMapper;
    private final IpSeriesMapper ipSeriesMapper;
    private final SysUserMapper sysUserMapper;
    private final MigrationInventoryDetailMapper migrationInventoryDetailMapper;
    private final RevenueShareMapper revenueShareMapper;
    private final PayOrderMapper payOrderMapper;

    @Override
    public IPage<MachineMigrationDetailVO> getMigrationPage(String status, Long machineId, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<MachineMigration> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq(MachineMigration::getStatus, status);
        }
        if (machineId != null) {
            wrapper.eq(MachineMigration::getMachineId, machineId);
        }
        wrapper.orderByDesc(MachineMigration::getCreatedTime);

        Page<MachineMigration> page = new Page<>(pageNum, pageSize);
        IPage<MachineMigration> migrationPage = baseMapper.selectPage(page, wrapper);

        return migrationPage.convert(this::convertToDetailVO);
    }

    @Override
    public MachineMigrationDetailVO getMigrationDetail(Long id) {
        MachineMigration migration = baseMapper.selectById(id);
        if (migration == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        MachineMigrationDetailVO vo = convertToDetailVO(migration);
        vo.setInventoryDetails(getMigrationInventory(id));

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createMigration(MachineMigrationDTO dto) {
        MachinePoint machine = machinePointMapper.selectById(dto.getMachineId());
        if (machine == null) {
            throw new BusinessException("机器不存在");
        }

        Mall oldMall = mallMapper.selectById(machine.getMallId());
        Mall newMall = mallMapper.selectById(dto.getNewMallId());
        if (newMall == null) {
            throw new BusinessException("新商场不存在");
        }

        LambdaQueryWrapper<MachineGrid> gridWrapper = new LambdaQueryWrapper<>();
        gridWrapper.eq(MachineGrid::getMachineId, dto.getMachineId())
                .eq(MachineGrid::getStatus, "FILLED");
        List<MachineGrid> grids = machineGridMapper.selectList(gridWrapper);
        Map<Long, Integer> inventoryBeforeMap = new HashMap<>();
        for (MachineGrid grid : grids) {
            if (grid.getSkuId() != null) {
                inventoryBeforeMap.merge(grid.getSkuId(), 1, Integer::sum);
            }
        }
        int totalInventoryBefore = grids.size();

        MachineMigration migration = new MachineMigration();
        migration.setMigrationNo("MM" + IdUtil.getSnowflakeNextIdStr());
        migration.setMachineId(dto.getMachineId());
        migration.setMachineCode(machine.getMachineCode());
        migration.setMachineName(machine.getMachineName());
        migration.setOldMallId(machine.getMallId());
        migration.setOldMallName(oldMall != null ? oldMall.getName() : "");
        migration.setOldLocationDetail(machine.getLocationDetail());
        migration.setOldCommissionRate(oldMall != null ? oldMall.getCommissionRate() : BigDecimal.ZERO);
        migration.setNewMallId(dto.getNewMallId());
        migration.setNewMallName(newMall.getName());
        migration.setNewLocationDetail(dto.getNewLocationDetail());
        migration.setNewCommissionRate(dto.getNewCommissionRate());
        migration.setMigrationReason(dto.getMigrationReason());
        migration.setMigrationStartTime(dto.getMigrationStartTime());
        migration.setMigrationEndTime(dto.getMigrationEndTime());
        migration.setDowntimeMinutes(dto.getDowntimeMinutes());
        migration.setDowntimeLoss(dto.getDowntimeLoss());
        migration.setLossRemark(dto.getLossRemark());
        migration.setTotalInventoryBefore(totalInventoryBefore);
        migration.setInventoryTransferStatus("PENDING");
        migration.setStatus("IN_PROGRESS");
        migration.setOperatorId(UserContext.getUserId());
        SysUser operator = sysUserMapper.selectById(UserContext.getUserId());
        migration.setOperatorName(operator != null ? operator.getNickname() : "");
        migration.setCreatedTime(LocalDateTime.now());
        migration.setUpdatedTime(LocalDateTime.now());
        baseMapper.insert(migration);

        for (Map.Entry<Long, Integer> entry : inventoryBeforeMap.entrySet()) {
            Long skuId = entry.getKey();
            Integer qtyBefore = entry.getValue();

            BlindboxSku sku = blindboxSkuMapper.selectById(skuId);
            IpSeries series = sku != null ? ipSeriesMapper.selectById(sku.getSeriesId()) : null;

            MigrationInventoryDetail detail = new MigrationInventoryDetail();
            detail.setMigrationId(migration.getId());
            detail.setMigrationNo(migration.getMigrationNo());
            detail.setSkuId(skuId);
            detail.setSkuName(sku != null ? sku.getSkuName() : "");
            detail.setSkuCode(sku != null ? sku.getSkuCode() : "");
            detail.setSeriesId(sku != null ? sku.getSeriesId() : null);
            detail.setSeriesName(series != null ? series.getName() : "");
            detail.setQtyBefore(qtyBefore);
            detail.setQtyAfter(0);
            detail.setQtyTransferred(0);
            detail.setQtyLost(0);
            detail.setTransferStatus("PENDING");
            detail.setCreatedTime(LocalDateTime.now());
            migrationInventoryDetailMapper.insert(detail);
        }

        machine.setStatus("STOPPED");
        machinePointMapper.updateById(machine);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeMigration(Long migrationId) {
        MachineMigration migration = baseMapper.selectById(migrationId);
        if (migration == null) {
            throw new BusinessException("迁移记录不存在");
        }
        if (!"IN_PROGRESS".equals(migration.getStatus())) {
            throw new BusinessException("该迁移无法完成");
        }

        LambdaQueryWrapper<MigrationInventoryDetail> detailWrapper = new LambdaQueryWrapper<>();
        detailWrapper.eq(MigrationInventoryDetail::getMigrationId, migrationId);
        List<MigrationInventoryDetail> details = migrationInventoryDetailMapper.selectList(detailWrapper);

        int totalInventoryAfter = 0;
        int totalTransferred = 0;
        int totalLost = 0;

        for (MigrationInventoryDetail detail : details) {
            totalInventoryAfter += detail.getQtyAfter() != null ? detail.getQtyAfter() : 0;
            totalTransferred += detail.getQtyTransferred() != null ? detail.getQtyTransferred() : 0;
            totalLost += detail.getQtyLost() != null ? detail.getQtyLost() : 0;
        }

        migration.setTotalInventoryAfter(totalInventoryAfter);
        migration.setTransferredInventory(totalTransferred);
        migration.setLostInventory(totalLost);
        migration.setInventoryTransferStatus(totalLost > 0 ? "PARTIAL" : "COMPLETE");
        migration.setStatus("COMPLETED");
        migration.setMigrationEndTime(LocalDateTime.now());
        migration.setUpdatedTime(LocalDateTime.now());
        baseMapper.updateById(migration);

        MachinePoint machine = machinePointMapper.selectById(migration.getMachineId());
        if (machine != null) {
            machine.setMallId(migration.getNewMallId());
            machine.setLocationDetail(migration.getNewLocationDetail());
            machine.setStatus("ONLINE");
            machinePointMapper.updateById(machine);

            Mall newMall = mallMapper.selectById(migration.getNewMallId());
            if (newMall != null) {
                newMall.setCommissionRate(migration.getNewCommissionRate());
                mallMapper.updateById(newMall);
            }
        }
    }

    @Override
    public List<MigrationInventoryDetailVO> getMigrationInventory(Long migrationId) {
        LambdaQueryWrapper<MigrationInventoryDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MigrationInventoryDetail::getMigrationId, migrationId);
        List<MigrationInventoryDetail> details = migrationInventoryDetailMapper.selectList(wrapper);

        return details.stream().map(detail -> {
            MigrationInventoryDetailVO vo = new MigrationInventoryDetailVO();
            BeanUtils.copyProperties(detail, vo);

            if (detail.getSkuId() != null) {
                BlindboxSku sku = blindboxSkuMapper.selectById(detail.getSkuId());
                if (sku != null) {
                    vo.setSkuImage(sku.getImageUrl());
                }
            }

            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public IPage<SegmentRevenueVO> getSegmentRevenuePage(Long machineId, Long migrationId, Integer pageNum, Integer pageSize) {
        final LocalDateTime finalOldPeriodEnd;
        final LocalDateTime finalNewPeriodStart;
        final Long finalOldMallId;
        final Long finalNewMallId;
        final BigDecimal finalOldCommissionRate;
        final BigDecimal finalNewCommissionRate;

        if (migrationId != null) {
            MachineMigration migration = baseMapper.selectById(migrationId);
            if (migration != null) {
                finalOldPeriodEnd = migration.getMigrationStartTime();
                finalNewPeriodStart = migration.getMigrationEndTime();
                finalOldMallId = migration.getOldMallId();
                finalNewMallId = migration.getNewMallId();
                finalOldCommissionRate = migration.getOldCommissionRate();
                finalNewCommissionRate = migration.getNewCommissionRate();
            } else {
                finalOldPeriodEnd = null;
                finalNewPeriodStart = null;
                finalOldMallId = null;
                finalNewMallId = null;
                finalOldCommissionRate = null;
                finalNewCommissionRate = null;
            }
        } else {
            finalOldPeriodEnd = null;
            finalNewPeriodStart = null;
            finalOldMallId = null;
            finalNewMallId = null;
            finalOldCommissionRate = null;
            finalNewCommissionRate = null;
        }

        LambdaQueryWrapper<RevenueShare> wrapper = new LambdaQueryWrapper<>();
        if (machineId != null) {
            wrapper.eq(RevenueShare::getOperatorId, machineId);
        }
        wrapper.orderByDesc(RevenueShare::getCreatedTime);

        Page<RevenueShare> page = new Page<>(pageNum, pageSize);
        IPage<RevenueShare> revenuePage = revenueShareMapper.selectPage(page, wrapper);

        return revenuePage.convert(revenue -> {
            SegmentRevenueVO vo = new SegmentRevenueVO();
            BeanUtils.copyProperties(revenue, vo);

            if (revenue.getOrderId() != null) {
                PayOrder order = payOrderMapper.selectById(revenue.getOrderId());
                if (order != null) {
                    vo.setMachineId(order.getMachineId());
                    vo.setOrderTime(order.getCreatedTime());

                    MachinePoint machine = machinePointMapper.selectById(order.getMachineId());
                    if (machine != null) {
                        vo.setMachineCode(machine.getMachineCode());
                    }

                    if (finalOldPeriodEnd != null && order.getCreatedTime().isBefore(finalOldPeriodEnd)) {
                        vo.setPeriodType("OLD_LOCATION");
                        vo.setPeriodStart(null);
                        vo.setPeriodEnd(finalOldPeriodEnd);
                        vo.setMallId(finalOldMallId);
                        vo.setCommissionRate(finalOldCommissionRate);
                    } else if (finalNewPeriodStart != null && !order.getCreatedTime().isBefore(finalNewPeriodStart)) {
                        vo.setPeriodType("NEW_LOCATION");
                        vo.setPeriodStart(finalNewPeriodStart);
                        vo.setPeriodEnd(null);
                        vo.setMallId(finalNewMallId);
                        vo.setCommissionRate(finalNewCommissionRate);
                    } else {
                        vo.setPeriodType("MIGRATION_PERIOD");
                        vo.setPeriodStart(finalOldPeriodEnd);
                        vo.setPeriodEnd(finalNewPeriodStart);
                    }
                }
            }

            if (vo.getMallId() != null) {
                Mall mall = mallMapper.selectById(vo.getMallId());
                if (mall != null) {
                    vo.setMallName(mall.getName());
                    if (vo.getCommissionRate() == null) {
                        vo.setCommissionRate(mall.getCommissionRate());
                    }
                }
            }

            return vo;
        });
    }

    @Override
    public List<MachineMigration> getMachineMigrationHistory(Long machineId) {
        LambdaQueryWrapper<MachineMigration> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MachineMigration::getMachineId, machineId);
        wrapper.orderByDesc(MachineMigration::getCreatedTime);
        return baseMapper.selectList(wrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateMigrationInventory(Long migrationId, List<MigrationInventoryItemDTO> items) {
        MachineMigration migration = baseMapper.selectById(migrationId);
        if (migration == null) {
            throw new BusinessException("迁移记录不存在");
        }
        if (!"IN_PROGRESS".equals(migration.getStatus())) {
            throw new BusinessException("该迁移已完成，无法修改库存");
        }

        for (MigrationInventoryItemDTO item : items) {
            LambdaQueryWrapper<MigrationInventoryDetail> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MigrationInventoryDetail::getMigrationId, migrationId)
                    .eq(MigrationInventoryDetail::getSkuId, item.getSkuId());
            MigrationInventoryDetail detail = migrationInventoryDetailMapper.selectOne(wrapper);

            if (detail != null) {
                int qtyAfter = item.getQtyAfter() != null ? item.getQtyAfter() : 0;
                int qtyBefore = detail.getQtyBefore() != null ? detail.getQtyBefore() : 0;
                int qtyTransferred = Math.min(qtyBefore, qtyAfter);
                int qtyLost = Math.max(0, qtyBefore - qtyAfter);

                detail.setQtyAfter(qtyAfter);
                detail.setQtyTransferred(qtyTransferred);
                detail.setQtyLost(qtyLost);
                detail.setTransferStatus(qtyLost > 0 ? "PARTIAL" : "COMPLETE");
                detail.setRemark(item.getRemark());
                migrationInventoryDetailMapper.updateById(detail);
            }
        }

        migration.setUpdatedTime(LocalDateTime.now());
        baseMapper.updateById(migration);
    }

    private MachineMigrationDetailVO convertToDetailVO(MachineMigration migration) {
        MachineMigrationDetailVO vo = new MachineMigrationDetailVO();
        BeanUtils.copyProperties(migration, vo);

        if (migration.getOperatorId() != null) {
            SysUser user = sysUserMapper.selectById(migration.getOperatorId());
            if (user != null) {
                vo.setOperatorName(user.getNickname());
            }
        }

        return vo;
    }
}
