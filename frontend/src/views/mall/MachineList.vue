<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">机器列表</h2>
      <el-button type="primary" @click="handleRefresh">
        <el-icon><Refresh /></el-icon>
        刷新
      </el-button>
    </div>

    <el-row :gutter="20" v-loading="loading">
      <el-col :span="8" v-for="item in machineList" :key="item.id">
        <el-card class="machine-card" shadow="hover">
          <div class="machine-header">
            <div class="machine-icon">
              <el-icon :size="32"><Monitor /></el-icon>
            </div>
            <el-tag :type="getStatusType(item.status)" size="small" effect="light">
              {{ getStatusLabel(item.status) }}
            </el-tag>
          </div>
          <div class="machine-body">
            <h3 class="machine-name">{{ item.name }}</h3>
            <p class="machine-no">编号：{{ item.machineNo }}</p>
            <p class="machine-location">
              <el-icon><Location /></el-icon>
              {{ item.location }}
            </p>
            <div class="machine-stats">
              <div class="stat-item">
                <span class="label">格口</span>
                <span class="value">{{ item.gridCount }}</span>
              </div>
              <div class="stat-item">
                <span class="label">SKU</span>
                <span class="value">{{ item.skuCount }}</span>
              </div>
              <div class="stat-item">
                <span class="label">今日订单</span>
                <span class="value">{{ Math.floor(Math.random() * 50) + 10 }}</span>
              </div>
            </div>
          </div>
          <div class="machine-footer">
            <span class="last-online">最后在线：{{ item.lastOnline }}</span>
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
import { ElMessage } from 'element-plus'
import { getMallMachineList } from '@/api/mall'
import { statusMap } from '@/mock'

const loading = ref(false)
const machineList = ref([])
const total = ref(0)

const queryForm = reactive({
  page: 1,
  pageSize: 9
})

function getStatusType(status) {
  return statusMap[status]?.type || 'info'
}

function getStatusLabel(status) {
  return statusMap[status]?.label || status
}

async function loadData() {
  loading.value = true
  try {
    const res = await getMallMachineList(1, queryForm)
    machineList.value = res.list
    total.value = res.total
  } finally {
    loading.value = false
  }
}

function handleRefresh() {
  loadData()
  ElMessage.success('刷新成功')
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.machine-card {
  margin-bottom: 20px;
  
  .machine-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 16px;
    
    .machine-icon {
      width: 56px;
      height: 56px;
      border-radius: 12px;
      background: linear-gradient(135deg, #667eea, #764ba2);
      color: #fff;
      display: flex;
      align-items: center;
      justify-content: center;
    }
  }
  
  .machine-body {
    .machine-name {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
      margin-bottom: 6px;
    }
    
    .machine-no {
      font-size: 13px;
      color: #909399;
      margin-bottom: 10px;
    }
    
    .machine-location {
      display: flex;
      align-items: center;
      gap: 4px;
      font-size: 13px;
      color: #606266;
      margin-bottom: 16px;
      
      .el-icon {
        color: #909399;
        font-size: 14px;
      }
    }
    
    .machine-stats {
      display: flex;
      justify-content: space-between;
      padding: 12px 0;
      border-top: 1px solid #ebeef5;
      
      .stat-item {
        text-align: center;
        flex: 1;
        
        .label {
          display: block;
          font-size: 12px;
          color: #909399;
          margin-bottom: 4px;
        }
        
        .value {
          font-size: 18px;
          font-weight: 600;
          color: #303133;
        }
      }
    }
  }
  
  .machine-footer {
    padding-top: 12px;
    border-top: 1px dashed #ebeef5;
    
    .last-online {
      font-size: 12px;
      color: #909399;
    }
  }
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>
