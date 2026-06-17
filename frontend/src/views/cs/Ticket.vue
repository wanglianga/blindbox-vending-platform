<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">工单列表</h2>
      <div class="header-stats">
        <el-tag type="warning" size="large">待处理 {{ pendingCount }}</el-tag>
        <el-tag type="primary" size="large">处理中 {{ processingCount }}</el-tag>
      </div>
    </div>

    <el-card class="mb-20">
      <el-form :inline="true" :model="queryForm" @submit.prevent>
        <el-form-item label="类型">
          <el-select v-model="queryForm.type" placeholder="全部" clearable style="width: 140px">
            <el-option label="退款" value="REFUND" />
            <el-option label="咨询" value="CONSULT" />
            <el-option label="投诉" value="COMPLAINT" />
            <el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="全部" clearable style="width: 140px">
            <el-option label="待处理" value="PENDING" />
            <el-option label="处理中" value="PROCESSING" />
            <el-option label="已完成" value="COMPLETED" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="queryForm.keyword" placeholder="工单标题/用户" clearable style="width: 200px" />
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
      <el-table :data="ticketList" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="ticketNo" label="工单号" width="160" />
        <el-table-column label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getTypeTag(row.type)" size="small">{{ getTypeLabel(row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="180" show-overflow-tooltip />
        <el-table-column prop="userName" label="用户" width="100" />
        <el-table-column prop="userPhone" label="手机号" width="140" />
        <el-table-column prop="machineName" label="关联机器" width="140" show-overflow-tooltip />
        <el-table-column label="优先级" width="100">
          <template #default="{ row }">
            <el-tag :type="getPriorityType(row.priority)" size="small">{{ getPriorityLabel(row.priority) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">{{ getStatusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status !== 'COMPLETED'" type="primary" size="small" @click="handleProcess(row)">
              处理
            </el-button>
            <el-button type="primary" link size="small" @click="handleDetail(row)">详情</el-button>
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

    <el-dialog v-model="processVisible" title="处理工单" width="560px">
      <div v-if="currentTicket" class="ticket-info">
        <el-descriptions :column="1" border size="small">
          <el-descriptions-item label="工单号">{{ currentTicket.ticketNo }}</el-descriptions-item>
          <el-descriptions-item label="类型">{{ getTypeLabel(currentTicket.type) }}</el-descriptions-item>
          <el-descriptions-item label="标题">{{ currentTicket.title }}</el-descriptions-item>
          <el-descriptions-item label="用户">{{ currentTicket.userName }} ({{ currentTicket.userPhone }})</el-descriptions-item>
        </el-descriptions>
      </div>
      
      <el-form :model="processForm" label-width="100px" class="mt-20">
        <el-form-item label="处理结果">
          <el-radio-group v-model="processForm.result">
            <el-radio value="RESOLVED">已解决</el-radio>
            <el-radio value="TRANSFERRED">已转交</el-radio>
            <el-radio value="REJECTED">已驳回</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="处理说明">
          <el-input v-model="processForm.remark" type="textarea" :rows="4" placeholder="请输入处理说明" />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="processVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getTicketList, handleTicket } from '@/api/cs'
import { ticketStatusMap } from '@/mock'

const loading = ref(false)
const ticketList = ref([])
const total = ref(0)
const processVisible = ref(false)
const currentTicket = ref(null)

const queryForm = reactive({
  type: '',
  status: '',
  keyword: '',
  page: 1,
  pageSize: 10
})

const processForm = reactive({
  result: 'RESOLVED',
  remark: ''
})

const pendingCount = computed(() => ticketList.value.filter(t => t.status === 'PENDING').length)
const processingCount = computed(() => ticketList.value.filter(t => t.status === 'PROCESSING').length)

function getTypeTag(type) {
  const types = { REFUND: 'danger', CONSULT: 'primary', COMPLAINT: 'warning', OTHER: 'info' }
  return types[type] || 'info'
}

function getTypeLabel(type) {
  const labels = { REFUND: '退款', CONSULT: '咨询', COMPLAINT: '投诉', OTHER: '其他' }
  return labels[type] || type
}

function getPriorityType(priority) {
  const types = { HIGH: 'danger', MEDIUM: 'warning', LOW: 'info' }
  return types[priority] || 'info'
}

function getPriorityLabel(priority) {
  const labels = { HIGH: '高', MEDIUM: '中', LOW: '低' }
  return labels[priority] || priority
}

function getStatusType(status) {
  return ticketStatusMap[status]?.type || 'info'
}

function getStatusLabel(status) {
  return ticketStatusMap[status]?.label || status
}

async function loadData() {
  loading.value = true
  try {
    const res = await getTicketList(queryForm)
    ticketList.value = res.list
    total.value = res.total
  } finally {
    loading.value = false
  }
}

function handleReset() {
  queryForm.type = ''
  queryForm.status = ''
  queryForm.keyword = ''
  queryForm.page = 1
  loadData()
}

function handleProcess(row) {
  currentTicket.value = row
  processForm.result = 'RESOLVED'
  processForm.remark = ''
  processVisible.value = true
}

function handleDetail(row) {
  ElMessage.info('查看工单详情')
}

async function handleSubmit() {
  if (!processForm.remark) {
    ElMessage.warning('请输入处理说明')
    return
  }
  await handleTicket(currentTicket.value.id, processForm)
  ElMessage.success('处理成功')
  processVisible.value = false
  loadData()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.header-stats {
  display: flex;
  gap: 10px;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.ticket-info {
  margin-bottom: 20px;
}

.mt-20 {
  margin-top: 20px;
}
</style>
