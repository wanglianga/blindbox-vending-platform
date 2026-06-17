package com.blindbox.controller.operator;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blindbox.common.Result;
import com.blindbox.entity.IpSeries;
import com.blindbox.service.IpSeriesService;
import com.blindbox.vo.IpSeriesDetailVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/operator/series")
@RequiredArgsConstructor
public class OperatorSeriesController {

    private final IpSeriesService ipSeriesService;

    @GetMapping("/page")
    public Result<IPage<IpSeries>> getSeriesPage(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(ipSeriesService.getSeriesPage(name, status, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public Result<IpSeriesDetailVO> getSeriesDetail(@PathVariable Long id) {
        return Result.success(ipSeriesService.getSeriesDetail(id));
    }

    @PutMapping("/{id}/status")
    public Result<Void> toggleStatus(@PathVariable Long id, @RequestParam Integer status) {
        ipSeriesService.toggleStatus(id, status);
        return Result.success();
    }
}
