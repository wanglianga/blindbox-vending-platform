import request from '@/utils/request'

export function getTicketList(params) {
  return request.get('/cs/ticket/page', { params }).then(res => {
    const list = (res.list || []).map(item => ({
      id: item.id,
      ticketNo: item.ticketNo,
      type: item.type,
      title: item.title,
      userName: item.userName,
      userPhone: item.userPhone,
      machineName: item.machineName,
      status: item.status,
      priority: item.priority,
      createTime: item.createTime
    }))
    return {
      list,
      total: res.total,
      page: res.page,
      pageSize: res.pageSize
    }
  })
}

export function getTicketDetail(id) {
  return request.get(`/cs/ticket/${id}`).then(res => ({
    id: res.id,
    ticketNo: res.ticketNo,
    type: res.type,
    title: res.title,
    userName: res.userName,
    userPhone: res.userPhone,
    machineName: res.machineName,
    status: res.status,
    priority: res.priority,
    createTime: res.createTime
  }))
}

export function handleTicket(id, data) {
  return request.post(`/cs/ticket/${id}/handle`, data)
}

export function getRefundList(params) {
  return request.get('/cs/refund/page', { params }).then(res => {
    const list = (res.list || []).map(item => ({
      id: item.id,
      refundNo: item.refundNo,
      orderNo: item.orderNo,
      amount: item.amount,
      reason: item.reason,
      userName: item.userName,
      userPhone: item.userPhone,
      status: item.status,
      applyTime: item.applyTime,
      approveTime: item.approveTime,
      rejectReason: item.rejectReason
    }))
    return {
      list,
      total: res.total,
      page: res.page,
      pageSize: res.pageSize
    }
  })
}

export function approveRefund(id, remark) {
  return request.post(`/cs/refund/${id}/approve`, { remark })
}

export function rejectRefund(id, reason) {
  return request.post(`/cs/refund/${id}/reject`, { reason })
}

export function queryProbability(seriesId) {
  return request.get(`/cs/probability/${seriesId}`).then(res => ({
    series: res.series,
    skus: res.skus || []
  }))
}

export function getProbabilityAuditLog(params) {
  return request.get('/cs/probability/audit-log', { params }).then(res => {
    const list = (res.list || []).map(item => ({
      id: item.id,
      seriesName: item.seriesName,
      operator: item.operator,
      action: item.action,
      before: item.before,
      after: item.after,
      time: item.time
    }))
    return {
      list,
      total: res.total
    }
  })
}

export function getStuckRecordPage(params) {
  return request.get('/cs/stuck/page', { params }).then(res => {
    const list = (res.list || []).map(item => ({
      id: item.id,
      recordNo: item.recordNo,
      ticketNo: item.ticketNo,
      orderNo: item.orderNo,
      machineCode: item.machineCode,
      machineName: item.machineName,
      gridNo: item.gridNo,
      skuName: item.skuName,
      handleDecision: item.handleDecision,
      status: item.status,
      csHandlerName: item.csHandlerName,
      handleTime: item.handleTime,
      createdTime: item.createdTime
    }))
    return {
      list,
      total: res.total,
      page: res.page,
      pageSize: res.pageSize
    }
  })
}

export function getStuckRecordDetail(id) {
  return request.get(`/cs/stuck/${id}`).then(res => ({
    id: res.id,
    recordNo: res.recordNo,
    ticketId: res.ticketId,
    ticketNo: res.ticketNo,
    orderId: res.orderId,
    orderNo: res.orderNo,
    machineId: res.machineId,
    machineCode: res.machineCode,
    machineName: res.machineName,
    gridNo: res.gridNo,
    skuId: res.skuId,
    skuName: res.skuName,
    skuImage: res.skuImage,
    payAmount: res.payAmount,
    payStatus: res.payStatus,
    shipStatus: res.shipStatus,
    motorStatus: res.motorStatus,
    payFlowChecked: res.payFlowChecked,
    motorStatusChecked: res.motorStatusChecked,
    sensorStatusChecked: res.sensorStatusChecked,
    inventoryChangeChecked: res.inventoryChangeChecked,
    monitorPhotoChecked: res.monitorPhotoChecked,
    checkRemark: res.checkRemark,
    handleDecision: res.handleDecision,
    refundAmount: res.refundAmount,
    reissueSkuId: res.reissueSkuId,
    reissueSkuName: res.reissueSkuName,
    reissueGridNo: res.reissueGridNo,
    repairContent: res.repairContent,
    status: res.status,
    csHandlerId: res.csHandlerId,
    csHandlerName: res.csHandlerName,
    handleTime: res.handleTime,
    repairerId: res.repairerId,
    repairerName: res.repairerName,
    repairerConfirmResult: res.repairerConfirmResult,
    repairerConfirmTime: res.repairerConfirmTime,
    inventoryCorrectionId: res.inventoryCorrectionId,
    createdTime: res.createdTime
  }))
}

export function getStuckRecordByTicketId(ticketId) {
  return request.get(`/cs/stuck/ticket/${ticketId}`)
}

export function handleStuck(data) {
  return request.post('/cs/stuck/handle', data)
}

export function repairerConfirm(data) {
  return request.post('/cs/stuck/repairer/confirm', data)
}

export function getInventoryCorrectionPage(params) {
  return request.get('/cs/stuck/correction/page', { params }).then(res => {
    const list = (res.list || []).map(item => ({
      id: item.id,
      correctionNo: item.correctionNo,
      stuckRecordNo: item.stuckRecordNo,
      machineCode: item.machineCode,
      machineName: item.machineName,
      gridNo: item.gridNo,
      skuName: item.skuName,
      beforeInventory: item.beforeInventory,
      afterInventory: item.afterInventory,
      correctionQty: item.correctionQty,
      correctionRevenue: item.correctionRevenue,
      correctionType: item.correctionType,
      reason: item.reason,
      operatorName: item.operatorName,
      operateTime: item.operateTime,
      status: item.status,
      createdTime: item.createdTime
    }))
    return {
      list,
      total: res.total,
      page: res.page,
      pageSize: res.pageSize
    }
  })
}

export function getInventoryCorrectionDetail(id) {
  return request.get(`/cs/stuck/correction/${id}`)
}
