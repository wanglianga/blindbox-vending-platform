package com.blindbox.controller.supplier;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blindbox.common.Result;
import com.blindbox.common.UserContext;
import com.blindbox.entity.IpSeries;
import com.blindbox.service.IpSeriesService;
import com.blindbox.vo.IpSeriesDetailVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/supplier/series")
@RequiredArgsConstructor
public class SupplierSeriesController {

    private final IpSeriesService ipSeriesService;

    @GetMapping("/page")
    public Result<IPage<IpSeries>> getMySeriesPage(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<IpSeries> page = ipSeriesService.getSeriesPage(name, status, pageNum, pageSize);
        return Result.success(page);
    }

    @GetMapping("/{id}")
    public Result<IpSeriesDetailVO> getSeriesDetail(@PathVariable Long id) {
        return Result.success(ipSeriesService.getSeriesDetail(id));
    }

    @PostMapping
    public Result<Void> addSeries(@RequestBody IpSeries ipSeries) {
        ipSeries.setSupplierId(UserContext.getCompanyId());
        ipSeriesService.addSeries(ipSeries);
        return Result.success();
    }

    @PutMapping
    public Result<Void> updateSeries(@RequestBody IpSeries ipSeries) {
        ipSeriesService.updateSeries(ipSeries);
        return Result.success();
    }

    @PutMapping("/{id}/hidden-rule")
    public Result<Void> setHiddenRule(@PathVariable Long id, @RequestParam String hiddenRule) {
        ipSeriesService.setHiddenRule(id, hiddenRule);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<Void> toggleStatus(@PathVariable Long id, @RequestParam Integer status) {
        ipSeriesService.toggleStatus(id, status);
        return Result.success();
    }
}
