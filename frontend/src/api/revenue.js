import request from '@/utils/request'

export function getRevenueStats(params) {
  return request.get('/operator/revenue/stats', { params }).then(res => ({
    todayRevenue: res.todayRevenue,
    todayOrders: res.todayOrders,
    weekRevenue: res.weekRevenue,
    monthRevenue: res.monthRevenue,
    trend: res.trend || []
  }))
}

export function getRevenueShareList(params) {
  return request.get('/operator/revenue/page', { params }).then(res => {
    const list = (res.list || []).map(item => ({
      id: item.id,
      period: item.period,
      totalAmount: item.totalAmount,
      operatorShare: item.operatorShare,
      operatorRate: item.operatorRate,
      mallShare: item.mallShare,
      mallRate: item.mallRate,
      supplierShare: item.supplierShare,
      supplierRate: item.supplierRate,
      status: item.status
    }))
    return {
      list,
      total: res.total,
      page: res.page,
      pageSize: res.pageSize
    }
  })
}

export function getSupplierRevenue(params) {
  return request.get('/supplier/revenue/stats', { params }).then(res => ({
    totalRevenue: res.totalRevenue,
    totalOrders: res.totalOrders,
    settlementAmount: res.settlementAmount,
    pendingAmount: res.pendingAmount,
    trend: res.trend || []
  }))
}

export function getMallRevenue(params) {
  return request.get('/mall/revenue/stats', { params }).then(res => ({
    totalRevenue: res.totalRevenue,
    totalOrders: res.totalOrders,
    machineCount: res.machineCount,
    trend: res.trend || []
  }))
}
