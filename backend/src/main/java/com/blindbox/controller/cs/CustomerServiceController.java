package com.blindbox.controller.cs;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blindbox.common.Result;
import com.blindbox.entity.CsTicket;
import com.blindbox.entity.RefundOrder;
import com.blindbox.service.CustomerServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cs")
@RequiredArgsConstructor
public class CustomerServiceController {

    private final CustomerServiceService customerServiceService;

    @GetMapping("/ticket/page")
    public Result<IPage<CsTicket>> getTicketList(
            @RequestParam(required = false) String ticketType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(customerServiceService.getTicketList(ticketType, status, priority, pageNum, pageSize));
    }

    @GetMapping("/ticket/{id}")
    public Result<CsTicket> getTicketDetail(@PathVariable Long id) {
        return Result.success(customerServiceService.getTicketDetail(id));
    }

    @PostMapping("/ticket/{id}/handle")
    public Result<Void> handleTicket(@PathVariable Long id, @RequestParam String handleResult) {
        customerServiceService.handleTicket(id, handleResult);
        return Result.success();
    }

    @GetMapping("/refund/page")
    public Result<IPage<RefundOrder>> getRefundList(
            @RequestParam(required = false) String refundType,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(customerServiceService.getRefundList(refundType, status, pageNum, pageSize));
    }

    @GetMapping("/refund/{id}")
    public Result<RefundOrder> getRefundDetail(@PathVariable Long id) {
        return Result.success(customerServiceService.getRefundDetail(id));
    }

    @PostMapping("/refund/{id}/audit")
    public Result<Void> auditRefund(
            @PathVariable Long id,
            @RequestParam Integer approved,
            @RequestParam(required = false) String auditRemark) {
        customerServiceService.auditRefund(id, approved, auditRemark);
        return Result.success();
    }

    @PostMapping("/duplicate-pay/{orderId}")
    public Result<Void> handleDuplicatePay(
            @PathVariable Long orderId,
            @RequestParam(required = false) String remark) {
        customerServiceService.handleDuplicatePay(orderId, remark);
        return Result.success();
    }

    @PostMapping("/probability/{ticketId}")
    public Result<Void> handleProbabilityQuestion(
            @PathVariable Long ticketId,
            @RequestParam String answer) {
        customerServiceService.handleProbabilityQuestion(ticketId, answer);
        return Result.success();
    }
}
