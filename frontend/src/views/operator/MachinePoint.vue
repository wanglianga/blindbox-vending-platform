<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">机器点位管理</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增机器
      </el-button>
    </div>

    <el-card class="mb-20">
      <el-form :inline="true" :model="queryForm" @submit.prevent>
        <el-form-item label="关键词">
          <el-input v-model="queryForm.keyword" placeholder="请输入机器名称/编号/商场" clearable style="width: 240px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="全部" clearable style="width: 140px">
            <el-option label="在线" value="ONLINE" />
            <el-option label="离线" value="OFFLINE" />
            <el-option label="故障" value="FAULT" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
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
      <el-table :data="tableData" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="machineNo" label="机器编号" width="120" />
        <el-table-column prop="name" label="机器名称" min-width="160" />
        <el-table-column prop="mallName" label="所属商场" width="140" />
        <el-table-column prop="location" label="位置" min-width="200" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">{{ getStatusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="gridCount" label="格口数" width="90" align="center" />
        <el-table-column prop="skuCount" label="SKU数" width="90" align="center" />
        <el-table-column prop="lastOnline" label="最后在线" width="170" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleView(row)">查看</el-button>
            <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="queryForm.page"
          v-model:page-size="queryForm.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="560px">
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="机器编号" prop="machineNo">
          <el-input v-model="formData.machineNo" placeholder="请输入机器编号" />
        </el-form-item>
        <el-form-item label="机器名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入机器名称" />
        </el-form-item>
        <el-form-item label="所属商场" prop="mallName">
          <el-input v-model="formData.mallName" placeholder="请输入商场名称" />
        </el-form-item>
        <el-form-item label="位置" prop="location">
          <el-input v-model="formData.location" placeholder="请输入具体位置" />
        </el-form-item>
        <el-form-item label="格口数" prop="gridCount">
          <el-input-number v-model="formData.gridCount" :min="1" :max="200" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="formData.status" style="width: 100%">
            <el-option label="在线" value="ONLINE" />
            <el-option label="离线" value="OFFLINE" />
            <el-option label="故障" value="FAULT" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMachineList, addMachine, updateMachine, deleteMachine } from '@/api/machine'
import { statusMap } from '@/mock'

const loading = ref(false)
const submitting = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref(null)

const queryForm = reactive({
  keyword: '',
  status: '',
  page: 1,
  pageSize: 10
})

const formData = reactive({
  id: null,
  machineNo: '',
  name: '',
  mallName: '',
  location: '',
  gridCount: 60,
  status: 'ONLINE'
})

const formRules = {
  machineNo: [{ required: true, message: '请输入机器编号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入机器名称', trigger: 'blur' }],
  mallName: [{ required: true, message: '请输入商场名称', trigger: 'blur' }]
}

const dialogTitle = computed(() => dialogType.value === 'add' ? '新增机器' : '编辑机器')

function getStatusType(status) {
  return statusMap[status]?.type || 'info'
}

function getStatusLabel(status) {
  return statusMap[status]?.label || status
}

async function loadData() {
  loading.value = true
  try {
    const res = await getMachineList(queryForm)
    tableData.value = res.list
    total.value = res.total
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  queryForm.page = 1
  loadData()
}

function handleReset() {
  queryForm.keyword = ''
  queryForm.status = ''
  queryForm.page = 1
  loadData()
}

function handleAdd() {
  dialogType.value = 'add'
  Object.assign(formData, {
    id: null,
    machineNo: '',
    name: '',
    mallName: '',
    location: '',
    gridCount: 60,
    status: 'ONLINE'
  })
  dialogVisible.value = true
}

function handleEdit(row) {
  dialogType.value = 'edit'
  Object.assign(formData, row)
  dialogVisible.value = true
}

function handleView(row) {
  ElMessage.info('查看详情功能开发中')
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  submitting.value = true
  try {
    if (dialogType.value === 'add') {
      await addMachine(formData)
      ElMessage.success('新增成功')
    } else {
      await updateMachine(formData.id, formData)
      ElMessage.success('编辑成功')
    }
    dialogVisible.value = false
    loadData()
  } finally {
    submitting.value = false
  }
}

function handleDelete(row) {
  ElMessageBox.confirm(`确定要删除机器"${row.name}"吗？`, '提示', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  }).then(async () => {
    await deleteMachine(row.id)
    ElMessage.success('删除成功')
    loadData()
  }).catch(() => {})
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
</style>
