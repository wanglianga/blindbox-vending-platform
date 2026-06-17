<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">收益统计</h2>
      <el-radio-group v-model="timeRange" size="default">
        <el-radio-button value="week">近7天</el-radio-button>
        <el-radio-button value="month">近30天</el-radio-button>
        <el-radio-button value="year">今年</el-radio-button>
      </el-radio-group>
    </div>

    <el-row :gutter="20" class="mb-20">
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon icon-green">
            <el-icon><Money /></el-icon>
          </div>
          <div class="stat-info">
            <p class="stat-label">总收益</p>
            <p class="stat-value">¥{{ formatMoney(stats.totalRevenue) }}</p>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon icon-blue">
            <el-icon><ShoppingBag /></el-icon>
          </div>
          <div class="stat-info">
            <p class="stat-label">总订单</p>
            <p class="stat-value">{{ stats.totalOrders }}</p>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon icon-orange">
            <el-icon><Wallet /></el-icon>
          </div>
          <div class="stat-info">
            <p class="stat-label">已结算</p>
            <p class="stat-value">¥{{ formatMoney(stats.settlementAmount) }}</p>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon icon-purple">
            <el-icon><Timer /></el-icon>
          </div>
          <div class="stat-info">
            <p class="stat-label">待结算</p>
            <p class="stat-value">¥{{ formatMoney(stats.pendingAmount) }}</p>
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

    <el-row :gutter="20">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>系列收益排行</span>
          </template>
          <el-table :data="seriesRank" style="width: 100%">
            <el-table-column label="排名" width="80">
              <template #default="{ $index }">
                <el-tag :type="getRankType($index)" size="small">{{ $index + 1 }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="name" label="系列名称" />
            <el-table-column prop="sales" label="销量" width="100" align="center" />
            <el-table-column label="收益" width="140">
              <template #default="{ row }">
                <span class="amount">¥{{ formatMoney(row.revenue) }}</span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>结算记录</span>
          </template>
          <el-table :data="settlementList" style="width: 100%">
            <el-table-column prop="period" label="账期" min-width="160" />
            <el-table-column label="金额" width="140">
              <template #default="{ row }">
                <span class="amount">¥{{ formatMoney(row.amount) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === 'COMPLETED' ? 'success' : 'warning'" size="small">
                  {{ row.status === 'COMPLETED' ? '已到账' : '待结算' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import * as echarts from 'echarts'
import { getSupplierRevenue } from '@/api/revenue'

const chartRef = ref(null)
let chartInstance = null

const timeRange = ref('week')
const stats = ref({
  totalRevenue: 134250,
  totalOrders: 1950,
  settlementAmount: 112000,
  pendingAmount: 22250
})

const seriesRank = ref([
  { name: 'Molly星座系列', sales: 820, revenue: 32390 },
  { name: 'Dimoo太空系列', sales: 650, revenue: 28925 },
  { name: '泡泡玛特经典系列', sales: 540, revenue: 18630 },
  { name: 'Labubu森林系列', sales: 380, revenue: 18810 },
  { name: 'Hirono小野系列', sales: 320, revenue: 11040 }
])

const settlementList = ref([
  { period: '2024-01-01 ~ 2024-01-15', amount: 67125, status: 'PENDING' },
  { period: '2023-12-16 ~ 2023-12-31', amount: 78000, status: 'COMPLETED' },
  { period: '2023-12-01 ~ 2023-12-15', amount: 72375, status: 'COMPLETED' }
])

function formatMoney(value) {
  return value.toLocaleString()
}

function getRankType(index) {
  if (index === 0) return 'danger'
  if (index === 1) return 'warning'
  if (index === 2) return 'primary'
  return 'info'
}

function initChart() {
  if (!chartRef.value) return
  chartInstance = echarts.init(chartRef.value)
  updateChart()
}

function updateChart() {
  const data = stats.value.trend || []
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'cross' }
    },
    legend: {
      data: ['收益', '订单数']
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
    yAxis: [
      {
        type: 'value',
        name: '收益(元)'
      },
      {
        type: 'value',
        name: '订单数'
      }
    ],
    series: [
      {
        name: '收益',
        type: 'line',
        smooth: true,
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(103, 194, 58, 0.3)' },
            { offset: 1, color: 'rgba(103, 194, 58, 0.05)' }
          ])
        },
        lineStyle: { color: '#67c23a', width: 3 },
        itemStyle: { color: '#67c23a' },
        data: data.map(item => item.amount)
      },
      {
        name: '订单数',
        type: 'line',
        smooth: true,
        yAxisIndex: 1,
        lineStyle: { color: '#409eff', width: 2 },
        itemStyle: { color: '#409eff' },
        data: data.map(item => item.orders)
      }
    ]
  }
  chartInstance.setOption(option)
}

async function loadData() {
  const res = await getSupplierRevenue({ timeRange: timeRange.value })
  stats.value = {
    totalRevenue: res.totalRevenue,
    totalOrders: res.totalOrders,
    settlementAmount: res.settlementAmount,
    pendingAmount: res.pendingAmount,
    trend: res.trend
  }
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
    font-size: 24px;
    color: #fff;
    
    &.icon-blue { background: linear-gradient(135deg, #667eea, #764ba2); }
    &.icon-green { background: linear-gradient(135deg, #43cea2, #185a9d); }
    &.icon-orange { background: linear-gradient(135deg, #f6d365, #fda085); }
    &.icon-purple { background: linear-gradient(135deg, #a18cd1, #fbc2eb); }
  }
  
  .stat-info {
    .stat-label {
      color: #909399;
      font-size: 13px;
      margin-bottom: 6px;
    }
    
    .stat-value {
      font-size: 22px;
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
