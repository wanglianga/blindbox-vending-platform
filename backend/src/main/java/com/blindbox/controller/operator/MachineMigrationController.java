package com.blindbox.controller.operator;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blindbox.common.Result;
import com.blindbox.dto.MachineMigrationDTO;
import com.blindbox.dto.MigrationInventoryItemDTO;
import com.blindbox.entity.MachineMigration;
import com.blindbox.service.MachineMigrationService;
import com.blindbox.vo.MachineMigrationDetailVO;
import com.blindbox.vo.MigrationInventoryDetailVO;
import com.blindbox.vo.SegmentRevenueVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/operator/migration")
@RequiredArgsConstructor
public class MachineMigrationController {

    private final MachineMigrationService machineMigrationService;

    @GetMapping("/page")
    public Result<IPage<MachineMigrationDetailVO>> getMigrationPage(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long machineId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(machineMigrationService.getMigrationPage(status, machineId, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public Result<MachineMigrationDetailVO> getMigrationDetail(@PathVariable Long id) {
        return Result.success(machineMigrationService.getMigrationDetail(id));
    }

    @PostMapping
    public Result<Void> createMigration(@Valid @RequestBody MachineMigrationDTO dto) {
        machineMigrationService.createMigration(dto);
        return Result.success();
    }

    @PostMapping("/{id}/complete")
    public Result<Void> completeMigration(@PathVariable Long id) {
        machineMigrationService.completeMigration(id);
        return Result.success();
    }

    @PutMapping("/{id}/inventory")
    public Result<Void> updateMigrationInventory(
            @PathVariable Long id,
            @Valid @RequestBody List<MigrationInventoryItemDTO> items) {
        machineMigrationService.updateMigrationInventory(id, items);
        return Result.success();
    }

    @GetMapping("/{id}/inventory")
    public Result<List<MigrationInventoryDetailVO>> getMigrationInventory(@PathVariable Long id) {
        return Result.success(machineMigrationService.getMigrationInventory(id));
    }

    @GetMapping("/{id}/revenue")
    public Result<IPage<SegmentRevenueVO>> getSegmentRevenue(
            @PathVariable Long id,
            @RequestParam(required = false) Long machineId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(machineMigrationService.getSegmentRevenuePage(machineId, id, pageNum, pageSize));
    }

    @GetMapping("/machine/{machineId}/history")
    public Result<List<MachineMigration>> getMachineMigrationHistory(@PathVariable Long machineId) {
        return Result.success(machineMigrationService.getMachineMigrationHistory(machineId));
    }
}
