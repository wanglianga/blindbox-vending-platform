package com.blindbox.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blindbox.common.BusinessException;
import com.blindbox.common.ResultCode;
import com.blindbox.entity.*;
import com.blindbox.mapper.*;
import com.blindbox.service.DrawService;
import com.blindbox.vo.DrawResultVO;
import com.blindbox.vo.MachineDetailVO;
import com.blindbox.vo.SkuProbabilityVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DrawServiceImpl implements DrawService {

    private final MachinePointMapper machinePointMapper;
    private final MachineGridMapper machineGridMapper;
    private final IpSeriesMapper ipSeriesMapper;
    private final BlindboxSkuMapper blindboxSkuMapper;
    private final PayOrderMapper payOrderMapper;
    private final RevenueShareMapper revenueShareMapper;
    private final MallMapper mallMapper;
    private final ProbabilityAuditLogMapper probabilityAuditLogMapper;

    @Override
    public MachineDetailVO getMachineDetail(String machineCode) {
        MachinePoint machine = machinePointMapper.selectOne(
                new LambdaQueryWrapper<MachinePoint>()
                        .eq(MachinePoint::getMachineCode, machineCode)
        );
        if (machine == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        MachineDetailVO vo = new MachineDetailVO();
        vo.setMachineId(machine.getId());
        vo.setMachineCode(machine.getMachineCode());
        vo.setMachineName(machine.getMachineName());
        vo.setStatus(machine.getStatus());
        vo.setTotalGridCount(machine.getGridCount());

        if (machine.getCurrentSeriesId() != null) {
            IpSeries series = ipSeriesMapper.selectById(machine.getCurrentSeriesId());
            if (series != null) {
                vo.setSeriesId(series.getId());
                vo.setSeriesName(series.getName());
                vo.setSeriesCover(series.getCoverImage());
                vo.setPrice(series.getPrice());
                vo.setHiddenRule(series.getHiddenRule());
            }
        }

        List<MachineGrid> grids = machineGridMapper.selectList(
                new LambdaQueryWrapper<MachineGrid>()
                        .eq(MachineGrid::getMachineId, machine.getId())
        );

        int filledCount = 0;
        int emptyCount = 0;
        Map<Long, Integer> skuStockMap = new HashMap<>();

        for (MachineGrid grid : grids) {
            if ("FILLED".equals(grid.getStatus()) && grid.getSkuId() != null) {
                filledCount++;
                skuStockMap.merge(grid.getSkuId(), 1, Integer::sum);
            } else {
                emptyCount++;
            }
        }

        vo.setFilledGridCount(filledCount);
        vo.setEmptyGridCount(emptyCount);

        if (machine.getCurrentSeriesId() != null) {
            List<BlindboxSku> skuList = blindboxSkuMapper.selectList(
                    new LambdaQueryWrapper<BlindboxSku>()
                            .eq(BlindboxSku::getSeriesId, machine.getCurrentSeriesId())
                            .orderByAsc(BlindboxSku::getSortOrder)
            );

            List<SkuProbabilityVO> probabilityList = new ArrayList<>();
            int totalFilled = filledCount;

            for (BlindboxSku sku : skuList) {
                SkuProbabilityVO skuVO = new SkuProbabilityVO();
                skuVO.setSkuId(sku.getId());
                skuVO.setSkuName(sku.getSkuName());
                skuVO.setSkuCode(sku.getSkuCode());
                skuVO.setImageUrl(sku.getImageUrl());
                skuVO.setIsHidden(sku.getIsHidden());
                skuVO.setDisplayProbability(sku.getProbability());

                int stockCount = skuStockMap.getOrDefault(sku.getId(), 0);
                skuVO.setStockCount(stockCount);

                BigDecimal actualProbability = totalFilled > 0
                        ? BigDecimal.valueOf(stockCount).divide(BigDecimal.valueOf(totalFilled), 6, RoundingMode.HALF_UP)
                        : BigDecimal.ZERO;
                skuVO.setActualProbability(actualProbability);

                probabilityList.add(skuVO);
            }

            vo.setSkuProbabilityList(probabilityList);
        }

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DrawResultVO draw(String machineCode, String payType) {
        MachinePoint machine = machinePointMapper.selectOne(
                new LambdaQueryWrapper<MachinePoint>()
                        .eq(MachinePoint::getMachineCode, machineCode)
        );
        if (machine == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        if ("OFFLINE".equals(machine.getStatus())) {
            throw new BusinessException(ResultCode.MACHINE_OFFLINE);
        }
        if ("FAULT".equals(machine.getStatus())) {
            throw new BusinessException(ResultCode.MACHINE_FAULT);
        }
        if ("STOPPED".equals(machine.getStatus())) {
            throw new BusinessException("机器已停机");
        }

        if (machine.getCurrentSeriesId() == null) {
            throw new BusinessException("机器暂未投放商品");
        }

        IpSeries series = ipSeriesMapper.selectById(machine.getCurrentSeriesId());
        if (series == null) {
            throw new BusinessException("系列不存在");
        }

        List<MachineGrid> filledGrids = machineGridMapper.selectList(
                new LambdaQueryWrapper<MachineGrid>()
                        .eq(MachineGrid::getMachineId, machine.getId())
                        .eq(MachineGrid::getStatus, "FILLED")
                        .isNotNull(MachineGrid::getSkuId)
        );

        if (filledGrids.isEmpty()) {
            throw new BusinessException(ResultCode.INVENTORY_NOT_ENOUGH);
        }

        MachineGrid selectedGrid = drawByStock(filledGrids);
        if (selectedGrid == null) {
            throw new BusinessException(ResultCode.DRAW_FAILED);
        }

        BlindboxSku drawnSku = blindboxSkuMapper.selectById(selectedGrid.getSkuId());

        PayOrder order = new PayOrder();
        order.setOrderNo("PO" + IdUtil.getSnowflakeNextIdStr());
        order.setMachineId(machine.getId());
        order.setSeriesId(series.getId());
        order.setPayAmount(series.getPrice());
        order.setPayType(payType != null ? payType : "WECHAT");
        order.setPayStatus("PAID");
        order.setPayTime(LocalDateTime.now());
        order.setDrawSkuId(drawnSku.getId());
        order.setGridNo(selectedGrid.getGridNo());
        order.setShipStatus("SHIPPING");
        order.setMotorStatus("NORMAL");
        payOrderMapper.insert(order);

        boolean shipSuccess = controlMotorShip(selectedGrid);

        if (shipSuccess) {
            selectedGrid.setStatus("EMPTY");
            selectedGrid.setSkuId(null);
            selectedGrid.setLastCheckTime(LocalDateTime.now());
            machineGridMapper.updateById(selectedGrid);

            order.setShipStatus("SUCCESS");
            order.setShipTime(LocalDateTime.now());
            payOrderMapper.updateById(order);

            generateRevenueShare(order, machine, series, drawnSku);

            auditProbabilityAfterDraw(machine.getId(), series.getId(), order.getId());
        } else {
            order.setShipStatus("JAMMED");
            order.setMotorStatus("FAULT");
            payOrderMapper.updateById(order);
        }

        DrawResultVO result = new DrawResultVO();
        result.setOrderNo(order.getOrderNo());
        result.setSkuId(drawnSku.getId());
        result.setSkuName(drawnSku.getSkuName());
        result.setSkuCode(drawnSku.getSkuCode());
        result.setSkuImage(drawnSku.getImageUrl());
        result.setIsHidden(drawnSku.getIsHidden());
        result.setGridNo(selectedGrid.getGridNo());
        result.setPayAmount(order.getPayAmount());
        result.setPayStatus(order.getPayStatus());
        result.setShipStatus(order.getShipStatus());
        result.setMotorStatus(order.getMotorStatus());
        result.setPayTime(order.getPayTime());
        result.setShipTime(order.getShipTime());

        return result;
    }

    private MachineGrid drawByStock(List<MachineGrid> filledGrids) {
        if (filledGrids == null || filledGrids.isEmpty()) {
            return null;
        }

        Map<Long, List<MachineGrid>> skuGridMap = filledGrids.stream()
                .collect(Collectors.groupingBy(MachineGrid::getSkuId));

        List<Long> skuIds = new ArrayList<>(skuGridMap.keySet());
        List<Integer> weights = new ArrayList<>();

        for (Long skuId : skuIds) {
            weights.add(skuGridMap.get(skuId).size());
        }

        int totalWeight = weights.stream().mapToInt(Integer::intValue).sum();
        int random = RandomUtil.randomInt(totalWeight);

        int cumulativeWeight = 0;
        Long selectedSkuId = null;

        for (int i = 0; i < skuIds.size(); i++) {
            cumulativeWeight += weights.get(i);
            if (random < cumulativeWeight) {
                selectedSkuId = skuIds.get(i);
                break;
            }
        }

        if (selectedSkuId == null) {
            selectedSkuId = skuIds.get(skuIds.size() - 1);
        }

        List<MachineGrid> skuGrids = skuGridMap.get(selectedSkuId);
        return skuGrids.get(RandomUtil.randomInt(skuGrids.size()));
    }

    private boolean controlMotorShip(MachineGrid grid) {
        try {
            log.info("电机控制：格口 {} 开始出货...", grid.getGridNo());
            Thread.sleep(100);
            log.info("电机控制：格口 {} 出货完成", grid.getGridNo());
            return true;
        } catch (InterruptedException e) {
            log.error("电机控制异常", e);
            return false;
        }
    }

    private void generateRevenueShare(PayOrder order, MachinePoint machine, IpSeries series, BlindboxSku sku) {
        RevenueShare revenueShare = new RevenueShare();
        revenueShare.setOrderId(order.getId());
        revenueShare.setOrderNo(order.getOrderNo());
        revenueShare.setTotalAmount(order.getPayAmount());
        revenueShare.setMallId(machine.getMallId());
        revenueShare.setSupplierId(series.getSupplierId());
        revenueShare.setOperatorId(machine.getOperatorId());
        revenueShare.setSettleStatus("UNSETTLED");

        BigDecimal totalAmount = order.getPayAmount();

        Mall mall = mallMapper.selectById(machine.getMallId());
        BigDecimal mallCommission = BigDecimal.ZERO;
        if (mall != null && mall.getCommissionRate() != null) {
            mallCommission = totalAmount.multiply(mall.getCommissionRate())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }
        revenueShare.setMallCommission(mallCommission);

        BigDecimal remainingAfterMall = totalAmount.subtract(mallCommission);

        BigDecimal platformShare = remainingAfterMall.multiply(BigDecimal.valueOf(0.1))
                .setScale(2, RoundingMode.HALF_UP);
        revenueShare.setPlatformShare(platformShare);

        BigDecimal remaining = remainingAfterMall.subtract(platformShare);

        BigDecimal supplierShare = remaining.multiply(BigDecimal.valueOf(0.6))
                .setScale(2, RoundingMode.HALF_UP);
        revenueShare.setSupplierShare(supplierShare);

        BigDecimal operatorShare = remaining.subtract(supplierShare);
        revenueShare.setOperatorShare(operatorShare);

        revenueShareMapper.insert(revenueShare);
    }

    private void auditProbabilityAfterDraw(Long machineId, Long seriesId, Long orderId) {
        try {
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
            StringBuilder auditInfo = new StringBuilder();
            for (BlindboxSku sku : skuList) {
                long actualCount = skuCountMap.getOrDefault(sku.getId(), 0L);
                double actualProbability = totalCount > 0 ? (double) actualCount / totalCount : 0;
                double expectedProbability = sku.getProbability() != null ? sku.getProbability().doubleValue() : 0;

                auditInfo.append(String.format("SKU[%s]: 预期=%.4f, 实量=%d, 实概=%.4f; ",
                        sku.getSkuName(), expectedProbability, actualCount, actualProbability));

                if (Math.abs(actualProbability - expectedProbability) > 0.1 && actualCount > 0) {
                    isConsistent = false;
                }
            }

            ProbabilityAuditLog auditLog = new ProbabilityAuditLog();
            auditLog.setMachineId(machineId);
            auditLog.setSeriesId(seriesId);
            auditLog.setAuditType("DRAW");
            auditLog.setBeforeAudit("抽盒后审计，订单: " + orderId);
            auditLog.setAfterAudit(auditInfo.toString());
            auditLog.setIsConsistent(isConsistent ? 1 : 0);
            auditLog.setCreatedTime(LocalDateTime.now());
            probabilityAuditLogMapper.insert(auditLog);
        } catch (Exception e) {
            log.error("抽盒后概率审计异常", e);
        }
    }
}
