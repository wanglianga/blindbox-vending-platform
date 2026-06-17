<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">我的系列</h2>
    </div>

    <el-row :gutter="20" v-loading="loading">
      <el-col :span="8" v-for="item in seriesList" :key="item.id">
        <el-card class="series-card" shadow="hover">
          <div class="series-header">
            <el-image :src="item.coverImage" class="series-cover" fit="cover" />
            <el-tag :type="item.status === 'ON_SALE' ? 'success' : 'info'" size="small" class="status-tag">
              {{ item.status === 'ON_SALE' ? '售卖中' : '已下架' }}
            </el-tag>
          </div>
          <div class="series-body">
            <h3 class="series-name">{{ item.name }}</h3>
            <div class="series-info">
              <div class="info-item">
                <span class="label">款式数</span>
                <span class="value">{{ item.skuCount }}</span>
              </div>
              <div class="info-item">
                <span class="label">售价</span>
                <span class="value">¥{{ item.price }}</span>
              </div>
              <div class="info-item">
                <span class="label">销量</span>
                <span class="value">{{ item.totalSales }}</span>
              </div>
            </div>
            <div class="series-revenue">
              <span class="label">总销售额</span>
              <span class="amount">¥{{ (item.price * item.totalSales).toLocaleString() }}</span>
            </div>
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getSeriesList } from '@/api/series'

const loading = ref(false)
const seriesList = ref([])
const total = ref(0)

const queryForm = reactive({
  page: 1,
  pageSize: 9
})

async function loadData() {
  loading.value = true
  try {
    const res = await getSeriesList(queryForm)
    seriesList.value = res.list
    total.value = res.total
  } finally {
    loading.value = false
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
      margin-bottom: 12px;
    }
    
    .series-info {
      display: flex;
      justify-content: space-between;
      margin-bottom: 12px;
      padding-bottom: 12px;
      border-bottom: 1px dashed #ebeef5;
      
      .info-item {
        text-align: center;
        
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
    
    .series-revenue {
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      .label {
        font-size: 13px;
        color: #909399;
      }
      
      .amount {
        font-size: 18px;
        font-weight: 600;
        color: #f56c6c;
      }
    }
  }
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>
