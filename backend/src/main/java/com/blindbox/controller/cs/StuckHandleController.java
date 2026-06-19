package com.blindbox.controller.cs;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blindbox.common.Result;
import com.blindbox.dto.RepairerConfirmDTO;
import com.blindbox.dto.StuckHandleDTO;
import com.blindbox.service.StuckHandleService;
import com.blindbox.vo.InventoryCorrectionVO;
import com.blindbox.vo.StuckHandleDetailVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cs/stuck")
@RequiredArgsConstructor
public class StuckHandleController {

    private final StuckHandleService stuckHandleService;

    @GetMapping("/page")
    public Result<IPage<StuckHandleDetailVO>> getStuckRecordPage(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String machineCode,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(stuckHandleService.getStuckRecordPage(status, machineCode, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public Result<StuckHandleDetailVO> getStuckRecordDetail(@PathVariable Long id) {
        return Result.success(stuckHandleService.getStuckRecordDetail(id));
    }

    @GetMapping("/ticket/{ticketId}")
    public Result<StuckHandleDetailVO> getStuckRecordByTicketId(@PathVariable Long ticketId) {
        return Result.success(stuckHandleService.getStuckRecordByTicketId(ticketId));
    }

    @PostMapping("/handle")
    public Result<Void> handleStuck(@Valid @RequestBody StuckHandleDTO dto) {
        stuckHandleService.handleStuck(dto);
        return Result.success();
    }

    @PostMapping("/repairer/confirm")
    public Result<Void> repairerConfirm(@Valid @RequestBody RepairerConfirmDTO dto) {
        stuckHandleService.repairerConfirm(dto);
        return Result.success();
    }

    @GetMapping("/correction/page")
    public Result<IPage<InventoryCorrectionVO>> getInventoryCorrectionPage(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long machineId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(stuckHandleService.getInventoryCorrectionPage(status, machineId, pageNum, pageSize));
    }

    @GetMapping("/correction/{id}")
    public Result<InventoryCorrectionVO> getInventoryCorrectionDetail(@PathVariable Long id) {
        return Result.success(stuckHandleService.getInventoryCorrectionDetail(id));
    }
}
