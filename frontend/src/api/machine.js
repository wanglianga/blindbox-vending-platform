import request from '@/utils/request'

export function getMachineList(params) {
  return request.get('/operator/machine/page', { params }).then(res => {
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

export function getMachineDetail(id) {
  return request.get(`/operator/machine/${id}`).then(res => ({
    id: res.id,
    machineNo: res.machineNo,
    name: res.name,
    location: res.location,
    mallName: res.mallName,
    status: res.status,
    gridCount: res.gridCount,
    skuCount: res.skuCount,
    lastOnline: res.lastOnline,
    createTime: res.createTime
  }))
}

export function addMachine(data) {
  return request.post('/operator/machine', data)
}

export function updateMachine(id, data) {
  return request.put(`/operator/machine/${id}`, data)
}

export function deleteMachine(id) {
  return request.delete(`/operator/machine/${id}`)
}
