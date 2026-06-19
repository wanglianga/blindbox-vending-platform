package com.blindbox.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RepairerConfirmDTO {

    @NotNull(message = "卡货处理记录ID不能为空")
    private Long stuckRecordId;

    @NotBlank(message = "维修确认结果不能为空")
    private String confirmResult;

    private String remark;
}
