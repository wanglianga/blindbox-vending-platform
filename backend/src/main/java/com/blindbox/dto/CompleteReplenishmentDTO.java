package com.blindbox.dto;

import com.blindbox.entity.ReplenishmentDetail;
import lombok.Data;

import java.util.List;

@Data
public class CompleteReplenishmentDTO {

    private Long planId;
    private List<ReplenishmentDetail> details;
}
