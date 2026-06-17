import request from '@/utils/request'

export function getMallSalesStats(mallId) {
  return request.get(`/mall/sales-stats/${mallId || ''}`).then(res => ({
    totalSales: res.totalSales,
    totalOrders: res.totalOrders,
    avgOrderValue: res.avgOrderValue,
    conversionRate: res.conversionRate,
    salesTrend: res.salesTrend || [],
    topMachines: res.topMachines || []
  }))
}

export function getMallMachineList(mallId, params) {
  return request.get('/mall/machine/page', { params: { mallId, ...params } }).then(res => {
    const list = (res.list || []).map(item => ({
      id: item.id,
      machineNo: item.machineNo,
      name: item.name,
      location: item.location,
      mallName: item.mallName,
      status: item.status,
      gridCount: item.gridCount,
      skuCount: item.skuCount,
      lastOnline: item.lastOnline,
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
