<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">IP系列管理</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增系列
      </el-button>
    </div>

    <el-card class="mb-20">
      <el-form :inline="true" :model="queryForm" @submit.prevent>
        <el-form-item label="关键词">
          <el-input v-model="queryForm.keyword" placeholder="请输入系列名称" clearable style="width: 240px" />
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

    <el-row :gutter="20" v-loading="loading">
      <el-col :span="8" v-for="item in tableData" :key="item.id">
        <el-card class="series-card" shadow="hover">
          <div class="series-header">
            <el-image :src="item.coverImage" class="series-cover" fit="cover" />
            <el-tag :type="item.status === 'ON_SALE' ? 'success' : 'info'" size="small" class="status-tag">
              {{ item.status === 'ON_SALE' ? '售卖中' : '已下架' }}
            </el-tag>
          </div>
          <div class="series-body">
            <h3 class="series-name">{{ item.name }}</h3>
            <p class="series-supplier">供应商：{{ item.supplierName }}</p>
            <div class="series-info">
              <span>{{ item.skuCount }}款</span>
              <span>¥{{ item.price }}</span>
              <span>销量 {{ item.totalSales }}</span>
            </div>
          </div>
          <div class="series-footer">
            <el-button type="primary" size="small" @click="handleViewSku(item)">查看款式</el-button>
            <el-button type="primary" size="small" text @click="handleEdit(item)">编辑</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <div class="pagination-wrapper">
      <el-pagination
        v-model:current-page="queryForm.page"
        v-model:page-size="queryForm.pageSize"
        :page-sizes="[9, 18, 36]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadData"
        @current-change="loadData"
      />
    </div>

    <el-dialog v-model="skuDialogVisible" :title="currentSeries?.name + ' - 款式管理'" width="800px">
      <div class="sku-header">
        <el-button type="primary" size="small" @click="handleAddSku">
          <el-icon><Plus /></el-icon>
          新增款式
        </el-button>
        <el-button type="warning" size="small" @click="handleSetProbability">
          <el-icon><Setting /></el-icon>
          概率设置
        </el-button>
      </div>
      <el-table :data="skuList" stripe style="width: 100%">
        <el-table-column label="图片" width="80">
          <template #default="{ row }">
            <el-image :src="row.image" class="sku-image" fit="cover" />
          </template>
        </el-table-column>
        <el-table-column prop="name" label="款式名称" min-width="160" />
        <el-table-column prop="price" label="价格" width="100">
          <template #default="{ row }">¥{{ row.price }}</template>
        </el-table-column>
        <el-table-column label="概率" width="120">
          <template #default="{ row }">
            <el-tag :type="row.isHidden ? 'danger' : 'info'" size="small">
              {{ (row.probability * 100).toFixed(1) }}%
            </el-tag>
            <el-tag v-if="row.isHidden" type="warning" size="small" style="margin-left: 4px">隐藏</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="100" align="center" />
        <el-table-column label="操作" width="140">
          <template #default="{ row }">
            <el-button type="primary" link size="small">编辑</el-button>
            <el-button type="danger" link size="small">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="560px">
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="系列名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入系列名称" />
        </el-form-item>
        <el-form-item label="供应商" prop="supplierName">
          <el-input v-model="formData.supplierName" placeholder="请输入供应商名称" />
        </el-form-item>
        <el-form-item label="售价" prop="price">
          <el-input-number v-model="formData.price" :min="0" :step="10" />
          <span style="margin-left: 8px">元</span>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="formData.status" style="width: 100%">
            <el-option label="售卖中" value="ON_SALE" />
            <el-option label="已下架" value="OFF_SHELF" />
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
import { ElMessage } from 'element-plus'
import { getSeriesList, getSkuList, addSeries, updateSeries } from '@/api/series'

const loading = ref(false)
const submitting = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const skuDialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref(null)
const currentSeries = ref(null)
const skuList = ref([])

const queryForm = reactive({
  keyword: '',
  page: 1,
  pageSize: 9
})

const formData = reactive({
  id: null,
  name: '',
  supplierName: '',
  price: 69,
  status: 'ON_SALE'
})

const formRules = {
  name: [{ required: true, message: '请输入系列名称', trigger: 'blur' }],
  supplierName: [{ required: true, message: '请输入供应商名称', trigger: 'blur' }]
}

const dialogTitle = computed(() => dialogType.value === 'add' ? '新增系列' : '编辑系列')

async function loadData() {
  loading.value = true
  try {
    const res = await getSeriesList(queryForm)
    tableData.value = res.list
    total.value = res.total
  } finally {
    loading.value = false
  }
}

function handleReset() {
  queryForm.keyword = ''
  queryForm.page = 1
  loadData()
}

function handleAdd() {
  dialogType.value = 'add'
  Object.assign(formData, {
    id: null,
    name: '',
    supplierName: '',
    price: 69,
    status: 'ON_SALE'
  })
  dialogVisible.value = true
}

function handleEdit(row) {
  dialogType.value = 'edit'
  Object.assign(formData, row)
  dialogVisible.value = true
}

async function handleViewSku(row) {
  currentSeries.value = row
  skuDialogVisible.value = true
  skuList.value = await getSkuList(row.id)
}

function handleAddSku() {
  ElMessage.info('新增款式功能开发中')
}

function handleSetProbability() {
  ElMessage.info('概率设置功能开发中')
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  submitting.value = true
  try {
    if (dialogType.value === 'add') {
      await addSeries(formData)
      ElMessage.success('新增成功')
    } else {
      await updateSeries(formData.id, formData)
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
.series-card {
  margin-bottom: 20px;
  
  .series-header {
    position: relative;
    margin: -20px -20px 16px;
    
    .series-cover {
      width: 100%;
      height: 180px;
      border-radius: 4px 4px 0 0;
    }
    
    .status-tag {
      position: absolute;
      top: 12px;
      right: 12px;
    }
  }
  
  .series-body {
    .series-name {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
      margin-bottom: 8px;
    }
    
    .series-supplier {
      font-size: 13px;
      color: #909399;
      margin-bottom: 12px;
    }
    
    .series-info {
      display: flex;
      justify-content: space-between;
      font-size: 13px;
      color: #606266;
    }
  }
  
  .series-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: 16px;
    padding-top: 16px;
    border-top: 1px solid #ebeef5;
  }
}

.sku-image {
  width: 50px;
  height: 50px;
  border-radius: 4px;
}

.sku-header {
  display: flex;
  gap: 10px;
  margin-bottom: 16px;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>
