<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">补货记录</h2>
    </div>

    <el-card class="mb-20">
      <el-form :inline="true" :model="queryForm" @submit.prevent>
        <el-form-item label="机器">
          <el-input v-model="queryForm.keyword" placeholder="机器名称/编号" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="时间">
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
      <el-table :data="recordList" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="recordNo" label="记录编号" width="160" />
        <el-table-column prop="machineName" label="机器名称" min-width="160" />
        <el-table-column prop="machineAddress" label="位置" min-width="180" show-overflow-tooltip />
        <el-table-column prop="replenishCount" label="补货数量" width="100" align="center" />
        <el-table-column prop="skuCount" label="SKU种类" width="100" align="center" />
        <el-table-column label="耗时" width="100" align="center">
          <template #default="{ row }">
            {{ row.duration }}分钟
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="170" />
        <el-table-column prop="endTime" label="结束时间" width="170" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleDetail(row)">详情</el-button>
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
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>

    <el-dialog v-model="detailVisible" title="补货详情" width="600px">
      <div v-if="currentRecord">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="记录编号">{{ currentRecord.recordNo }}</el-descriptions-item>
          <el-descriptions-item label="机器">{{ currentRecord.machineName }}</el-descriptions-item>
          <el-descriptions-item label="补货数量">{{ currentRecord.replenishCount }}</el-descriptions-item>
          <el-descriptions-item label="SKU种类">{{ currentRecord.skuCount }}</el-descriptions-item>
          <el-descriptions-item label="开始时间">{{ currentRecord.startTime }}</el-descriptions-item>
          <el-descriptions-item label="结束时间">{{ currentRecord.endTime }}</el-descriptions-item>
        </el-descriptions>
        
        <h4 style="margin: 20px 0 10px">补货明细</h4>
        <el-table :data="currentRecord.details" size="small" border>
          <el-table-column prop="skuName" label="商品" />
          <el-table-column prop="quantity" label="数量" width="100" align="center" />
          <el-table-column prop="gridNos" label="格口" />
        </el-table>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const recordList = ref([])
const total = ref(0)
const detailVisible = ref(false)
const currentRecord = ref(null)

const queryForm = reactive({
  keyword: '',
  dateRange: [],
  page: 1,
  pageSize: 10
})

const mockRecords = [
  { id: 1, recordNo: 'RR20240115001', machineName: '万达广场一号机', machineAddress: '万达广场1层东门', replenishCount: 25, skuCount: 8, duration: 35, startTime: '2024-01-15 10:00:00', endTime: '2024-01-15 10:35:00' },
  { id: 2, recordNo: 'RR20240115002', machineName: '朝阳大悦城店', machineAddress: '朝阳大悦城B1层', replenishCount: 18, skuCount: 6, duration: 28, startTime: '2024-01-15 14:00:00', endTime: '2024-01-15 14:28:00' },
  { id: 3, recordNo: 'RR20240114003', machineName: '合生汇店', machineAddress: '合生汇购物中心2层', replenishCount: 35, skuCount: 10, duration: 45, startTime: '2024-01-14 09:30:00', endTime: '2024-01-14 10:15:00' },
  { id: 4, recordNo: 'RR20240114004', machineName: '西单店', machineAddress: '西单大悦城4层', replenishCount: 12, skuCount: 5, duration: 20, startTime: '2024-01-14 15:00:00', endTime: '2024-01-14 15:20:00' },
  { id: 5, recordNo: 'RR20240114005', machineName: '王府井店', machineAddress: '王府井APM 1层', replenishCount: 22, skuCount: 7, duration: 32, startTime: '2024-01-14 11:00:00', endTime: '2024-01-14 11:32:00' }
]

function loadData() {
  loading.value = true
  setTimeout(() => {
    recordList.value = mockRecords
    total.value = mockRecords.length
    loading.value = false
  }, 300)
}

function handleReset() {
  queryForm.keyword = ''
  queryForm.dateRange = []
  queryForm.page = 1
  loadData()
}

function handleDetail(row) {
  currentRecord.value = {
    ...row,
    details: [
      { skuName: '基础款A-红', quantity: 3, gridNos: 'A02, A03, B01' },
      { skuName: '基础款B-蓝', quantity: 3, gridNos: 'A04, A05, B02' },
      { skuName: '基础款C-绿', quantity: 3, gridNos: 'A06, B03, B04' },
      { skuName: '基础款D-黄', quantity: 3, gridNos: 'B05, B06, C01' },
      { skuName: '基础款E-紫', quantity: 3, gridNos: 'C02, C03, C04' }
    ]
  }
  detailVisible.value = true
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>
