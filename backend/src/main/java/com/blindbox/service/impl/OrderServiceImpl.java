package com.blindbox.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blindbox.common.BusinessException;
import com.blindbox.common.ResultCode;
import com.blindbox.entity.BlindboxSku;
import com.blindbox.entity.IpSeries;
import com.blindbox.entity.MachinePoint;
import com.blindbox.entity.PayOrder;
import com.blindbox.mapper.BlindboxSkuMapper;
import com.blindbox.mapper.IpSeriesMapper;
import com.blindbox.mapper.MachinePointMapper;
import com.blindbox.mapper.PayOrderMapper;
import com.blindbox.service.OrderService;
import com.blindbox.vo.OrderDetailVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<PayOrderMapper, PayOrder> implements OrderService {

    private final MachinePointMapper machinePointMapper;
    private final IpSeriesMapper ipSeriesMapper;
    private final BlindboxSkuMapper blindboxSkuMapper;

    @Override
    public IPage<OrderDetailVO> getOrderPage(Long machineId, String payStatus, String startDate, String endDate, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<PayOrder> wrapper = new LambdaQueryWrapper<>();
        if (machineId != null) {
            wrapper.eq(PayOrder::getMachineId, machineId);
        }
        if (payStatus != null && !payStatus.isEmpty()) {
            wrapper.eq(PayOrder::getPayStatus, payStatus);
        }
        if (startDate != null && !startDate.isEmpty()) {
            LocalDateTime start = LocalDate.parse(startDate).atStartOfDay();
            wrapper.ge(PayOrder::getCreatedTime, start);
        }
        if (endDate != null && !endDate.isEmpty()) {
            LocalDateTime end = LocalDate.parse(endDate).atTime(LocalTime.MAX);
            wrapper.le(PayOrder::getCreatedTime, end);
        }
        wrapper.orderByDesc(PayOrder::getCreatedTime);

        Page<PayOrder> page = new Page<>(pageNum, pageSize);
        IPage<PayOrder> orderPage = baseMapper.selectPage(page, wrapper);

        IPage<OrderDetailVO> result = new Page<>();
        BeanUtils.copyProperties(orderPage, result);

        List<OrderDetailVO> voList = new ArrayList<>();
        for (PayOrder order : orderPage.getRecords()) {
            OrderDetailVO vo = convertToVO(order);
            voList.add(vo);
        }
        result.setRecords(voList);

        return result;
    }

    @Override
    public OrderDetailVO getOrderDetail(Long id) {
        PayOrder order = baseMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_EXIST);
        }
        return convertToVO(order);
    }

    private OrderDetailVO convertToVO(PayOrder order) {
        OrderDetailVO vo = new OrderDetailVO();
        BeanUtils.copyProperties(order, vo);

        MachinePoint machine = machinePointMapper.selectById(order.getMachineId());
        if (machine != null) {
            vo.setMachineName(machine.getMachineName());
        }

        IpSeries series = ipSeriesMapper.selectById(order.getSeriesId());
        if (series != null) {
            vo.setSeriesName(series.getName());
        }

        if (order.getDrawSkuId() != null) {
            BlindboxSku sku = blindboxSkuMapper.selectById(order.getDrawSkuId());
            if (sku != null) {
                vo.setSkuName(sku.getSkuName());
                vo.setSkuImage(sku.getImageUrl());
                vo.setIsHidden(sku.getIsHidden());
            }
        }

        return vo;
    }
}
