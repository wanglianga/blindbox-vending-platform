package com.blindbox.controller.supplier;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blindbox.common.Result;
import com.blindbox.common.UserContext;
import com.blindbox.entity.RevenueShare;
import com.blindbox.service.RevenueService;
import com.blindbox.vo.RevenueStatsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/supplier/revenue")
@RequiredArgsConstructor
public class SupplierRevenueController {

    private final RevenueService revenueService;

    @GetMapping("/page")
    public Result<IPage<RevenueShare>> getRevenuePage(
            @RequestParam(required = false) String settleStatus,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long supplierId = UserContext.getCompanyId();
        return Result.success(revenueService.getRevenuePage(null, supplierId, settleStatus, pageNum, pageSize));
    }

    @GetMapping("/stats")
    public Result<RevenueStatsVO> getRevenueStats(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return Result.success(revenueService.getRevenueStats(startDate, endDate));
    }
}
