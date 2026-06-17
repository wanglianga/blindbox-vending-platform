package com.blindbox.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.blindbox.entity.MachinePoint;
import com.blindbox.vo.MachinePointVO;

public interface MachinePointService extends IService<MachinePoint> {

    IPage<MachinePointVO> getMachinePage(Long mallId, String status, Integer pageNum, Integer pageSize);

    MachinePointVO getMachineDetail(Long id);

    void addMachine(MachinePoint machinePoint);

    void updateMachine(MachinePoint machinePoint);

    void deleteMachine(Long id);

    void toggleStatus(Long id, String status);
}
