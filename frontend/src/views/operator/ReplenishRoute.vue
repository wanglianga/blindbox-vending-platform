<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">补货路线</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增路线
      </el-button>
    </div>

    <el-card>
      <el-table :data="tableData" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="routeCode" label="路线编号" width="120" />
        <el-table-column prop="routeName" label="路线名称" min-width="160" />
        <el-table-column prop="machineCount" label="机器数量" width="100" align="center" />
        <el-table-column prop="replenisherName" label="补货员" width="120" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'info'" size="small">
              {{ row.status === 'ACTIVE' ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleView(row)">查看机器</el-button>
            <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="560px">
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="路线编号" prop="routeCode">
          <el-input v-model="formData.routeCode" placeholder="请输入路线编号" />
        </el-form-item>
        <el-form-item label="路线名称" prop="routeName">
          <el-input v-model="formData.routeName" placeholder="请输入路线名称" />
        </el-form-item>
        <el-form-item label="补货员" prop="replenisherName">
          <el-input v-model="formData.replenisherName" placeholder="请输入补货员姓名" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="formData.status" style="width: 100%">
            <el-option label="启用" value="ACTIVE" />
            <el-option label="停用" value="INACTIVE" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="machineDialogVisible" title="路线机器列表" width="700px">
      <el-table :data="machineList" stripe style="width: 100%">
        <el-table-column prop="machineNo" label="机器编号" width="120" />
        <el-table-column prop="name" label="机器名称" min-width="160" />
        <el-table-column prop="location" label="位置" min-width="200" show-overflow-tooltip />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">{{ getStatusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="emptyGrid" label="空格数" width="80" align="center" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRouteList, addRoute, updateRoute, deleteRoute } from '@/api/replenish'
import { getMachineList } from '@/api/machine'
import { statusMap } from '@/mock'

const loading = ref(false)
const submitting = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const machineDialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref(null)
const machineList = ref([])

const queryForm = reactive({
  page: 1,
  pageSize: 10
})

const formData = reactive({
  id: null,
  routeCode: '',
  routeName: '',
  replenisherName: '',
  status: 'ACTIVE'
})

const formRules = {
  routeCode: [{ required: true, message: '请输入路线编号', trigger: 'blur' }],
  routeName: [{ required: true, message: '请输入路线名称', trigger: 'blur' }],
  replenisherName: [{ required: true, message: '请输入补货员', trigger: 'blur' }]
}

const dialogTitle = computed(() => dialogType.value === 'add' ? '新增路线' : '编辑路线')

function getStatusType(status) {
  return statusMap[status]?.type || 'info'
}

function getStatusLabel(status) {
  return statusMap[status]?.label || status
}

async function loadData() {
  loading.value = true
  try {
    const res = await getRouteList(queryForm)
    tableData.value = res.list
    total.value = res.total
  } finally {
    loading.value = false
  }
}

function handleAdd() {
  dialogType.value = 'add'
  Object.assign(formData, {
    id: null,
    routeCode: '',
    routeName: '',
    replenisherName: '',
    status: 'ACTIVE'
  })
  dialogVisible.value = true
}

function handleEdit(row) {
  dialogType.value = 'edit'
  Object.assign(formData, row)
  dialogVisible.value = true
}

async function handleView(row) {
  machineDialogVisible.value = true
  const res = await getMachineList()
  machineList.value = res.list.slice(0, 5).map(m => ({ ...m, emptyGrid: Math.floor(Math.random() * 20) + 5 }))
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  submitting.value = true
  try {
    if (dialogType.value === 'add') {
      await addRoute(formData)
      ElMessage.success('新增成功')
    } else {
      await updateRoute(formData.id, formData)
      ElMessage.success('编辑成功')
    }
    dialogVisible.value = false
    loadData()
  } finally {
    submitting.value = false
  }
}

function handleDelete(row) {
  ElMessageBox.confirm(`确定要删除路线"${row.routeName}"吗？`, '提示', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  }).then(async () => {
    await deleteRoute(row.id)
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
