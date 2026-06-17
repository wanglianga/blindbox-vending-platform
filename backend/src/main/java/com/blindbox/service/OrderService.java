package com.blindbox.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.blindbox.entity.PayOrder;
import com.blindbox.vo.OrderDetailVO;

public interface OrderService extends IService<PayOrder> {

    IPage<OrderDetailVO> getOrderPage(Long machineId, String payStatus, String startDate, String endDate, Integer pageNum, Integer pageSize);

    OrderDetailVO getOrderDetail(Long id);
}
