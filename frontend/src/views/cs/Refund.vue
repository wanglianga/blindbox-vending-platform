<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">退款审核</h2>
    </div>

    <el-card class="mb-20">
      <el-form :inline="true" :model="queryForm" @submit.prevent>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="全部" clearable style="width: 140px">
            <el-option label="待审核" value="PENDING" />
            <el-option label="已同意" value="APPROVED" />
            <el-option label="已完成" value="COMPLETED" />
            <el-option label="已拒绝" value="REJECTED" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="queryForm.keyword" placeholder="退款单号/订单号" clearable style="width: 220px" />
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
      <el-table :data="refundList" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="refundNo" label="退款单号" width="160" />
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column label="金额" width="120">
          <template #default="{ row }">
            <span class="amount">¥{{ row.amount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="reason" label="退款原因" min-width="160" show-overflow-tooltip />
        <el-table-column prop="userName" label="用户" width="100" />
        <el-table-column prop="userPhone" label="手机号" width="140" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">{{ getStatusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="applyTime" label="申请时间" width="170" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 'PENDING'" type="success" size="small" @click="handleApprove(row)">
              同意
            </el-button>
            <el-button v-if="row.status === 'PENDING'" type="danger" size="small" @click="handleReject(row)">
              拒绝
            </el-button>
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

    <el-dialog v-model="rejectVisible" title="拒绝退款" width="460px">
      <el-form :model="rejectForm" label-width="80px">
        <el-form-item label="拒绝原因">
          <el-input v-model="rejectForm.reason" type="textarea" :rows="4" placeholder="请输入拒绝原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectVisible = false">取消</el-button>
        <el-button type="danger" @click="submitReject">确认拒绝</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRefundList, approveRefund, rejectRefund } from '@/api/cs'
import { refundStatusMap } from '@/mock'

const loading = ref(false)
const refundList = ref([])
const total = ref(0)
const rejectVisible = ref(false)
const currentRefund = ref(null)
const rejectForm = reactive({ reason: '' })

const queryForm = reactive({
  status: '',
  keyword: '',
  page: 1,
  pageSize: 10
})

function getStatusType(status) {
  return refundStatusMap[status]?.type || 'info'
}

function getStatusLabel(status) {
  return refundStatusMap[status]?.label || status
}

async function loadData() {
  loading.value = true
  try {
    const res = await getRefundList(queryForm)
    refundList.value = res.list
    total.value = res.total
  } finally {
    loading.value = false
  }
}

function handleReset() {
  queryForm.status = ''
  queryForm.keyword = ''
  queryForm.page = 1
  loadData()
}

function handleApprove(row) {
  ElMessageBox.confirm(`确定同意退款 ¥${row.amount} 吗？`, '提示', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  }).then(async () => {
    await approveRefund(row.id, '')
    ElMessage.success('已同意退款')
    loadData()
  }).catch(() => {})
}

function handleReject(row) {
  currentRefund.value = row
  rejectForm.reason = ''
  rejectVisible.value = true
}

async function submitReject() {
  if (!rejectForm.reason) {
    ElMessage.warning('请输入拒绝原因')
    return
  }
  await rejectRefund(currentRefund.value.id, rejectForm.reason)
  ElMessage.success('已拒绝退款')
  rejectVisible.value = false
  loadData()
}

function handleDetail(row) {
  ElMessage.info('查看退款详情')
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
</style>
