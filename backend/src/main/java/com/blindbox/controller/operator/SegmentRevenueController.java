package com.blindbox.controller.operator;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blindbox.common.Result;
import com.blindbox.service.MachineMigrationService;
import com.blindbox.vo.SegmentRevenueVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/operator/segment-revenue")
@RequiredArgsConstructor
public class SegmentRevenueController {

    private final MachineMigrationService machineMigrationService;

    @GetMapping("/page")
    public Result<IPage<SegmentRevenueVO>> getSegmentRevenuePage(
            @RequestParam(required = false) Long machineId,
            @RequestParam(required = false) Long migrationId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(machineMigrationService.getSegmentRevenuePage(machineId, migrationId, pageNum, pageSize));
    }
}
