package com.blindbox.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blindbox.common.BusinessException;
import com.blindbox.common.ResultCode;
import com.blindbox.common.UserContext;
import com.blindbox.entity.*;
import com.blindbox.mapper.*;
import com.blindbox.service.CustomerServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CustomerServiceServiceImpl extends ServiceImpl<CsTicketMapper, CsTicket> implements CustomerServiceService {

    private final RefundOrderMapper refundOrderMapper;
    private final PayOrderMapper payOrderMapper;
    private final MachineGridMapper machineGridMapper;
    private final MachinePointMapper machinePointMapper;
    private final BlindboxSkuMapper blindboxSkuMapper;
    private final ProbabilityAuditLogMapper probabilityAuditLogMapper;

    @Override
    public IPage<CsTicket> getTicketList(String ticketType, String status, String priority, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<CsTicket> wrapper = new LambdaQueryWrapper<>();
        if (ticketType != null && !ticketType.isEmpty()) {
            wrapper.eq(CsTicket::getTicketType, ticketType);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(CsTicket::getStatus, status);
        }
        if (priority != null && !priority.isEmpty()) {
            wrapper.eq(CsTicket::getPriority, priority);
        }
        wrapper.orderByDesc(CsTicket::getCreatedTime);

        Page<CsTicket> page = new Page<>(pageNum, pageSize);
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public CsTicket getTicketDetail(Long id) {
        CsTicket ticket = baseMapper.selectById(id);
        if (ticket == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return ticket;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleTicket(Long id, String handleResult) {
        CsTicket ticket = baseMapper.selectById(id);
        if (ticket == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        if ("CLOSED".equals(ticket.getStatus())) {
            throw new BusinessException("工单已关闭，无需处理");
        }

        ticket.setStatus("CLOSED");
        ticket.setHandlerId(UserContext.getUserId());
        ticket.setHandleResult(handleResult);
        ticket.setHandleTime(LocalDateTime.now());
        baseMapper.updateById(ticket);
    }

    @Override
    public IPage<RefundOrder> getRefundList(String refundType, String status, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<RefundOrder> wrapper = new LambdaQueryWrapper<>();
        if (refundType != null && !refundType.isEmpty()) {
            wrapper.eq(RefundOrder::getRefundType, refundType);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(RefundOrder::getStatus, status);
        }
        wrapper.orderByDesc(RefundOrder::getCreatedTime);

        Page<RefundOrder> page = new Page<>(pageNum, pageSize);
        return refundOrderMapper.selectPage(page, wrapper);
    }

    @Override
    public RefundOrder getRefundDetail(Long id) {
        RefundOrder refund = refundOrderMapper.selectById(id);
        if (refund == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return refund;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditRefund(Long id, Integer approved, String auditRemark) {
        RefundOrder refund = refundOrderMapper.selectById(id);
        if (refund == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        if (!"PENDING".equals(refund.getStatus())) {
            throw new BusinessException("退款申请已处理，无法重复审核");
        }

        refund.setAuditorId(UserContext.getUserId());
        refund.setAuditTime(LocalDateTime.now());
        refund.setAuditRemark(auditRemark);

        if (approved == 1) {
            refund.setStatus("APPROVED");
        } else {
            refund.setStatus("REJECTED");
        }

        refundOrderMapper.updateById(refund);

        if (approved == 1) {
            PayOrder order = payOrderMapper.selectById(refund.getOrderId());
            if (order != null) {
                order.setPayStatus("REFUNDED");
                payOrderMapper.updateById(order);
            }
            refund.setStatus("COMPLETED");
            refund.setRefundTime(LocalDateTime.now());
            refundOrderMapper.updateById(refund);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleDuplicatePay(Long orderId, String remark) {
        PayOrder order = payOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_EXIST);
        }

        RefundOrder refund = new RefundOrder();
        refund.setRefundNo("RF" + IdUtil.getSnowflakeNextIdStr());
        refund.setOrderId(orderId);
        refund.setOrderNo(order.getOrderNo());
        refund.setRefundAmount(order.getPayAmount());
        refund.setRefundType("DUPLICATE_PAY");
        refund.setRefundReason(remark);
        refund.setApplicant("客服处理");
        refund.setStatus("APPROVED");
        refund.setAuditorId(UserContext.getUserId());
        refund.setAuditTime(LocalDateTime.now());
        refund.setAuditRemark("重复扣款，已审核通过");
        refund.setRefundTime(LocalDateTime.now());
        refundOrderMapper.insert(refund);

        order.setPayStatus("REFUNDED");
        payOrderMapper.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleProbabilityQuestion(Long ticketId, String answer) {
        CsTicket ticket = baseMapper.selectById(ticketId);
        if (ticket == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        if (ticket.getOrderId() != null) {
            PayOrder order = payOrderMapper.selectById(ticket.getOrderId());
            if (order != null && order.getMachineId() != null) {
                MachinePoint machine = machinePointMapper.selectById(order.getMachineId());
                if (machine != null && machine.getCurrentSeriesId() != null) {
                    ProbabilityAuditLog auditLog = new ProbabilityAuditLog();
                    auditLog.setMachineId(machine.getId());
                    auditLog.setSeriesId(machine.getCurrentSeriesId());
                    auditLog.setAuditType("MANUAL");
                    auditLog.setBeforeAudit("客服手动审计，工单ID: " + ticketId);
                    auditLog.setAfterAudit(answer);
                    auditLog.setIsConsistent(1);
                    auditLog.setOperatorId(UserContext.getUserId());
                    auditLog.setCreatedTime(LocalDateTime.now());
                    probabilityAuditLogMapper.insert(auditLog);
                }
            }
        }

        ticket.setStatus("CLOSED");
        ticket.setHandlerId(UserContext.getUserId());
        ticket.setHandleResult(answer);
        ticket.setHandleTime(LocalDateTime.now());
        baseMapper.updateById(ticket);
    }
}
