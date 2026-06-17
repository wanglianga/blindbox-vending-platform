<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">卡货处理</h2>
      <el-button type="primary" @click="handleRefresh">
        <el-icon><Refresh /></el-icon>
        刷新
      </el-button>
    </div>

    <el-alert
      title="当前有 3 个卡货待处理"
      type="warning"
      :closable="false"
      class="mb-20"
      show-icon
    />

    <el-table :data="stuckList" stripe style="width: 100%" v-loading="loading">
      <el-table-column prop="machineName" label="机器名称" min-width="160" />
      <el-table-column prop="gridNo" label="格口" width="100" align="center" />
      <el-table-column prop="skuName" label="商品" min-width="160" />
      <el-table-column prop="orderNo" label="订单号" width="180" />
      <el-table-column prop="userName" label="用户" width="100" />
      <el-table-column prop="userPhone" label="手机号" width="140" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 'PENDING' ? 'warning' : 'success'" size="small">
            {{ row.status === 'PENDING' ? '待处理' : '已处理' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="reportTime" label="上报时间" width="170" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button v-if="row.status === 'PENDING'" type="primary" size="small" @click="handleHandle(row)">
            处理
          </el-button>
          <el-button type="primary" link size="small" @click="handleDetail(row)">
            详情
          </el-button>
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

    <el-dialog v-model="dialogVisible" title="卡货处理" width="500px">
      <div v-if="currentStuck" class="stuck-detail">
        <el-descriptions :column="1" border size="small">
          <el-descriptions-item label="机器">{{ currentStuck.machineName }}</el-descriptions-item>
          <el-descriptions-item label="格口">{{ currentStuck.gridNo }}</el-descriptions-item>
          <el-descriptions-item label="商品">{{ currentStuck.skuName }}</el-descriptions-item>
          <el-descriptions-item label="订单号">{{ currentStuck.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="用户">{{ currentStuck.userName }} ({{ currentStuck.userPhone }})</el-descriptions-item>
        </el-descriptions>
      </div>
      
      <el-form :model="handleForm" label-width="100px" class="mt-20">
        <el-form-item label="处理方式">
          <el-radio-group v-model="handleForm.type">
            <el-radio value="REPLENISH">重新补货</el-radio>
            <el-radio value="REFUND">退款处理</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="处理说明">
          <el-input v-model="handleForm.remark" type="textarea" :rows="3" placeholder="请输入处理说明" />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确认处理</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const total = ref(3)
const dialogVisible = ref(false)
const currentStuck = ref(null)

const queryForm = reactive({
  page: 1,
  pageSize: 10
})

const handleForm = reactive({
  type: 'REPLENISH',
  remark: ''
})

const stuckList = ref([
  { id: 1, machineName: '万达广场一号机', gridNo: 'B03', skuName: '基础款G-橙', orderNo: 'BB202401150012', userName: '张**', userPhone: '138****1234', status: 'PENDING', reportTime: '2024-01-15 14:30:00' },
  { id: 2, machineName: '朝阳大悦城店', gridNo: 'A05', skuName: '基础款D-黄', orderNo: 'BB202401150008', userName: '李**', userPhone: '139****5678', status: 'PENDING', reportTime: '2024-01-15 13:15:00' },
  { id: 3, machineName: '合生汇店', gridNo: 'C02', skuName: '基础款B-蓝', orderNo: 'BB202401150005', userName: '王**', userPhone: '137****9012', status: 'PENDING', reportTime: '2024-01-15 11:45:00' }
])

function loadData() {
  loading.value = true
  setTimeout(() => {
    loading.value = false
  }, 300)
}

function handleRefresh() {
  loadData()
  ElMessage.success('刷新成功')
}

function handleHandle(row) {
  currentStuck.value = row
  handleForm.type = 'REPLENISH'
  handleForm.remark = ''
  dialogVisible.value = true
}

function handleDetail(row) {
  ElMessage.info('查看详情')
}

function handleSubmit() {
  ElMessage.success('处理成功')
  dialogVisible.value = false
  if (currentStuck.value) {
    currentStuck.value.status = 'PROCESSED'
  }
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

.stuck-detail {
  margin-bottom: 20px;
}

.mt-20 {
  margin-top: 20px;
}
</style>
