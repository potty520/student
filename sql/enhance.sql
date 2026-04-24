-- 学生成绩管理系统数据库增强脚本
-- 作者: Qoder
-- 版本: 1.1.0
-- 更新时间: 2025-08-25

USE `student_grade_system`;

-- 系统日志表 (SM02需求)
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `log_type` tinyint(4) NOT NULL COMMENT '日志类型 1:登录 2:操作 3:错误 4:系统',
  `operation` varchar(100) DEFAULT NULL COMMENT '操作内容',
  `method` varchar(100) DEFAULT NULL COMMENT '请求方法',
  `params` text COMMENT '请求参数',
  `result` text COMMENT '操作结果',
  `ip` varchar(50) DEFAULT NULL COMMENT 'IP地址',
  `user_agent` varchar(500) DEFAULT NULL COMMENT '用户代理',
  `user_id` bigint(20) DEFAULT NULL COMMENT '操作用户ID',
  `username` varchar(50) DEFAULT NULL COMMENT '用户名',
  `execution_time` bigint(20) DEFAULT NULL COMMENT '执行时间(毫秒)',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态 0:失败 1:成功',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_log_type` (`log_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统日志表';

-- 成绩统计表 (自动计算三率统计)
DROP TABLE IF EXISTS `score_statistics`;
CREATE TABLE `score_statistics` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `exam_id` bigint(20) NOT NULL COMMENT '考试ID',
  `class_id` bigint(20) DEFAULT NULL COMMENT '班级ID',
  `course_id` bigint(20) NOT NULL COMMENT '课程ID',
  `stat_type` tinyint(4) NOT NULL COMMENT '统计类型 1:班级 2:年级 3:全校',
  `total_count` int(11) NOT NULL DEFAULT '0' COMMENT '总人数',
  `attend_count` int(11) NOT NULL DEFAULT '0' COMMENT '实考人数',
  `absent_count` int(11) NOT NULL DEFAULT '0' COMMENT '缺考人数',
  `max_score` decimal(5,1) DEFAULT NULL COMMENT '最高分',
  `min_score` decimal(5,1) DEFAULT NULL COMMENT '最低分',
  `avg_score` decimal(5,1) DEFAULT NULL COMMENT '平均分',
  `excellent_count` int(11) DEFAULT '0' COMMENT '优秀人数(>=85)',
  `excellent_rate` decimal(5,2) DEFAULT NULL COMMENT '优秀率',
  `good_count` int(11) DEFAULT '0' COMMENT '良好人数(>=70)',
  `good_rate` decimal(5,2) DEFAULT NULL COMMENT '良好率',
  `pass_count` int(11) DEFAULT '0' COMMENT '及格人数(>=60)',
  `pass_rate` decimal(5,2) DEFAULT NULL COMMENT '及格率',
  `fail_count` int(11) DEFAULT '0' COMMENT '不及格人数(<60)',
  `fail_rate` decimal(5,2) DEFAULT NULL COMMENT '不及格率',
  `score_segments` json DEFAULT NULL COMMENT '分数段分布',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_exam_class_course_type` (`exam_id`,`class_id`,`course_id`,`stat_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成绩统计表';

-- 学期表
DROP TABLE IF EXISTS `school_semester`;
CREATE TABLE `school_semester` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `semester_name` varchar(50) NOT NULL COMMENT '学期名称',
  `school_year` varchar(20) NOT NULL COMMENT '学年',
  `semester` tinyint(4) NOT NULL COMMENT '学期 1:第一学期 2:第二学期',
  `start_date` date NOT NULL COMMENT '开始日期',
  `end_date` date NOT NULL COMMENT '结束日期',
  `is_current` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否当前学期 0:否 1:是',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态 0:未开始 1:进行中 2:已结束',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '删除标记 0:未删除 1:已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_school_year_semester` (`school_year`,`semester`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学期表';

-- 消息通知表 (PM02需求)
-- 注意：sys_message_receive 可能存在外键引用 sys_message，需先删子表再删父表
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `sys_message_receive`;
DROP TABLE IF EXISTS `sys_message`;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE `sys_message` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` varchar(200) NOT NULL COMMENT '消息标题',
  `content` text NOT NULL COMMENT '消息内容',
  `message_type` tinyint(4) NOT NULL COMMENT '消息类型 1:系统通知 2:成绩发布 3:考试通知 4:家长会通知',
  `sender_id` bigint(20) DEFAULT NULL COMMENT '发送人ID',
  `sender_name` varchar(50) DEFAULT NULL COMMENT '发送人姓名',
  `target_type` tinyint(4) NOT NULL COMMENT '接收对象类型 1:全部 2:角色 3:班级 4:个人',
  `target_ids` varchar(500) DEFAULT NULL COMMENT '接收对象ID列表',
  `send_method` tinyint(4) NOT NULL COMMENT '发送方式 1:站内信 2:短信 3:微信 4:邮件',
  `send_time` datetime DEFAULT NULL COMMENT '发送时间',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态 0:草稿 1:已发送 2:发送失败',
  `read_count` int(11) DEFAULT '0' COMMENT '已读人数',
  `total_count` int(11) DEFAULT '0' COMMENT '总接收人数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '删除标记 0:未删除 1:已删除',
  PRIMARY KEY (`id`),
  KEY `idx_sender_id` (`sender_id`),
  KEY `idx_message_type` (`message_type`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息通知表';

-- 消息接收表
CREATE TABLE `sys_message_receive` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `message_id` bigint(20) NOT NULL COMMENT '消息ID',
  `receiver_id` bigint(20) NOT NULL COMMENT '接收人ID',
  `receiver_name` varchar(50) DEFAULT NULL COMMENT '接收人姓名',
  `is_read` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否已读 0:未读 1:已读',
  `read_time` datetime DEFAULT NULL COMMENT '阅读时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_message_receiver` (`message_id`,`receiver_id`),
  KEY `idx_receiver_id` (`receiver_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息接收表';

-- 数据备份记录表 (SM03需求)
DROP TABLE IF EXISTS `sys_backup`;
CREATE TABLE `sys_backup` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `backup_name` varchar(100) NOT NULL COMMENT '备份名称',
  `backup_type` tinyint(4) NOT NULL COMMENT '备份类型 1:手动 2:自动',
  `backup_path` varchar(500) NOT NULL COMMENT '备份路径',
  `file_size` bigint(20) DEFAULT NULL COMMENT '文件大小(字节)',
  `backup_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '备份状态 0:进行中 1:成功 2:失败',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `error_message` text COMMENT '错误信息',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_backup_type` (`backup_type`),
  KEY `idx_start_time` (`start_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据备份记录表';

-- 学生总成绩表 (GM03需求 - 存储每次考试的总分和排名)
DROP TABLE IF EXISTS `student_total_score`;
CREATE TABLE `student_total_score` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `exam_id` bigint(20) NOT NULL COMMENT '考试ID',
  `student_id` bigint(20) NOT NULL COMMENT '学生ID',
  `total_score` decimal(8,1) DEFAULT NULL COMMENT '总分',
  `avg_score` decimal(5,1) DEFAULT NULL COMMENT '平均分',
  `subject_count` int(11) DEFAULT '0' COMMENT '考试科目数',
  `class_rank` int(11) DEFAULT NULL COMMENT '班级排名',
  `grade_rank` int(11) DEFAULT NULL COMMENT '年级排名',
  `class_total` int(11) DEFAULT NULL COMMENT '班级总人数',
  `grade_total` int(11) DEFAULT NULL COMMENT '年级总人数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_exam_student` (`exam_id`,`student_id`),
  KEY `idx_student_id` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生总成绩表';

-- 成绩变化趋势表 (GM04需求)
DROP TABLE IF EXISTS `score_trend`;
CREATE TABLE `score_trend` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `student_id` bigint(20) NOT NULL COMMENT '学生ID',
  `course_id` bigint(20) NOT NULL COMMENT '课程ID',
  `current_exam_id` bigint(20) NOT NULL COMMENT '当前考试ID',
  `previous_exam_id` bigint(20) DEFAULT NULL COMMENT '上次考试ID',
  `current_score` decimal(5,1) DEFAULT NULL COMMENT '当前成绩',
  `previous_score` decimal(5,1) DEFAULT NULL COMMENT '上次成绩',
  `score_change` decimal(6,1) DEFAULT NULL COMMENT '成绩变化',
  `trend_type` tinyint(4) DEFAULT NULL COMMENT '趋势类型 1:上升 2:下降 3:持平',
  `current_rank` int(11) DEFAULT NULL COMMENT '当前排名',
  `previous_rank` int(11) DEFAULT NULL COMMENT '上次排名',
  `rank_change` int(11) DEFAULT NULL COMMENT '排名变化',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_student_course_exam` (`student_id`,`course_id`,`current_exam_id`),
  KEY `idx_current_exam` (`current_exam_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成绩变化趋势表';

-- 添加学年学期到当前学期表
INSERT INTO `school_semester` (`semester_name`, `school_year`, `semester`, `start_date`, `end_date`, `is_current`, `status`) VALUES
('2024-2025学年第一学期', '2024-2025', 1, '2024-09-01', '2025-01-20', 1, 1),
('2024-2025学年第二学期', '2024-2025', 2, '2025-02-20', '2025-07-10', 0, 0);

-- 添加更多权限数据
-- 适配当前表结构：sys_permission(path/perms/sort_order/status/...)
INSERT INTO `sys_permission` (`permission_code`, `permission_name`, `permission_type`, `parent_id`, `path`, `perms`, `icon`, `sort_order`, `status`, `is_frame`, `is_cache`, `description`, `deleted`) VALUES
-- 新增功能权限
('grade:statistics:export', '导出统计', 2, 17, NULL, 'grade:statistics:export', NULL, 3031, 1, 0, 0, NULL, 0),
('grade:statistics:print', '打印报表', 2, 17, NULL, 'grade:statistics:print', NULL, 3032, 1, 0, 0, NULL, 0),
('sys:message', '消息通知', 1, 1, '/system/message', 'sys:message', 'ChatLineRound', 104, 1, 0, 0, NULL, 0),
('sys:message:send', '发送消息', 2, 33, NULL, 'sys:message:send', NULL, 1041, 1, 0, 0, NULL, 0),
('sys:backup', '数据备份', 1, 1, '/system/backup', 'sys:backup', 'Download', 105, 1, 0, 0, NULL, 0),
('sys:backup:create', '创建备份', 2, 34, NULL, 'sys:backup:create', NULL, 1051, 1, 0, 0, NULL, 0),
('sys:backup:restore', '恢复备份', 2, 34, NULL, 'sys:backup:restore', NULL, 1052, 1, 0, 0, NULL, 0),
('report:trend', '成绩趋势分析', 1, 4, '/report/trend', 'report:trend', 'TrendCharts', 404, 1, 0, 0, NULL, 0),
('basic:grade:add', '新增年级', 2, 0, NULL, 'basic:grade:add', NULL, 1101, 1, 0, 0, '基础信息-年级新增', 0),
('basic:grade:edit', '编辑年级', 2, 0, NULL, 'basic:grade:edit', NULL, 1102, 1, 0, 0, '基础信息-年级编辑', 0),
('basic:grade:delete', '删除年级', 2, 0, NULL, 'basic:grade:delete', NULL, 1103, 1, 0, 0, '基础信息-年级删除', 0),
('basic:class:add', '新增班级', 2, 0, NULL, 'basic:class:add', NULL, 1201, 1, 0, 0, '基础信息-班级新增', 0),
('basic:class:edit', '编辑班级', 2, 0, NULL, 'basic:class:edit', NULL, 1202, 1, 0, 0, '基础信息-班级编辑', 0),
('basic:class:delete', '删除班级', 2, 0, NULL, 'basic:class:delete', NULL, 1203, 1, 0, 0, '基础信息-班级删除', 0),
('basic:course:add', '新增课程', 2, 0, NULL, 'basic:course:add', NULL, 1301, 1, 0, 0, '基础信息-课程新增', 0),
('basic:course:edit', '编辑课程', 2, 0, NULL, 'basic:course:edit', NULL, 1302, 1, 0, 0, '基础信息-课程编辑', 0),
('basic:course:delete', '删除课程', 2, 0, NULL, 'basic:course:delete', NULL, 1303, 1, 0, 0, '基础信息-课程删除', 0)
-- 幂等：若 permission_code 已存在则更新关键字段，避免重复执行报错
ON DUPLICATE KEY UPDATE
  `permission_name` = VALUES(`permission_name`),
  `permission_type` = VALUES(`permission_type`),
  `parent_id` = VALUES(`parent_id`),
  `path` = VALUES(`path`),
  `perms` = VALUES(`perms`),
  `icon` = VALUES(`icon`),
  `sort_order` = VALUES(`sort_order`),
  `status` = VALUES(`status`),
  `is_frame` = VALUES(`is_frame`),
  `is_cache` = VALUES(`is_cache`),
  `description` = VALUES(`description`),
  `deleted` = VALUES(`deleted`);

-- 管理员角色自动绑定上述权限（幂等）
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT r.id, p.id
FROM `sys_role` r
JOIN `sys_permission` p ON p.deleted = 0
WHERE r.role_code = 'ADMIN'
  AND p.permission_code IN (
    'grade:statistics:export',
    'grade:statistics:print',
    'sys:message',
    'sys:message:send',
    'sys:backup',
    'sys:backup:create',
    'sys:backup:restore',
    'report:trend',
    'basic:grade:add',
    'basic:grade:edit',
    'basic:grade:delete',
    'basic:class:add',
    'basic:class:edit',
    'basic:class:delete',
    'basic:course:add',
    'basic:course:edit',
    'basic:course:delete'
  )
  AND NOT EXISTS (
    SELECT 1
    FROM `sys_role_permission` rp
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

-- 更新已有考试数据状态
-- 适配当前表结构：exam_info
INSERT INTO `exam_info` (`exam_code`, `exam_name`, `exam_type`, `school_year`, `semester`, `start_date`, `end_date`, `grade_ids`, `course_ids`, `description`, `status`, `deleted`) VALUES
('EXAM_2024_MID', '2024-2025学年第一学期期中考试', 2, '2024-2025', 1, '2024-11-15', '2024-11-17', '1,2,3,4,5,6', '1,2,3,4,5', '期中考试安排', 1, 0),
('EXAM_2024_FINAL', '2024-2025学年第一学期期末考试', 3, '2024-2025', 1, '2025-01-10', '2025-01-12', '1,2,3,4,5,6', '1,2,3,4,5,6,7,8', '期末考试安排', 0, 0)
ON DUPLICATE KEY UPDATE
  `exam_name` = VALUES(`exam_name`),
  `exam_type` = VALUES(`exam_type`),
  `school_year` = VALUES(`school_year`),
  `semester` = VALUES(`semester`),
  `start_date` = VALUES(`start_date`),
  `end_date` = VALUES(`end_date`),
  `grade_ids` = VALUES(`grade_ids`),
  `course_ids` = VALUES(`course_ids`),
  `description` = VALUES(`description`),
  `status` = VALUES(`status`),
  `deleted` = VALUES(`deleted`);

-- 添加更多教师任课关系
-- 适配当前表结构：sys_teacher_class_course（无school_year/semester字段）
-- 注意：不要硬编码 teacher_id/class_id/course_id（可能与现有数据不一致），改为按编码查ID再插入
INSERT INTO `sys_teacher_class_course` (`teacher_id`, `class_id`, `course_id`, `status`, `deleted`)
SELECT t.id, c.id, co.id, 1, 0
FROM `sys_teacher` t
JOIN `sys_class` c
JOIN `sys_course` co
WHERE t.teacher_code = 'T2025001' AND c.class_code = 'C7-01' AND co.course_code = 'C-CH-001'
ON DUPLICATE KEY UPDATE `status`=VALUES(`status`), `deleted`=VALUES(`deleted`);

INSERT INTO `sys_teacher_class_course` (`teacher_id`, `class_id`, `course_id`, `status`, `deleted`)
SELECT t.id, c.id, co.id, 1, 0
FROM `sys_teacher` t
JOIN `sys_class` c
JOIN `sys_course` co
WHERE t.teacher_code = 'T2025001' AND c.class_code = 'C8-01' AND co.course_code = 'C-CH-001'
ON DUPLICATE KEY UPDATE `status`=VALUES(`status`), `deleted`=VALUES(`deleted`);

INSERT INTO `sys_teacher_class_course` (`teacher_id`, `class_id`, `course_id`, `status`, `deleted`)
SELECT t.id, c.id, co.id, 1, 0
FROM `sys_teacher` t
JOIN `sys_class` c
JOIN `sys_course` co
WHERE t.teacher_code = 'T2025002' AND c.class_code = 'C7-01' AND co.course_code = 'C-MA-001'
ON DUPLICATE KEY UPDATE `status`=VALUES(`status`), `deleted`=VALUES(`deleted`);

INSERT INTO `sys_teacher_class_course` (`teacher_id`, `class_id`, `course_id`, `status`, `deleted`)
SELECT t.id, c.id, co.id, 1, 0
FROM `sys_teacher` t
JOIN `sys_class` c
JOIN `sys_course` co
WHERE t.teacher_code = 'T2025002' AND c.class_code = 'C8-01' AND co.course_code = 'C-MA-001'
ON DUPLICATE KEY UPDATE `status`=VALUES(`status`), `deleted`=VALUES(`deleted`);

-- 创建视图：班级成绩概览
CREATE OR REPLACE VIEW `v_class_score_overview` AS
SELECT 
    c.id as class_id,
    c.class_name,
    g.grade_name,
    e.id as exam_id,
    e.exam_name,
    co.course_name,
    COUNT(s.id) as total_students,
    COUNT(sc.id) as attend_students,
    ROUND(AVG(sc.score), 1) as avg_score,
    MAX(sc.score) as max_score,
    MIN(sc.score) as min_score,
    SUM(CASE WHEN sc.score >= 85 THEN 1 ELSE 0 END) as excellent_count,
    SUM(CASE WHEN sc.score >= 60 THEN 1 ELSE 0 END) as pass_count
FROM sys_class c
LEFT JOIN sys_grade g ON c.grade_id = g.id
LEFT JOIN sys_student s ON c.id = s.class_id AND s.status = 1 AND s.deleted = 0
LEFT JOIN exam_score sc ON s.id = sc.student_id AND sc.deleted = 0
LEFT JOIN exam_info e ON sc.exam_id = e.id AND e.deleted = 0
LEFT JOIN sys_course co ON sc.course_id = co.id AND co.deleted = 0
WHERE c.deleted = 0 AND g.deleted = 0
GROUP BY c.id, e.id, co.id;

-- 创建视图：学生成绩详情
CREATE OR REPLACE VIEW `v_student_score_detail` AS
SELECT 
    s.id as student_id,
    s.student_code,
    s.student_name,
    c.class_name,
    g.grade_name,
    e.exam_name,
    co.course_name,
    sc.score,
    sc.class_rank,
    sc.grade_rank,
    sc.absent,
    CASE 
        WHEN sc.score >= 85 THEN '优秀'
        WHEN sc.score >= 70 THEN '良好'
        WHEN sc.score >= 60 THEN '及格'
        ELSE '不及格'
    END as grade_level
FROM sys_student s
LEFT JOIN sys_class c ON s.class_id = c.id
LEFT JOIN sys_grade g ON c.grade_id = g.id
LEFT JOIN exam_score sc ON s.id = sc.student_id AND sc.deleted = 0
LEFT JOIN exam_info e ON sc.exam_id = e.id AND e.deleted = 0
LEFT JOIN sys_course co ON sc.course_id = co.id AND co.deleted = 0
WHERE s.deleted = 0 AND s.status = 1;

-- 添加索引优化
-- 幂等创建索引（避免重复执行报 1061）
SET @db_name = DATABASE();

SET @sql = (
  SELECT IF(
    EXISTS(
      SELECT 1 FROM information_schema.statistics
      WHERE table_schema = @db_name AND table_name = 'exam_score' AND index_name = 'idx_exam_course'
    ),
    'SELECT 1',
    'ALTER TABLE `exam_score` ADD INDEX `idx_exam_course` (`exam_id`, `course_id`)'
  )
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = (
  SELECT IF(
    EXISTS(
      SELECT 1 FROM information_schema.statistics
      WHERE table_schema = @db_name AND table_name = 'exam_score' AND index_name = 'idx_student_exam'
    ),
    'SELECT 1',
    'ALTER TABLE `exam_score` ADD INDEX `idx_student_exam` (`student_id`, `exam_id`)'
  )
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = (
  SELECT IF(
    EXISTS(
      SELECT 1 FROM information_schema.statistics
      WHERE table_schema = @db_name AND table_name = 'sys_class' AND index_name = 'idx_grade_id'
    ),
    'SELECT 1',
    'ALTER TABLE `sys_class` ADD INDEX `idx_grade_id` (`grade_id`)'
  )
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = (
  SELECT IF(
    EXISTS(
      SELECT 1 FROM information_schema.statistics
      WHERE table_schema = @db_name AND table_name = 'sys_student' AND index_name = 'idx_class_id'
    ),
    'SELECT 1',
    'ALTER TABLE `sys_student` ADD INDEX `idx_class_id` (`class_id`)'
  )
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = (
  SELECT IF(
    EXISTS(
      SELECT 1 FROM information_schema.statistics
      WHERE table_schema = @db_name AND table_name = 'sys_teacher_class_course' AND index_name = 'idx_teacher_class_course'
    ),
    'SELECT 1',
    'ALTER TABLE `sys_teacher_class_course` ADD INDEX `idx_teacher_class_course` (`teacher_id`, `class_id`, `course_id`)'
  )
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;