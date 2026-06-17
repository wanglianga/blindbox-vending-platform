<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">用户管理</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增用户
      </el-button>
    </div>

    <el-card class="mb-20">
      <el-form :inline="true" :model="queryForm" @submit.prevent>
        <el-form-item label="关键词">
          <el-input v-model="queryForm.keyword" placeholder="账号/姓名/手机号" clearable style="width: 240px" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="queryForm.role" placeholder="全部" clearable style="width: 160px">
            <el-option label="运营公司" value="OPERATOR" />
            <el-option label="供应商" value="SUPPLIER" />
            <el-option label="商场" value="MALL" />
            <el-option label="补货员" value="REPLENISHER" />
            <el-option label="客服" value="CUSTOMER_SERVICE" />
          </el-select>
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
      <el-table :data="tableData" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="username" label="账号" width="140" />
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column label="角色" width="120">
          <template #default="{ row }">
            <el-tag :type="getRoleType(row.role)" size="small">{{ getRoleLabel(row.role) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="140" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ENABLED' ? 'success' : 'info'" size="small">
              {{ row.status === 'ENABLED' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="primary" link size="small" @click="handleResetPwd(row)">重置密码</el-button>
            <el-button :type="row.status === 'ENABLED' ? 'warning' : 'success'" link size="small" @click="handleToggleStatus(row)">
              {{ row.status === 'ENABLED' ? '禁用' : '启用' }}
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="520px">
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="账号" prop="username">
          <el-input v-model="formData.username" placeholder="请输入账号" />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="formData.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="formData.role" style="width: 100%">
            <el-option label="运营公司" value="OPERATOR" />
            <el-option label="供应商" value="SUPPLIER" />
            <el-option label="商场" value="MALL" />
            <el-option label="补货员" value="REPLENISHER" />
            <el-option label="客服" value="CUSTOMER_SERVICE" />
          </el-select>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item v-if="dialogType === 'add'" label="密码" prop="password">
          <el-input v-model="formData.password" type="password" placeholder="请输入初始密码" show-password />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="formData.status" style="width: 100%">
            <el-option label="启用" value="ENABLED" />
            <el-option label="禁用" value="DISABLED" />
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
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserList, addUser, updateUser, updateUserStatus } from '@/api/user'
import { roleMap } from '@/mock'

const loading = ref(false)
const submitting = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref(null)

const queryForm = reactive({
  keyword: '',
  role: '',
  page: 1,
  pageSize: 10
})

const formData = reactive({
  id: null,
  username: '',
  name: '',
  role: 'OPERATOR',
  phone: '',
  password: '',
  status: 'ENABLED'
})

const formRules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入初始密码', trigger: 'blur' }]
}

const dialogTitle = computed(() => dialogType.value === 'add' ? '新增用户' : '编辑用户')

function getRoleType(role) {
  const types = {
    OPERATOR: 'primary',
    SUPPLIER: 'success',
    MALL: 'warning',
    REPLENISHER: 'info',
    CUSTOMER_SERVICE: 'danger'
  }
  return types[role] || 'info'
}

function getRoleLabel(role) {
  return roleMap[role] || role
}

async function loadData() {
  loading.value = true
  try {
    const res = await getUserList(queryForm)
    tableData.value = res.list
    total.value = res.total
  } finally {
    loading.value = false
  }
}

function handleReset() {
  queryForm.keyword = ''
  queryForm.role = ''
  queryForm.page = 1
  loadData()
}

function handleAdd() {
  dialogType.value = 'add'
  Object.assign(formData, {
    id: null,
    username: '',
    name: '',
    role: 'OPERATOR',
    phone: '',
    password: '',
    status: 'ENABLED'
  })
  dialogVisible.value = true
}

function handleEdit(row) {
  dialogType.value = 'edit'
  Object.assign(formData, row)
  dialogVisible.value = true
}

function handleResetPwd(row) {
  ElMessageBox.confirm(`确定要重置用户"${row.name}"的密码吗？`, '提示', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  }).then(() => {
    ElMessage.success('密码已重置为 123456')
  }).catch(() => {})
}

function handleToggleStatus(row) {
  const action = row.status === 'ENABLED' ? '禁用' : '启用'
  ElMessageBox.confirm(`确定要${action}用户"${row.name}"吗？`, '提示', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  }).then(async () => {
    const newStatus = row.status === 'ENABLED' ? 'DISABLED' : 'ENABLED'
    await updateUserStatus(row.id, newStatus)
    ElMessage.success(`${action}成功`)
    loadData()
  }).catch(() => {})
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  submitting.value = true
  try {
    if (dialogType.value === 'add') {
      await addUser(formData)
      ElMessage.success('新增成功')
    } else {
      await updateUser(formData.id, formData)
      ElMessage.success('编辑成功')
    }
    dialogVisible.value = false
    loadData()
  } finally {
    submitting.value = false
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
</style>
