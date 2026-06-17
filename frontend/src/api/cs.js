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
