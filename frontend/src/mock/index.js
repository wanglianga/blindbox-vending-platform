export const mockUsers = [
  { id: 1, username: 'admin', password: '123456', role: 'OPERATOR', name: '系统管理员', avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png' },
  { id: 2, username: 'supplier', password: '123456', role: 'SUPPLIER', name: '供应商001', avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png' },
  { id: 3, username: 'mall', password: '123456', role: 'MALL', name: '商场经理', avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png' },
  { id: 4, username: 'replenisher', password: '123456', role: 'REPLENISHER', name: '补货员小王', avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png' },
  { id: 5, username: 'cs', password: '123456', role: 'CUSTOMER_SERVICE', name: '客服小李', avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png' }
]

export const mockMachines = [
  { id: 1, machineNo: 'BB001', name: '万达广场一号机', location: '万达广场1层东门', mallName: '万达广场', status: 'ONLINE', gridCount: 60, skuCount: 12, lastOnline: '2024-01-15 14:30:00', createTime: '2023-12-01 10:00:00' },
  { id: 2, machineNo: 'BB002', name: '朝阳大悦城店', location: '朝阳大悦城B1层', mallName: '朝阳大悦城', status: 'ONLINE', gridCount: 60, skuCount: 10, lastOnline: '2024-01-15 14:25:00', createTime: '2023-12-05 14:00:00' },
  { id: 3, machineNo: 'BB003', name: '三里屯店', location: '三里屯太古里南区', mallName: '三里屯太古里', status: 'OFFLINE', gridCount: 60, skuCount: 8, lastOnline: '2024-01-14 09:00:00', createTime: '2023-12-10 09:00:00' },
  { id: 4, machineNo: 'BB004', name: '合生汇店', location: '合生汇购物中心2层', mallName: '合生汇', status: 'ONLINE', gridCount: 80, skuCount: 15, lastOnline: '2024-01-15 14:28:00', createTime: '2023-12-15 11:00:00' },
  { id: 5, machineNo: 'BB005', name: '国贸店', location: '国贸商城3层', mallName: '国贸商城', status: 'FAULT', gridCount: 60, skuCount: 10, lastOnline: '2024-01-15 10:00:00', createTime: '2023-12-20 16:00:00' },
  { id: 6, machineNo: 'BB006', name: '西单店', location: '西单大悦城4层', mallName: '西单大悦城', status: 'ONLINE', gridCount: 60, skuCount: 12, lastOnline: '2024-01-15 14:29:00', createTime: '2024-01-02 08:00:00' },
  { id: 7, machineNo: 'BB007', name: '王府井店', location: '王府井APM 1层', mallName: '王府井APM', status: 'ONLINE', gridCount: 60, skuCount: 11, lastOnline: '2024-01-15 14:27:00', createTime: '2024-01-05 10:00:00' },
  { id: 8, machineNo: 'BB008', name: '五道口店', location: '五道口购物中心B1', mallName: '五道口购物中心', status: 'OFFLINE', gridCount: 60, skuCount: 9, lastOnline: '2024-01-13 18:00:00', createTime: '2024-01-08 14:00:00' }
]

export const mockSeries = [
  { id: 1, name: '泡泡玛特经典系列', supplierName: '泡泡玛特', skuCount: 12, price: 69, totalSales: 15860, status: 'ON_SALE', coverImage: 'https://via.placeholder.com/120x120/409eff/ffffff?text=IP01', createTime: '2023-11-01' },
  { id: 2, name: 'Molly星座系列', supplierName: '泡泡玛特', skuCount: 14, price: 79, totalSales: 23450, status: 'ON_SALE', coverImage: 'https://via.placeholder.com/120x120/67c23a/ffffff?text=IP02', createTime: '2023-10-15' },
  { id: 3, name: 'Dimoo太空系列', supplierName: '泡泡玛特', skuCount: 10, price: 89, totalSales: 18900, status: 'ON_SALE', coverImage: 'https://via.placeholder.com/120x120/e6a23c/ffffff?text=IP03', createTime: '2023-12-01' },
  { id: 4, name: 'Labubu森林系列', supplierName: '龙家升工作室', skuCount: 8, price: 99, totalSales: 31200, status: 'ON_SALE', coverImage: 'https://via.placeholder.com/120x120/f56c6c/ffffff?text=IP04', createTime: '2023-09-20' },
  { id: 5, name: 'Skullpanda密林古堡', supplierName: 'Skullpanda', skuCount: 12, price: 79, totalSales: 27800, status: 'OFF_SHELF', coverImage: 'https://via.placeholder.com/120x120/909399/ffffff?text=IP05', createTime: '2023-08-10' },
  { id: 6, name: 'Hirono小野系列', supplierName: '泡泡玛特', skuCount: 10, price: 69, totalSales: 14500, status: 'ON_SALE', coverImage: 'https://via.placeholder.com/120x120/409eff/ffffff?text=IP06', createTime: '2024-01-01' }
]

export const mockSkus = [
  { id: 1, seriesId: 1, name: '隐藏款-金色特别版', price: 69, probability: 0.01, image: 'https://via.placeholder.com/100x100/ffd700/ffffff?text=HIDDEN', stock: 50, isHidden: true },
  { id: 2, seriesId: 1, name: '基础款A-红', price: 69, probability: 0.15, image: 'https://via.placeholder.com/100x100/f56c6c/ffffff?text=A', stock: 300 },
  { id: 3, seriesId: 1, name: '基础款B-蓝', price: 69, probability: 0.15, image: 'https://via.placeholder.com/100x100/409eff/ffffff?text=B', stock: 300 },
  { id: 4, seriesId: 1, name: '基础款C-绿', price: 69, probability: 0.15, image: 'https://via.placeholder.com/100x100/67c23a/ffffff?text=C', stock: 300 },
  { id: 5, seriesId: 1, name: '基础款D-黄', price: 69, probability: 0.12, image: 'https://via.placeholder.com/100x100/e6a23c/ffffff?text=D', stock: 250 },
  { id: 6, seriesId: 1, name: '基础款E-紫', price: 69, probability: 0.12, image: 'https://via.placeholder.com/100x100/909399/ffffff?text=E', stock: 250 },
  { id: 7, seriesId: 1, name: '基础款F-粉', price: 69, probability: 0.10, image: 'https://via.placeholder.com/100x100/ffb6c1/ffffff?text=F', stock: 200 },
  { id: 8, seriesId: 1, name: '基础款G-橙', price: 69, probability: 0.10, image: 'https://via.placeholder.com/100x100/ff8c00/ffffff?text=G', stock: 200 },
  { id: 9, seriesId: 1, name: '基础款H-青', price: 69, probability: 0.10, image: 'https://via.placeholder.com/100x100/00ced1/ffffff?text=H', stock: 200 }
]

export const mockOrders = [
  { id: 1, orderNo: 'BB202401150001', machineName: '万达广场一号机', seriesName: '泡泡玛特经典系列', skuName: '基础款A-红', amount: 69, payMethod: 'WECHAT', status: 'COMPLETED', payTime: '2024-01-15 14:30:25', userName: '张**', userPhone: '138****1234' },
  { id: 2, orderNo: 'BB202401150002', machineName: '朝阳大悦城店', seriesName: 'Molly星座系列', skuName: '隐藏款-双子座', amount: 79, payMethod: 'ALIPAY', status: 'COMPLETED', payTime: '2024-01-15 14:25:10', userName: '李**', userPhone: '139****5678' },
  { id: 3, orderNo: 'BB202401150003', machineName: '合生汇店', seriesName: 'Dimoo太空系列', skuName: '宇航员款', amount: 89, payMethod: 'WECHAT', status: 'COMPLETED', payTime: '2024-01-15 14:20:45', userName: '王**', userPhone: '137****9012' },
  { id: 4, orderNo: 'BB202401150004', machineName: '万达广场一号机', seriesName: '泡泡玛特经典系列', skuName: '基础款C-绿', amount: 69, payMethod: 'WECHAT', status: 'PENDING', payTime: '-', userName: '赵**', userPhone: '136****3456' },
  { id: 5, orderNo: 'BB202401150005', machineName: '国贸店', seriesName: 'Labubu森林系列', skuName: '小鹿款', amount: 99, payMethod: 'ALIPAY', status: 'REFUNDING', payTime: '2024-01-15 10:15:30', userName: '陈**', userPhone: '135****7890' },
  { id: 6, orderNo: 'BB202401140006', machineName: '西单店', seriesName: '泡泡玛特经典系列', skuName: '基础款B-蓝', amount: 69, payMethod: 'WECHAT', status: 'COMPLETED', payTime: '2024-01-14 18:45:20', userName: '刘**', userPhone: '134****2345' },
  { id: 7, orderNo: 'BB202401140007', machineName: '王府井店', seriesName: 'Hirono小野系列', skuName: '流浪者款', amount: 69, payMethod: 'WECHAT', status: 'COMPLETED', payTime: '2024-01-14 16:30:15', userName: '周**', userPhone: '133****6789' },
  { id: 8, orderNo: 'BB202401140008', machineName: '三里屯店', seriesName: 'Dimoo太空系列', skuName: '外星人款', amount: 89, payMethod: 'ALIPAY', status: 'COMPLETED', payTime: '2024-01-14 14:20:00', userName: '吴**', userPhone: '132****0123' },
  { id: 9, orderNo: 'BB202401140009', machineName: '朝阳大悦城店', seriesName: 'Molly星座系列', skuName: '狮子座款', amount: 79, payMethod: 'WECHAT', status: 'REFUNDED', payTime: '2024-01-14 12:10:30', userName: '郑**', userPhone: '131****4567' },
  { id: 10, orderNo: 'BB202401140010', machineName: '合生汇店', seriesName: 'Labubu森林系列', skuName: '松鼠款', amount: 99, payMethod: 'WECHAT', status: 'COMPLETED', payTime: '2024-01-14 10:05:45', userName: '孙**', userPhone: '130****8901' }
]

export const mockRevenue = [
  { date: '01-09', amount: 12500, orders: 180 },
  { date: '01-10', amount: 14200, orders: 205 },
  { date: '01-11', amount: 13800, orders: 198 },
  { date: '01-12', amount: 16500, orders: 240 },
  { date: '01-13', amount: 22300, orders: 325 },
  { date: '01-14', amount: 25800, orders: 380 },
  { date: '01-15', amount: 18600, orders: 270 }
]

export const mockRevenueShares = [
  { id: 1, period: '2024-01-01 ~ 2024-01-15', totalAmount: 268500, operatorShare: 80550, operatorRate: 30, mallShare: 53700, mallRate: 20, supplierShare: 134250, supplierRate: 50, status: 'SETTLED' },
  { id: 2, period: '2023-12-16 ~ 2023-12-31', totalAmount: 312000, operatorShare: 93600, operatorRate: 30, mallShare: 62400, mallRate: 20, supplierShare: 156000, supplierRate: 50, status: 'SETTLED' },
  { id: 3, period: '2023-12-01 ~ 2023-12-15', totalAmount: 289500, operatorShare: 86850, operatorRate: 30, mallShare: 57900, mallRate: 20, supplierShare: 144750, supplierRate: 50, status: 'SETTLED' }
]

export const mockReplenishTasks = [
  { id: 1, taskNo: 'RT20240115001', machineName: '万达广场一号机', machineAddress: '万达广场1层东门', status: 'PENDING', priority: 'HIGH', planTime: '2024-01-15 16:00:00', gridTotal: 60, emptyGrid: 25, createTime: '2024-01-15 10:00:00' },
  { id: 2, taskNo: 'RT20240115002', machineName: '朝阳大悦城店', machineAddress: '朝阳大悦城B1层', status: 'IN_PROGRESS', priority: 'MEDIUM', planTime: '2024-01-15 17:00:00', gridTotal: 60, emptyGrid: 18, createTime: '2024-01-15 09:30:00' },
  { id: 3, taskNo: 'RT20240115003', machineName: '合生汇店', machineAddress: '合生汇购物中心2层', status: 'PENDING', priority: 'HIGH', planTime: '2024-01-15 18:00:00', gridTotal: 80, emptyGrid: 35, createTime: '2024-01-15 11:00:00' },
  { id: 4, taskNo: 'RT20240115004', machineName: '西单店', machineAddress: '西单大悦城4层', status: 'COMPLETED', priority: 'LOW', planTime: '2024-01-15 14:00:00', gridTotal: 60, emptyGrid: 10, createTime: '2024-01-15 08:00:00' },
  { id: 5, taskNo: 'RT20240114005', machineName: '王府井店', machineAddress: '王府井APM 1层', status: 'COMPLETED', priority: 'MEDIUM', planTime: '2024-01-14 16:00:00', gridTotal: 60, emptyGrid: 22, createTime: '2024-01-14 09:00:00' }
]

export const mockGrids = [
  { id: 1, gridNo: 'A01', status: 'FILLED', skuName: '基础款A-红', skuImage: 'https://via.placeholder.com/60x60/f56c6c/ffffff?text=A' },
  { id: 2, gridNo: 'A02', status: 'FILLED', skuName: '基础款B-蓝', skuImage: 'https://via.placeholder.com/60x60/409eff/ffffff?text=B' },
  { id: 3, gridNo: 'A03', status: 'EMPTY', skuName: '-', skuImage: '' },
  { id: 4, gridNo: 'A04', status: 'FILLED', skuName: '基础款C-绿', skuImage: 'https://via.placeholder.com/60x60/67c23a/ffffff?text=C' },
  { id: 5, gridNo: 'A05', status: 'FILLED', skuName: '基础款D-黄', skuImage: 'https://via.placeholder.com/60x60/e6a23c/ffffff?text=D' },
  { id: 6, gridNo: 'A06', status: 'EMPTY', skuName: '-', skuImage: '' },
  { id: 7, gridNo: 'B01', status: 'FILLED', skuName: '基础款E-紫', skuImage: 'https://via.placeholder.com/60x60/909399/ffffff?text=E' },
  { id: 8, gridNo: 'B02', status: 'FILLED', skuName: '基础款F-粉', skuImage: 'https://via.placeholder.com/60x60/ffb6c1/ffffff?text=F' },
  { id: 9, gridNo: 'B03', status: 'FILLED', skuName: '基础款G-橙', skuImage: 'https://via.placeholder.com/60x60/ff8c00/ffffff?text=G' },
  { id: 10, gridNo: 'B04', status: 'EMPTY', skuName: '-', skuImage: '' },
  { id: 11, gridNo: 'B05', status: 'FILLED', skuName: '基础款H-青', skuImage: 'https://via.placeholder.com/60x60/00ced1/ffffff?text=H' },
  { id: 12, gridNo: 'B06', status: 'FILLED', skuName: '基础款A-红', skuImage: 'https://via.placeholder.com/60x60/f56c6c/ffffff?text=A' },
  { id: 13, gridNo: 'C01', status: 'FILLED', skuName: '基础款B-蓝', skuImage: 'https://via.placeholder.com/60x60/409eff/ffffff?text=B' },
  { id: 14, gridNo: 'C02', status: 'FILLED', skuName: '基础款C-绿', skuImage: 'https://via.placeholder.com/60x60/67c23a/ffffff?text=C' },
  { id: 15, gridNo: 'C03', status: 'FILLED', skuName: '基础款D-黄', skuImage: 'https://via.placeholder.com/60x60/e6a23c/ffffff?text=D' },
  { id: 16, gridNo: 'C04', status: 'EMPTY', skuName: '-', skuImage: '' },
  { id: 17, gridNo: 'C05', status: 'FILLED', skuName: '基础款E-紫', skuImage: 'https://via.placeholder.com/60x60/909399/ffffff?text=E' },
  { id: 18, gridNo: 'C06', status: 'FILLED', skuName: '基础款F-粉', skuImage: 'https://via.placeholder.com/60x60/ffb6c1/ffffff?text=F' }
]

export const mockTickets = [
  { id: 1, ticketNo: 'TK20240115001', type: 'REFUND', title: '卡货申请退款', userName: '张**', userPhone: '138****1234', machineName: '万达广场一号机', status: 'PENDING', priority: 'HIGH', createTime: '2024-01-15 14:35:00' },
  { id: 2, ticketNo: 'TK20240115002', type: 'CONSULT', title: '咨询中奖概率', userName: '李**', userPhone: '139****5678', machineName: '-', status: 'PROCESSING', priority: 'LOW', createTime: '2024-01-15 13:20:00' },
  { id: 3, ticketNo: 'TK20240115003', type: 'COMPLAINT', title: '机器故障无法购买', userName: '王**', userPhone: '137****9012', machineName: '国贸店', status: 'PENDING', priority: 'HIGH', createTime: '2024-01-15 11:45:00' },
  { id: 4, ticketNo: 'TK20240115004', type: 'REFUND', title: '购买后未出货', userName: '赵**', userPhone: '136****3456', machineName: '合生汇店', status: 'COMPLETED', priority: 'MEDIUM', createTime: '2024-01-15 10:30:00' },
  { id: 5, ticketNo: 'TK20240114005', type: 'CONSULT', title: '如何查看购买记录', userName: '陈**', userPhone: '135****7890', machineName: '-', status: 'COMPLETED', priority: 'LOW', createTime: '2024-01-14 16:40:00' },
  { id: 6, ticketNo: 'TK20240114006', type: 'OTHER', title: '建议增加新系列', userName: '刘**', userPhone: '134****2345', machineName: '-', status: 'PROCESSING', priority: 'LOW', createTime: '2024-01-14 14:20:00' }
]

export const mockRefunds = [
  { id: 1, refundNo: 'RD20240115001', orderNo: 'BB202401150005', amount: 99, reason: '卡货未出货', userName: '陈**', userPhone: '135****7890', status: 'PENDING', applyTime: '2024-01-15 10:20:00' },
  { id: 2, refundNo: 'RD20240115002', orderNo: 'BB202401150001', amount: 69, reason: '重复扣款', userName: '张**', userPhone: '138****1234', status: 'APPROVED', approveTime: '2024-01-15 14:40:00', applyTime: '2024-01-15 14:38:00' },
  { id: 3, refundNo: 'RD20240114003', orderNo: 'BB202401140009', amount: 79, reason: '商品有瑕疵', userName: '郑**', userPhone: '131****4567', status: 'COMPLETED', approveTime: '2024-01-14 18:30:00', applyTime: '2024-01-14 17:00:00' },
  { id: 4, refundNo: 'RD20240114004', orderNo: 'BB202401140006', amount: 69, reason: '用户申请取消', userName: '刘**', userPhone: '134****2345', status: 'REJECTED', rejectReason: '商品已出货，无法退款', applyTime: '2024-01-14 19:00:00' }
]

export const mockRoutes = [
  { id: 1, routeName: '朝阳线', routeCode: 'R001', machineCount: 5, replenisherName: '小王', status: 'ACTIVE', createTime: '2023-12-01' },
  { id: 2, routeName: '西单线', routeCode: 'R002', machineCount: 3, replenisherName: '小李', status: 'ACTIVE', createTime: '2023-12-05' },
  { id: 3, routeName: '三里屯线', routeCode: 'R003', machineCount: 4, replenisherName: '小张', status: 'INACTIVE', createTime: '2023-12-10' }
]

export const roleMap = {
  OPERATOR: '运营公司',
  SUPPLIER: '供应商',
  MALL: '商场',
  REPLENISHER: '补货员',
  CUSTOMER_SERVICE: '客服'
}

export const statusMap = {
  ONLINE: { label: '在线', type: 'success' },
  OFFLINE: { label: '离线', type: 'info' },
  FAULT: { label: '故障', type: 'danger' }
}

export const orderStatusMap = {
  PENDING: { label: '待支付', type: 'warning' },
  COMPLETED: { label: '已完成', type: 'success' },
  REFUNDING: { label: '退款中', type: 'info' },
  REFUNDED: { label: '已退款', type: 'danger' }
}

export const taskStatusMap = {
  PENDING: { label: '待处理', type: 'warning' },
  IN_PROGRESS: { label: '进行中', type: 'primary' },
  COMPLETED: { label: '已完成', type: 'success' }
}

export const ticketStatusMap = {
  PENDING: { label: '待处理', type: 'warning' },
  PROCESSING: { label: '处理中', type: 'primary' },
  COMPLETED: { label: '已完成', type: 'success' }
}

export const refundStatusMap = {
  PENDING: { label: '待审核', type: 'warning' },
  APPROVED: { label: '已同意', type: 'primary' },
  COMPLETED: { label: '已完成', type: 'success' },
  REJECTED: { label: '已拒绝', type: 'danger' }
}
