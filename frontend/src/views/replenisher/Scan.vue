<template>
  <div class="page-container scan-page">
    <div class="page-header">
      <h2 class="page-title">扫码补货</h2>
    </div>

    <el-card v-if="!currentMachine" class="scan-card">
      <div class="scan-area">
        <div class="scan-icon">
          <el-icon :size="64"><QrCode /></el-icon>
        </div>
        <h3>扫描机器二维码</h3>
        <p>请扫描机器上的二维码开始补货</p>
        <el-button type="primary" size="large" @click="simulateScan">
          模拟扫码
        </el-button>
        <div class="or-divider">
          <span>或</span>
        </div>
        <el-input v-model="machineCode" placeholder="输入机器编号" style="width: 300px" />
        <el-button type="primary" style="margin-top: 10px" @click="searchMachine">
          查询
        </el-button>
      </div>
    </el-card>

    <template v-else>
      <el-card class="machine-info-card mb-20">
        <div class="machine-header">
          <div class="machine-icon">
            <el-icon :size="32"><Monitor /></el-icon>
          </div>
          <div class="machine-detail">
            <h3>{{ currentMachine.name }}</h3>
            <p>{{ currentMachine.location }}</p>
          </div>
          <el-tag type="success">在线</el-tag>
        </div>
        <div class="machine-stats">
          <div class="stat-item">
            <span class="label">总格口</span>
            <span class="value">{{ currentMachine.gridCount }}</span>
          </div>
          <div class="stat-item">
            <span class="label">已占用</span>
            <span class="value filled">{{ filledCount }}</span>
          </div>
          <div class="stat-item">
            <span class="label">空闲</span>
            <span class="value empty">{{ emptyCount }}</span>
          </div>
        </div>
      </el-card>

      <el-card class="mb-20">
        <template #header>
          <div class="card-header">
            <span>格口库存</span>
            <div class="header-actions">
              <el-button type="success" size="small" @click="handleOpenAll">
                <el-icon><Unlock /></el-icon>
                一键开柜
              </el-button>
              <el-button type="primary" size="small" @click="handleComplete">
                <el-icon><Check /></el-icon>
                完成补货
              </el-button>
            </div>
          </div>
        </template>

        <div class="grid-container">
          <div
            v-for="grid in gridList"
            :key="grid.id"
            class="grid-item"
            :class="{ filled: grid.status === 'FILLED', empty: grid.status === 'EMPTY', selected: selectedGrids.includes(grid.id) }"
            @click="toggleGrid(grid)"
          >
            <div class="grid-no">{{ grid.gridNo }}</div>
            <div v-if="grid.status === 'FILLED'" class="grid-sku">
              <el-image :src="grid.skuImage" class="sku-img" fit="cover" />
            </div>
            <div v-else class="grid-empty">
              <el-icon><Plus /></el-icon>
            </div>
            <div class="grid-status">
              {{ grid.status === 'FILLED' ? '有货' : '空' }}
            </div>
          </div>
        </div>
      </el-card>

      <el-card>
        <template #header>
          <span>补货记录</span>
        </template>
        <el-table :data="replenishRecords" size="small">
          <el-table-column prop="gridNo" label="格口" width="80" />
          <el-table-column prop="skuName" label="商品" />
          <el-table-column prop="quantity" label="数量" width="80" align="center" />
          <el-table-column prop="time" label="时间" width="170" />
        </el-table>
      </el-card>
    </template>
  </div>
</template>

<script setup>
import { ref, computed, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getGridList, openGrid, completeReplenish } from '@/api/replenish'
import { mockMachines } from '@/mock'

const machineCode = ref('')
const currentMachine = ref(null)
const gridList = ref([])
const selectedGrids = ref([])
const replenishRecords = ref([
  { gridNo: 'A03', skuName: '基础款A-红', quantity: 1, time: '2024-01-15 15:30:00' },
  { gridNo: 'A06', skuName: '基础款B-蓝', quantity: 1, time: '2024-01-15 15:32:00' },
  { gridNo: 'B04', skuName: '基础款C-绿', quantity: 1, time: '2024-01-15 15:35:00' }
])

const filledCount = computed(() => gridList.value.filter(g => g.status === 'FILLED').length)
const emptyCount = computed(() => gridList.value.filter(g => g.status === 'EMPTY').length)

function simulateScan() {
  currentMachine.value = mockMachines[0]
  loadGrids()
}

async function searchMachine() {
  if (!machineCode.value) {
    ElMessage.warning('请输入机器编号')
    return
  }
  const machine = mockMachines.find(m => m.machineNo === machineCode.value)
  if (machine) {
    currentMachine.value = machine
    loadGrids()
  } else {
    ElMessage.error('未找到该机器')
  }
}

async function loadGrids() {
  gridList.value = await getGridList(currentMachine.value.id)
}

function toggleGrid(grid) {
  if (grid.status === 'FILLED') {
    ElMessage.info('该格口已有货物')
    return
  }
  
  const index = selectedGrids.value.indexOf(grid.id)
  if (index > -1) {
    selectedGrids.value.splice(index, 1)
  } else {
    selectedGrids.value.push(grid.id)
  }
}

async function handleOpenAll() {
  try {
    await ElMessageBox.confirm('确定要打开所有空格口吗？', '提示', {
      type: 'warning'
    })
    ElMessage.success('已打开所有空格口')
  } catch {
  }
}

async function handleComplete() {
  try {
    await ElMessageBox.confirm('确定完成本次补货吗？', '提示', {
      type: 'success'
    })
    await completeReplenish(1, { grids: selectedGrids.value })
    ElMessage.success('补货完成')
    selectedGrids.value = []
  } catch {
  }
}
</script>

<style scoped lang="scss">
.scan-page {
  .scan-card {
    .scan-area {
      text-align: center;
      padding: 40px 0;
      
      .scan-icon {
        width: 120px;
        height: 120px;
        border-radius: 50%;
        background: linear-gradient(135deg, #667eea, #764ba2);
        color: #fff;
        display: flex;
        align-items: center;
        justify-content: center;
        margin: 0 auto 24px;
      }
      
      h3 {
        font-size: 20px;
        color: #303133;
        margin-bottom: 8px;
      }
      
      p {
        color: #909399;
        margin-bottom: 24px;
      }
      
      .or-divider {
        position: relative;
        margin: 20px 0;
        
        &::before {
          content: '';
          position: absolute;
          left: 50%;
          top: 50%;
          transform: translate(-50%, -50%);
          width: 300px;
          height: 1px;
          background: #ebeef5;
        }
        
        span {
          position: relative;
          display: inline-block;
          padding: 0 12px;
          background: #fff;
          color: #909399;
          font-size: 12px;
        }
      }
    }
  }
  
  .machine-info-card {
    .machine-header {
      display: flex;
      align-items: center;
      gap: 16px;
      margin-bottom: 20px;
      
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
      
      .machine-detail {
        flex: 1;
        
        h3 {
          font-size: 18px;
          color: #303133;
          margin-bottom: 4px;
        }
        
        p {
          font-size: 13px;
          color: #909399;
          margin: 0;
        }
      }
    }
    
    .machine-stats {
      display: flex;
      gap: 20px;
      
      .stat-item {
        flex: 1;
        text-align: center;
        padding: 16px;
        background: #f5f7fa;
        border-radius: 8px;
        
        .label {
          display: block;
          font-size: 13px;
          color: #909399;
          margin-bottom: 6px;
        }
        
        .value {
          font-size: 24px;
          font-weight: 600;
          color: #303133;
          
          &.filled { color: #67c23a; }
          &.empty { color: #f56c6c; }
        }
      }
    }
  }
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    .header-actions {
      display: flex;
      gap: 10px;
    }
  }
  
  .grid-container {
    display: grid;
    grid-template-columns: repeat(6, 1fr);
    gap: 12px;
  }
  
  .grid-item {
    border: 2px solid #ebeef5;
    border-radius: 8px;
    padding: 10px;
    text-align: center;
    cursor: pointer;
    transition: all 0.3s;
    
    &:hover {
      border-color: #409eff;
    }
    
    &.filled {
      background: #f0f9eb;
      border-color: #e1f3d8;
    }
    
    &.empty {
      background: #fef0f0;
      border-color: #fde2e2;
    }
    
    &.selected {
      border-color: #409eff;
      box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
    }
    
    .grid-no {
      font-size: 12px;
      color: #909399;
      margin-bottom: 6px;
    }
    
    .grid-sku {
      .sku-img {
        width: 40px;
        height: 40px;
        margin: 0 auto 6px;
        border-radius: 4px;
      }
    }
    
    .grid-empty {
      width: 40px;
      height: 40px;
      margin: 0 auto 6px;
      border-radius: 4px;
      background: #fff;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #909399;
    }
    
    .grid-status {
      font-size: 11px;
      color: #606266;
    }
  }
}
</style>
