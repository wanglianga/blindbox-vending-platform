<template>
  <div class="dashboard">
    <el-row :gutter="20" class="mb-20">
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon icon-blue">
            <el-icon><Monitor /></el-icon>
          </div>
          <div class="stat-info">
            <p class="stat-label">机器总数</p>
            <p class="stat-value">{{ stats.machineCount }}</p>
            <p class="stat-trend">
              <span class="trend-up">↑ 12%</span>
              较上月
            </p>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon icon-green">
            <el-icon><ShoppingBag /></el-icon>
          </div>
          <div class="stat-info">
            <p class="stat-label">今日订单</p>
            <p class="stat-value">{{ stats.todayOrders }}</p>
            <p class="stat-trend">
              <span class="trend-up">↑ 8%</span>
              较昨日
            </p>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon icon-orange">
            <el-icon><Money /></el-icon>
          </div>
          <div class="stat-info">
            <p class="stat-label">今日营收</p>
            <p class="stat-value">¥{{ formatMoney(stats.todayRevenue) }}</p>
            <p class="stat-trend">
              <span class="trend-up">↑ 15%</span>
              较昨日
            </p>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon icon-purple">
            <el-icon><Collection /></el-icon>
          </div>
          <div class="stat-info">
            <p class="stat-label">IP系列</p>
            <p class="stat-value">{{ stats.seriesCount }}</p>
            <p class="stat-trend">
              <span class="trend-up">↑ 3</span>
              本月新增
            </p>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="16">
        <div class="chart-card">
          <div class="chart-header">
            <h3>销售趋势</h3>
            <el-radio-group v-model="timeRange" size="small">
              <el-radio-button value="week">近7天</el-radio-button>
              <el-radio-button value="month">近30天</el-radio-button>
            </el-radio-group>
          </div>
          <div ref="chartRef" class="chart-container"></div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="chart-card">
          <div class="chart-header">
            <h3>机器状态分布</h3>
          </div>
          <div ref="pieChartRef" class="chart-container pie-chart"></div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <el-col :span="12">
        <div class="chart-card">
          <div class="chart-header">
            <h3>热销IP排行</h3>
          </div>
          <el-table :data="topSeries" style="width: 100%">
            <el-table-column label="排名" width="80">
              <template #default="{ $index }">
                <el-tag :type="getRankType($index)" size="small">{{ $index + 1 }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="name" label="系列名称" />
            <el-table-column prop="totalSales" label="销量" width="120" />
            <el-table-column label="销售额" width="120">
              <template #default="{ row }">
                ¥{{ row.price * row.totalSales }}
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-col>
      <el-col :span="12">
        <div class="chart-card">
          <div class="chart-header">
            <h3>最新订单</h3>
          </div>
          <el-table :data="recentOrders" style="width: 100%">
            <el-table-column prop="orderNo" label="订单号" width="180" />
            <el-table-column prop="machineName" label="机器" show-overflow-tooltip />
            <el-table-column label="金额" width="100">
              <template #default="{ row }">
                <span class="amount">¥{{ row.amount }}</span>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)" size="small">{{ getStatusLabel(row.status) }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import * as echarts from 'echarts'
import { getRevenueStats } from '@/api/revenue'
import { getOrderList } from '@/api/order'
import { getSeriesList } from '@/api/series'
import { getMachineList } from '@/api/machine'
import { orderStatusMap } from '@/mock'

const chartRef = ref(null)
const pieChartRef = ref(null)
let chartInstance = null
let pieChartInstance = null

const timeRange = ref('week')
const stats = ref({
  machineCount: 8,
  todayOrders: 270,
  todayRevenue: 18600,
  seriesCount: 6
})

const topSeries = ref([])
const recentOrders = ref([])

function formatMoney(value) {
  return value.toLocaleString()
}

function getRankType(index) {
  if (index === 0) return 'danger'
  if (index === 1) return 'warning'
  if (index === 2) return 'primary'
  return 'info'
}

function getStatusType(status) {
  return orderStatusMap[status]?.type || 'info'
}

function getStatusLabel(status) {
  return orderStatusMap[status]?.label || status
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
      boundaryGap: false,
      data: data.map(item => item.date)
    },
    yAxis: [
      {
        type: 'value',
        name: '销售额(元)',
        position: 'left'
      },
      {
        type: 'value',
        name: '订单数',
        position: 'right'
      }
    ],
    series: [
      {
        name: '销售额',
        type: 'line',
        smooth: true,
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
            { offset: 1, color: 'rgba(64, 158, 255, 0.05)' }
          ])
        },
        lineStyle: { color: '#409eff', width: 2 },
        itemStyle: { color: '#409eff' },
        data: data.map(item => item.amount)
      },
      {
        name: '订单数',
        type: 'line',
        smooth: true,
        yAxisIndex: 1,
        lineStyle: { color: '#67c23a', width: 2 },
        itemStyle: { color: '#67c23a' },
        data: data.map(item => item.orders)
      }
    ]
  }
  chartInstance.setOption(option)
}

function initPieChart() {
  if (!pieChartRef.value) return
  pieChartInstance = echarts.init(pieChartRef.value)
  
  const option = {
    tooltip: {
      trigger: 'item'
    },
    legend: {
      orient: 'vertical',
      right: '5%',
      top: 'center'
    },
    series: [
      {
        name: '机器状态',
        type: 'pie',
        radius: ['50%', '75%'],
        center: ['35%', '50%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 8,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 14,
            fontWeight: 'bold'
          }
        },
        data: [
          { value: 5, name: '在线', itemStyle: { color: '#67c23a' } },
          { value: 2, name: '离线', itemStyle: { color: '#909399' } },
          { value: 1, name: '故障', itemStyle: { color: '#f56c6c' } }
        ]
      }
    ]
  }
  pieChartInstance.setOption(option)
}

async function loadData() {
  const [revenueRes, ordersRes, seriesRes, machinesRes] = await Promise.all([
    getRevenueStats(),
    getOrderList({ page: 1, pageSize: 5 }),
    getSeriesList({ page: 1, pageSize: 5 }),
    getMachineList()
  ])
  
  stats.value = {
    machineCount: machinesRes.total,
    todayOrders: revenueRes.todayOrders,
    todayRevenue: revenueRes.todayRevenue,
    seriesCount: seriesRes.total,
    trend: revenueRes.trend
  }
  
  topSeries.value = seriesRes.list.sort((a, b) => b.totalSales - a.totalSales).slice(0, 5)
  recentOrders.value = ordersRes.list
}

onMounted(() => {
  loadData().then(() => {
    initChart()
    initPieChart()
  })
  
  window.addEventListener('resize', () => {
    chartInstance?.resize()
    pieChartInstance?.resize()
  })
})
</script>

<style scoped lang="scss">
.dashboard {
  .stat-card {
    background: #fff;
    border-radius: 8px;
    padding: 20px;
    display: flex;
    align-items: center;
    gap: 16px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.06);
    
    .stat-icon {
      width: 60px;
      height: 60px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 28px;
      color: #fff;
      
      &.icon-blue { background: linear-gradient(135deg, #667eea, #764ba2); }
      &.icon-green { background: linear-gradient(135deg, #43cea2, #185a9d); }
      &.icon-orange { background: linear-gradient(135deg, #f6d365, #fda085); }
      &.icon-purple { background: linear-gradient(135deg, #a18cd1, #fbc2eb); }
    }
    
    .stat-info {
      flex: 1;
      
      .stat-label {
        color: #909399;
        font-size: 14px;
        margin-bottom: 8px;
      }
      
      .stat-value {
        font-size: 24px;
        font-weight: 600;
        color: #303133;
        margin-bottom: 6px;
      }
      
      .stat-trend {
        font-size: 12px;
        color: #909399;
        
        .trend-up { color: #67c23a; margin-right: 4px; }
        .trend-down { color: #f56c6c; margin-right: 4px; }
      }
    }
  }
  
  .chart-card {
    background: #fff;
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.06);
    
    .chart-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;
      
      h3 {
        font-size: 16px;
        font-weight: 600;
        color: #303133;
        margin: 0;
      }
    }
    
    .chart-container {
      height: 320px;
      
      &.pie-chart {
        height: 280px;
      }
    }
  }
  
  .amount {
    color: #f56c6c;
    font-weight: 600;
  }
}
</style>
