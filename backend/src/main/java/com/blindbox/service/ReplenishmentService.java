package com.blindbox.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.blindbox.entity.MachineGrid;
import com.blindbox.entity.ReplenishmentDetail;
import com.blindbox.entity.ReplenishmentPlan;
import com.blindbox.vo.ReplenishmentPlanVO;

import java.util.List;

public interface ReplenishmentService extends IService<ReplenishmentPlan> {

    IPage<ReplenishmentPlanVO> getMyTaskList(String status, Integer pageNum, Integer pageSize);

    ReplenishmentPlanVO getTaskDetail(Long planId);

    void startReplenishment(Long planId);

    void completeReplenishment(Long planId, List<ReplenishmentDetail> details);

    List<MachineGrid> getMachineGridInventory(Long machineId);

    void checkGridInventory(Long machineId, List<MachineGrid> grids);

    void handleJam(Long orderId, String handleResult);

    IPage<ReplenishmentPlan> getReplenishmentRecord(Integer pageNum, Integer pageSize);

    List<ReplenishmentDetail> getReplenishmentDetails(Long planId);
}
