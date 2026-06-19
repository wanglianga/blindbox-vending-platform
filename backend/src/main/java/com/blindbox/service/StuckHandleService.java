package com.blindbox.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.blindbox.dto.RepairerConfirmDTO;
import com.blindbox.dto.StuckHandleDTO;
import com.blindbox.entity.StuckHandleRecord;
import com.blindbox.vo.InventoryCorrectionVO;
import com.blindbox.vo.StuckHandleDetailVO;

public interface StuckHandleService extends IService<StuckHandleRecord> {

    IPage<StuckHandleDetailVO> getStuckRecordPage(String status, String machineCode, Integer pageNum, Integer pageSize);

    StuckHandleDetailVO getStuckRecordDetail(Long id);

    StuckHandleDetailVO getStuckRecordByTicketId(Long ticketId);

    void handleStuck(StuckHandleDTO dto);

    void repairerConfirm(RepairerConfirmDTO dto);

    IPage<InventoryCorrectionVO> getInventoryCorrectionPage(String status, Long machineId, Integer pageNum, Integer pageSize);

    InventoryCorrectionVO getInventoryCorrectionDetail(Long id);
}
