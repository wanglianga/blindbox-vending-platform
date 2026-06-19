<template>
  <div class="migration-inventory">
    <el-card class="search-card">
      <el-form :inline="true" :model="queryForm" class="search-form">
      <el-form-item label="迁移单号">
        <el-input v-model="queryForm.migrationNo" placeholder="请输入迁移单号" clearable />
      </el-form-item>
      <el-form-item label="机器编号">
        <el-input v-model="queryForm.machineCode" placeholder="请输入机器编号" clearable />
      </el-form-item>
      <el-form-item label="迁移状态">
        <el-select v-model="queryForm.status" placeholder="请选择" clearable>
          <el-option label="待迁移" value="PENDING" />
          <el-option label="迁移中" value="IN_PROGRESS" />
          <el-option label="已完成" value="COMPLETED" />
          <el-option label="已取消" value="CANCELLED" />
        </el-select>
      </el-form-item>
      <el-form-item label="库存转移状态">
        <el-select v-model="queryForm.inventoryTransferStatus" placeholder="请选择" clearable>
          <el-option label="待确认" value="PENDING" />
          <el-option label="完整转移" value="COMPLETE" />
          <el-option label="部分转移" value="PARTIAL" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button @click="resetForm">重置</el-button>
      </el-form-item>
    </el-form>
    </el-card>

    <el-card class="table-card">
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="migrationNo" label="迁移单号" width="160" />
        <el-table-column prop="machineCode" label="机器编号" width="120" />
        <el-table-column prop="machineName" label="机器名称" width="150" />
        <el-table-column prop="oldMallName" label="原点位" width="150" />
        <el-table-column prop="newMallName" label="新点位" width="150" />
        <el-table-column prop="migrationReason" label="迁移原因" width="120" />
        <el-table-column prop="totalInventoryBefore" label="迁移前库存" width="120" />
        <el-table-column prop="transferredInventory" label="已转移库存" width="120" />
        <el-table-column prop="lostInventory" label="丢失库存" width="120" />
        <el-table-column label="库存转移状态" width="130">
          <template #default="{ row }">
            <el-tag :type="getTransferStatusType(row.inventoryTransferStatus)">
              {{ getTransferStatusText(row.inventoryTransferStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="迁移状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="migrationStartTime" label="迁移开始时间" width="180" />
        <el-table-column prop="operatorName" label="操作人" width="100" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="viewDetail(row)">查看详情</el-button>
            <el-button type="success" link @click="viewInventory(row)">查看库存</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="queryForm.page"
        v-model:page-size="queryForm.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadData"
        @current-change="loadData"
        class="pagination"
      />
    </el-card>

    <el-dialog v-model="detailVisible" title="迁移详情" width="900px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="迁移单号">{{ detailData.migrationNo }}</el-descriptions-item>
        <el-descriptions-item label="机器编号">{{ detailData.machineCode }}</el-descriptions-item>
        <el-descriptions-item label="机器名称">{{ detailData.machineName }}</el-descriptions-item>
        <el-descriptions-item label="原点位">{{ detailData.oldMallName }}</el-descriptions-item>
        <el-descriptions-item label="新点位">{{ detailData.newMallName }}</el-descriptions-item>
        <el-descriptions-item label="迁移原因">{{ getMigrationReasonText(detailData.migrationReason) }}</el-descriptions-item>
        <el-descriptions-item label="原扣点率">{{ detailData.oldCommissionRate }}%</el-descriptions-item>
        <el-descriptions-item label="新扣点率">{{ detailData.newCommissionRate }}%</el-descriptions-item>
        <el-descriptions-item label="迁移开始时间">{{ detailData.migrationStartTime }}</el-descriptions-item>
        <el-descriptions-item label="迁移结束时间">{{ detailData.migrationEndTime || '进行中' }}</el-descriptions-item>
        <el-descriptions-item label="停机时长(分钟)">{{ detailData.downtimeMinutes }}</el-descriptions-item>
        <el-descriptions-item label="停机损失">¥{{ detailData.downtimeLoss }}</el-descriptions-item>
        <el-descriptions-item label="迁移前总库存">{{ detailData.totalInventoryBefore }}</el-descriptions-item>
        <el-descriptions-item label="迁移后总库存">{{ detailData.totalInventoryAfter }}</el-descriptions-item>
        <el-descriptions-item label="已转移库存">{{ detailData.transferredInventory }}</el-descriptions-item>
        <el-descriptions-item label="丢失库存">{{ detailData.lostInventory }}</el-descriptions-item>
        <el-descriptions-item label="库存转移状态" :span="2">
          <el-tag :type="getTransferStatusType(detailData.inventoryTransferStatus)">
            {{ getTransferStatusText(detailData.inventoryTransferStatus) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="损失说明" :span="2">{{ detailData.lossRemark || '无' }}</el-descriptions-item>
        <el-descriptions-item label="操作人">{{ detailData.operatorName }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detailData.createdTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <el-dialog v-model="inventoryVisible" title="库存转移明细" width="1000px">
      <el-table :data="inventoryData" border stripe>
        <el-table-column prop="skuCode" label="SKU编码" width="120" />
        <el-table-column prop="skuName" label="SKU名称" width="200">
          <template #default="{ row }">
            <div class="sku-info">
              <el-image :src="row.skuImage" style="width: 40px; height: 40px; margin-right: 8px;" fit="cover" />
              <span>{{ row.skuName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="seriesName" label="所属系列" width="150" />
        <el-table-column prop="qtyBefore" label="迁移前数量" width="100" />
        <el-table-column prop="qtyAfter" label="迁移后数量" width="100" />
        <el-table-column prop="qtyTransferred" label="转移数量" width="100" />
        <el-table-column prop="qtyLost" label="丢失数量" width="100" />
        <el-table-column label="转移状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.transferStatus === 'COMPLETE' ? 'success' : row.transferStatus === 'PARTIAL' ? 'warning' : 'danger'">
              {{ row.transferStatus === 'COMPLETE' ? '完整' : row.transferStatus === 'PARTIAL' ? '部分' : '待确认' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getSupplierMigrationPage, getSupplierMigrationDetail, getSupplierMigrationInventory } from '@/api/machine'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const detailVisible = ref(false)
const inventoryVisible = ref(false)
const detailData = ref({})
const inventoryData = ref([])

const queryForm = reactive({
  migrationNo: '',
  machineCode: '',
  status: '',
  inventoryTransferStatus: '',
  page: 1,
  pageSize: 10
})

async function loadData() {
  loading.value = true
  try {
    const res = await getSupplierMigrationPage(queryForm)
    tableData.value = res.list
    total.value = res.total
  } catch (e) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

function resetForm() {
  queryForm.migrationNo = ''
  queryForm.machineCode = ''
  queryForm.status = ''
  queryForm.inventoryTransferStatus = ''
  queryForm.page = 1
  loadData()
}

async function viewDetail(row) {
  try {
    const res = await getSupplierMigrationDetail(row.id)
    detailData.value = res
    detailVisible.value = true
  } catch (e) {
    ElMessage.error('加载详情失败')
  }
}

async function viewInventory(row) {
  try {
    const res = await getSupplierMigrationInventory(row.id)
    inventoryData.value = res
    inventoryVisible.value = true
  } catch (e) {
    ElMessage.error('加载库存明细失败')
  }
}

function getStatusText(status) {
  const map = {
    PENDING: '待迁移',
    IN_PROGRESS: '迁移中',
    COMPLETED: '已完成',
    CANCELLED: '已取消'
  }
  return map[status] || status
}

function getStatusType(status) {
  const map = {
    PENDING: 'info',
    IN_PROGRESS: 'warning',
    COMPLETED: 'success',
    CANCELLED: 'danger'
  }
  return map[status] || 'info'
}

function getTransferStatusText(status) {
  const map = {
    PENDING: '待确认',
    COMPLETE: '完整转移',
    PARTIAL: '部分转移'
  }
  return map[status] || status
}

function getTransferStatusType(status) {
  const map = {
    PENDING: 'info',
    COMPLETE: 'success',
    PARTIAL: 'warning'
  }
  return map[status] || 'info'
}

function getMigrationReasonText(reason) {
  const map = {
    MALL_MOVE: '商场挪机',
    POWER_OUTAGE: '断电施工',
    FLOOR_ADJUST: '调整楼层',
    OTHER: '其他原因'
  }
  return map[reason] || reason
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.migration-inventory {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.table-card {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  justify-content: flex-end;
}

.sku-info {
  display: flex;
  align-items: center;
}
</style>
