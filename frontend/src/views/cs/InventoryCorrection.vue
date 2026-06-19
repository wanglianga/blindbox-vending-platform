<template>
  <div class="inventory-correction">
    <el-card class="search-card">
      <el-form :inline="true" :model="queryForm" class="search-form">
      <el-form-item label="修正单号">
        <el-input v-model="queryForm.correctionNo" placeholder="请输入修正单号" clearable />
      </el-form-item>
      <el-form-item label="机器编号">
        <el-input v-model="queryForm.machineCode" placeholder="请输入机器编号" clearable />
      </el-form-item>
      <el-form-item label="卡货处理单号">
        <el-input v-model="queryForm.stuckRecordNo" placeholder="请输入卡货处理单号" clearable />
      </el-form-item>
      <el-form-item label="修正类型">
        <el-select v-model="queryForm.correctionType" placeholder="请选择" clearable>
          <el-option label="反向修正" value="REVERSE_CORRECTION" />
          <el-option label="盘点修正" value="INVENTORY_CHECK" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryForm.status" placeholder="请选择" clearable>
          <el-option label="待确认" value="PENDING" />
          <el-option label="已确认" value="CONFIRMED" />
          <el-option label="已取消" value="CANCELLED" />
        </el-select>
      </el-form-item>
      <el-form-item label="操作人">
        <el-input v-model="queryForm.operatorName" placeholder="请输入操作人" clearable />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button @click="resetForm">重置</el-button>
      </el-form-item>
    </el-form>
    </el-card>

    <el-card class="table-card">
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="correctionNo" label="修正单号" width="160" />
        <el-table-column prop="stuckRecordNo" label="卡货处理单号" width="160" />
        <el-table-column prop="machineCode" label="机器编号" width="120" />
        <el-table-column prop="machineName" label="机器名称" width="150" />
        <el-table-column prop="gridNo" label="格口号" width="80" />
        <el-table-column prop="skuName" label="商品名称" width="200" />
        <el-table-column prop="beforeInventory" label="修正前库存" width="120" />
        <el-table-column prop="correctionQty" label="修正数量" width="100">
          <template #default="{ row }">
            <span :class="row.correctionQty > 0 ? 'text-success' : 'text-danger'">
              {{ row.correctionQty > 0 ? '+' : '' }}{{ row.correctionQty }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="afterInventory" label="修正后库存" width="120" />
        <el-table-column prop="correctionRevenue" label="收益修正" width="120">
          <template #default="{ row }">
            <span :class="row.correctionRevenue < 0 ? 'text-danger' : 'text-success'">
              ¥{{ row.correctionRevenue }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="修正类型" width="120">
          <template #default="{ row }">
            <el-tag :type="row.correctionType === 'REVERSE_CORRECTION' ? 'warning' : 'info'">
              {{ row.correctionType === 'REVERSE_CORRECTION' ? '反向修正' : '盘点修正' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="operatorName" label="操作人" width="100" />
        <el-table-column prop="operateTime" label="操作时间" width="180" />
        <el-table-column prop="reason" label="修正原因" min-width="200" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="viewDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="queryForm.page"
        v-model:page-size="queryForm.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadData"
        @current-change="loadData"
        class="pagination"
      />
    </el-card>

    <el-dialog v-model="detailVisible" title="库存修正详情" width="800px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="修正单号">{{ detailData.correctionNo }}</el-descriptions-item>
        <el-descriptions-item label="卡货处理单号">{{ detailData.stuckRecordNo }}</el-descriptions-item>
        <el-descriptions-item label="机器编号">{{ detailData.machineCode }}</el-descriptions-item>
        <el-descriptions-item label="机器名称">{{ detailData.machineName }}</el-descriptions-item>
        <el-descriptions-item label="格口号">{{ detailData.gridNo }}</el-descriptions-item>
        <el-descriptions-item label="商品名称">{{ detailData.skuName }}</el-descriptions-item>
        <el-descriptions-item label="修正前库存">{{ detailData.beforeInventory }}</el-descriptions-item>
        <el-descriptions-item label="修正数量">
          <span :class="detailData.correctionQty > 0 ? 'text-success' : 'text-danger'">
            {{ detailData.correctionQty > 0 ? '+' : '' }}{{ detailData.correctionQty }}
          </span>
        </el-descriptions-item>
        <el-descriptions-item label="修正后库存">{{ detailData.afterInventory }}</el-descriptions-item>
        <el-descriptions-item label="收益修正">
          <span :class="detailData.correctionRevenue < 0 ? 'text-danger' : 'text-success'">
            ¥{{ detailData.correctionRevenue }}
          </span>
        </el-descriptions-item>
        <el-descriptions-item label="修正类型">
          <el-tag :type="detailData.correctionType === 'REVERSE_CORRECTION' ? 'warning' : 'info'">
            {{ detailData.correctionType === 'REVERSE_CORRECTION' ? '反向修正' : '盘点修正' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(detailData.status)">
            {{ getStatusText(detailData.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="操作人">{{ detailData.operatorName }}</el-descriptions-item>
        <el-descriptions-item label="操作时间">{{ detailData.operateTime }}</el-descriptions-item>
        <el-descriptions-item label="修正原因" :span="2">{{ detailData.reason }}</el-descriptions-item>
        <el-descriptions-item label="创建时间" :span="2">{{ detailData.createdTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getInventoryCorrectionPage, getInventoryCorrectionDetail } from '@/api/cs'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const detailVisible = ref(false)
const detailData = ref({})

const queryForm = reactive({
  correctionNo: '',
  machineCode: '',
  stuckRecordNo: '',
  correctionType: '',
  status: '',
  operatorName: '',
  page: 1,
  pageSize: 10
})

async function loadData() {
  loading.value = true
  try {
    const res = await getInventoryCorrectionPage(queryForm)
    tableData.value = res.list
    total.value = res.total
  } catch (e) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

function resetForm() {
  queryForm.correctionNo = ''
  queryForm.machineCode = ''
  queryForm.stuckRecordNo = ''
  queryForm.correctionType = ''
  queryForm.status = ''
  queryForm.operatorName = ''
  queryForm.page = 1
  loadData()
}

async function viewDetail(row) {
  try {
    const res = await getInventoryCorrectionDetail(row.id)
    detailData.value = res
    detailVisible.value = true
  } catch (e) {
    ElMessage.error('加载详情失败')
  }
}

function getStatusText(status) {
  const map = {
    PENDING: '待确认',
    CONFIRMED: '已确认',
    CANCELLED: '已取消'
  }
  return map[status] || status
}

function getStatusType(status) {
  const map = {
    PENDING: 'info',
    CONFIRMED: 'success',
    CANCELLED: 'danger'
  }
  return map[status] || 'info'
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.inventory-correction {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.table-card {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  justify-content: flex-end;
}

.text-success {
  color: #67c23a;
  font-weight: bold;
}

.text-danger {
  color: #f56c6c;
  font-weight: bold;
}
</style>
