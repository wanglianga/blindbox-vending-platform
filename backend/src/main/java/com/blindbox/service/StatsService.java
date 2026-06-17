package com.blindbox.service;

import com.blindbox.vo.MachineRuntimeStatsVO;
import com.blindbox.vo.SalesStatsVO;

public interface StatsService {

    SalesStatsVO getSalesStats(Long mallId, String startDate, String endDate);

    MachineRuntimeStatsVO getRuntimeStats(Long machineId, String startDate, String endDate);
}
