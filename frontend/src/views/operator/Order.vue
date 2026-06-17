<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">订单管理</h2>
      <el-button type="primary" @click="handleExport">
        <el-icon><Download /></el-icon>
        导出订单
      </el-button>
    </div>

    <el-card class="mb-20">
      <el-form :inline="true" :model="queryForm" @submit.prevent>
        <el-form-item label="关键词">
          <el-input v-model="queryForm.keyword" placeholder="订单号/用户名" clearable style="width: 220px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="全部" clearable style="width: 140px">
            <el-option label="待支付" value="PENDING" />
            <el-option label="已完成" value="COMPLETED" />
            <el-option label="退款中" value="REFUNDING" />
            <el-option label="已退款" value="REFUNDED" />
          </el-select>
        </el-form-item>
        <el-form-item label="下单时间">
          <el-date-picker
            v-model="queryForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            style="width: 260px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <el-table :data="tableData" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="orderNo" label="订单号" width="200" />
        <el-table-column prop="machineName" label="机器" min-width="140" show-overflow-tooltip />
        <el-table-column prop="seriesName" label="系列" min-width="140" show-overflow-tooltip />
        <el-table-column prop="skuName" label="款式" min-width="140" show-overflow-tooltip />
        <el-table-column label="金额" width="100">
          <template #default="{ row }">
            <span class="amount">¥{{ row.amount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="payMethod" label="支付方式" width="100">
          <template #default="{ row }">
            {{ row.payMethod === 'WECHAT' ? '微信' : '支付宝' }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">{{ getStatusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="userName" label="用户" width="100" />
        <el-table-column prop="userPhone" label="手机号" width="140" />
        <el-table-column prop="payTime" label="支付时间" width="170" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="queryForm.page"
          v-model:page-size="queryForm.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>

    <el-dialog v-model="detailVisible" title="订单详情" width="600px">
      <div v-if="currentOrder" class="order-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单号">{{ currentOrder.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(currentOrder.status)">{{ getStatusLabel(currentOrder.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="机器">{{ currentOrder.machineName }}</el-descriptions-item>
          <el-descriptions-item label="支付时间">{{ currentOrder.payTime }}</el-descriptions-item>
          <el-descriptions-item label="系列">{{ currentOrder.seriesName }}</el-descriptions-item>
          <el-descriptions-item label="款式">{{ currentOrder.skuName }}</el-descriptions-item>
          <el-descriptions-item label="金额">
            <span class="amount">¥{{ currentOrder.amount }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="支付方式">
            {{ currentOrder.payMethod === 'WECHAT' ? '微信支付' : '支付宝' }}
          </el-descriptions-item>
          <el-descriptions-item label="用户">{{ currentOrder.userName }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ currentOrder.userPhone }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getOrderList, getOrderDetail, exportOrders } from '@/api/order'
import { orderStatusMap } from '@/mock'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const detailVisible = ref(false)
const currentOrder = ref(null)

const queryForm = reactive({
  keyword: '',
  status: '',
  dateRange: [],
  page: 1,
  pageSize: 10
})

function getStatusType(status) {
  return orderStatusMap[status]?.type || 'info'
}

function getStatusLabel(status) {
  return orderStatusMap[status]?.label || status
}

async function loadData() {
  loading.value = true
  try {
    const res = await getOrderList(queryForm)
    tableData.value = res.list
    total.value = res.total
  } finally {
    loading.value = false
  }
}

function handleReset() {
  queryForm.keyword = ''
  queryForm.status = ''
  queryForm.dateRange = []
  queryForm.page = 1
  loadData()
}

async function handleDetail(row) {
  currentOrder.value = await getOrderDetail(row.id)
  detailVisible.value = true
}

async function handleExport() {
  try {
    await exportOrders(queryForm)
    ElMessage.success('导出成功')
  } catch {
    ElMessage.error('导出失败')
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.amount {
  color: #f56c6c;
  font-weight: 600;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.order-detail {
  .amount {
    font-size: 18px;
  }
}
</style>
