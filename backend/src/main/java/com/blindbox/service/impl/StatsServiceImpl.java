package com.blindbox.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blindbox.entity.MachinePoint;
import com.blindbox.entity.MachineRuntimeLog;
import com.blindbox.entity.PayOrder;
import com.blindbox.entity.RevenueShare;
import com.blindbox.mapper.MachinePointMapper;
import com.blindbox.mapper.MachineRuntimeLogMapper;
import com.blindbox.mapper.PayOrderMapper;
import com.blindbox.mapper.RevenueShareMapper;
import com.blindbox.service.StatsService;
import com.blindbox.vo.MachineRuntimeStatsVO;
import com.blindbox.vo.SalesStatsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final PayOrderMapper payOrderMapper;
    private final RevenueShareMapper revenueShareMapper;
    private final MachinePointMapper machinePointMapper;
    private final MachineRuntimeLogMapper machineRuntimeLogMapper;

    @Override
    public SalesStatsVO getSalesStats(Long mallId, String startDate, String endDate) {
        LambdaQueryWrapper<PayOrder> orderWrapper = new LambdaQueryWrapper<>();
        if (mallId != null) {
            List<MachinePoint> machines = machinePointMapper.selectList(
                    new LambdaQueryWrapper<MachinePoint>()
                            .eq(MachinePoint::getMallId, mallId)
                            .select(MachinePoint::getId)
            );
            if (!machines.isEmpty()) {
                orderWrapper.in(PayOrder::getMachineId, machines.stream().map(MachinePoint::getId).toList());
            }
        }
        orderWrapper.eq(PayOrder::getPayStatus, "PAID");
        if (startDate != null && !startDate.isEmpty()) {
            LocalDateTime start = LocalDate.parse(startDate).atStartOfDay();
            orderWrapper.ge(PayOrder::getPayTime, start);
        }
        if (endDate != null && !endDate.isEmpty()) {
            LocalDateTime end = LocalDate.parse(endDate).atTime(LocalTime.MAX);
            orderWrapper.le(PayOrder::getPayTime, end);
        }

        List<PayOrder> orders = payOrderMapper.selectList(orderWrapper);

        SalesStatsVO stats = new SalesStatsVO();
        stats.setOrderCount((long) orders.size());
        stats.setTotalAmount(BigDecimal.ZERO);
        stats.setMallCommission(BigDecimal.ZERO);

        for (PayOrder order : orders) {
            stats.setTotalAmount(stats.getTotalAmount().add(order.getPayAmount()));
        }

        if (mallId != null) {
            LambdaQueryWrapper<RevenueShare> revenueWrapper = new LambdaQueryWrapper<>();
            revenueWrapper.eq(RevenueShare::getMallId, mallId);
            if (startDate != null && !startDate.isEmpty()) {
                LocalDateTime start = LocalDate.parse(startDate).atStartOfDay();
                revenueWrapper.ge(RevenueShare::getCreatedTime, start);
            }
            if (endDate != null && !endDate.isEmpty()) {
                LocalDateTime end = LocalDate.parse(endDate).atTime(LocalTime.MAX);
                revenueWrapper.le(RevenueShare::getCreatedTime, end);
            }
            List<RevenueShare> revenues = revenueShareMapper.selectList(revenueWrapper);
            for (RevenueShare r : revenues) {
                stats.setMallCommission(stats.getMallCommission().add(r.getMallCommission()));
            }

            Long machineCount = machinePointMapper.selectCount(
                    new LambdaQueryWrapper<MachinePoint>().eq(MachinePoint::getMallId, mallId)
            );
            stats.setMachineCount(machineCount);
        }

        return stats;
    }

    @Override
    public MachineRuntimeStatsVO getRuntimeStats(Long machineId, String startDate, String endDate) {
        LambdaQueryWrapper<MachineRuntimeLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MachineRuntimeLog::getMachineId, machineId);
        if (startDate != null && !startDate.isEmpty()) {
            LocalDateTime start = LocalDate.parse(startDate).atStartOfDay();
            wrapper.ge(MachineRuntimeLog::getEventTime, start);
        }
        if (endDate != null && !endDate.isEmpty()) {
            LocalDateTime end = LocalDate.parse(endDate).atTime(LocalTime.MAX);
            wrapper.le(MachineRuntimeLog::getEventTime, end);
        }

        List<MachineRuntimeLog> logs = machineRuntimeLogMapper.selectList(wrapper);

        MachineRuntimeStatsVO stats = new MachineRuntimeStatsVO();
        long totalSeconds = 0;
        long offlineSeconds = 0;
        long faultSeconds = 0;

        for (MachineRuntimeLog log : logs) {
            if (log.getDurationSeconds() != null) {
                totalSeconds += log.getDurationSeconds();
                if ("OFFLINE".equals(log.getEventType())) {
                    offlineSeconds += log.getDurationSeconds();
                } else if ("FAULT".equals(log.getEventType())) {
                    faultSeconds += log.getDurationSeconds();
                }
            }
        }

        if (totalSeconds == 0 && startDate != null && endDate != null) {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            totalSeconds = java.time.Duration.between(start.atStartOfDay(), end.atTime(LocalTime.MAX)).getSeconds();
        }

        stats.setTotalSeconds(totalSeconds);
        stats.setOfflineSeconds(offlineSeconds);
        stats.setFaultSeconds(faultSeconds);
        stats.setOnlineSeconds(totalSeconds - offlineSeconds - faultSeconds);
        stats.setOnlineRate(totalSeconds > 0 ? (double) stats.getOnlineSeconds() / totalSeconds * 100 : 0.0);

        return stats;
    }
}
