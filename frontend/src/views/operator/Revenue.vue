<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">收益分账</h2>
    </div>

    <el-row :gutter="20" class="mb-20">
      <el-col :span="6">
        <div class="stat-card">
          <p class="stat-label">今日营收</p>
          <p class="stat-value">¥{{ formatMoney(stats.todayRevenue) }}</p>
          <p class="stat-desc">较昨日 <span class="up">↑ 15.2%</span></p>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <p class="stat-label">本周营收</p>
          <p class="stat-value">¥{{ formatMoney(stats.weekRevenue) }}</p>
          <p class="stat-desc">较上周 <span class="up">↑ 8.6%</span></p>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <p class="stat-label">本月营收</p>
          <p class="stat-value">¥{{ formatMoney(stats.monthRevenue) }}</p>
          <p class="stat-desc">较上月 <span class="up">↑ 12.3%</span></p>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <p class="stat-label">今日订单</p>
          <p class="stat-value">{{ stats.todayOrders }}</p>
          <p class="stat-desc">较昨日 <span class="up">↑ 6.8%</span></p>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mb-20">
      <el-col :span="16">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>销售趋势</span>
              <el-radio-group v-model="timeRange" size="small">
                <el-radio-button value="week">近7天</el-radio-button>
                <el-radio-button value="month">近30天</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div ref="chartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header>
            <span>分账比例</span>
          </template>
          <div ref="pieChartRef" class="pie-chart"></div>
          <div class="share-legend">
            <div class="legend-item">
              <span class="dot dot-operator"></span>
              <span>运营方 30%</span>
            </div>
            <div class="legend-item">
              <span class="dot dot-supplier"></span>
              <span>供应商 50%</span>
            </div>
            <div class="legend-item">
              <span class="dot dot-mall"></span>
              <span>商场 20%</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card>
      <template #header>
        <span>分账明细</span>
      </template>
      <el-table :data="shareList" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="period" label="账期" min-width="200" />
        <el-table-column label="总营收" width="140">
          <template #default="{ row }">
            <span class="amount">¥{{ formatMoney(row.totalAmount) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="运营方(30%)" width="140">
          <template #default="{ row }">¥{{ formatMoney(row.operatorShare) }}</template>
        </el-table-column>
        <el-table-column label="供应商(50%)" width="140">
          <template #default="{ row }">¥{{ formatMoney(row.supplierShare) }}</template>
        </el-table-column>
        <el-table-column label="商场(20%)" width="140">
          <template #default="{ row }">¥{{ formatMoney(row.mallShare) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'SETTLED' ? 'success' : 'warning'" size="small">
              {{ row.status === 'SETTLED' ? '已结算' : '待结算' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button type="primary" link size="small">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="queryForm.page"
          v-model:page-size="queryForm.pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadShareList"
          @current-change="loadShareList"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import * as echarts from 'echarts'
import { getRevenueStats, getRevenueShareList } from '@/api/revenue'

const chartRef = ref(null)
const pieChartRef = ref(null)
let chartInstance = null
let pieChartInstance = null

const loading = ref(false)
const timeRange = ref('week')
const stats = ref({
  todayRevenue: 18600,
  weekRevenue: 123700,
  monthRevenue: 268500,
  todayOrders: 270
})
const shareList = ref([])
const total = ref(0)

const queryForm = reactive({
  page: 1,
  pageSize: 10
})

function formatMoney(value) {
  return value.toLocaleString()
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
      axisPointer: { type: 'shadow' }
    },
    legend: {
      data: ['营收', '订单数']
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
        name: '营收(元)'
      },
      {
        type: 'value',
        name: '订单数'
      }
    ],
    series: [
      {
        name: '营收',
        type: 'bar',
        data: data.map(item => item.amount),
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#667eea' },
            { offset: 1, color: '#764ba2' }
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

function initPieChart() {
  if (!pieChartRef.value) return
  pieChartInstance = echarts.init(pieChartRef.value)
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c}% ({d}%)'
    },
    series: [
      {
        type: 'pie',
        radius: ['50%', '75%'],
        center: ['50%', '50%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 6,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: { show: false },
        data: [
          { value: 30, name: '运营方', itemStyle: { color: '#409eff' } },
          { value: 50, name: '供应商', itemStyle: { color: '#67c23a' } },
          { value: 20, name: '商场', itemStyle: { color: '#e6a23c' } }
        ]
      }
    ]
  }
  pieChartInstance.setOption(option)
}

async function loadData() {
  const res = await getRevenueStats()
  stats.value = {
    todayRevenue: res.todayRevenue,
    todayOrders: res.todayOrders,
    weekRevenue: res.weekRevenue,
    monthRevenue: res.monthRevenue,
    trend: res.trend
  }
}

async function loadShareList() {
  loading.value = true
  try {
    const res = await getRevenueShareList(queryForm)
    shareList.value = res.list
    total.value = res.total
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData().then(() => {
    initChart()
    initPieChart()
  })
  loadShareList()
  
  window.addEventListener('resize', () => {
    chartInstance?.resize()
    pieChartInstance?.resize()
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
    font-size: 14px;
    margin-bottom: 10px;
  }
  
  .stat-value {
    font-size: 26px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 8px;
  }
  
  .stat-desc {
    font-size: 12px;
    color: #909399;
    
    .up { color: #67c23a; }
    .down { color: #f56c6c; }
  }
}

.chart-container {
  height: 320px;
}

.pie-chart {
  height: 200px;
}

.share-legend {
  display: flex;
  justify-content: space-around;
  padding-top: 10px;
  
  .legend-item {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 13px;
    color: #606266;
    
    .dot {
      width: 10px;
      height: 10px;
      border-radius: 50%;
      
      &.dot-operator { background: #409eff; }
      &.dot-supplier { background: #67c23a; }
      &.dot-mall { background: #e6a23c; }
    }
  }
}

.amount {
  color: #f56c6c;
  font-weight: 600;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
