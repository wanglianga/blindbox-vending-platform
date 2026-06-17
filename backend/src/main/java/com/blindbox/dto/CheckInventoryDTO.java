package com.blindbox.dto;

import com.blindbox.entity.MachineGrid;
import lombok.Data;

import java.util.List;

@Data
public class CheckInventoryDTO {

    private Long machineId;
    private List<MachineGrid> grids;
}
