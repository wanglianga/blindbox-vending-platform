package com.blindbox.controller.operator;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blindbox.common.Result;
import com.blindbox.service.OrderService;
import com.blindbox.vo.OrderDetailVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/operator/order")
@RequiredArgsConstructor
public class OperatorOrderController {

    private final OrderService orderService;

    @GetMapping("/page")
    public Result<IPage<OrderDetailVO>> getOrderPage(
            @RequestParam(required = false) Long machineId,
            @RequestParam(required = false) String payStatus,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(orderService.getOrderPage(machineId, payStatus, startDate, endDate, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public Result<OrderDetailVO> getOrderDetail(@PathVariable Long id) {
        return Result.success(orderService.getOrderDetail(id));
    }
}
