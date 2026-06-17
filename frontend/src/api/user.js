import request from '@/utils/request'

export function getUserList(params) {
  return request.get('/operator/user/page', { params }).then(res => {
    const list = (res.list || []).map(item => ({
      id: item.id,
      username: item.username,
      name: item.nickname || item.name,
      role: item.roleType || item.role,
      phone: item.phone,
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

export function addUser(data) {
  return request.post('/operator/user', data)
}

export function updateUser(id, data) {
  return request.put(`/operator/user/${id}`, data)
}

export function deleteUser(id) {
  return request.delete(`/operator/user/${id}`)
}

export function updateUserStatus(id, status) {
  return request.put(`/operator/user/${id}/status`, { status })
}
