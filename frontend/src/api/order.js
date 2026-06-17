import request from '@/utils/request'

export function getOrderList(params) {
  return request.get('/operator/order/page', { params }).then(res => {
    const list = (res.list || []).map(item => ({
      id: item.id,
      orderNo: item.orderNo,
      machineName: item.machineName,
      seriesName: item.seriesName,
      skuName: item.skuName,
      amount: item.amount,
      payMethod: item.payMethod,
      status: item.status,
      payTime: item.payTime,
      userName: item.userName,
      userPhone: item.userPhone
    }))
    return {
      list,
      total: res.total,
      page: res.page,
      pageSize: res.pageSize
    }
  })
}

export function getOrderDetail(id) {
  return request.get(`/operator/order/${id}`).then(res => ({
    id: res.id,
    orderNo: res.orderNo,
    machineName: res.machineName,
    seriesName: res.seriesName,
    skuName: res.skuName,
    amount: res.amount,
    payMethod: res.payMethod,
    status: res.status,
    payTime: res.payTime,
    userName: res.userName,
    userPhone: res.userPhone
  }))
}

export function exportOrders(params) {
  return request.get('/operator/order/export', { params, responseType: 'blob' })
}
