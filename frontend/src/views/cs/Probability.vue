<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">概率查询</h2>
    </div>

    <el-card class="mb-20">
      <el-form :inline="true" :model="queryForm" @submit.prevent>
        <el-form-item label="选择系列">
          <el-select v-model="queryForm.seriesId" placeholder="请选择IP系列" style="width: 260px" @change="handleSeriesChange">
            <el-option v-for="s in seriesList" :key="s.id" :label="s.name" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-row :gutter="20" v-if="seriesInfo">
      <el-col :span="8">
        <el-card class="series-card">
          <el-image :src="seriesInfo.coverImage" class="series-cover" fit="cover" />
          <div class="series-info">
            <h3>{{ seriesInfo.name }}</h3>
            <p class="supplier">供应商：{{ seriesInfo.supplierName }}</p>
            <div class="stats">
              <div>
                <span class="label">售价</span>
                <span class="value">¥{{ seriesInfo.price }}</span>
              </div>
              <div>
                <span class="label">款式</span>
                <span class="value">{{ seriesInfo.skuCount }}款</span>
              </div>
              <div>
                <span class="label">总销量</span>
                <span class="value">{{ seriesInfo.totalSales }}</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>款式概率详情</span>
              <span class="prob-tip">
                <el-icon><InfoFilled /></el-icon>
                概率总和：{{ totalProb }}%
              </span>
            </div>
          </template>

          <el-table :data="skuList" stripe style="width: 100%" v-loading="loading">
            <el-table-column label="排名" width="80" align="center">
              <template #default="{ $index }">
                <span class="rank">{{ $index + 1 }}</span>
              </template>
            </el-table-column>
            <el-table-column label="图片" width="80">
              <template #default="{ row }">
                <el-image :src="row.image" class="sku-image" fit="cover" />
              </template>
            </el-table-column>
            <el-table-column prop="name" label="款式名称" min-width="180">
              <template #default="{ row }">
                <span>{{ row.name }}</span>
                <el-tag v-if="row.isHidden" type="danger" size="small" style="margin-left: 8px">隐藏款</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="price" label="价格" width="100">
              <template #default="{ row }">¥{{ row.price }}</template>
            </el-table-column>
            <el-table-column label="概率" width="150">
              <template #default="{ row }">
                <div class="prob-bar">
                  <div class="prob-fill" :style="{ width: (row.probability * 100 * 3) + 'px' }"></div>
                  <span class="prob-text">{{ (row.probability * 100).toFixed(2) }}%</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="stock" label="库存" width="100" align="center" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="mt-20" v-if="auditLogs.length > 0">
      <template #header>
        <span>概率变更审计日志</span>
      </template>
      <el-table :data="auditLogs" size="small" style="width: 100%">
        <el-table-column prop="time" label="时间" width="170" />
        <el-table-column prop="seriesName" label="系列" width="160" />
        <el-table-column prop="operator" label="操作人" width="120" />
        <el-table-column prop="action" label="操作" width="120" />
        <el-table-column prop="before" label="变更前" />
        <el-table-column prop="after" label="变更后" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { getSeriesList, getSkuList } from '@/api/series'
import { getProbabilityAuditLog } from '@/api/cs'

const loading = ref(false)
const seriesList = ref([])
const skuList = ref([])
const seriesInfo = ref(null)
const auditLogs = ref([])

const queryForm = reactive({
  seriesId: null
})

const totalProb = computed(() => {
  const sum = skuList.value.reduce((acc, item) => acc + item.probability, 0)
  return (sum * 100).toFixed(2)
})

async function loadSeries() {
  const res = await getSeriesList({ page: 1, pageSize: 100 })
  seriesList.value = res.list
  if (res.list.length > 0) {
    queryForm.seriesId = res.list[0].id
    loadData()
  }
}

async function loadData() {
  if (!queryForm.seriesId) return
  
  loading.value = true
  try {
    const [seriesRes, skuRes, auditRes] = await Promise.all([
      getSeriesList({ page: 1, pageSize: 100 }),
      getSkuList(queryForm.seriesId),
      getProbabilityAuditLog({ seriesId: queryForm.seriesId })
    ])
    
    seriesInfo.value = seriesRes.list.find(s => s.id === queryForm.seriesId)
    skuList.value = skuRes.sort((a, b) => b.probability - a.probability)
    auditLogs.value = auditRes.list
  } finally {
    loading.value = false
  }
}

function handleSeriesChange() {
  loadData()
}

onMounted(() => {
  loadSeries()
})
</script>

<style scoped lang="scss">
.series-card {
  .series-cover {
    width: 100%;
    height: 200px;
    border-radius: 8px;
    margin-bottom: 16px;
  }
  
  .series-info {
    h3 {
      font-size: 18px;
      font-weight: 600;
      color: #303133;
      margin-bottom: 8px;
    }
    
    .supplier {
      font-size: 13px;
      color: #909399;
      margin-bottom: 16px;
    }
    
    .stats {
      display: flex;
      justify-content: space-between;
      
      div {
        text-align: center;
        flex: 1;
        
        .label {
          display: block;
          font-size: 12px;
          color: #909399;
          margin-bottom: 4px;
        }
        
        .value {
          font-size: 16px;
          font-weight: 600;
          color: #303133;
        }
      }
    }
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  
  .prob-tip {
    font-size: 13px;
    color: #909399;
    display: flex;
    align-items: center;
    gap: 4px;
  }
}

.sku-image {
  width: 50px;
  height: 50px;
  border-radius: 4px;
}

.rank {
  display: inline-block;
  width: 24px;
  height: 24px;
  line-height: 24px;
  text-align: center;
  background: #f5f7fa;
  border-radius: 50%;
  font-size: 12px;
  color: #606266;
}

.prob-bar {
  display: flex;
  align-items: center;
  gap: 8px;
  
  .prob-fill {
    height: 8px;
    background: linear-gradient(90deg, #667eea, #764ba2);
    border-radius: 4px;
    min-width: 4px;
  }
  
  .prob-text {
    font-size: 12px;
    color: #606266;
    font-weight: 600;
  }
}

.mt-20 {
  margin-top: 20px;
}
</style>
