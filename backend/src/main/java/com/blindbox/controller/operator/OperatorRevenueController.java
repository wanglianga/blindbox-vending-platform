package com.blindbox.controller.operator;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blindbox.common.Result;
import com.blindbox.entity.RevenueShare;
import com.blindbox.service.RevenueService;
import com.blindbox.vo.RevenueStatsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/operator/revenue")
@RequiredArgsConstructor
public class OperatorRevenueController {

    private final RevenueService revenueService;

    @GetMapping("/page")
    public Result<IPage<RevenueShare>> getRevenuePage(
            @RequestParam(required = false) Long mallId,
            @RequestParam(required = false) Long supplierId,
            @RequestParam(required = false) String settleStatus,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(revenueService.getRevenuePage(mallId, supplierId, settleStatus, pageNum, pageSize));
    }

    @GetMapping("/stats")
    public Result<RevenueStatsVO> getRevenueStats(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return Result.success(revenueService.getRevenueStats(startDate, endDate));
    }
}
