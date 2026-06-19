package com.blindbox.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.blindbox.dto.MachineMigrationDTO;
import com.blindbox.dto.MigrationInventoryItemDTO;
import com.blindbox.entity.MachineMigration;
import com.blindbox.vo.MachineMigrationDetailVO;
import com.blindbox.vo.MigrationInventoryDetailVO;
import com.blindbox.vo.SegmentRevenueVO;

import java.util.List;

public interface MachineMigrationService extends IService<MachineMigration> {

    IPage<MachineMigrationDetailVO> getMigrationPage(String status, Long machineId, Integer pageNum, Integer pageSize);

    MachineMigrationDetailVO getMigrationDetail(Long id);

    void createMigration(MachineMigrationDTO dto);

    void completeMigration(Long migrationId);

    void updateMigrationInventory(Long migrationId, List<MigrationInventoryItemDTO> items);

    List<MigrationInventoryDetailVO> getMigrationInventory(Long migrationId);

    IPage<SegmentRevenueVO> getSegmentRevenuePage(Long machineId, Long migrationId, Integer pageNum, Integer pageSize);

    List<MachineMigration> getMachineMigrationHistory(Long machineId);
}
