import { createRouter, createWebHashHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('@/layout/MainLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页', icon: 'HomeFilled', roles: ['OPERATOR', 'SUPPLIER', 'MALL', 'REPLENISHER', 'CUSTOMER_SERVICE'] }
      },
      {
        path: 'machine-point',
        name: 'MachinePoint',
        component: () => import('@/views/operator/MachinePoint.vue'),
        meta: { title: '机器点位管理', icon: 'Monitor', roles: ['OPERATOR'] }
      },
      {
        path: 'ip-series',
        name: 'IpSeries',
        component: () => import('@/views/operator/IpSeries.vue'),
        meta: { title: 'IP系列管理', icon: 'Collection', roles: ['OPERATOR'] }
      },
      {
        path: 'order',
        name: 'Order',
        component: () => import('@/views/operator/Order.vue'),
        meta: { title: '订单管理', icon: 'List', roles: ['OPERATOR'] }
      },
      {
        path: 'revenue-operator',
        name: 'RevenueOperator',
        component: () => import('@/views/operator/Revenue.vue'),
        meta: { title: '收益分账', icon: 'Money', roles: ['OPERATOR'] }
      },
      {
        path: 'replenish-route',
        name: 'ReplenishRoute',
        component: () => import('@/views/operator/ReplenishRoute.vue'),
        meta: { title: '补货路线', icon: 'Location', roles: ['OPERATOR'] }
      },
      {
        path: 'user-manage',
        name: 'UserManage',
        component: () => import('@/views/operator/UserManage.vue'),
        meta: { title: '用户管理', icon: 'User', roles: ['OPERATOR'] }
      },
      {
        path: 'my-series',
        name: 'MySeries',
        component: () => import('@/views/supplier/MySeries.vue'),
        meta: { title: '我的系列', icon: 'Collection', roles: ['SUPPLIER'] }
      },
      {
        path: 'sku-manage',
        name: 'SkuManage',
        component: () => import('@/views/supplier/SkuManage.vue'),
        meta: { title: '款式管理', icon: 'Goods', roles: ['SUPPLIER'] }
      },
      {
        path: 'revenue-supplier',
        name: 'RevenueSupplier',
        component: () => import('@/views/supplier/Revenue.vue'),
        meta: { title: '收益统计', icon: 'Money', roles: ['SUPPLIER'] }
      },
      {
        path: 'mall-machines',
        name: 'MallMachines',
        component: () => import('@/views/mall/MachineList.vue'),
        meta: { title: '机器列表', icon: 'Monitor', roles: ['MALL'] }
      },
      {
        path: 'mall-stats',
        name: 'MallStats',
        component: () => import('@/views/mall/SalesStats.vue'),
        meta: { title: '销售统计', icon: 'DataLine', roles: ['MALL'] }
      },
      {
        path: 'mall-revenue',
        name: 'MallRevenue',
        component: () => import('@/views/mall/Revenue.vue'),
        meta: { title: '收益查看', icon: 'Money', roles: ['MALL'] }
      },
      {
        path: 'replenish-task',
        name: 'ReplenishTask',
        component: () => import('@/views/replenisher/Task.vue'),
        meta: { title: '补货任务', icon: 'List', roles: ['REPLENISHER'] }
      },
      {
        path: 'scan-replenish',
        name: 'ScanReplenish',
        component: () => import('@/views/replenisher/Scan.vue'),
        meta: { title: '扫码补货', icon: 'Camera', roles: ['REPLENISHER'] }
      },
      {
        path: 'stuck-handle',
        name: 'StuckHandle',
        component: () => import('@/views/replenisher/Stuck.vue'),
        meta: { title: '卡货处理', icon: 'Warning', roles: ['REPLENISHER'] }
      },
      {
        path: 'replenish-record',
        name: 'ReplenishRecord',
        component: () => import('@/views/replenisher/Record.vue'),
        meta: { title: '补货记录', icon: 'Document', roles: ['REPLENISHER'] }
      },
      {
        path: 'ticket-list',
        name: 'TicketList',
        component: () => import('@/views/cs/Ticket.vue'),
        meta: { title: '工单列表', icon: 'Service', roles: ['CUSTOMER_SERVICE'] }
      },
      {
        path: 'refund-audit',
        name: 'RefundAudit',
        component: () => import('@/views/cs/Refund.vue'),
        meta: { title: '退款审核', icon: 'Refund', roles: ['CUSTOMER_SERVICE'] }
      },
      {
        path: 'probability-query',
        name: 'ProbabilityQuery',
        component: () => import('@/views/cs/Probability.vue'),
        meta: { title: '概率查询', icon: 'PieChart', roles: ['CUSTOMER_SERVICE'] }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  if (to.meta.title) {
    document.title = `${to.meta.title} - 盲盒自动售货机管理平台`
  }

  if (to.path === '/login') {
    next()
    return
  }

  if (!userStore.token) {
    next('/login')
    return
  }

  if (to.meta.roles && !to.meta.roles.includes(userStore.userInfo.role)) {
    next('/dashboard')
    return
  }

  next()
})

export default router
