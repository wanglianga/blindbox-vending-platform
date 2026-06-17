package com.blindbox.controller.operator;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blindbox.common.Result;
import com.blindbox.entity.MachinePoint;
import com.blindbox.service.MachinePointService;
import com.blindbox.vo.MachinePointVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/operator/machine")
@RequiredArgsConstructor
public class OperatorMachineController {

    private final MachinePointService machinePointService;

    @GetMapping("/page")
    public Result<IPage<MachinePointVO>> getMachinePage(
            @RequestParam(required = false) Long mallId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(machinePointService.getMachinePage(mallId, status, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public Result<MachinePointVO> getMachineDetail(@PathVariable Long id) {
        return Result.success(machinePointService.getMachineDetail(id));
    }

    @PostMapping
    public Result<Void> addMachine(@RequestBody MachinePoint machinePoint) {
        machinePointService.addMachine(machinePoint);
        return Result.success();
    }

    @PutMapping
    public Result<Void> updateMachine(@RequestBody MachinePoint machinePoint) {
        machinePointService.updateMachine(machinePoint);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteMachine(@PathVariable Long id) {
        machinePointService.deleteMachine(id);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<Void> toggleStatus(@PathVariable Long id, @RequestParam String status) {
        machinePointService.toggleStatus(id, status);
        return Result.success();
    }
}
