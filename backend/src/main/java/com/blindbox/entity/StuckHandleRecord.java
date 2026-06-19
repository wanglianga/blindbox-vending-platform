package com.blindbox.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("stuck_handle_record")
public class StuckHandleRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String recordNo;

    private Long ticketId;

    private String ticketNo;

    private Long orderId;

    private String orderNo;

    private Long machineId;

    private String machineCode;

    private Integer gridNo;

    private Long skuId;

    private String skuName;

    private String payFlowChecked;

    private String motorStatusChecked;

    private String sensorStatusChecked;

    private String inventoryChangeChecked;

    private String monitorPhotoChecked;

    private String checkRemark;

    private String handleDecision;

    private BigDecimal refundAmount;

    private Long reissueSkuId;

    private String reissueGridNo;

    private String repairContent;

    private String status;

    private Long csHandlerId;

    private LocalDateTime handleTime;

    private Long repairerId;

    private String repairerConfirmResult;

    private LocalDateTime repairerConfirmTime;

    private Long inventoryCorrectionId;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

    @TableLogic
    private Integer deleted;
}
