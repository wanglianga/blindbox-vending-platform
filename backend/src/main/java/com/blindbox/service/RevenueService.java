package com.blindbox.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.blindbox.entity.RevenueShare;
import com.blindbox.vo.RevenueStatsVO;

public interface RevenueService extends IService<RevenueShare> {

    IPage<RevenueShare> getRevenuePage(Long mallId, Long supplierId, String settleStatus, Integer pageNum, Integer pageSize);

    RevenueStatsVO getRevenueStats(String startDate, String endDate);
}
