-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS beijing DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE beijing;

-- ==================== 基础表结构 ====================

-- 1. 用户表
CREATE TABLE user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) COMMENT '昵称',
    password VARCHAR(100) NOT NULL COMMENT '密码（MD5加密）',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    avatar VARCHAR(500) COMMENT '头像URL',
    role VARCHAR(20) DEFAULT 'user' COMMENT '角色：user普通用户，inheritor传承人，admin管理员',
    interest_tags VARCHAR(255) COMMENT '感兴趣的非遗类别，逗号分隔',
    invite_code VARCHAR(50) COMMENT '传承人邀请码',
    bio TEXT COMMENT '个人简介',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_email (email),
    INDEX idx_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 2. 非遗项目表
CREATE TABLE heritage_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '项目ID',
    name VARCHAR(100) NOT NULL COMMENT '项目名称',
    category VARCHAR(50) NOT NULL COMMENT '类别（传统技艺、曲艺等）',
    region VARCHAR(50) COMMENT '申报地区',
    level VARCHAR(20) COMMENT '级别（国家级/市级）',
    number VARCHAR(50) COMMENT '遗产编号',
    description TEXT COMMENT '详细介绍',
    image_url VARCHAR(500) COMMENT '图片URL',
    video_url VARCHAR(500) COMMENT '视频URL',
    inheritor_id BIGINT COMMENT '关联的传承人ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_category (category),
    INDEX idx_inheritor (inheritor_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='非遗项目表';

-- 3. 传承人档案表
CREATE TABLE inheritor (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '传承人ID',
    name VARCHAR(50) NOT NULL COMMENT '传承人姓名',
    level VARCHAR(20) COMMENT '级别（国家级/市级）',
    gender VARCHAR(10) COMMENT '性别',
    birth_year INT COMMENT '出生年份',
    biography TEXT COMMENT '人物简介/口述史',
    image_url VARCHAR(500) COMMENT '照片',
    user_id BIGINT COMMENT '关联的用户ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='传承人档案表';

-- 4. 非遗点位表
CREATE TABLE heritage_site (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '点位ID',
    name VARCHAR(100) NOT NULL COMMENT '点位名称',
    address VARCHAR(255) COMMENT '详细地址',
    longitude DECIMAL(10,6) COMMENT '经度',
    latitude DECIMAL(10,6) COMMENT '纬度',
    heritage_item_id BIGINT COMMENT '关联的非遗项目ID',
    contact VARCHAR(100) COMMENT '联系方式',
    open_hours VARCHAR(100) COMMENT '开放时间',
    type VARCHAR(50) COMMENT '类型：博物馆、工坊、演出场所、工作室等',
    description TEXT COMMENT '点位描述',
    image_url VARCHAR(500) COMMENT '点位图片',
    INDEX idx_item (heritage_item_id),
    INDEX idx_type (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='非遗点位表';

-- 5. 活动表
CREATE TABLE activity (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '活动ID',
    title VARCHAR(100) NOT NULL COMMENT '活动标题',
    type VARCHAR(50) COMMENT '活动类型：体验课、讲座、展览等',
    inheritor_id BIGINT COMMENT '主办传承人ID',
    site_id BIGINT COMMENT '举办点位ID',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    max_participants INT DEFAULT 0 COMMENT '最大参与人数',
    current_participants INT DEFAULT 0 COMMENT '当前报名人数',
    description TEXT COMMENT '活动描述',
    image_url VARCHAR(500) COMMENT '活动封面图',
    status INT DEFAULT 0 COMMENT '状态：0-待审核，1-已发布，2-已结束',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_inheritor (inheritor_id),
    INDEX idx_site (site_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动表';

-- 6. 收藏表（来自 beijing1.sql，保留详细注释）
CREATE TABLE collection (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '收藏ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    target_type VARCHAR(20) NOT NULL COMMENT '收藏类型：item, site, activity',
    target_id BIGINT NOT NULL COMMENT '对应类型的ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    UNIQUE KEY uk_user_target (user_id, target_type, target_id) COMMENT '唯一约束，防止重复收藏',
    INDEX idx_user (user_id),
    INDEX idx_target (target_type, target_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

-- 7. 用户足迹表
CREATE TABLE footprint (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '足迹ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    target_type VARCHAR(20) NOT NULL COMMENT '目标类型：item, site, activity',
    target_id BIGINT NOT NULL COMMENT '目标ID',
    action VARCHAR(20) COMMENT '动作：view, collect, signup等',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发生时间',
    INDEX idx_user (user_id),
    INDEX idx_time (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户足迹表';

-- 8. 招募发布表（来自 beijing2.sql）
CREATE TABLE IF NOT EXISTS recruit_post (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    inheritor_id BIGINT NOT NULL,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    inheritor_name VARCHAR(50),
    applicant_count INT DEFAULT 0,
    status INT DEFAULT 0 COMMENT '0-招募中,1-已结束',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 9. 招募申请表（来自 beijing2.sql）
CREATE TABLE IF NOT EXISTS recruit_application (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    recruit_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    status VARCHAR(20) DEFAULT 'pending' COMMENT 'pending,accepted,rejected',
    apply_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    process_time DATETIME,
    UNIQUE KEY uk_recruit_user (recruit_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 10. 活动报名表（来自 beijing4.sql）
CREATE TABLE IF NOT EXISTS activity_signup (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    activity_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    signup_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_activity_user (activity_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 11. 登录日志表（来自 beijing5.sql）
CREATE TABLE IF NOT EXISTS login_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    login_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_time (user_id, login_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 12. 评论表（来自 beijing5.sql）
CREATE TABLE IF NOT EXISTS comment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user (user_id),
    INDEX idx_time (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==================== 额外表（来自 beijing1.sql） ====================

-- 13. 作品表
CREATE TABLE IF NOT EXISTS artwork (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '作品ID',
    title VARCHAR(100) NOT NULL COMMENT '作品标题',
    description TEXT COMMENT '作品描述',
    image_url VARCHAR(500) NOT NULL COMMENT '图片URL',
    inheritor_id BIGINT NOT NULL COMMENT '发布作品的传承人ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    INDEX idx_inheritor (inheritor_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='传承人作品表';

-- ==================== 表结构修改（来自 beijing2.sql 和 beijing.3.sql） ====================

-- 修改 activity_signup 表，增加状态字段（beijing2.sql）
ALTER TABLE activity_signup ADD COLUMN status VARCHAR(20) DEFAULT 'pending' COMMENT 'pending, accepted, rejected' AFTER user_id;
ALTER TABLE activity_signup ADD COLUMN apply_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间';
ALTER TABLE activity_signup ADD COLUMN process_time DATETIME COMMENT '处理时间';

-- 修改 recruit_post 表，增加最大申请人数（beijing.3.sql）
ALTER TABLE recruit_post ADD COLUMN max_applicants INT DEFAULT 10 COMMENT '最大申请人数' AFTER applicant_count;

-- ==================== 插入初始数据（来自 beijing.sql） ====================

-- 插入管理员用户（密码为 123456 的 MD5 值：e10adc3949ba59abbe56e057f20f883e）
INSERT INTO user (username, password, email, role, created_at) VALUES
('admin', 'e10adc3949ba59abbe56e057f20f883e', 'admin@jingyun.com', 'admin', NOW());

-- 插入示例传承人
INSERT INTO inheritor (name, level, gender, birth_year, biography, image_url) VALUES
('钟连盛', '国家级', '男', 1962, '景泰蓝技艺代表性传承人，从事景泰蓝制作40余年。', 'https://picsum.photos/200/200?random=1'),
('李金斗', '国家级', '男', 1947, '著名相声表演艺术家，相声非遗传承人。', 'https://picsum.photos/200/200?random=2'),
('殷秀云', '国家级', '女', 1953, '雕漆技艺大师，作品多次获奖。', 'https://picsum.photos/200/200?random=3'),
('郎志丽', '国家级', '女', 1942, '面人郎第三代传人，面塑艺术大师。', 'https://picsum.photos/200/200?random=4');

-- 插入示例非遗项目
INSERT INTO heritage_item (name, category, region, level, number, description, inheritor_id, image_url) VALUES
('景泰蓝制作技艺', '传统技艺', '东城区', '国家级', 'Ⅷ-1', '铜胎掐丝珐琅，燕京八绝之一，工艺繁复，色彩绚丽。', 1, 'https://picsum.photos/400/200?random=101'),
('京剧', '传统戏剧', '西城区', '国家级', 'Ⅳ-1', '国粹，融合唱念做打，生旦净末丑。', NULL, 'https://picsum.photos/400/200?random=102'),
('相声', '曲艺', '西城区', '国家级', 'Ⅴ-1', '说学逗唱，幽默机锋，京津地区深受喜爱。', 2, 'https://picsum.photos/400/200?random=103'),
('雕漆技艺', '传统技艺', '东城区', '国家级', 'Ⅷ-2', '在漆胎上雕刻花纹，工艺繁复，用料考究。', 3, 'https://picsum.photos/400/200?random=104'),
('北京面人郎', '传统美术', '海淀区', '国家级', 'Ⅶ-1', '面塑艺术，细腻传神。', 4, 'https://picsum.photos/400/200?random=105');

-- 插入示例非遗点位（初始4个）
INSERT INTO heritage_site (name, address, longitude, latitude, heritage_item_id, contact, open_hours, type, description, image_url) VALUES
('景泰蓝博物馆', '东城区安乐林路10号', 116.410, 39.916, 1, '010-12345678', '9:00-17:00 (周一闭馆)', '博物馆', '展示景泰蓝历史与精品，可观摩制作流程。', 'https://picsum.photos/400/200?random=201'),
('老舍茶馆', '西城区前门西大街正阳市场3号楼', 116.395, 39.900, NULL, '010-12345679', '10:00-22:00', '演出场所', '品茶听相声，感受京味文化。', 'https://picsum.photos/400/200?random=202'),
('内联升非遗工坊', '西城区大栅栏街34号', 116.403, 39.906, NULL, '010-12345680', '9:30-20:00', '工坊', '体验手工纳底，定制专属布鞋。', 'https://picsum.photos/400/200?random=203'),
('荣宝斋', '西城区琉璃厂西街19号', 116.397, 39.912, NULL, '010-12345681', '9:00-18:00', '工作室', '百年老店，木版水印和文房四宝。', 'https://picsum.photos/400/200?random=204');

-- 插入更多非遗点位（共16个，使总数达到20）
INSERT INTO heritage_site (name, address, longitude, latitude, heritage_item_id, contact, open_hours, type, description, image_url) VALUES
('北京珐琅厂', '东城区安乐林路10号', 116.411, 39.915, 1, '010-6714 3672', '9:00-17:00', '工坊', '景泰蓝制作体验和展示。', 'https://picsum.photos/400/200?random=205'),
('天桥印象博物馆', '西城区天桥南大街7号', 116.394, 39.883, NULL, '010-6303 7654', '9:00-17:00', '博物馆', '展示老天桥民俗文化。', 'https://picsum.photos/400/200?random=206'),
('北京皮影剧团', '西城区前门大街', 116.398, 39.894, NULL, '010-6701 2345', '10:00-18:00', '演出场所', '皮影戏演出和展示。', 'https://picsum.photos/400/200?random=207'),
('北京绢人工艺工作室', '朝阳区高碑店', 116.523, 39.906, NULL, '010-8576 5432', '9:30-17:30', '工作室', '绢人制作工艺展示。', 'https://picsum.photos/400/200?random=208'),
('北京玉雕厂', '东城区光明路', 116.431, 39.879, NULL, '010-6718 7654', '9:00-18:00', '工坊', '玉雕技艺展示。', 'https://picsum.photos/400/200?random=209'),
('北京宫毯织造技艺展示中心', '朝阳区北苑路', 116.422, 40.028, NULL, '010-8492 3456', '9:00-17:00', '博物馆', '宫毯织造技艺展示。', 'https://picsum.photos/400/200?random=210'),
('北京雕漆厂', '东城区光明路', 116.432, 39.878, 4, '010-6712 7890', '9:00-17:00', '工坊', '雕漆工艺制作。', 'https://picsum.photos/400/200?random=211'),
('北京风筝工坊', '海淀区厂洼街', 116.297, 39.961, NULL, '010-6845 6789', '9:00-18:00', '工坊', '传统风筝制作。', 'https://picsum.photos/400/200?random=212'),
('北京鬃人工作室', '西城区琉璃厂', 116.395, 39.910, NULL, '010-8315 4321', '9:30-17:30', '工作室', '鬃人艺术。', 'https://picsum.photos/400/200?random=213'),
('北京毛猴工作室', '西城区南锣鼓巷', 116.404, 39.935, NULL, '010-6401 8765', '10:00-18:00', '工作室', '毛猴制作展示。', 'https://picsum.photos/400/200?random=214'),
('北京传统音乐厅', '西城区北新华街', 116.389, 39.904, NULL, '010-6605 4321', '按演出时间', '演出场所', '传统音乐演出。', 'https://picsum.photos/400/200?random=215'),
('北京曲艺团', '西城区前门', 116.397, 39.896, 3, '010-6702 3456', '按演出时间', '演出场所', '相声、鼓曲等演出。', 'https://picsum.photos/400/200?random=216'),
('北京中医药大学中医药博物馆', '朝阳区北三环东路11号', 116.427, 39.978, NULL, '010-6428 7654', '9:00-16:00', '博物馆', '中医药文物展示。', 'https://picsum.photos/400/200?random=217'),
('北京民俗博物馆', '朝阳区朝阳门外大街141号', 116.447, 39.925, NULL, '010-6551 2345', '9:00-16:30', '博物馆', '北京民俗文化。', 'https://picsum.photos/400/200?random=218'),
('北京花丝镶嵌工坊', '通州区宋庄', 116.724, 39.951, NULL, '010-8956 7890', '9:00-18:00', '工坊', '花丝镶嵌工艺。', 'https://picsum.photos/400/200?random=219'),
('北京二锅头酒传统酿造技艺展示中心', '大兴区瀛海镇', 116.435, 39.756, NULL, '010-6928 1234', '9:00-17:00', '博物馆', '二锅头酿造工艺展示。', 'https://picsum.photos/400/200?random=220');

-- 插入示例活动
INSERT INTO activity (title, type, inheritor_id, site_id, start_time, end_time, max_participants, description, status) VALUES
('景泰蓝体验课', '体验课', 1, 1, '2026-04-15 14:00:00', '2026-04-15 17:00:00', 15, '亲手体验掐丝、点蓝，制作专属小件。', 1),
('相声开放桌', '演出', 2, 2, '2026-04-20 19:30:00', '2026-04-20 21:30:00', 50, '老舍茶馆相声专场，可与演员交流互动。', 1),
('内联升纳底体验', '体验课', NULL, 3, '2026-04-22 09:30:00', '2026-04-22 11:30:00', 10, '学习手工纳底，感受千层底技艺。', 1);

-- 插入示例收藏（假设用户ID为1，即admin）
INSERT INTO collection (user_id, target_type, target_id) VALUES
(1, 'item', 1),
(1, 'item', 3),
(1, 'site', 2);

-- 插入示例足迹
INSERT INTO footprint (user_id, target_type, target_id, action) VALUES
(1, 'item', 1, 'view'),
(1, 'item', 2, 'view'),
(1, 'site', 3, 'view'),
(1, 'activity', 1, 'signup');