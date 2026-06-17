import request from '@/utils/request'

export function getReplenishTaskList(params) {
  return request.get('/replenisher/task/page', { params }).then(res => {
    const list = (res.list || []).map(item => ({
      id: item.id,
      taskNo: item.taskNo,
      machineName: item.machineName,
      machineAddress: item.machineAddress,
      status: item.status,
      priority: item.priority,
      planTime: item.planTime,
      gridTotal: item.gridTotal,
      emptyGrid: item.emptyGrid,
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

export function getTaskDetail(id) {
  return request.get(`/replenisher/task/${id}`).then(res => ({
    id: res.id,
    taskNo: res.taskNo,
    machineName: res.machineName,
    machineAddress: res.machineAddress,
    status: res.status,
    priority: res.priority,
    planTime: res.planTime,
    gridTotal: res.gridTotal,
    emptyGrid: res.emptyGrid,
    createTime: res.createTime
  }))
}

export function getGridList(machineId) {
  return request.get(`/replenisher/machine/${machineId}/grids`).then(res => {
    return (res.list || res || []).map(item => ({
      id: item.id,
      gridNo: item.gridNo,
      status: item.status,
      skuName: item.skuName,
      skuImage: item.skuImage
    }))
  })
}

export function openGrid(machineId, gridId) {
  return request.post(`/replenisher/machine/${machineId}/grid/${gridId}/open`)
}

export function completeReplenish(taskId, data) {
  return request.post(`/replenisher/task/${taskId}/complete`, data)
}

export function getRouteList(params) {
  return request.get('/operator/route/page', { params }).then(res => {
    const list = (res.list || []).map(item => ({
      id: item.id,
      routeName: item.routeName,
      routeCode: item.routeCode,
      machineCount: item.machineCount,
      replenisherName: item.replenisherName,
      status: item.status,
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

export function addRoute(data) {
  return request.post('/operator/route', data)
}

export function updateRoute(id, data) {
  return request.put(`/operator/route/${id}`, data)
}

export function deleteRoute(id) {
  return request.delete(`/operator/route/${id}`)
}
