package com.blindbox.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("refund_order")
public class RefundOrder {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String refundNo;

    private Long orderId;

    private String orderNo;

    private BigDecimal refundAmount;

    private String refundType;

    private String refundReason;

    private String applicant;

    private String status;

    private Long auditorId;

    private LocalDateTime auditTime;

    private String auditRemark;

    private LocalDateTime refundTime;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

    @TableLogic
    private Integer deleted;
}
