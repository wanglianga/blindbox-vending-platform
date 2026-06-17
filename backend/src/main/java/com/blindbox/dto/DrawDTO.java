package com.blindbox.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DrawDTO {

    @NotBlank(message = "机器编号不能为空")
    private String machineCode;

    private String payType;
}
