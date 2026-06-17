package com.blindbox.controller.replenisher;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blindbox.common.Result;
import com.blindbox.dto.CheckInventoryDTO;
import com.blindbox.dto.CompleteReplenishmentDTO;
import com.blindbox.entity.MachineGrid;
import com.blindbox.entity.ReplenishmentDetail;
import com.blindbox.entity.ReplenishmentPlan;
import com.blindbox.service.ReplenishmentService;
import com.blindbox.vo.ReplenishmentPlanVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/replenisher")
@RequiredArgsConstructor
public class ReplenisherController {

    private final ReplenishmentService replenishmentService;

    @GetMapping("/task/page")
    public Result<IPage<ReplenishmentPlanVO>> getMyTaskList(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(replenishmentService.getMyTaskList(status, pageNum, pageSize));
    }

    @GetMapping("/task/{planId}")
    public Result<ReplenishmentPlanVO> getTaskDetail(@PathVariable Long planId) {
        return Result.success(replenishmentService.getTaskDetail(planId));
    }

    @PostMapping("/task/{planId}/start")
    public Result<Void> startReplenishment(@PathVariable Long planId) {
        replenishmentService.startReplenishment(planId);
        return Result.success();
    }

    @PostMapping("/task/complete")
    public Result<Void> completeReplenishment(@RequestBody CompleteReplenishmentDTO dto) {
        replenishmentService.completeReplenishment(dto.getPlanId(), dto.getDetails());
        return Result.success();
    }

    @GetMapping("/machine/{machineId}/grid")
    public Result<List<MachineGrid>> getMachineGridInventory(@PathVariable Long machineId) {
        return Result.success(replenishmentService.getMachineGridInventory(machineId));
    }

    @PostMapping("/machine/check-inventory")
    public Result<Void> checkGridInventory(@RequestBody CheckInventoryDTO dto) {
        replenishmentService.checkGridInventory(dto.getMachineId(), dto.getGrids());
        return Result.success();
    }

    @PostMapping("/jam/{orderId}/handle")
    public Result<Void> handleJam(@PathVariable Long orderId, @RequestParam String handleResult) {
        replenishmentService.handleJam(orderId, handleResult);
        return Result.success();
    }

    @GetMapping("/record/page")
    public Result<IPage<ReplenishmentPlan>> getReplenishmentRecord(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(replenishmentService.getReplenishmentRecord(pageNum, pageSize));
    }

    @GetMapping("/record/{planId}/detail")
    public Result<List<ReplenishmentDetail>> getReplenishmentDetails(@PathVariable Long planId) {
        return Result.success(replenishmentService.getReplenishmentDetails(planId));
    }
}
