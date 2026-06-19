<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">点位迁移管理</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新建迁移
      </el-button>
    </div>

    <el-card class="mb-20">
      <el-form :inline="true" :model="queryForm" @submit.prevent>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="全部" clearable style="width: 160px">
            <el-option label="进行中" value="IN_PROGRESS" />
            <el-option label="已完成" value="COMPLETED" />
            <el-option label="已取消" value="CANCELLED" />
          </el-select>
        </el-form-item>
        <el-form-item label="机器ID">
          <el-input v-model="queryForm.machineId" placeholder="请输入机器ID" clearable style="width: 140px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <el-table :data="migrationList" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="migrationNo" label="迁移单号" width="160" />
        <el-table-column prop="machineName" label="机器名称" width="140" />
        <el-table-column prop="oldMallName" label="原商场" width="140" />
        <el-table-column prop="newMallName" label="新商场" width="140" />
        <el-table-column label="迁移原因" width="120">
          <template #default="{ row }">
            <el-tag size="small">{{ getReasonLabel(row.migrationReason) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="migrationStartTime" label="迁移开始时间" width="170" />
        <el-table-column prop="downtimeMinutes" label="停机时长(分钟)" width="120" />
        <el-table-column label="库存转移" width="120">
          <template #default="{ row }">
            <span>{{ row.transferredInventory || 0 }}/{{ row.totalInventoryBefore || 0 }}</span>
            <el-tag
              v-if="row.inventoryTransferStatus"
              :type="row.inventoryTransferStatus === 'COMPLETE' ? 'success' : 'warning'"
              size="small"
              class="ml-5"
            >
              {{ getTransferStatusLabel(row.inventoryTransferStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="operatorName" label="操作人" width="100" />
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleDetail(row)">详情</el-button>
            <el-button
              v-if="row.status === 'IN_PROGRESS'"
              type="success"
              link
              size="small"
              @click="handleInventory(row)"
            >
              库存录入
            </el-button>
            <el-button
              v-if="row.status === 'IN_PROGRESS'"
              type="warning"
              link
              size="small"
              @click="handleComplete(row)"
            >
              完成迁移
            </el-button>
            <el-button type="info" link size="small" @click="handleRevenue(row)">分段收益</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="queryForm.page"
          v-model:page-size="queryForm.pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>

    <el-dialog v-model="addVisible" title="新建点位迁移" width="720px">
      <el-form :model="addForm" label-width="120px">
        <el-form-item label="机器" required>
          <el-select v-model="addForm.machineId" placeholder="请选择机器" style="width: 100%">
            <el-option
              v-for="machine in machineList"
              :key="machine.id"
              :label="machine.machineName + '(' + machine.machineCode + ')'"
              :value="machine.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="新商场" required>
          <el-select v-model="addForm.newMallId" placeholder="请选择新商场" style="width: 100%">
            <el-option v-for="mall in mallList" :key="mall.id" :label="mall.name" :value="mall.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="新位置">
          <el-input v-model="addForm.newLocationDetail" placeholder="请输入新位置描述" />
        </el-form-item>
        <el-form-item label="新扣点比例(%)" required>
          <el-input-number v-model="addForm.newCommissionRate" :min="0" :max="100" :precision="2" />
        </el-form-item>
        <el-form-item label="迁移原因" required>
          <el-select v-model="addForm.migrationReason" placeholder="请选择迁移原因" style="width: 100%">
            <el-option label="商场要求挪机" value="MALL_REQUEST" />
            <el-option label="断电施工" value="POWER_CONSTRUCTION" />
            <el-option label="调整楼层" value="FLOOR_ADJUST" />
            <el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item label="迁移开始时间" required>
          <el-date-picker
            v-model="addForm.migrationStartTime"
            type="datetime"
            placeholder="选择开始时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="预计停机时长(分钟)">
          <el-input-number v-model="addForm.downtimeMinutes" :min="0" />
        </el-form-item>
        <el-form-item label="预计停机损失">
          <el-input-number v-model="addForm.downtimeLoss" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="损失说明">
          <el-input v-model="addForm.lossRemark" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAdd">确认创建</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailVisible" title="迁移详情" width="720px">
      <el-descriptions :column="2" border v-if="currentMigration">
        <el-descriptions-item label="迁移单号">{{ currentMigration.migrationNo }}</el-descriptions-item>
        <el-descriptions-item label="机器">{{ currentMigration.machineName }}</el-descriptions-item>
        <el-descriptions-item label="原商场">{{ currentMigration.oldMallName }}</el-descriptions-item>
        <el-descriptions-item label="新商场">{{ currentMigration.newMallName }}</el-descriptions-item>
        <el-descriptions-item label="原位置">{{ currentMigration.oldLocationDetail }}</el-descriptions-item>
        <el-descriptions-item label="新位置">{{ currentMigration.newLocationDetail }}</el-descriptions-item>
        <el-descriptions-item label="原扣点">{{ currentMigration.oldCommissionRate }}%</el-descriptions-item>
        <el-descriptions-item label="新扣点">{{ currentMigration.newCommissionRate }}%</el-descriptions-item>
        <el-descriptions-item label="迁移原因">{{ getReasonLabel(currentMigration.migrationReason) }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentMigration.status)">
            {{ getStatusLabel(currentMigration.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="开始时间">{{ currentMigration.migrationStartTime }}</el-descriptions-item>
        <el-descriptions-item label="完成时间">{{ currentMigration.migrationEndTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="停机时长">{{ currentMigration.downtimeMinutes }}分钟</el-descriptions-item>
        <el-descriptions-item label="停机损失">¥{{ currentMigration.downtimeLoss }}</el-descriptions-item>
        <el-descriptions-item label="损失说明" :span="2">{{ currentMigration.lossRemark }}</el-descriptions-item>
        <el-descriptions-item label="迁移前库存">{{ currentMigration.totalInventoryBefore }}</el-descriptions-item>
        <el-descriptions-item label="迁移后库存">{{ currentMigration.totalInventoryAfter }}</el-descriptions-item>
        <el-descriptions-item label="已转移">{{ currentMigration.transferredInventory }}</el-descriptions-item>
        <el-descriptions-item label="丢失">{{ currentMigration.lostInventory }}</el-descriptions-item>
        <el-descriptions-item label="操作人">{{ currentMigration.operatorName }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentMigration.createdTime }}</el-descriptions-item>
      </el-descriptions>

      <el-divider content-position="left">库存明细</el-divider>
      <el-table :data="currentMigration.inventoryDetails || []" size="small" v-loading="inventoryLoading">
        <el-table-column prop="skuName" label="商品名称" min-width="140" />
        <el-table-column prop="skuCode" label="SKU编码" width="120" />
        <el-table-column prop="seriesName" label="系列" width="120" />
        <el-table-column prop="qtyBefore" label="迁移前数量" width="100" align="center" />
        <el-table-column prop="qtyAfter" label="迁移后数量" width="100" align="center" />
        <el-table-column prop="qtyTransferred" label="已转移" width="80" align="center" />
        <el-table-column prop="qtyLost" label="丢失" width="80" align="center" />
        <el-table-column label="转移状态" width="100">
          <template #default="{ row }">
            <el-tag
              :type="row.transferStatus === 'COMPLETE' ? 'success' : row.transferStatus === 'PARTIAL' ? 'warning' : 'info'"
              size="small"
            >
              {{ getTransferStatusLabel(row.transferStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="120" />
      </el-table>
    </el-dialog>

    <el-dialog v-model="inventoryVisible" title="迁移库存录入" width="720px">
      <el-alert
        title="请录入每个SKU迁移后的实际数量，系统将自动计算转移和丢失数量"
        type="info"
        show-icon
        :closable="false"
        class="mb-20"
      />
      <el-table :data="inventoryForm" size="small" v-loading="inventoryLoading">
        <el-table-column prop="skuName" label="商品名称" min-width="140" />
        <el-table-column prop="seriesName" label="系列" width="120" />
        <el-table-column prop="qtyBefore" label="迁移前数量" width="100" align="center" />
        <el-table-column label="迁移后数量" width="150">
          <template #default="{ row }">
            <el-input-number v-model="row.qtyAfter" :min="0" size="small" />
          </template>
        </el-table-column>
        <el-table-column label="备注" min-width="150">
          <template #default="{ row }">
            <el-input v-model="row.remark" size="small" placeholder="备注" />
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <el-button @click="inventoryVisible = false">取消</el-button>
        <el-button type="primary" @click="submitInventory">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="revenueVisible" title="分段收益查看" width="900px">
      <el-alert
        title="系统根据迁移时间自动将收益分为：旧点位、迁移中、新点位三个阶段"
        type="info"
        show-icon
        :closable="false"
        class="mb-20"
      />
      <el-table :data="revenueList" size="small" v-loading="revenueLoading">
        <el-table-column prop="orderNo" label="订单号" width="160" />
        <el-table-column label="阶段" width="100">
          <template #default="{ row }">
            <el-tag :type="getPeriodType(row.periodType)" size="small">
              {{ getPeriodLabel(row.periodType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="mallName" label="商场" width="120" />
        <el-table-column prop="commissionRate" label="扣点比例" width="100" />
        <el-table-column prop="totalAmount" label="订单金额" width="100" />
        <el-table-column prop="mallCommission" label="商场扣点" width="100" />
        <el-table-column prop="supplierShare" label="供应商分成" width="110" />
        <el-table-column prop="operatorShare" label="运营分成" width="100" />
        <el-table-column prop="platformShare" label="平台分成" width="100" />
        <el-table-column prop="orderTime" label="订单时间" width="170" />
        <el-table-column prop="settleStatus" label="结算状态" width="100" />
      </el-table>
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="revenuePage"
          v-model:page-size="10"
          :page-sizes="[10, 20, 50]"
          :total="revenueTotal"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadRevenueData"
          @current-change="loadRevenueData"
        />
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import {
  getMigrationPage,
  getMigrationDetail,
  createMigration,
  completeMigration,
  updateMigrationInventory,
  getMigrationInventory,
  getSegmentRevenue
} from '@/api/machine'

const loading = ref(false)
const inventoryLoading = ref(false)
const revenueLoading = ref(false)
const migrationList = ref([])
const total = ref(0)
const addVisible = ref(false)
const detailVisible = ref(false)
const inventoryVisible = ref(false)
const revenueVisible = ref(false)
const currentMigration = ref(null)
const currentMigrationId = ref(null)
const inventoryForm = ref([])
const revenueList = ref([])
const revenueTotal = ref(0)
const revenuePage = ref(1)
const machineList = ref([])
const mallList = ref([])

const queryForm = reactive({
  status: '',
  machineId: '',
  page: 1,
  pageSize: 10
})

const addForm = reactive({
  machineId: null,
  newMallId: null,
  newLocationDetail: '',
  newCommissionRate: 0,
  migrationReason: '',
  migrationStartTime: null,
  downtimeMinutes: 0,
  downtimeLoss: 0,
  lossRemark: ''
})

function getReasonLabel(reason) {
  const map = {
    MALL_REQUEST: '商场要求挪机',
    POWER_CONSTRUCTION: '断电施工',
    FLOOR_ADJUST: '调整楼层',
    OTHER: '其他'
  }
  return map[reason] || reason
}

function getTransferStatusLabel(status) {
  const map = {
    PENDING: '待转移',
    COMPLETE: '全部转移',
    PARTIAL: '部分转移'
  }
  return map[status] || status
}

function getStatusType(status) {
  const map = {
    IN_PROGRESS: 'warning',
    COMPLETED: 'success',
    CANCELLED: 'info'
  }
  return map[status] || 'info'
}

function getStatusLabel(status) {
  const map = {
    IN_PROGRESS: '进行中',
    COMPLETED: '已完成',
    CANCELLED: '已取消'
  }
  return map[status] || status
}

function getPeriodType(type) {
  const map = {
    OLD_LOCATION: 'info',
    MIGRATION_PERIOD: 'warning',
    NEW_LOCATION: 'success'
  }
  return map[type] || 'info'
}

function getPeriodLabel(type) {
  const map = {
    OLD_LOCATION: '旧点位',
    MIGRATION_PERIOD: '迁移中',
    NEW_LOCATION: '新点位'
  }
  return map[type] || type
}

async function loadData() {
  loading.value = true
  try {
    const params = {
      ...queryForm,
      machineId: queryForm.machineId ? Number(queryForm.machineId) : undefined
    }
    const res = await getMigrationPage(params)
    migrationList.value = res.list
    total.value = res.total
  } finally {
    loading.value = false
  }
}

function handleReset() {
  queryForm.status = ''
  queryForm.machineId = ''
  queryForm.page = 1
  loadData()
}

function handleAdd() {
  addVisible.value = true
}

async function submitAdd() {
  if (!addForm.machineId || !addForm.newMallId || !addForm.newCommissionRate ||
      !addForm.migrationReason || !addForm.migrationStartTime) {
    ElMessage.warning('请填写必填项')
    return
  }
  await createMigration(addForm)
  ElMessage.success('创建成功')
  addVisible.value = false
  loadData()
}

async function handleDetail(row) {
  const res = await getMigrationDetail(row.id)
  currentMigration.value = res
  detailVisible.value = true
}

async function handleInventory(row) {
  currentMigrationId.value = row.id
  inventoryLoading.value = true
  try {
    const res = await getMigrationInventory(row.id)
    inventoryForm.value = res.map(item => ({
      ...item,
      skuId: item.skuId,
      qtyAfter: item.qtyAfter || 0,
      remark: item.remark || ''
    }))
    inventoryVisible.value = true
  } finally {
    inventoryLoading.value = false
  }
}

async function submitInventory() {
  const items = inventoryForm.value.map(item => ({
    skuId: item.skuId,
    qtyAfter: item.qtyAfter,
    remark: item.remark
  }))
  await updateMigrationInventory(currentMigrationId.value, items)
  ElMessage.success('保存成功')
  inventoryVisible.value = false
  loadData()
}

async function handleComplete(row) {
  ElMessageBox.confirm('确定完成该迁移吗？完成后将自动更新机器点位信息。', '提示', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  }).then(async () => {
    await completeMigration(row.id)
    ElMessage.success('迁移已完成')
    loadData()
  }).catch(() => {})
}

async function handleRevenue(row) {
  currentMigrationId.value = row.id
  revenuePage.value = 1
  await loadRevenueData()
  revenueVisible.value = true
}

async function loadRevenueData() {
  revenueLoading.value = true
  try {
    const res = await getSegmentRevenue({
      migrationId: currentMigrationId.value,
      pageNum: revenuePage.value,
      pageSize: 10
    })
    revenueList.value = res.list
    revenueTotal.value = res.total
  } finally {
    revenueLoading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.mb-20 {
  margin-bottom: 20px;
}

.ml-5 {
  margin-left: 5px;
}
</style>
