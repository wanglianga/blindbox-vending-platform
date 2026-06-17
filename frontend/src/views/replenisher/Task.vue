<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">补货任务</h2>
      <div class="header-actions">
        <el-radio-group v-model="statusFilter" size="small">
          <el-radio-button value="">全部</el-radio-button>
          <el-radio-button value="PENDING">待处理</el-radio-button>
          <el-radio-button value="IN_PROGRESS">进行中</el-radio-button>
          <el-radio-button value="COMPLETED">已完成</el-radio-button>
        </el-radio-group>
      </div>
    </div>

    <el-table :data="taskList" stripe style="width: 100%" v-loading="loading">
      <el-table-column prop="taskNo" label="任务编号" width="160" />
      <el-table-column prop="machineName" label="机器名称" min-width="160" />
      <el-table-column prop="machineAddress" label="地址" min-width="200" show-overflow-tooltip />
      <el-table-column label="优先级" width="100">
        <template #default="{ row }">
          <el-tag :type="getPriorityType(row.priority)" size="small">
            {{ getPriorityLabel(row.priority) }}
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
      <el-table-column label="空格/总格口" width="120" align="center">
        <template #default="{ row }">
          {{ row.emptyGrid }} / {{ row.gridTotal }}
        </template>
      </el-table-column>
      <el-table-column prop="planTime" label="计划时间" width="170" />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button v-if="row.status === 'PENDING'" type="primary" size="small" @click="handleStart(row)">
            开始补货
          </el-button>
          <el-button v-else-if="row.status === 'IN_PROGRESS'" type="success" size="small" @click="handleDetail(row)">
            继续补货
          </el-button>
          <el-button v-else type="primary" link size="small" @click="handleView(row)">
            查看详情
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
  </div>
</template>

<script setup>
import { ref, reactive, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getReplenishTaskList } from '@/api/replenish'
import { taskStatusMap } from '@/mock'

const router = useRouter()
const loading = ref(false)
const taskList = ref([])
const total = ref(0)
const statusFilter = ref('')

const queryForm = reactive({
  page: 1,
  pageSize: 10,
  status: ''
})

function getPriorityType(priority) {
  const types = { HIGH: 'danger', MEDIUM: 'warning', LOW: 'info' }
  return types[priority] || 'info'
}

function getPriorityLabel(priority) {
  const labels = { HIGH: '高', MEDIUM: '中', LOW: '低' }
  return labels[priority] || priority
}

function getStatusType(status) {
  return taskStatusMap[status]?.type || 'info'
}

function getStatusLabel(status) {
  return taskStatusMap[status]?.label || status
}

async function loadData() {
  loading.value = true
  try {
    const res = await getReplenishTaskList(queryForm)
    taskList.value = res.list
    total.value = res.total
  } finally {
    loading.value = false
  }
}

function handleStart(row) {
  ElMessage.success(`开始处理任务：${row.taskNo}`)
  router.push('/scan-replenish')
}

function handleDetail(row) {
  router.push('/scan-replenish')
}

function handleView(row) {
  ElMessage.info('查看详情')
}

watch(statusFilter, (val) => {
  queryForm.status = val
  queryForm.page = 1
  loadData()
})

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.header-actions {
  display: flex;
  align-items: center;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>
