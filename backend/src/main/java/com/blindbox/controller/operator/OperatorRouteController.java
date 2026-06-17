package com.blindbox.controller.operator;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blindbox.common.Result;
import com.blindbox.entity.ReplenishmentRoute;
import com.blindbox.service.ReplenishmentRouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/operator/route")
@RequiredArgsConstructor
public class OperatorRouteController {

    private final ReplenishmentRouteService replenishmentRouteService;

    @GetMapping("/page")
    public Result<IPage<ReplenishmentRoute>> getRoutePage(
            @RequestParam(required = false) String routeName,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(replenishmentRouteService.getRoutePage(routeName, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public Result<ReplenishmentRoute> getRouteDetail(@PathVariable Long id) {
        return Result.success(replenishmentRouteService.getRouteDetail(id));
    }

    @PostMapping
    public Result<Void> addRoute(@RequestBody ReplenishmentRoute route) {
        replenishmentRouteService.addRoute(route);
        return Result.success();
    }

    @PutMapping
    public Result<Void> updateRoute(@RequestBody ReplenishmentRoute route) {
        replenishmentRouteService.updateRoute(route);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteRoute(@PathVariable Long id) {
        replenishmentRouteService.deleteRoute(id);
        return Result.success();
    }
}
