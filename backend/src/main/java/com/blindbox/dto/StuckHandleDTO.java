package com.blindbox.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class StuckHandleDTO {

    @NotNull(message = "工单ID不能为空")
    private Long ticketId;

    @NotBlank(message = "支付流水核查结果不能为空")
    private String payFlowChecked;

    @NotBlank(message = "电机状态核查结果不能为空")
    private String motorStatusChecked;

    @NotBlank(message = "传感器状态核查结果不能为空")
    private String sensorStatusChecked;

    @NotBlank(message = "库存变化核查结果不能为空")
    private String inventoryChangeChecked;

    @NotBlank(message = "监控照片核查结果不能为空")
    private String monitorPhotoChecked;

    private String checkRemark;

    @NotBlank(message = "处理决定不能为空")
    private String handleDecision;

    private BigDecimal refundAmount;

    private Long reissueSkuId;

    private String reissueGridNo;

    private String repairContent;
}
