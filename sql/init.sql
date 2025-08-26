-- 学生成绩管理系统数据库初始化脚本
-- 作者: Qoder
-- 版本: 1.0.0
-- 创建时间: 2025-08-25

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `student_grade_system` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `student_grade_system`;

-- 权限表
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `permission_code` varchar(100) NOT NULL COMMENT '权限编码',
  `permission_name` varchar(50) NOT NULL COMMENT '权限名称',
  `permission_type` tinyint(4) NOT NULL COMMENT '权限类型 1:菜单 2:按钮 3:接口',
  `parent_id` bigint(20) DEFAULT NULL COMMENT '父权限ID',
  `permission_path` varchar(200) DEFAULT NULL COMMENT '权限路径',
  `icon` varchar(50) DEFAULT NULL COMMENT '图标',
  `sort_order` int(11) DEFAULT '0' COMMENT '排序',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态 0:禁用 1:启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '删除标记 0:未删除 1:已删除',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_permission_code` (`permission_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 角色表
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_code` varchar(50) NOT NULL COMMENT '角色编码',
  `role_name` varchar(50) NOT NULL COMMENT '角色名称',
  `description` varchar(200) DEFAULT NULL COMMENT '角色描述',
  `sort_order` int(11) DEFAULT '0' COMMENT '排序',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态 0:禁用 1:启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '删除标记 0:未删除 1:已删除',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 用户表
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `real_name` varchar(50) NOT NULL COMMENT '真实姓名',
  `gender` tinyint(4) DEFAULT '1' COMMENT '性别 0:女 1:男',
  `phone` varchar(11) DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(200) DEFAULT NULL COMMENT '头像URL',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '用户状态 0:禁用 1:启用',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `last_login_ip` varchar(50) DEFAULT NULL COMMENT '最后登录IP',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '删除标记 0:未删除 1:已删除',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 角色权限关联表
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `permission_id` bigint(20) NOT NULL COMMENT '权限ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_permission` (`role_id`,`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- 用户角色关联表
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 年级表
DROP TABLE IF EXISTS `school_grade`;
CREATE TABLE `school_grade` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `grade_name` varchar(50) NOT NULL COMMENT '年级名称',
  `grade_code` varchar(20) NOT NULL COMMENT '年级编码',
  `stage` tinyint(4) NOT NULL COMMENT '学段 1:小学 2:初中 3:高中',
  `grade_level` int(11) NOT NULL COMMENT '年级序号',
  `school_year` varchar(20) NOT NULL COMMENT '学年',
  `sort_order` int(11) DEFAULT '0' COMMENT '排序',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态 0:禁用 1:启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '删除标记 0:未删除 1:已删除',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_grade_code` (`grade_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='年级表';

-- 教师表
DROP TABLE IF EXISTS `school_teacher`;
CREATE TABLE `school_teacher` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `teacher_code` varchar(20) NOT NULL COMMENT '教师工号',
  `teacher_name` varchar(50) NOT NULL COMMENT '教师姓名',
  `gender` tinyint(4) DEFAULT '1' COMMENT '性别 0:女 1:男',
  `id_card` varchar(18) DEFAULT NULL COMMENT '身份证号',
  `phone` varchar(11) DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `birth_date` date DEFAULT NULL COMMENT '出生日期',
  `hire_date` date DEFAULT NULL COMMENT '入职日期',
  `position` varchar(50) DEFAULT NULL COMMENT '职位',
  `education` varchar(20) DEFAULT NULL COMMENT '学历',
  `major` varchar(50) DEFAULT NULL COMMENT '专业',
  `user_id` bigint(20) DEFAULT NULL COMMENT '关联用户ID',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态 0:离职 1:在职',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '删除标记 0:未删除 1:已删除',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_teacher_code` (`teacher_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师表';

-- 班级表
DROP TABLE IF EXISTS `school_class`;
CREATE TABLE `school_class` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `class_name` varchar(50) NOT NULL COMMENT '班级名称',
  `class_code` varchar(20) NOT NULL COMMENT '班级编码',
  `grade_id` bigint(20) NOT NULL COMMENT '所属年级ID',
  `head_teacher_id` bigint(20) DEFAULT NULL COMMENT '班主任ID',
  `student_count` int(11) DEFAULT '0' COMMENT '班级人数',
  `sort_order` int(11) DEFAULT '0' COMMENT '排序',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态 0:禁用 1:启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '删除标记 0:未删除 1:已删除',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_class_code` (`class_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班级表';

-- 学生表
DROP TABLE IF EXISTS `school_student`;
CREATE TABLE `school_student` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `student_code` varchar(20) NOT NULL COMMENT '学号',
  `student_name` varchar(50) NOT NULL COMMENT '学生姓名',
  `gender` tinyint(4) DEFAULT '1' COMMENT '性别 0:女 1:男',
  `id_card` varchar(18) DEFAULT NULL COMMENT '身份证号',
  `birth_date` date DEFAULT NULL COMMENT '出生日期',
  `enrollment_date` date DEFAULT NULL COMMENT '入学日期',
  `class_id` bigint(20) NOT NULL COMMENT '所属班级ID',
  `guardian_name` varchar(50) DEFAULT NULL COMMENT '监护人姓名',
  `guardian_relation` varchar(20) DEFAULT NULL COMMENT '监护人关系',
  `guardian_phone` varchar(11) DEFAULT NULL COMMENT '监护人电话',
  `address` varchar(200) DEFAULT NULL COMMENT '家庭住址',
  `user_id` bigint(20) DEFAULT NULL COMMENT '关联用户ID',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态 0:休学 1:在读 2:毕业 3:转学',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '删除标记 0:未删除 1:已删除',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_student_code` (`student_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生表';

-- 课程表
DROP TABLE IF EXISTS `school_course`;
CREATE TABLE `school_course` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `course_code` varchar(20) NOT NULL COMMENT '课程编码',
  `course_name` varchar(50) NOT NULL COMMENT '课程名称',
  `stage` tinyint(4) NOT NULL COMMENT '学段 1:小学 2:初中 3:高中',
  `course_type` tinyint(4) NOT NULL COMMENT '科目类型 1:主科 2:副科',
  `full_score` decimal(5,1) NOT NULL COMMENT '满分分值',
  `pass_score` decimal(5,1) DEFAULT NULL COMMENT '及格分数',
  `good_score` decimal(5,1) DEFAULT NULL COMMENT '良好分数',
  `excellent_score` decimal(5,1) DEFAULT NULL COMMENT '优秀分数',
  `score_type` tinyint(4) NOT NULL COMMENT '计分方式 1:百分制 2:等级制 3:120分制',
  `sort_order` int(11) DEFAULT '0' COMMENT '排序',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态 0:禁用 1:启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '删除标记 0:未删除 1:已删除',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_course_code` (`course_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程表';

-- 考试表
DROP TABLE IF EXISTS `school_exam`;
CREATE TABLE `school_exam` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `exam_code` varchar(50) NOT NULL COMMENT '考试编码',
  `exam_name` varchar(100) NOT NULL COMMENT '考试名称',
  `exam_type` tinyint(4) NOT NULL COMMENT '考试类型 1:月考 2:期中考试 3:期末考试 4:模拟考试 5:其他',
  `school_year` varchar(20) NOT NULL COMMENT '学年',
  `semester` tinyint(4) NOT NULL COMMENT '学期 1:第一学期 2:第二学期',
  `start_date` date NOT NULL COMMENT '考试开始日期',
  `end_date` date NOT NULL COMMENT '考试结束日期',
  `grade_ids` varchar(100) NOT NULL COMMENT '参考年级',
  `course_ids` varchar(100) NOT NULL COMMENT '考试科目',
  `description` varchar(500) DEFAULT NULL COMMENT '考试描述',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态 0:未开始 1:进行中 2:已结束 3:已发布',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '删除标记 0:未删除 1:已删除',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_exam_code` (`exam_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考试表';

-- 成绩表
DROP TABLE IF EXISTS `school_score`;
CREATE TABLE `school_score` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `exam_id` bigint(20) NOT NULL COMMENT '考试ID',
  `student_id` bigint(20) NOT NULL COMMENT '学生ID',
  `course_id` bigint(20) NOT NULL COMMENT '课程ID',
  `score` decimal(5,1) DEFAULT NULL COMMENT '分数',
  `grade_level` varchar(10) DEFAULT NULL COMMENT '等级',
  `class_rank` int(11) DEFAULT NULL COMMENT '班级排名',
  `grade_rank` int(11) DEFAULT NULL COMMENT '年级排名',
  `absent` tinyint(4) DEFAULT '0' COMMENT '是否缺考 0:正常 1:缺考',
  `teacher_id` bigint(20) DEFAULT NULL COMMENT '录入教师ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '删除标记 0:未删除 1:已删除',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_exam_student_course` (`exam_id`,`student_id`,`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成绩表';

-- 教师任课关系表
DROP TABLE IF EXISTS `teacher_class_course`;
CREATE TABLE `teacher_class_course` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `teacher_id` bigint(20) NOT NULL COMMENT '教师ID',
  `class_id` bigint(20) NOT NULL COMMENT '班级ID',
  `course_id` bigint(20) NOT NULL COMMENT '课程ID',
  `school_year` varchar(20) DEFAULT NULL COMMENT '学年',
  `semester` tinyint(4) DEFAULT NULL COMMENT '学期 1:第一学期 2:第二学期',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态 0:停用 1:启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '删除标记 0:未删除 1:已删除',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_teacher_class_course` (`teacher_id`,`class_id`,`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师任课关系表';