<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">款式管理</h2>
      <div class="header-actions">
        <el-select v-model="selectedSeries" placeholder="选择系列" style="width: 200px; margin-right: 10px" @change="handleSeriesChange">
          <el-option v-for="s in seriesList" :key="s.id" :label="s.name" :value="s.id" />
        </el-select>
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增款式
        </el-button>
      </div>
    </div>

    <el-card>
      <el-table :data="skuList" stripe style="width: 100%" v-loading="loading">
        <el-table-column label="图片" width="80">
          <template #default="{ row }">
            <el-image :src="row.image" class="sku-image" fit="cover" />
          </template>
        </el-table-column>
        <el-table-column prop="name" label="款式名称" min-width="180" />
        <el-table-column label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.isHidden ? 'danger' : 'info'" size="small">
              {{ row.isHidden ? '隐藏款' : '普通款' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="price" label="价格" width="100">
          <template #default="{ row }">¥{{ row.price }}</template>
        </el-table-column>
        <el-table-column label="概率" width="120">
          <template #default="{ row }">
            {{ (row.probability * 100).toFixed(1) }}%
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="100" align="center" />
        <el-table-column label="销量" width="100" align="center">
          <template #default="{ row }">
            {{ Math.floor(Math.random() * 500) + 100 }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small">编辑</el-button>
            <el-button type="primary" link size="small">设置概率</el-button>
            <el-button type="danger" link size="small">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="560px">
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="款式名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入款式名称" />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="formData.price" :min="0" :step="10" />
          <span style="margin-left: 8px">元</span>
        </el-form-item>
        <el-form-item label="概率" prop="probability">
          <el-slider v-model="formData.probability" :min="0.001" :max="0.5" :step="0.001" :format-tooltip="formatProb" />
          <div style="text-align: right; color: #909399; font-size: 13px">
            {{ (formData.probability * 100).toFixed(1) }}%
          </div>
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="formData.stock" :min="0" />
        </el-form-item>
        <el-form-item label="隐藏款" prop="isHidden">
          <el-switch v-model="formData.isHidden" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getSeriesList, getSkuList } from '@/api/series'
import { mockSkus } from '@/mock'

const loading = ref(false)
const seriesList = ref([])
const skuList = ref([])
const selectedSeries = ref(null)
const dialogVisible = ref(false)
const formRef = ref(null)

const formData = reactive({
  name: '',
  price: 69,
  probability: 0.1,
  stock: 200,
  isHidden: false
})

const formRules = {
  name: [{ required: true, message: '请输入款式名称', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }]
}

const dialogTitle = computed(() => '新增款式')

function formatProb(val) {
  return (val * 100).toFixed(1) + '%'
}

async function loadSeries() {
  const res = await getSeriesList({ page: 1, pageSize: 100 })
  seriesList.value = res.list
  if (res.list.length > 0 && !selectedSeries.value) {
    selectedSeries.value = res.list[0].id
    loadSkus()
  }
}

async function loadSkus() {
  if (!selectedSeries.value) return
  loading.value = true
  try {
    skuList.value = await getSkuList(selectedSeries.value)
  } finally {
    loading.value = false
  }
}

function handleSeriesChange() {
  loadSkus()
}

function handleAdd() {
  Object.assign(formData, {
    name: '',
    price: 69,
    probability: 0.1,
    stock: 200,
    isHidden: false
  })
  dialogVisible.value = true
}

function handleSubmit() {
  formRef.value.validate((valid) => {
    if (valid) {
      ElMessage.success('新增成功')
      dialogVisible.value = false
    }
  })
}

onMounted(() => {
  loadSeries()
})
</script>

<style scoped lang="scss">
.sku-image {
  width: 50px;
  height: 50px;
  border-radius: 4px;
}

.header-actions {
  display: flex;
  align-items: center;
}
</style>
