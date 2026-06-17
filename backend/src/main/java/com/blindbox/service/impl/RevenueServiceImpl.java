package com.blindbox.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blindbox.entity.RevenueShare;
import com.blindbox.mapper.RevenueShareMapper;
import com.blindbox.service.RevenueService;
import com.blindbox.vo.RevenueStatsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RevenueServiceImpl extends ServiceImpl<RevenueShareMapper, RevenueShare> implements RevenueService {

    @Override
    public IPage<RevenueShare> getRevenuePage(Long mallId, Long supplierId, String settleStatus, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<RevenueShare> wrapper = new LambdaQueryWrapper<>();
        if (mallId != null) {
            wrapper.eq(RevenueShare::getMallId, mallId);
        }
        if (supplierId != null) {
            wrapper.eq(RevenueShare::getSupplierId, supplierId);
        }
        if (settleStatus != null && !settleStatus.isEmpty()) {
            wrapper.eq(RevenueShare::getSettleStatus, settleStatus);
        }
        wrapper.orderByDesc(RevenueShare::getCreatedTime);

        Page<RevenueShare> page = new Page<>(pageNum, pageSize);
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public RevenueStatsVO getRevenueStats(String startDate, String endDate) {
        LambdaQueryWrapper<RevenueShare> wrapper = new LambdaQueryWrapper<>();
        if (startDate != null && !startDate.isEmpty()) {
            LocalDateTime start = LocalDate.parse(startDate).atStartOfDay();
            wrapper.ge(RevenueShare::getCreatedTime, start);
        }
        if (endDate != null && !endDate.isEmpty()) {
            LocalDateTime end = LocalDate.parse(endDate).atTime(LocalTime.MAX);
            wrapper.le(RevenueShare::getCreatedTime, end);
        }

        List<RevenueShare> list = baseMapper.selectList(wrapper);

        RevenueStatsVO stats = new RevenueStatsVO();
        stats.setTotalAmount(BigDecimal.ZERO);
        stats.setMallCommission(BigDecimal.ZERO);
        stats.setSupplierShare(BigDecimal.ZERO);
        stats.setOperatorShare(BigDecimal.ZERO);
        stats.setPlatformShare(BigDecimal.ZERO);
        stats.setOrderCount((long) list.size());

        for (RevenueShare revenue : list) {
            stats.setTotalAmount(stats.getTotalAmount().add(revenue.getTotalAmount()));
            stats.setMallCommission(stats.getMallCommission().add(revenue.getMallCommission()));
            stats.setSupplierShare(stats.getSupplierShare().add(revenue.getSupplierShare()));
            stats.setOperatorShare(stats.getOperatorShare().add(revenue.getOperatorShare()));
            stats.setPlatformShare(stats.getPlatformShare().add(revenue.getPlatformShare()));
        }

        return stats;
    }
}
