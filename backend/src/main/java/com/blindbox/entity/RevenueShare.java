package com.blindbox.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("revenue_share")
public class RevenueShare {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long orderId;

    private String orderNo;

    private BigDecimal totalAmount;

    private BigDecimal mallCommission;

    private BigDecimal supplierShare;

    private BigDecimal operatorShare;

    private BigDecimal platformShare;

    private Long mallId;

    private Long supplierId;

    private Long operatorId;

    private String settleStatus;

    private LocalDateTime settleTime;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

    @TableLogic
    private Integer deleted;
}
