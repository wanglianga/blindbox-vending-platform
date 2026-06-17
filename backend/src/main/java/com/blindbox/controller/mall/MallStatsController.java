package com.blindbox.controller.mall;

import com.blindbox.common.Result;
import com.blindbox.common.UserContext;
import com.blindbox.service.StatsService;
import com.blindbox.vo.MachineRuntimeStatsVO;
import com.blindbox.vo.SalesStatsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mall/stats")
@RequiredArgsConstructor
public class MallStatsController {

    private final StatsService statsService;

    @GetMapping("/sales")
    public Result<SalesStatsVO> getSalesStats(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        Long mallId = UserContext.getCompanyId();
        return Result.success(statsService.getSalesStats(mallId, startDate, endDate));
    }

    @GetMapping("/runtime")
    public Result<MachineRuntimeStatsVO> getRuntimeStats(
            @RequestParam Long machineId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return Result.success(statsService.getRuntimeStats(machineId, startDate, endDate));
    }
}
