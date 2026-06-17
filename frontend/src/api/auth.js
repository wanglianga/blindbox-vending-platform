import request from '@/utils/request'

export function login(username, password) {
  return request.post('/auth/login', { username, password }).then(res => {
    const userInfo = res.userInfo || {}
    return {
      token: res.token,
      userInfo: {
        id: userInfo.id,
        username: userInfo.username,
        name: userInfo.nickname || userInfo.name,
        role: userInfo.roleType || userInfo.role,
        phone: userInfo.phone,
        avatar: userInfo.avatar
      }
    }
  })
}

export function logout() {
  return request.post('/auth/logout')
}

export function getUserInfo() {
  return request.get('/auth/userinfo').then(res => {
    return {
      id: res.id,
      username: res.username,
      name: res.nickname || res.name,
      role: res.roleType || res.role,
      phone: res.phone,
      avatar: res.avatar
    }
  })
}
