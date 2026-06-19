package com.blindbox.controller.supplier;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blindbox.common.Result;
import com.blindbox.entity.MachineMigration;
import com.blindbox.mapper.MachinePointMapper;
import com.blindbox.service.MachineMigrationService;
import com.blindbox.vo.MachineMigrationDetailVO;
import com.blindbox.vo.MigrationInventoryDetailVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/supplier/migration")
@RequiredArgsConstructor
public class SupplierMigrationController {

    private final MachineMigrationService machineMigrationService;
    private final MachinePointMapper machinePointMapper;

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

    @GetMapping("/{id}/inventory")
    public Result<List<MigrationInventoryDetailVO>> getMigrationInventory(@PathVariable Long id) {
        return Result.success(machineMigrationService.getMigrationInventory(id));
    }

    @GetMapping("/machine/{machineId}/history")
    public Result<List<MachineMigration>> getMachineMigrationHistory(@PathVariable Long machineId) {
        return Result.success(machineMigrationService.getMachineMigrationHistory(machineId));
    }
}
