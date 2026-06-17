package com.blindbox.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blindbox.common.BusinessException;
import com.blindbox.common.ResultCode;
import com.blindbox.entity.IpSeries;
import com.blindbox.entity.MachineGrid;
import com.blindbox.entity.MachinePoint;
import com.blindbox.entity.Mall;
import com.blindbox.mapper.IpSeriesMapper;
import com.blindbox.mapper.MachineGridMapper;
import com.blindbox.mapper.MachinePointMapper;
import com.blindbox.mapper.MallMapper;
import com.blindbox.service.MachinePointService;
import com.blindbox.vo.MachinePointVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MachinePointServiceImpl extends ServiceImpl<MachinePointMapper, MachinePoint> implements MachinePointService {

    private final MallMapper mallMapper;
    private final IpSeriesMapper ipSeriesMapper;
    private final MachineGridMapper machineGridMapper;

    @Override
    public IPage<MachinePointVO> getMachinePage(Long mallId, String status, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<MachinePoint> wrapper = new LambdaQueryWrapper<>();
        if (mallId != null) {
            wrapper.eq(MachinePoint::getMallId, mallId);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(MachinePoint::getStatus, status);
        }
        wrapper.orderByDesc(MachinePoint::getCreatedTime);

        Page<MachinePoint> page = new Page<>(pageNum, pageSize);
        IPage<MachinePoint> machinePage = baseMapper.selectPage(page, wrapper);

        IPage<MachinePointVO> result = new Page<>();
        BeanUtils.copyProperties(machinePage, result);

        List<MachinePointVO> voList = new ArrayList<>();
        for (MachinePoint machine : machinePage.getRecords()) {
            MachinePointVO vo = convertToVO(machine);
            voList.add(vo);
        }
        result.setRecords(voList);

        return result;
    }

    @Override
    public MachinePointVO getMachineDetail(Long id) {
        MachinePoint machine = baseMapper.selectById(id);
        if (machine == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return convertToVO(machine);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addMachine(MachinePoint machinePoint) {
        baseMapper.insert(machinePoint);

        List<MachineGrid> grids = new ArrayList<>();
        for (int i = 1; i <= machinePoint.getGridCount(); i++) {
            MachineGrid grid = new MachineGrid();
            grid.setMachineId(machinePoint.getId());
            grid.setGridNo(i);
            grid.setStatus("EMPTY");
            grids.add(grid);
        }
        for (MachineGrid grid : grids) {
            machineGridMapper.insert(grid);
        }
    }

    @Override
    public void updateMachine(MachinePoint machinePoint) {
        MachinePoint exist = baseMapper.selectById(machinePoint.getId());
        if (exist == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        baseMapper.updateById(machinePoint);
    }

    @Override
    public void deleteMachine(Long id) {
        MachinePoint exist = baseMapper.selectById(id);
        if (exist == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        baseMapper.deleteById(id);
    }

    @Override
    public void toggleStatus(Long id, String status) {
        MachinePoint exist = baseMapper.selectById(id);
        if (exist == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        exist.setStatus(status);
        baseMapper.updateById(exist);
    }

    private MachinePointVO convertToVO(MachinePoint machine) {
        MachinePointVO vo = new MachinePointVO();
        BeanUtils.copyProperties(machine, vo);

        Mall mall = mallMapper.selectById(machine.getMallId());
        if (mall != null) {
            vo.setMallName(mall.getName());
        }

        if (machine.getCurrentSeriesId() != null) {
            IpSeries series = ipSeriesMapper.selectById(machine.getCurrentSeriesId());
            if (series != null) {
                vo.setSeriesName(series.getName());
            }
        }

        return vo;
    }
}
