<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">收益查看</h2>
      <el-radio-group v-model="timeRange" size="default">
        <el-radio-button value="week">近7天</el-radio-button>
        <el-radio-button value="month">近30天</el-radio-button>
      </el-radio-group>
    </div>

    <el-row :gutter="20" class="mb-20">
      <el-col :span="8">
        <div class="stat-card stat-total">
          <div class="stat-icon">
            <el-icon :size="28"><Money /></el-icon>
          </div>
          <div class="stat-content">
            <p class="stat-label">总收益</p>
            <p class="stat-value">¥{{ formatMoney(totalRevenue) }}</p>
          </div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="stat-card stat-order">
          <div class="stat-icon">
            <el-icon :size="28"><ShoppingBag /></el-icon>
          </div>
          <div class="stat-content">
            <p class="stat-label">总订单</p>
            <p class="stat-value">{{ totalOrders }}</p>
          </div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="stat-card stat-machine">
          <div class="stat-icon">
            <el-icon :size="28"><Monitor /></el-icon>
          </div>
          <div class="stat-content">
            <p class="stat-label">机器数量</p>
            <p class="stat-value">{{ machineCount }}</p>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-card class="mb-20">
      <template #header>
        <span>收益趋势</span>
      </template>
      <div ref="chartRef" class="chart-container"></div>
    </el-card>

    <el-card>
      <template #header>
        <span>分账记录</span>
      </template>
      <el-table :data="shareList" stripe style="width: 100%">
        <el-table-column prop="period" label="账期" min-width="200" />
        <el-table-column label="总销售额" width="140">
          <template #default="{ row }">
            ¥{{ formatMoney(row.totalAmount) }}
          </template>
        </el-table-column>
        <el-table-column label="商场分成(20%)" width="140">
          <template #default="{ row }">
            <span class="amount">¥{{ formatMoney(row.mallAmount) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'SETTLED' ? 'success' : 'warning'" size="small">
              {{ row.status === 'SETTLED' ? '已结算' : '待结算' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="settleTime" label="结算时间" width="170" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import * as echarts from 'echarts'
import { getMallRevenue } from '@/api/revenue'

const chartRef = ref(null)
let chartInstance = null

const timeRange = ref('week')
const totalRevenue = ref(53700)
const totalOrders = ref(780)
const machineCount = ref(3)

const shareList = ref([
  { period: '2024-01-01 ~ 2024-01-15', totalAmount: 268500, mallAmount: 53700, status: 'SETTLED', settleTime: '2024-01-16 10:00:00' },
  { period: '2023-12-16 ~ 2023-12-31', totalAmount: 312000, mallAmount: 62400, status: 'SETTLED', settleTime: '2024-01-01 10:00:00' },
  { period: '2023-12-01 ~ 2023-12-15', totalAmount: 289500, mallAmount: 57900, status: 'SETTLED', settleTime: '2023-12-16 10:00:00' }
])

const trendData = ref([])

function formatMoney(value) {
  return value.toLocaleString()
}

function initChart() {
  if (!chartRef.value) return
  chartInstance = echarts.init(chartRef.value)
  updateChart()
}

function updateChart() {
  const data = trendData.value
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: data.map(item => item.date)
    },
    yAxis: {
      type: 'value',
      name: '收益(元)'
    },
    series: [
      {
        name: '收益',
        type: 'line',
        smooth: true,
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(230, 162, 60, 0.3)' },
            { offset: 1, color: 'rgba(230, 162, 60, 0.05)' }
          ])
        },
        lineStyle: { color: '#e6a23c', width: 3 },
        itemStyle: { color: '#e6a23c' },
        data: data.map(item => item.amount)
      }
    ]
  }
  chartInstance.setOption(option)
}

async function loadData() {
  const res = await getMallRevenue({ timeRange: timeRange.value })
  totalRevenue.value = res.totalRevenue
  totalOrders.value = res.totalOrders
  machineCount.value = res.machineCount
  trendData.value = res.trend
}

watch(timeRange, () => {
  loadData().then(() => updateChart())
})

onMounted(() => {
  loadData().then(() => initChart())
  
  window.addEventListener('resize', () => {
    chartInstance?.resize()
  })
})
</script>

<style scoped lang="scss">
.stat-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.06);
  
  .stat-icon {
    width: 56px;
    height: 56px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
  }
  
  &.stat-total .stat-icon {
    background: linear-gradient(135deg, #f6d365, #fda085);
  }
  
  &.stat-order .stat-icon {
    background: linear-gradient(135deg, #43cea2, #185a9d);
  }
  
  &.stat-machine .stat-icon {
    background: linear-gradient(135deg, #667eea, #764ba2);
  }
  
  .stat-content {
    .stat-label {
      color: #909399;
      font-size: 13px;
      margin-bottom: 6px;
    }
    
    .stat-value {
      font-size: 24px;
      font-weight: 600;
      color: #303133;
    }
  }
}

.chart-container {
  height: 320px;
}

.amount {
  color: #f56c6c;
  font-weight: 600;
}
</style>
