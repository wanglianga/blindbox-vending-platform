package com.blindbox.controller.mall;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blindbox.common.Result;
import com.blindbox.common.UserContext;
import com.blindbox.entity.RevenueShare;
import com.blindbox.service.RevenueService;
import com.blindbox.vo.RevenueStatsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mall/revenue")
@RequiredArgsConstructor
public class MallRevenueController {

    private final RevenueService revenueService;

    @GetMapping("/page")
    public Result<IPage<RevenueShare>> getRevenuePage(
            @RequestParam(required = false) String settleStatus,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long mallId = UserContext.getCompanyId();
        return Result.success(revenueService.getRevenuePage(mallId, null, settleStatus, pageNum, pageSize));
    }

    @GetMapping("/stats")
    public Result<RevenueStatsVO> getRevenueStats(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return Result.success(revenueService.getRevenueStats(startDate, endDate));
    }
}
