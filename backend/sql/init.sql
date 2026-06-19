-- 盲盒自动售货机平台数据库
CREATE DATABASE IF NOT EXISTS blindbox DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE blindbox;

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(64) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(128) NOT NULL COMMENT '密码',
    nickname VARCHAR(64) COMMENT '昵称',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(128) COMMENT '邮箱',
    avatar VARCHAR(255) COMMENT '头像',
    role_type VARCHAR(32) NOT NULL COMMENT '角色类型: OPERATOR-运营公司, SUPPLIER-供应商, MALL-商场, REPLENISHER-补货员, CUSTOMER_SERVICE-客服',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
    company_id BIGINT COMMENT '所属公司/商场ID',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_role_type (role_type),
    INDEX idx_company_id (company_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 运营公司表
CREATE TABLE IF NOT EXISTS operator_company (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(128) NOT NULL COMMENT '公司名称',
    contact_name VARCHAR(64) COMMENT '联系人',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    address VARCHAR(255) COMMENT '地址',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='运营公司表';

-- 供应商表
CREATE TABLE IF NOT EXISTS supplier (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(128) NOT NULL COMMENT '供应商名称',
    contact_name VARCHAR(64) COMMENT '联系人',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    address VARCHAR(255) COMMENT '地址',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商表';

-- 商场表
CREATE TABLE IF NOT EXISTS mall (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(128) NOT NULL COMMENT '商场名称',
    contact_name VARCHAR(64) COMMENT '联系人',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    address VARCHAR(255) COMMENT '地址',
    commission_rate DECIMAL(5,2) DEFAULT 0.00 COMMENT '商场扣点比例(%)',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商场表';

-- IP系列表
CREATE TABLE IF NOT EXISTS ip_series (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(128) NOT NULL COMMENT '系列名称',
    supplier_id BIGINT NOT NULL COMMENT '供应商ID',
    description TEXT COMMENT '系列描述',
    cover_image VARCHAR(255) COMMENT '封面图',
    price DECIMAL(10,2) NOT NULL COMMENT '单抽价格',
    total_skus INT DEFAULT 0 COMMENT 'SKU总数',
    hidden_rule TEXT COMMENT '隐藏款规则说明',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-下架, 1-上架',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_supplier_id (supplier_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='IP系列表';

-- 盲盒款式表(SKU)
CREATE TABLE IF NOT EXISTS blindbox_sku (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    series_id BIGINT NOT NULL COMMENT '系列ID',
    sku_name VARCHAR(128) NOT NULL COMMENT '款式名称',
    sku_code VARCHAR(64) COMMENT '款式编码',
    image_url VARCHAR(255) COMMENT '图片',
    is_hidden TINYINT DEFAULT 0 COMMENT '是否隐藏款: 0-否, 1-是',
    probability DECIMAL(8,6) DEFAULT 0.000000 COMMENT '抽中概率',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_series_id (series_id),
    INDEX idx_is_hidden (is_hidden)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='盲盒款式表';

-- 机器点位表
CREATE TABLE IF NOT EXISTS machine_point (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    machine_code VARCHAR(64) NOT NULL UNIQUE COMMENT '机器编号',
    machine_name VARCHAR(128) COMMENT '机器名称',
    mall_id BIGINT NOT NULL COMMENT '所属商场ID',
    operator_id BIGINT NOT NULL COMMENT '运营公司ID',
    location_detail VARCHAR(255) COMMENT '具体位置描述',
    ip_address VARCHAR(64) COMMENT '机器IP地址',
    grid_count INT DEFAULT 60 COMMENT '格口总数',
    status VARCHAR(32) DEFAULT 'OFFLINE' COMMENT '状态: ONLINE-在线, OFFLINE-离线, FAULT-故障, STOPPED-停机',
    last_online_time DATETIME COMMENT '最后在线时间',
    current_series_id BIGINT COMMENT '当前投放系列ID',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_mall_id (mall_id),
    INDEX idx_operator_id (operator_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='机器点位表';

-- 机器格口库存表
CREATE TABLE IF NOT EXISTS machine_grid (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    machine_id BIGINT NOT NULL COMMENT '机器ID',
    grid_no INT NOT NULL COMMENT '格口号',
    sku_id BIGINT COMMENT '当前存放SKU ID',
    status VARCHAR(32) DEFAULT 'EMPTY' COMMENT '状态: EMPTY-空, FILLED-有货, LOCKED-锁定中, FAULT-故障',
    last_check_time DATETIME COMMENT '最后盘点时间',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_machine_grid (machine_id, grid_no),
    INDEX idx_machine_id (machine_id),
    INDEX idx_sku_id (sku_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='机器格口库存表';

-- 补货计划表
CREATE TABLE IF NOT EXISTS replenishment_plan (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    plan_no VARCHAR(64) NOT NULL UNIQUE COMMENT '补货单号',
    replenisher_id BIGINT NOT NULL COMMENT '补货员ID',
    machine_id BIGINT NOT NULL COMMENT '机器ID',
    plan_date DATE NOT NULL COMMENT '计划补货日期',
    status VARCHAR(32) DEFAULT 'PENDING' COMMENT '状态: PENDING-待执行, IN_PROGRESS-进行中, COMPLETED-已完成, CANCELLED-已取消',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    remark VARCHAR(255) COMMENT '备注',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_replenisher_id (replenisher_id),
    INDEX idx_machine_id (machine_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='补货计划表';

-- 补货明细表
CREATE TABLE IF NOT EXISTS replenishment_detail (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    plan_id BIGINT NOT NULL COMMENT '补货计划ID',
    sku_id BIGINT NOT NULL COMMENT 'SKU ID',
    grid_no INT COMMENT '补充到哪个格口',
    replenish_qty INT DEFAULT 1 COMMENT '补货数量',
    before_qty INT DEFAULT 0 COMMENT '补货前数量',
    after_qty INT DEFAULT 0 COMMENT '补货后数量',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_plan_id (plan_id),
    INDEX idx_sku_id (sku_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='补货明细表';

-- 补货路线表
CREATE TABLE IF NOT EXISTS replenishment_route (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    route_name VARCHAR(128) NOT NULL COMMENT '路线名称',
    replenisher_id BIGINT COMMENT '负责补货员ID',
    mall_ids VARCHAR(255) COMMENT '途经商场IDs',
    machine_count INT DEFAULT 0 COMMENT '包含机器数',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_replenisher_id (replenisher_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='补货路线表';

-- 支付订单表
CREATE TABLE IF NOT EXISTS pay_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(64) NOT NULL UNIQUE COMMENT '订单号',
    machine_id BIGINT NOT NULL COMMENT '机器ID',
    series_id BIGINT NOT NULL COMMENT '系列ID',
    user_id BIGINT COMMENT '用户ID(C端用户)',
    pay_amount DECIMAL(10,2) NOT NULL COMMENT '支付金额',
    pay_type VARCHAR(32) COMMENT '支付方式: WECHAT-微信, ALIPAY-支付宝',
    pay_status VARCHAR(32) DEFAULT 'UNPAID' COMMENT '支付状态: UNPAID-待支付, PAID-已支付, REFUNDING-退款中, REFUNDED-已退款, CLOSED-已关闭',
    pay_time DATETIME COMMENT '支付时间',
    draw_sku_id BIGINT COMMENT '抽中SKU ID',
    grid_no INT COMMENT '出货格口号',
    ship_status VARCHAR(32) DEFAULT 'PENDING' COMMENT '出货状态: PENDING-待出货, SHIPPING-出货中, SUCCESS-出货成功, FAILED-出货失败, JAMMED-卡货',
    ship_time DATETIME COMMENT '出货时间',
    motor_status VARCHAR(32) COMMENT '电机状态: NORMAL-正常, FAULT-故障',
    remark VARCHAR(255) COMMENT '备注',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_machine_id (machine_id),
    INDEX idx_pay_status (pay_status),
    INDEX idx_ship_status (ship_status),
    INDEX idx_created_time (created_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付订单表';

-- 退款申请表
CREATE TABLE IF NOT EXISTS refund_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    refund_no VARCHAR(64) NOT NULL UNIQUE COMMENT '退款单号',
    order_id BIGINT NOT NULL COMMENT '原订单ID',
    order_no VARCHAR(64) NOT NULL COMMENT '原订单号',
    refund_amount DECIMAL(10,2) NOT NULL COMMENT '退款金额',
    refund_type VARCHAR(32) NOT NULL COMMENT '退款类型: DUPLICATE_PAY-重复扣款, NO_SHIP-未出货, PROBABILITY_ISSUE-概率质疑, OTHER-其他',
    refund_reason TEXT COMMENT '退款原因',
    applicant VARCHAR(64) COMMENT '申请人',
    status VARCHAR(32) DEFAULT 'PENDING' COMMENT '状态: PENDING-待审核, APPROVED-已同意, REJECTED-已拒绝, COMPLETED-已完成',
    auditor_id BIGINT COMMENT '审核人ID',
    audit_time DATETIME COMMENT '审核时间',
    audit_remark VARCHAR(255) COMMENT '审核备注',
    refund_time DATETIME COMMENT '退款完成时间',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_order_id (order_id),
    INDEX idx_status (status),
    INDEX idx_refund_type (refund_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退款申请表';

-- 收益分账表
CREATE TABLE IF NOT EXISTS revenue_share (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL COMMENT '订单ID',
    order_no VARCHAR(64) NOT NULL COMMENT '订单号',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
    mall_commission DECIMAL(10,2) DEFAULT 0.00 COMMENT '商场扣点金额',
    supplier_share DECIMAL(10,2) DEFAULT 0.00 COMMENT '供应商分成',
    operator_share DECIMAL(10,2) DEFAULT 0.00 COMMENT '运营公司分成',
    platform_share DECIMAL(10,2) DEFAULT 0.00 COMMENT '平台分成',
    mall_id BIGINT COMMENT '商场ID',
    supplier_id BIGINT COMMENT '供应商ID',
    operator_id BIGINT COMMENT '运营公司ID',
    settle_status VARCHAR(32) DEFAULT 'UNSETTLED' COMMENT '结算状态: UNSETTLED-未结算, SETTLED-已结算',
    settle_time DATETIME COMMENT '结算时间',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_order_id (order_id),
    INDEX idx_mall_id (mall_id),
    INDEX idx_settle_status (settle_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收益分账表';

-- 客服工单表
CREATE TABLE IF NOT EXISTS cs_ticket (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    ticket_no VARCHAR(64) NOT NULL UNIQUE COMMENT '工单号',
    user_id BIGINT COMMENT '用户ID',
    user_phone VARCHAR(20) COMMENT '用户手机号',
    order_id BIGINT COMMENT '关联订单ID',
    order_no VARCHAR(64) COMMENT '关联订单号',
    ticket_type VARCHAR(32) NOT NULL COMMENT '工单类型: DUPLICATE_PAY-重复扣款, NO_SHIP-未出货, PROBABILITY_QUESTION-概率质疑, OTHER-其他',
    title VARCHAR(255) NOT NULL COMMENT '工单标题',
    description TEXT COMMENT '问题描述',
    status VARCHAR(32) DEFAULT 'OPEN' COMMENT '状态: OPEN-待处理, PROCESSING-处理中, CLOSED-已关闭',
    priority VARCHAR(32) DEFAULT 'NORMAL' COMMENT '优先级: LOW-低, NORMAL-中, HIGH-高',
    handler_id BIGINT COMMENT '处理人ID',
    handle_result TEXT COMMENT '处理结果',
    handle_time DATETIME COMMENT '处理时间',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_ticket_type (ticket_type),
    INDEX idx_status (status),
    INDEX idx_user_phone (user_phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客服工单表';

-- 机器运行记录（停机时长等）
CREATE TABLE IF NOT EXISTS machine_runtime_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    machine_id BIGINT NOT NULL COMMENT '机器ID',
    event_type VARCHAR(32) NOT NULL COMMENT '事件类型: ONLINE-上线, OFFLINE-离线, FAULT-故障, REPAIRED-修复',
    event_time DATETIME NOT NULL COMMENT '事件时间',
    duration_seconds INT DEFAULT 0 COMMENT '持续时长(秒)',
    remark VARCHAR(255) COMMENT '备注',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_machine_id (machine_id),
    INDEX idx_event_type (event_type),
    INDEX idx_event_time (event_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='机器运行记录表';

-- 概率审计日志（确保展示概率与实际库存一致）
CREATE TABLE IF NOT EXISTS probability_audit_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    machine_id BIGINT NOT NULL COMMENT '机器ID',
    series_id BIGINT NOT NULL COMMENT '系列ID',
    audit_type VARCHAR(32) NOT NULL COMMENT '审计类型: REPLENISH-补货后, DRAW-抽盒后, MANUAL-手动审计',
    before_audit TEXT COMMENT '审计前各SKU实际数量和概率',
    after_audit TEXT COMMENT '审计后状态',
    is_consistent TINYINT DEFAULT 1 COMMENT '是否一致: 0-不一致, 1-一致',
    operator_id BIGINT COMMENT '操作人ID',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_machine_id (machine_id),
    INDEX idx_series_id (series_id),
    INDEX idx_is_consistent (is_consistent)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='概率审计日志表';

-- 初始化数据
-- 运营公司
INSERT INTO operator_company (id, name, contact_name, contact_phone) VALUES
(1, '欢乐盲盒运营有限公司', '张总', '13800138001');

-- 供应商
INSERT INTO supplier (id, name, contact_name, contact_phone) VALUES
(1, '创意潮玩供应商', '李经理', '13900139001'),
(2, '萌趣手办工厂', '王厂长', '13900139002');

-- 商场
INSERT INTO mall (id, name, contact_name, contact_phone, address, commission_rate) VALUES
(1, '万象城购物中心', '陈经理', '13700137001', '市中心繁华路1号', 15.00),
(2, '万达广场', '刘主管', '13700137002', '城东大道88号', 12.00);

-- IP系列
INSERT INTO ip_series (id, name, supplier_id, description, price, total_skus, hidden_rule, status) VALUES
(1, '萌宠小镇系列', 1, '可爱的萌宠小镇系列盲盒，包含6款常规+1款隐藏', 69.00, 7, '隐藏款为"金色猫咪"，抽中概率约为1/72', 1),
(2, '星际探险系列', 2, '炫酷的星际探险主题盲盒，包含8款常规+2款隐藏', 89.00, 10, '隐藏款有两款，"宇航队长"概率1/60，"外星访客"概率1/120', 1);

-- 盲盒SKU
INSERT INTO blindbox_sku (series_id, sku_name, sku_code, is_hidden, probability, sort_order) VALUES
-- 萌宠小镇系列
(1, '白色小狗', 'MC-001', 0, 0.1667, 1),
(1, '橘色小猫', 'MC-002', 0, 0.1667, 2),
(1, '粉色兔子', 'MC-003', 0, 0.1667, 3),
(1, '灰色仓鼠', 'MC-004', 0, 0.1667, 4),
(1, '棕色小熊', 'MC-005', 0, 0.1667, 5),
(1, '黄色小鸭', 'MC-006', 0, 0.1667, 6),
(1, '金色猫咪', 'MC-H01', 1, 0.0139, 7),
-- 星际探险系列
(2, '探索者一号', 'XJ-001', 0, 0.1200, 1),
(2, '机械狗', 'XJ-002', 0, 0.1200, 2),
(2, '星际医生', 'XJ-003', 0, 0.1200, 3),
(2, '工程师', 'XJ-004', 0, 0.1200, 4),
(2, '通信员', 'XJ-005', 0, 0.1200, 5),
(2, '导航员', 'XJ-006', 0, 0.1200, 6),
(2, '科学家', 'XJ-007', 0, 0.1200, 7),
(2, '飞行员', 'XJ-008', 0, 0.1200, 8),
(2, '宇航队长', 'XJ-H01', 1, 0.0167, 9),
(2, '外星访客', 'XJ-H02', 1, 0.0083, 10);

-- 机器点位
INSERT INTO machine_point (id, machine_code, machine_name, mall_id, operator_id, location_detail, ip_address, grid_count, status, current_series_id) VALUES
(1, 'BXM-0001', '万象城1号机', 1, 1, '1楼正门入口右侧', '192.168.1.101', 60, 'ONLINE', 1),
(2, 'BXM-0002', '万象城2号机', 1, 1, '3楼影院旁', '192.168.1.102', 60, 'ONLINE', 2),
(3, 'BXM-0003', '万达1号机', 2, 1, '2楼童装区', '192.168.2.101', 60, 'OFFLINE', 1);

-- 机器格口库存 (为1号机生成初始库存)
INSERT INTO machine_grid (machine_id, grid_no, sku_id, status)
SELECT 1, n, 
    CASE 
        WHEN n <= 10 THEN 1
        WHEN n <= 20 THEN 2
        WHEN n <= 30 THEN 3
        WHEN n <= 40 THEN 4
        WHEN n <= 50 THEN 5
        WHEN n <= 58 THEN 6
        ELSE 7
    END,
    'FILLED'
FROM (
    SELECT a.N + b.N * 10 + 1 n
    FROM 
        (SELECT 0 AS N UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) a,
        (SELECT 0 AS N UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5) b
    ORDER BY n
) t
WHERE n <= 60;

-- 为2号机生成初始库存
INSERT INTO machine_grid (machine_id, grid_no, sku_id, status)
SELECT 2, n, 
    CASE 
        WHEN n <= 7 THEN 8
        WHEN n <= 14 THEN 9
        WHEN n <= 21 THEN 10
        WHEN n <= 28 THEN 11
        WHEN n <= 35 THEN 12
        WHEN n <= 42 THEN 13
        WHEN n <= 49 THEN 14
        WHEN n <= 56 THEN 15
        WHEN n <= 58 THEN 16
        ELSE 17
    END,
    'FILLED'
FROM (
    SELECT a.N + b.N * 10 + 1 n
    FROM 
        (SELECT 0 AS N UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) a,
        (SELECT 0 AS N UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5) b
    ORDER BY n
) t
WHERE n <= 60;

-- 为3号机生成空库存
INSERT INTO machine_grid (machine_id, grid_no, status)
SELECT 3, n, 'EMPTY'
FROM (
    SELECT a.N + b.N * 10 + 1 n
    FROM 
        (SELECT 0 AS N UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) a,
        (SELECT 0 AS N UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5) b
    ORDER BY n
) t
WHERE n <= 60;

-- 系统用户
INSERT INTO sys_user (id, username, password, nickname, phone, role_type, status, company_id) VALUES
-- 运营公司管理员
(1, 'operator', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '运营管理员', '13800138001', 'OPERATOR', 1, 1),
-- 供应商
(2, 'supplier', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '供应商管理员', '13900139001', 'SUPPLIER', 1, 1),
-- 商场
(3, 'mall', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '商场管理员', '13700137001', 'MALL', 1, 1),
-- 补货员
(4, 'replenisher', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '补货员小王', '13600136001', 'REPLENISHER', 1, NULL),
-- 客服
(5, 'service', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '客服小李', '13500135001', 'CUSTOMER_SERVICE', 1, NULL);

-- 补货路线
INSERT INTO replenishment_route (id, route_name, replenisher_id, mall_ids, machine_count, sort_order) VALUES
(1, '市中心路线', 4, '1,2', 3, 1);

-- 补货计划
INSERT INTO replenishment_plan (id, plan_no, replenisher_id, machine_id, plan_date, status, remark) VALUES
(1, 'RP-20240115-001', 4, 1, CURDATE(), 'COMPLETED', '日常补货'),
(2, 'RP-20240115-002', 4, 2, CURDATE(), 'PENDING', '日常补货');

-- 模拟订单
INSERT INTO pay_order (id, order_no, machine_id, series_id, pay_amount, pay_type, pay_status, pay_time, draw_sku_id, grid_no, ship_status, ship_time, motor_status) VALUES
(1, 'PO2024011500001', 1, 1, 69.00, 'WECHAT', 'PAID', '2024-01-15 10:30:00', 2, 15, 'SUCCESS', '2024-01-15 10:30:05', 'NORMAL'),
(2, 'PO2024011500002', 1, 1, 69.00, 'ALIPAY', 'PAID', '2024-01-15 11:20:00', 5, 42, 'SUCCESS', '2024-01-15 11:20:08', 'NORMAL'),
(3, 'PO2024011500003', 2, 2, 89.00, 'WECHAT', 'PAID', '2024-01-15 14:00:00', 10, 25, 'JAMMED', NULL, 'FAULT');

-- 收益分账
INSERT INTO revenue_share (id, order_id, order_no, total_amount, mall_commission, supplier_share, operator_share, platform_share, mall_id, supplier_id, operator_id, settle_status) VALUES
(1, 1, 'PO2024011500001', 69.00, 10.35, 34.50, 17.25, 6.90, 1, 1, 1, 'UNSETTLED'),
(2, 2, 'PO2024011500002', 69.00, 10.35, 34.50, 17.25, 6.90, 1, 1, 1, 'UNSETTLED'),
(3, 3, 'PO2024011500003', 89.00, 13.35, 44.50, 22.25, 8.90, 1, 2, 1, 'UNSETTLED');

-- 客服工单
INSERT INTO cs_ticket (id, ticket_no, user_phone, order_id, order_no, ticket_type, title, description, status, priority) VALUES
(1, 'TK20240115001', '13912345678', 3, 'PO2024011500003', 'NO_SHIP', '支付成功但未出货', '用户支付后机器卡货，未出货，要求退款', 'PROCESSING', 'HIGH'),
(2, 'TK20240115002', '13887654321', NULL, NULL, 'PROBABILITY_QUESTION', '质疑隐藏款概率', '用户连续抽了20个都没中隐藏款，质疑概率是否真实', 'OPEN', 'NORMAL');

-- 卡货处理记录表
CREATE TABLE IF NOT EXISTS stuck_handle_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    record_no VARCHAR(64) NOT NULL UNIQUE COMMENT '处理记录编号',
    ticket_id BIGINT COMMENT '关联工单ID',
    ticket_no VARCHAR(64) COMMENT '关联工单号',
    order_id BIGINT COMMENT '关联订单ID',
    order_no VARCHAR(64) COMMENT '关联订单号',
    machine_id BIGINT COMMENT '机器ID',
    machine_code VARCHAR(64) COMMENT '机器编号',
    grid_no INT COMMENT '格口号',
    sku_id BIGINT COMMENT 'SKU ID',
    sku_name VARCHAR(128) COMMENT 'SKU名称',
    pay_flow_checked VARCHAR(32) COMMENT '支付流水核查结果: NORMAL-正常, ABNORMAL-异常',
    motor_status_checked VARCHAR(32) COMMENT '电机状态核查结果: NORMAL-正常, FAULT-故障',
    sensor_status_checked VARCHAR(32) COMMENT '传感器状态核查结果: NORMAL-正常, FAULT-故障',
    inventory_change_checked VARCHAR(32) COMMENT '库存变化核查结果: NORMAL-正常, ABNORMAL-异常',
    monitor_photo_checked VARCHAR(32) COMMENT '监控照片核查结果: NORMAL-正常, ABNORMAL-异常',
    check_remark TEXT COMMENT '核查备注',
    handle_decision VARCHAR(32) COMMENT '处理决定: REFUND-退款, REISSUE-补发, REPAIR-维修',
    refund_amount DECIMAL(10,2) COMMENT '退款金额',
    reissue_sku_id BIGINT COMMENT '补发SKU ID',
    reissue_grid_no VARCHAR(32) COMMENT '补发出货格口',
    repair_content TEXT COMMENT '维修内容',
    status VARCHAR(32) DEFAULT 'PENDING_REPAIR' COMMENT '状态: PENDING_REPAIR-待维修确认, COMPLETED-已完成',
    cs_handler_id BIGINT COMMENT '客服处理人ID',
    handle_time DATETIME COMMENT '处理时间',
    repairer_id BIGINT COMMENT '维修人员ID',
    repairer_confirm_result VARCHAR(32) COMMENT '维修确认结果: ITEM_STILL_IN_GRID-实物仍在, REPAIRED-已修复, ITEM_LOST-物品丢失',
    repairer_confirm_time DATETIME COMMENT '维修确认时间',
    inventory_correction_id BIGINT COMMENT '库存修正记录ID',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_ticket_id (ticket_id),
    INDEX idx_order_id (order_id),
    INDEX idx_machine_id (machine_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='卡货处理记录表';

-- 库存修正记录表
CREATE TABLE IF NOT EXISTS inventory_correction (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    correction_no VARCHAR(64) NOT NULL UNIQUE COMMENT '修正记录编号',
    stuck_record_id BIGINT COMMENT '关联卡货处理记录ID',
    stuck_record_no VARCHAR(64) COMMENT '关联卡货处理记录编号',
    machine_id BIGINT COMMENT '机器ID',
    machine_code VARCHAR(64) COMMENT '机器编号',
    grid_no INT COMMENT '格口号',
    sku_id BIGINT COMMENT 'SKU ID',
    sku_name VARCHAR(128) COMMENT 'SKU名称',
    before_inventory INT COMMENT '修正前库存',
    after_inventory INT COMMENT '修正后库存',
    correction_qty INT COMMENT '修正数量',
    before_revenue DECIMAL(10,2) COMMENT '修正前收益',
    after_revenue DECIMAL(10,2) COMMENT '修正后收益',
    correction_revenue DECIMAL(10,2) COMMENT '修正收益',
    correction_type VARCHAR(32) COMMENT '修正类型: REVERSE_CORRECTION-反向修正',
    reason TEXT COMMENT '修正原因',
    operator_id BIGINT COMMENT '操作人ID',
    operate_time DATETIME COMMENT '操作时间',
    status VARCHAR(32) DEFAULT 'COMPLETED' COMMENT '状态: PENDING-待处理, COMPLETED-已完成',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_machine_id (machine_id),
    INDEX idx_sku_id (sku_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存修正记录表';

-- 点位迁移记录表
CREATE TABLE IF NOT EXISTS machine_migration (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    migration_no VARCHAR(64) NOT NULL UNIQUE COMMENT '迁移单号',
    machine_id BIGINT NOT NULL COMMENT '机器ID',
    machine_code VARCHAR(64) NOT NULL COMMENT '机器编号',
    machine_name VARCHAR(128) COMMENT '机器名称',
    old_mall_id BIGINT NOT NULL COMMENT '原商场ID',
    old_mall_name VARCHAR(128) COMMENT '原商场名称',
    old_location_detail VARCHAR(255) COMMENT '原位置描述',
    old_commission_rate DECIMAL(5,2) COMMENT '原扣点比例(%)',
    new_mall_id BIGINT NOT NULL COMMENT '新商场ID',
    new_mall_name VARCHAR(128) COMMENT '新商场名称',
    new_location_detail VARCHAR(255) COMMENT '新位置描述',
    new_commission_rate DECIMAL(5,2) NOT NULL COMMENT '新扣点比例(%)',
    migration_reason VARCHAR(255) NOT NULL COMMENT '迁移原因: MALL_REQUEST-商场要求, POWER_CONSTRUCTION-断电施工, FLOOR_ADJUST-楼层调整, OTHER-其他',
    migration_start_time DATETIME NOT NULL COMMENT '迁移开始时间',
    migration_end_time DATETIME COMMENT '迁移完成时间',
    downtime_minutes INT DEFAULT 0 COMMENT '停机时长(分钟)',
    downtime_loss DECIMAL(10,2) DEFAULT 0.00 COMMENT '停机损失金额',
    loss_remark TEXT COMMENT '损失说明',
    total_inventory_before INT DEFAULT 0 COMMENT '迁移前总库存',
    total_inventory_after INT DEFAULT 0 COMMENT '迁移后总库存',
    transferred_inventory INT DEFAULT 0 COMMENT '已转移库存',
    lost_inventory INT DEFAULT 0 COMMENT '丢失库存',
    inventory_transfer_status VARCHAR(32) DEFAULT 'PENDING' COMMENT '库存转移状态: PENDING-待转移, COMPLETE-全部转移, PARTIAL-部分转移',
    status VARCHAR(32) DEFAULT 'IN_PROGRESS' COMMENT '状态: IN_PROGRESS-进行中, COMPLETED-已完成, CANCELLED-已取消',
    operator_id BIGINT COMMENT '操作人ID',
    operator_name VARCHAR(64) COMMENT '操作人姓名',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_machine_id (machine_id),
    INDEX idx_old_mall_id (old_mall_id),
    INDEX idx_new_mall_id (new_mall_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点位迁移记录表';

-- 迁移库存明细表
CREATE TABLE IF NOT EXISTS migration_inventory_detail (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    migration_id BIGINT NOT NULL COMMENT '迁移记录ID',
    migration_no VARCHAR(64) NOT NULL COMMENT '迁移单号',
    sku_id BIGINT NOT NULL COMMENT 'SKU ID',
    sku_name VARCHAR(128) COMMENT 'SKU名称',
    sku_code VARCHAR(64) COMMENT 'SKU编码',
    series_id BIGINT COMMENT '系列ID',
    series_name VARCHAR(128) COMMENT '系列名称',
    qty_before INT DEFAULT 0 COMMENT '迁移前数量',
    qty_after INT DEFAULT 0 COMMENT '迁移后数量',
    qty_transferred INT DEFAULT 0 COMMENT '转移数量',
    qty_lost INT DEFAULT 0 COMMENT '丢失数量',
    transfer_status VARCHAR(32) DEFAULT 'PENDING' COMMENT '转移状态: PENDING-待转移, COMPLETE-全部转移, PARTIAL-部分转移',
    remark VARCHAR(255) COMMENT '备注',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_migration_id (migration_id),
    INDEX idx_sku_id (sku_id),
    INDEX idx_series_id (series_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='迁移库存明细表';

-- 初始化卡货处理记录数据
INSERT INTO stuck_handle_record (id, record_no, ticket_id, ticket_no, order_id, order_no, machine_id, machine_code, grid_no, sku_id, sku_name, pay_flow_checked, motor_status_checked, sensor_status_checked, inventory_change_checked, monitor_photo_checked, check_remark, handle_decision, refund_amount, status, cs_handler_id, handle_time) VALUES
(1, 'SH20240115001', 1, 'TK20240115001', 3, 'PO2024011500003', 2, 'BXM-0002', 25, 10, '通信员', 'NORMAL', 'FAULT', 'NORMAL', 'NORMAL', 'ABNORMAL', '经核查，电机故障导致卡货，监控显示商品未掉落，用户反馈属实。', 'REPAIR', NULL, 'PENDING_REPAIR', 5, '2024-01-15 15:30:00');

-- 初始化点位迁移记录数据
INSERT INTO machine_migration (id, migration_no, machine_id, machine_code, machine_name, old_mall_id, old_mall_name, old_location_detail, old_commission_rate, new_mall_id, new_mall_name, new_location_detail, new_commission_rate, migration_reason, migration_start_time, downtime_minutes, downtime_loss, loss_remark, total_inventory_before, inventory_transfer_status, status, operator_id, operator_name, created_time) VALUES
(1, 'MM20240115001', 3, 'BXM-0003', '万达1号机', 2, '万达广场', '2楼童装区', 12.00, 1, '万象城购物中心', 'B1层超市入口旁', 15.00, 'FLOOR_ADJUST', '2024-01-16 08:00:00', 240, 500.00, '预计4小时停机，约影响500元销售额', 0, 'PENDING', 'IN_PROGRESS', 1, '运营管理员', '2024-01-15 16:00:00');
