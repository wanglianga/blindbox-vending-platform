package com.blindbox.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.blindbox.entity.CsTicket;
import com.blindbox.entity.RefundOrder;

public interface CustomerServiceService extends IService<CsTicket> {

    IPage<CsTicket> getTicketList(String ticketType, String status, String priority, Integer pageNum, Integer pageSize);

    CsTicket getTicketDetail(Long id);

    void handleTicket(Long id, String handleResult);

    IPage<RefundOrder> getRefundList(String refundType, String status, Integer pageNum, Integer pageSize);

    RefundOrder getRefundDetail(Long id);

    void auditRefund(Long id, Integer approved, String auditRemark);

    void handleDuplicatePay(Long orderId, String remark);

    void handleProbabilityQuestion(Long ticketId, String answer);
}
