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

export function getMigrationPage(params) {
  return request.get('/operator/migration/page', { params }).then(res => {
    const list = (res.list || []).map(item => ({
      id: item.id,
      migrationNo: item.migrationNo,
      machineId: item.machineId,
      machineCode: item.machineCode,
      machineName: item.machineName,
      oldMallName: item.oldMallName,
      newMallName: item.newMallName,
      migrationReason: item.migrationReason,
      migrationStartTime: item.migrationStartTime,
      migrationEndTime: item.migrationEndTime,
      downtimeMinutes: item.downtimeMinutes,
      downtimeLoss: item.downtimeLoss,
      totalInventoryBefore: item.totalInventoryBefore,
      totalInventoryAfter: item.totalInventoryAfter,
      transferredInventory: item.transferredInventory,
      lostInventory: item.lostInventory,
      inventoryTransferStatus: item.inventoryTransferStatus,
      status: item.status,
      operatorName: item.operatorName,
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

export function getMigrationDetail(id) {
  return request.get(`/operator/migration/${id}`).then(res => ({
    id: res.id,
    migrationNo: res.migrationNo,
    machineId: res.machineId,
    machineCode: res.machineCode,
    machineName: res.machineName,
    oldMallId: res.oldMallId,
    oldMallName: res.oldMallName,
    oldLocationDetail: res.oldLocationDetail,
    oldCommissionRate: res.oldCommissionRate,
    newMallId: res.newMallId,
    newMallName: res.newMallName,
    newLocationDetail: res.newLocationDetail,
    newCommissionRate: res.newCommissionRate,
    migrationReason: res.migrationReason,
    migrationStartTime: res.migrationStartTime,
    migrationEndTime: res.migrationEndTime,
    downtimeMinutes: res.downtimeMinutes,
    downtimeLoss: res.downtimeLoss,
    lossRemark: res.lossRemark,
    totalInventoryBefore: res.totalInventoryBefore,
    totalInventoryAfter: res.totalInventoryAfter,
    transferredInventory: res.transferredInventory,
    lostInventory: res.lostInventory,
    inventoryTransferStatus: res.inventoryTransferStatus,
    status: res.status,
    operatorId: res.operatorId,
    operatorName: res.operatorName,
    createdTime: res.createdTime,
    inventoryDetails: (res.inventoryDetails || []).map(item => ({
      id: item.id,
      skuId: item.skuId,
      skuName: item.skuName,
      skuCode: item.skuCode,
      skuImage: item.skuImage,
      seriesId: item.seriesId,
      seriesName: item.seriesName,
      qtyBefore: item.qtyBefore,
      qtyAfter: item.qtyAfter,
      qtyTransferred: item.qtyTransferred,
      qtyLost: item.qtyLost,
      transferStatus: item.transferStatus,
      remark: item.remark
    }))
  }))
}

export function createMigration(data) {
  return request.post('/operator/migration', data)
}

export function completeMigration(id) {
  return request.post(`/operator/migration/${id}/complete`)
}

export function updateMigrationInventory(id, data) {
  return request.put(`/operator/migration/${id}/inventory`, data)
}

export function getMigrationInventory(id) {
  return request.get(`/operator/migration/${id}/inventory`).then(res => 
    (res || []).map(item => ({
      id: item.id,
      skuId: item.skuId,
      skuName: item.skuName,
      skuCode: item.skuCode,
      skuImage: item.skuImage,
      seriesId: item.seriesId,
      seriesName: item.seriesName,
      qtyBefore: item.qtyBefore,
      qtyAfter: item.qtyAfter,
      qtyTransferred: item.qtyTransferred,
      qtyLost: item.qtyLost,
      transferStatus: item.transferStatus,
      remark: item.remark
    }))
  )
}

export function getSegmentRevenue(params) {
  return request.get('/operator/segment-revenue/page', { params }).then(res => {
    const list = (res.list || []).map(item => ({
      id: item.id,
      orderNo: item.orderNo,
      machineId: item.machineId,
      machineCode: item.machineCode,
      mallId: item.mallId,
      mallName: item.mallName,
      commissionRate: item.commissionRate,
      totalAmount: item.totalAmount,
      mallCommission: item.mallCommission,
      supplierShare: item.supplierShare,
      operatorShare: item.operatorShare,
      platformShare: item.platformShare,
      settleStatus: item.settleStatus,
      orderTime: item.orderTime,
      periodType: item.periodType,
      periodStart: item.periodStart,
      periodEnd: item.periodEnd,
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

export function getMachineMigrationHistory(machineId) {
  return request.get(`/operator/migration/machine/${machineId}/history`)
}

export function getSupplierMigrationPage(params) {
  return request.get('/supplier/migration/page', { params }).then(res => {
    const list = (res.list || []).map(item => ({
      id: item.id,
      migrationNo: item.migrationNo,
      machineId: item.machineId,
      machineCode: item.machineCode,
      machineName: item.machineName,
      oldMallName: item.oldMallName,
      newMallName: item.newMallName,
      migrationReason: item.migrationReason,
      migrationStartTime: item.migrationStartTime,
      migrationEndTime: item.migrationEndTime,
      totalInventoryBefore: item.totalInventoryBefore,
      totalInventoryAfter: item.totalInventoryAfter,
      transferredInventory: item.transferredInventory,
      lostInventory: item.lostInventory,
      inventoryTransferStatus: item.inventoryTransferStatus,
      status: item.status,
      operatorName: item.operatorName,
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

export function getSupplierMigrationDetail(id) {
  return request.get(`/supplier/migration/${id}`)
}

export function getSupplierMigrationInventory(id) {
  return request.get(`/supplier/migration/${id}/inventory`)
}
