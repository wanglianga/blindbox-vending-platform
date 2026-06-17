import request from '@/utils/request'

export function getSeriesList(params) {
  return request.get('/operator/series/page', { params }).then(res => {
    const list = (res.list || []).map(item => ({
      id: item.id,
      name: item.name,
      supplierName: item.supplierName,
      skuCount: item.skuCount,
      price: item.price,
      totalSales: item.totalSales,
      status: item.status,
      coverImage: item.coverImage,
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

export function getSeriesDetail(id) {
  return request.get(`/operator/series/${id}`).then(res => ({
    id: res.id,
    name: res.name,
    supplierName: res.supplierName,
    skuCount: res.skuCount,
    price: res.price,
    totalSales: res.totalSales,
    status: res.status,
    coverImage: res.coverImage,
    createTime: res.createTime
  }))
}

export function getSkuList(seriesId) {
  return request.get(`/operator/series/${seriesId}/skus`).then(res => {
    return (res.list || res || []).map(item => ({
      id: item.id,
      seriesId: item.seriesId,
      name: item.name,
      price: item.price,
      probability: item.probability,
      image: item.image,
      stock: item.stock,
      isHidden: item.isHidden
    }))
  })
}

export function updateProbability(seriesId, skuList) {
  return request.put(`/operator/series/${seriesId}/probability`, { skuList })
}

export function addSeries(data) {
  return request.post('/operator/series', data)
}

export function updateSeries(id, data) {
  return request.put(`/operator/series/${id}`, data)
}

export function deleteSeries(id) {
  return request.delete(`/operator/series/${id}`)
}
