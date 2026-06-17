package com.blindbox.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("pay_order")
public class PayOrder {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;

    private Long machineId;

    private Long seriesId;

    private Long userId;

    private BigDecimal payAmount;

    private String payType;

    private String payStatus;

    private LocalDateTime payTime;

    private Long drawSkuId;

    private Integer gridNo;

    private String shipStatus;

    private LocalDateTime shipTime;

    private String motorStatus;

    private String remark;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

    @TableLogic
    private Integer deleted;
}
