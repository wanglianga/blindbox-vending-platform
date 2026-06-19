<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">卡货退款判断</h2>
    </div>

    <el-card class="mb-20">
      <el-form :inline="true" :model="queryForm" @submit.prevent>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="全部" clearable style="width: 160px">
            <el-option label="待维修确认" value="PENDING_REPAIR" />
            <el-option label="已完成" value="COMPLETED" />
          </el-select>
        </el-form-item>
        <el-form-item label="机器编号">
          <el-input v-model="queryForm.machineCode" placeholder="请输入机器编号" clearable style="width: 180px" />
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
      <el-table :data="recordList" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="recordNo" label="处理单号" width="160" />
        <el-table-column prop="ticketNo" label="工单号" width="160" />
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="machineName" label="机器名称" width="160" />
        <el-table-column prop="gridNo" label="格口号" width="80" />
        <el-table-column prop="skuName" label="商品名称" min-width="140" show-overflow-tooltip />
        <el-table-column label="处理决定" width="100">
          <template #default="{ row }">
            <el-tag :type="getDecisionType(row.handleDecision)" size="small">
              {{ getDecisionLabel(row.handleDecision) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="csHandlerName" label="处理人" width="100" />
        <el-table-column prop="handleTime" label="处理时间" width="170" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleDetail(row)">详情</el-button>
            <el-button v-if="row.status === 'PENDING_REPAIR'" type="success" link size="small" @click="handleRepairConfirm(row)">
              维修确认
            </el-button>
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

    <el-dialog v-model="handleVisible" title="卡货处理" width="720px" @close="handleClose">
      <el-form :model="handleForm" label-width="120px" v-if="currentTicket">
        <el-alert
          title="请仔细核查以下五项内容，再做出处理决定"
          type="warning"
          show-icon
          :closable="false"
          class="mb-20"
        />
        <el-divider content-position="left">工单信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="工单号">
              <span>{{ currentTicket.ticketNo }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="订单号">
              <span>{{ currentTicket.orderNo }}</span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-divider content-position="left">核查项</el-divider>
        <el-form-item label="支付流水" required>
          <el-radio-group v-model="handleForm.payFlowChecked">
            <el-radio value="NORMAL">正常</el-radio>
            <el-radio value="ABNORMAL">异常</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="格口电机" required>
          <el-radio-group v-model="handleForm.motorStatusChecked">
            <el-radio value="NORMAL">正常</el-radio>
            <el-radio value="FAULT">故障</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="出货传感" required>
          <el-radio-group v-model="handleForm.sensorStatusChecked">
            <el-radio value="NORMAL">正常</el-radio>
            <el-radio value="FAULT">故障</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="库存变化" required>
          <el-radio-group v-model="handleForm.inventoryChangeChecked">
            <el-radio value="NORMAL">正常</el-radio>
            <el-radio value="ABNORMAL">异常</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="监控照片" required>
          <el-radio-group v-model="handleForm.monitorPhotoChecked">
            <el-radio value="NORMAL">正常</el-radio>
            <el-radio value="ABNORMAL">异常</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="核查备注">
          <el-input v-model="handleForm.checkRemark" type="textarea" :rows="3" placeholder="请输入核查备注" />
        </el-form-item>
        <el-divider content-position="left">处理决定</el-divider>
        <el-form-item label="处理决定" required>
          <el-radio-group v-model="handleForm.handleDecision">
            <el-radio value="REFUND">退款</el-radio>
            <el-radio value="REISSUE">补发</el-radio>
            <el-radio value="REPAIR">维修</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="handleForm.handleDecision === 'REFUND'" label="退款金额" required>
          <el-input-number v-model="handleForm.refundAmount" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item v-if="handleForm.handleDecision === 'REPAIR'" label="维修内容">
          <el-input v-model="handleForm.repairContent" type="textarea" :rows="3" placeholder="请输入维修内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="handleVisible = false">取消</el-button>
        <el-button type="primary" @click="submitHandle">确认处理</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="repairVisible" title="维修确认" width="500px">
      <el-form :model="repairForm" label-width="100px">
        <el-form-item label="确认结果" required>
          <el-radio-group v-model="repairForm.confirmResult">
            <el-radio value="ITEM_STILL_IN_GRID">实物仍在格口</el-radio>
            <el-radio value="REPAIRED">已修复</el-radio>
            <el-radio value="ITEM_LOST">物品丢失</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-alert
          v-if="repairForm.confirmResult === 'ITEM_STILL_IN_GRID'"
          title="选择此项将自动反向修正库存与收益"
          type="warning"
          show-icon
          :closable="false"
        />
        <el-form-item label="备注">
          <el-input v-model="repairForm.remark" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="repairVisible = false">取消</el-button>
        <el-button type="primary" @click="submitRepairConfirm">确认</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailVisible" title="卡货处理详情" width="720px">
      <el-descriptions :column="2" border v-if="currentRecord">
        <el-descriptions-item label="处理单号">{{ currentRecord.recordNo }}</el-descriptions-item>
        <el-descriptions-item label="工单号">{{ currentRecord.ticketNo }}</el-descriptions-item>
        <el-descriptions-item label="订单号">{{ currentRecord.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="机器">{{ currentRecord.machineName }}</el-descriptions-item>
        <el-descriptions-item label="格口号">{{ currentRecord.gridNo }}</el-descriptions-item>
        <el-descriptions-item label="商品">{{ currentRecord.skuName }}</el-descriptions-item>
        <el-descriptions-item label="支付金额">¥{{ currentRecord.payAmount }}</el-descriptions-item>
        <el-descriptions-item label="支付状态">{{ currentRecord.payStatus }}</el-descriptions-item>
        <el-descriptions-item label="出货状态">{{ currentRecord.shipStatus }}</el-descriptions-item>
        <el-descriptions-item label="电机状态">{{ currentRecord.motorStatus }}</el-descriptions-item>
        <el-descriptions-item label="支付流水" :span="2">
          <el-tag :type="currentRecord.payFlowChecked === 'NORMAL' ? 'success' : 'danger'">
            {{ currentRecord.payFlowChecked === 'NORMAL' ? '正常' : '异常' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="格口电机">
          <el-tag :type="currentRecord.motorStatusChecked === 'NORMAL' ? 'success' : 'danger'">
            {{ currentRecord.motorStatusChecked === 'NORMAL' ? '正常' : '故障' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="出货传感">
          <el-tag :type="currentRecord.sensorStatusChecked === 'NORMAL' ? 'success' : 'danger'">
            {{ currentRecord.sensorStatusChecked === 'NORMAL' ? '正常' : '故障' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="库存变化">
          <el-tag :type="currentRecord.inventoryChangeChecked === 'NORMAL' ? 'success' : 'danger'">
            {{ currentRecord.inventoryChangeChecked === 'NORMAL' ? '正常' : '异常' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="监控照片">
          <el-tag :type="currentRecord.monitorPhotoChecked === 'NORMAL' ? 'success' : 'danger'">
            {{ currentRecord.monitorPhotoChecked === 'NORMAL' ? '正常' : '异常' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="核查备注" :span="2">{{ currentRecord.checkRemark }}</el-descriptions-item>
        <el-descriptions-item label="处理决定">
          <el-tag type="primary">{{ getDecisionLabel(currentRecord.handleDecision) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentRecord.status)">{{ getStatusLabel(currentRecord.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item v-if="currentRecord.refundAmount" label="退款金额">
          ¥{{ currentRecord.refundAmount }}
        </el-descriptions-item>
        <el-descriptions-item v-if="currentRecord.repairContent" label="维修内容" :span="2">
          {{ currentRecord.repairContent }}
        </el-descriptions-item>
        <el-descriptions-item label="处理人">{{ currentRecord.csHandlerName }}</el-descriptions-item>
        <el-descriptions-item label="处理时间">{{ currentRecord.handleTime }}</el-descriptions-item>
        <el-descriptions-item v-if="currentRecord.repairerConfirmResult" label="维修确认">
          <el-tag type="info">{{ getRepairConfirmLabel(currentRecord.repairerConfirmResult) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item v-if="currentRecord.repairerConfirmTime" label="确认时间">
          {{ currentRecord.repairerConfirmTime }}
        </el-descriptions-item>
        <el-descriptions-item v-if="currentRecord.inventoryCorrectionId" label="库存修正">
          <el-tag type="warning">已生成修正记录</el-tag>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'
import { getStuckRecordPage, getStuckRecordDetail, handleStuck, repairerConfirm, getTicketDetail } from '@/api/cs'

const loading = ref(false)
const recordList = ref([])
const total = ref(0)
const handleVisible = ref(false)
const repairVisible = ref(false)
const detailVisible = ref(false)
const currentTicket = ref(null)
const currentRecord = ref(null)
const currentRepairRecord = ref(null)

const queryForm = reactive({
  status: '',
  machineCode: '',
  page: 1,
  pageSize: 10
})

const handleForm = reactive({
  ticketId: null,
  payFlowChecked: '',
  motorStatusChecked: '',
  sensorStatusChecked: '',
  inventoryChangeChecked: '',
  monitorPhotoChecked: '',
  checkRemark: '',
  handleDecision: '',
  refundAmount: 0,
  reissueSkuId: null,
  reissueGridNo: '',
  repairContent: ''
})

const repairForm = reactive({
  stuckRecordId: null,
  confirmResult: '',
  remark: ''
})

function getDecisionType(decision) {
  const map = {
    REFUND: 'warning',
    REISSUE: 'success',
    REPAIR: 'danger'
  }
  return map[decision] || 'info'
}

function getDecisionLabel(decision) {
  const map = {
    REFUND: '退款',
    REISSUE: '补发',
    REPAIR: '维修'
  }
  return map[decision] || decision
}

function getStatusType(status) {
  const map = {
    PENDING_REPAIR: 'warning',
    COMPLETED: 'success'
  }
  return map[status] || 'info'
}

function getStatusLabel(status) {
  const map = {
    PENDING_REPAIR: '待维修确认',
    COMPLETED: '已完成'
  }
  return map[status] || status
}

function getRepairConfirmLabel(result) {
  const map = {
    ITEM_STILL_IN_GRID: '实物仍在格口',
    REPAIRED: '已修复',
    ITEM_LOST: '物品丢失'
  }
  return map[result] || result
}

async function loadData() {
  loading.value = true
  try {
    const res = await getStuckRecordPage(queryForm)
    recordList.value = res.list
    total.value = res.total
  } finally {
    loading.value = false
  }
}

function handleReset() {
  queryForm.status = ''
  queryForm.machineCode = ''
  queryForm.page = 1
  loadData()
}

async function handleDetail(row) {
  const res = await getStuckRecordDetail(row.id)
  currentRecord.value = res
  detailVisible.value = true
}

async function handleRepairConfirm(row) {
  currentRepairRecord.value = row
  repairForm.stuckRecordId = row.id
  repairForm.confirmResult = ''
  repairForm.remark = ''
  repairVisible.value = true
}

async function submitRepairConfirm() {
  if (!repairForm.confirmResult) {
    ElMessage.warning('请选择确认结果')
    return
  }
  await repairerConfirm(repairForm)
  ElMessage.success('确认成功')
  repairVisible.value = false
  loadData()
}

function handleClose() {
  currentTicket.value = null
  Object.keys(handleForm).forEach(key => {
    if (typeof handleForm[key] === 'string') handleForm[key] = ''
    else if (typeof handleForm[key] === 'number') handleForm[key] = 0
    else handleForm[key] = null
  })
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
</style>
