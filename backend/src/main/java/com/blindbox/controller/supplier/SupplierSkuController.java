package com.blindbox.controller.supplier;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blindbox.common.Result;
import com.blindbox.entity.BlindboxSku;
import com.blindbox.service.BlindboxSkuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/supplier/sku")
@RequiredArgsConstructor
public class SupplierSkuController {

    private final BlindboxSkuService blindboxSkuService;

    @GetMapping("/page")
    public Result<IPage<BlindboxSku>> getSkuPage(
            @RequestParam Long seriesId,
            @RequestParam(required = false) Integer isHidden,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return Result.success(blindboxSkuService.getSkuPage(seriesId, isHidden, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public Result<BlindboxSku> getSkuDetail(@PathVariable Long id) {
        return Result.success(blindboxSkuService.getSkuDetail(id));
    }

    @PostMapping
    public Result<Void> addSku(@RequestBody BlindboxSku sku) {
        blindboxSkuService.addSku(sku);
        return Result.success();
    }

    @PutMapping
    public Result<Void> updateSku(@RequestBody BlindboxSku sku) {
        blindboxSkuService.updateSku(sku);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteSku(@PathVariable Long id) {
        blindboxSkuService.deleteSku(id);
        return Result.success();
    }

    @PutMapping("/{id}/probability")
    public Result<Void> setProbability(@PathVariable Long id, @RequestParam BigDecimal probability) {
        blindboxSkuService.setProbability(id, probability);
        return Result.success();
    }
}
