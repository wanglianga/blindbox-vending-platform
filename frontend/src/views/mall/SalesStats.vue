<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">销售统计</h2>
      <el-radio-group v-model="timeRange" size="default">
        <el-radio-button value="week">近7天</el-radio-button>
        <el-radio-button value="month">近30天</el-radio-button>
      </el-radio-group>
    </div>

    <el-row :gutter="20" class="mb-20">
      <el-col :span="6">
        <div class="stat-card">
          <p class="stat-label">总销售额</p>
          <p class="stat-value">¥{{ formatMoney(stats.totalSales) }}</p>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <p class="stat-label">总订单数</p>
          <p class="stat-value">{{ stats.totalOrders }}</p>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <p class="stat-label">客单价</p>
          <p class="stat-value">¥{{ stats.avgOrderValue }}</p>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <p class="stat-label">转化率</p>
          <p class="stat-value">{{ (stats.conversionRate * 100).toFixed(1) }}%</p>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="16">
        <el-card class="mb-20">
          <template #header>
            <span>销售趋势</span>
          </template>
          <div ref="chartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="mb-20">
          <template #header>
            <span>机器销售排行</span>
          </template>
          <div class="rank-list">
            <div class="rank-item" v-for="(item, index) in topMachines" :key="index">
              <div class="rank-left">
                <el-tag :type="getRankType(index)" size="small" class="rank-num">{{ index + 1 }}</el-tag>
                <span class="rank-name">{{ item.name }}</span>
              </div>
              <div class="rank-right">
                <span class="rank-amount">¥{{ formatMoney(item.sales) }}</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card>
      <template #header>
        <span>热销IP排行</span>
      </template>
      <div ref="barChartRef" class="bar-chart-container"></div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import * as echarts from 'echarts'
import { getMallSalesStats } from '@/api/mall'

const chartRef = ref(null)
const barChartRef = ref(null)
let chartInstance = null
let barChartInstance = null

const timeRange = ref('week')
const stats = ref({
  totalSales: 53700,
  totalOrders: 780,
  avgOrderValue: 68.8,
  conversionRate: 0.15
})

const topMachines = ref([
  { name: '万达广场一号机', sales: 22000, orders: 320 },
  { name: '朝阳大悦城店', sales: 18500, orders: 270 },
  { name: '合生汇店', sales: 13200, orders: 190 }
])

const topSeries = ref([
  { name: '泡泡玛特经典系列', sales: 15000 },
  { name: 'Molly星座系列', sales: 13500 },
  { name: 'Dimoo太空系列', sales: 11000 },
  { name: 'Labubu森林系列', sales: 8500 },
  { name: 'Hirono小野系列', sales: 5700 }
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
  const data = stats.value.salesTrend || []
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    legend: {
      data: ['销售额', '订单数']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: data.map(item => item.date)
    },
    yAxis: [
      {
        type: 'value',
        name: '销售额(元)'
      },
      {
        type: 'value',
        name: '订单数'
      }
    ],
    series: [
      {
        name: '销售额',
        type: 'bar',
        data: data.map(item => item.amount),
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#409eff' },
            { offset: 1, color: '#667eea' }
          ]),
          borderRadius: [4, 4, 0, 0]
        }
      },
      {
        name: '订单数',
        type: 'line',
        yAxisIndex: 1,
        smooth: true,
        data: data.map(item => item.orders),
        itemStyle: { color: '#67c23a' },
        lineStyle: { width: 3 }
      }
    ]
  }
  chartInstance.setOption(option)
}

function initBarChart() {
  if (!barChartRef.value) return
  barChartInstance = echarts.init(barChartRef.value)
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'value'
    },
    yAxis: {
      type: 'category',
      data: topSeries.value.map(item => item.name).reverse()
    },
    series: [
      {
        type: 'bar',
        data: topSeries.value.map(item => item.sales).reverse(),
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
            { offset: 0, color: '#667eea' },
            { offset: 1, color: '#764ba2' }
          ]),
          borderRadius: [0, 4, 4, 0]
        },
        label: {
          show: true,
          position: 'right',
          formatter: '¥{c}'
        }
      }
    ]
  }
  barChartInstance.setOption(option)
}

async function loadData() {
  const res = await getMallSalesStats(1)
  stats.value = {
    totalSales: res.totalSales,
    totalOrders: res.totalOrders,
    avgOrderValue: res.avgOrderValue,
    conversionRate: res.conversionRate,
    salesTrend: res.salesTrend
  }
  topMachines.value = res.topMachines
}

watch(timeRange, () => {
  loadData().then(() => {
    updateChart()
  })
})

onMounted(() => {
  loadData().then(() => {
    initChart()
    initBarChart()
  })
  
  window.addEventListener('resize', () => {
    chartInstance?.resize()
    barChartInstance?.resize()
  })
})
</script>

<style scoped lang="scss">
.stat-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.06);
  
  .stat-label {
    color: #909399;
    font-size: 13px;
    margin-bottom: 8px;
  }
  
  .stat-value {
    font-size: 24px;
    font-weight: 600;
    color: #303133;
  }
}

.chart-container {
  height: 320px;
}

.bar-chart-container {
  height: 280px;
}

.rank-list {
  .rank-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 12px 0;
    border-bottom: 1px solid #f5f7fa;
    
    &:last-child {
      border-bottom: none;
    }
    
    .rank-left {
      display: flex;
      align-items: center;
      gap: 10px;
      
      .rank-num {
        width: 24px;
        text-align: center;
      }
      
      .rank-name {
        font-size: 14px;
        color: #303133;
      }
    }
    
    .rank-right {
      .rank-amount {
        font-size: 16px;
        font-weight: 600;
        color: #f56c6c;
      }
    }
  }
}
</style>
