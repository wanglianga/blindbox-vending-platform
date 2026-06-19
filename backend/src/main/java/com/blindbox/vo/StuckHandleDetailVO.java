package com.blindbox.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class StuckHandleDetailVO {

    private Long id;
    private String recordNo;
    private Long ticketId;
    private String ticketNo;
    private Long orderId;
    private String orderNo;
    private Long machineId;
    private String machineCode;
    private String machineName;
    private Integer gridNo;
    private Long skuId;
    private String skuName;
    private String skuImage;
    private BigDecimal payAmount;
    private String payStatus;
    private String shipStatus;
    private String motorStatus;
    private String payFlowChecked;
    private String motorStatusChecked;
    private String sensorStatusChecked;
    private String inventoryChangeChecked;
    private String monitorPhotoChecked;
    private String checkRemark;
    private String handleDecision;
    private BigDecimal refundAmount;
    private Long reissueSkuId;
    private String reissueSkuName;
    private String reissueGridNo;
    private String repairContent;
    private String status;
    private Long csHandlerId;
    private String csHandlerName;
    private LocalDateTime handleTime;
    private Long repairerId;
    private String repairerName;
    private String repairerConfirmResult;
    private LocalDateTime repairerConfirmTime;
    private Long inventoryCorrectionId;
    private LocalDateTime createdTime;
}
