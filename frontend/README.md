# 盲盒自动售货机管理平台 - 前端

## 原始需求

> 开发盲盒自动售货机平台的Vue3前端管理后台。
> 
> 需要完成以下工作：
> 
> 1. 项目初始化：
>    - 使用Vite创建Vue3项目
>    - 安装依赖：vue-router, pinia, element-plus, axios, echarts
>    - 配置vite.config.js（代理/api到后端8080端口）
>    - 配置路由
>    - 配置Pinia状态管理（用户信息、token）
> 
> 2. 页面布局：
>    - 登录页面（账号密码登录，支持不同角色）
>    - 主布局（左侧菜单、顶部用户信息）
>    - 不同角色显示不同菜单
> 
> 3. 角色菜单：
>    - 运营公司(OPERATOR)：
>      * 机器点位管理
>      * IP系列管理
>      * 订单管理
>      * 收益分账
>      * 补货路线
>      * 用户管理
>    
>    - 供应商(SUPPLIER)：
>      * 我的系列
>      * 款式管理
>      * 收益统计
>    
>    - 商场(MALL)：
>      * 机器列表
>      * 销售统计
>      * 收益查看
>    
>    - 补货员(REPLENISHER)：
>      * 补货任务
>      * 扫码补货
>      * 卡货处理
>      * 补货记录
>    
>    - 客服(CUSTOMER_SERVICE)：
>      * 工单列表
>      * 退款审核
>      * 概率查询
> 
> 4. 核心页面功能：
>    - 机器点位管理：表格展示机器列表，支持增删改查、状态筛选
>    - IP系列管理：系列列表，查看款式、概率设置
>    - 订单管理：订单列表、详情、导出
>    - 收益分账：统计图表、分账明细
>    - 补货任务：任务列表、扫码开柜、格口库存展示
>    - 客服工单：工单列表、处理弹窗
>    - 数据统计图表：使用ECharts展示销售趋势、库存情况
> 
> 5. 技术细节：
>    - 使用Element Plus UI组件库
>    - Axios封装（请求拦截器加token，响应拦截器处理错误）
>    - 路由守卫（未登录跳登录页）
>    - 响应式布局
>    - 前端模拟数据（在后端未完成时也能展示）

## 启动方式

### 前置要求

- Node.js >= 16
- npm 或 pnpm

### 启动步骤

#### 1. 安装依赖

```bash
cd frontend
npm install
```

#### 2. 启动开发服务

```bash
npm run dev
```

访问地址：http://localhost:3000

### 测试账号

| 账号 | 密码 | 角色 |
|------|------|------|
| admin | 123456 | 运营公司 |
| supplier | 123456 | 供应商 |
| mall | 123456 | 商场 |
| replenisher | 123456 | 补货员 |
| cs | 123456 | 客服 |

## 项目结构

```
frontend/
├── src/
│   ├── api/              # API接口
│   │   ├── auth.js
│   │   ├── machine.js
│   │   ├── series.js
│   │   ├── order.js
│   │   ├── revenue.js
│   │   ├── replenish.js
│   │   ├── user.js
│   │   ├── cs.js
│   │   └── mall.js
│   ├── views/            # 页面组件
│   │   ├── login/
│   │   ├── dashboard/
│   │   ├── operator/
│   │   ├── supplier/
│   │   ├── mall/
│   │   ├── replenisher/
│   │   └── cs/
│   ├── router/           # 路由配置
│   │   └── index.js
│   ├── stores/           # Pinia状态管理
│   │   └── user.js
│   ├── utils/            # 工具函数
│   │   └── request.js
│   ├── mock/             # 模拟数据
│   │   └── index.js
│   ├── layout/           # 布局组件
│   │   └── MainLayout.vue
│   ├── styles/           # 全局样式
│   │   └── global.scss
│   ├── App.vue
│   └── main.js
├── public/
├── Dockerfile
├── .dockerignore
├── nginx.conf
├── vite.config.js
└── package.json
```

## 技术栈

- Vue 3 - 渐进式JavaScript框架
- Vite - 下一代前端构建工具
- Vue Router - 官方路由
- Pinia - 状态管理
- Element Plus - Vue3 UI组件库
- Axios - HTTP请求库
- ECharts - 数据可视化图表

## Docker 启动

### 前置要求

- Docker
- Docker Compose

### 一键启动

```bash
docker compose up --build
```

后台运行：

```bash
docker compose up --build -d
```

停止服务：

```bash
docker compose down
```

访问地址：http://localhost:3000

## 功能说明

### 运营公司 (OPERATOR)
- 机器点位管理：增删改查机器点位，状态筛选
- IP系列管理：系列管理、款式管理、概率设置
- 订单管理：订单列表、详情、导出
- 收益分账：销售趋势图表、分账明细
- 补货路线：路线管理、分配机器
- 用户管理：用户增删改查、角色管理

### 供应商 (SUPPLIER)
- 我的系列：查看管理自有IP系列
- 款式管理：SKU管理、概率设置
- 收益统计：销售趋势、结算记录

### 商场 (MALL)
- 机器列表：查看场内机器状态
- 销售统计：销售趋势、机器排行
- 收益查看：商场收益分成

### 补货员 (REPLENISHER)
- 补货任务：任务列表、优先级管理
- 扫码补货：扫码开柜、格口库存
- 卡货处理：卡货问题处理
- 补货记录：历史补货记录

### 客服 (CUSTOMER_SERVICE)
- 工单列表：客户工单处理
- 退款审核：退款申请审核
- 概率查询：IP系列概率详情
