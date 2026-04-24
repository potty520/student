-- =============================================
-- 学生成绩管理系统 - 数据库初始化脚本
-- 适用版本: MySQL 5.7+
-- =============================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CREATE DATABASE IF NOT EXISTS `student_grade_system`
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE `student_grade_system`;

-- ----------------------------
-- 用户表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `create_time` DATETIME NULL,
  `update_time` DATETIME NULL,
  `create_by` BIGINT NULL,
  `update_by` BIGINT NULL,
  `deleted` TINYINT DEFAULT 0,
  `status` TINYINT DEFAULT 1,
  `sort_order` INT DEFAULT 0,
  `remark` VARCHAR(500) NULL,
  `username` VARCHAR(50) NOT NULL,
  `password` VARCHAR(100) NOT NULL,
  `real_name` VARCHAR(50) NULL,
  `gender` INT NULL,
  `mobile` VARCHAR(20) NULL,
  `email` VARCHAR(100) NULL,
  `avatar` VARCHAR(200) NULL,
  `last_login_time` DATETIME NULL,
  `last_login_ip` VARCHAR(50) NULL,
  `login_count` INT DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_user_username` (`username`),
  KEY `idx_sys_user_deleted_status` (`deleted`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- 角色表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `create_time` DATETIME NULL,
  `update_time` DATETIME NULL,
  `create_by` BIGINT NULL,
  `update_by` BIGINT NULL,
  `deleted` TINYINT DEFAULT 0,
  `status` TINYINT DEFAULT 1,
  `sort_order` INT DEFAULT 0,
  `remark` VARCHAR(500) NULL,
  `role_code` VARCHAR(50) NOT NULL,
  `role_name` VARCHAR(50) NOT NULL,
  `description` VARCHAR(200) NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_role_role_code` (`role_code`),
  KEY `idx_sys_role_deleted_status` (`deleted`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- 权限表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_permission` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `create_time` DATETIME NULL,
  `update_time` DATETIME NULL,
  `create_by` BIGINT NULL,
  `update_by` BIGINT NULL,
  `deleted` TINYINT DEFAULT 0,
  `status` TINYINT DEFAULT 1,
  `sort_order` INT DEFAULT 0,
  `remark` VARCHAR(500) NULL,
  `permission_code` VARCHAR(100) NOT NULL,
  `permission_name` VARCHAR(100) NOT NULL,
  `permission_type` INT NOT NULL,
  `parent_id` BIGINT NULL,
  `path` VARCHAR(200) NULL,
  `component` VARCHAR(200) NULL,
  `perms` VARCHAR(100) NULL,
  `icon` VARCHAR(50) NULL,
  `is_frame` TINYINT DEFAULT 0,
  `is_cache` TINYINT DEFAULT 0,
  `description` VARCHAR(200) NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_permission_permission_code` (`permission_code`),
  KEY `idx_sys_permission_parent_id` (`parent_id`),
  KEY `idx_sys_permission_deleted_status` (`deleted`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- 用户角色关系表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_user_role` (
  `user_id` BIGINT NOT NULL,
  `role_id` BIGINT NOT NULL,
  PRIMARY KEY (`user_id`, `role_id`),
  KEY `idx_user_role_role_id` (`role_id`),
  CONSTRAINT `fk_user_role_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_user_role_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- 角色权限关系表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_role_permission` (
  `role_id` BIGINT NOT NULL,
  `permission_id` BIGINT NOT NULL,
  PRIMARY KEY (`role_id`, `permission_id`),
  KEY `idx_role_permission_permission_id` (`permission_id`),
  CONSTRAINT `fk_role_permission_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_role_permission_permission` FOREIGN KEY (`permission_id`) REFERENCES `sys_permission` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- 年级表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_grade` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `create_time` DATETIME NULL,
  `update_time` DATETIME NULL,
  `create_by` BIGINT NULL,
  `update_by` BIGINT NULL,
  `deleted` TINYINT DEFAULT 0,
  `status` TINYINT DEFAULT 1,
  `sort_order` INT DEFAULT 0,
  `remark` VARCHAR(500) NULL,
  `grade_code` VARCHAR(20) NOT NULL,
  `grade_name` VARCHAR(50) NOT NULL,
  `stage` INT NOT NULL,
  `school_year` VARCHAR(9) NOT NULL,
  `grade_level` INT NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_grade_grade_code` (`grade_code`),
  KEY `idx_sys_grade_deleted_status` (`deleted`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- 教师表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_teacher` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `create_time` DATETIME NULL,
  `update_time` DATETIME NULL,
  `create_by` BIGINT NULL,
  `update_by` BIGINT NULL,
  `deleted` TINYINT DEFAULT 0,
  `status` TINYINT DEFAULT 1,
  `sort_order` INT DEFAULT 0,
  `remark` VARCHAR(500) NULL,
  `teacher_code` VARCHAR(20) NOT NULL,
  `teacher_name` VARCHAR(50) NOT NULL,
  `gender` INT NULL,
  `birth_date` DATE NULL,
  `id_card` VARCHAR(18) NULL,
  `mobile` VARCHAR(20) NULL,
  `email` VARCHAR(100) NULL,
  `position` VARCHAR(50) NULL,
  `education` VARCHAR(50) NULL,
  `graduate_school` VARCHAR(100) NULL,
  `hire_date` DATE NULL,
  `user_id` BIGINT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_teacher_teacher_code` (`teacher_code`),
  UNIQUE KEY `uk_sys_teacher_id_card` (`id_card`),
  KEY `idx_sys_teacher_user_id` (`user_id`),
  KEY `idx_sys_teacher_deleted_status` (`deleted`, `status`),
  CONSTRAINT `fk_sys_teacher_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- 班级表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_class` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `create_time` DATETIME NULL,
  `update_time` DATETIME NULL,
  `create_by` BIGINT NULL,
  `update_by` BIGINT NULL,
  `deleted` TINYINT DEFAULT 0,
  `status` TINYINT DEFAULT 1,
  `sort_order` INT DEFAULT 0,
  `remark` VARCHAR(500) NULL,
  `class_code` VARCHAR(20) NOT NULL,
  `class_name` VARCHAR(50) NOT NULL,
  `grade_id` BIGINT NOT NULL,
  `head_teacher_id` BIGINT NULL,
  `student_count` INT DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_class_class_code` (`class_code`),
  KEY `idx_sys_class_grade_id` (`grade_id`),
  KEY `idx_sys_class_head_teacher_id` (`head_teacher_id`),
  KEY `idx_sys_class_deleted_status` (`deleted`, `status`),
  CONSTRAINT `fk_sys_class_grade` FOREIGN KEY (`grade_id`) REFERENCES `sys_grade` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_sys_class_teacher` FOREIGN KEY (`head_teacher_id`) REFERENCES `sys_teacher` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- 学生表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_student` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `create_time` DATETIME NULL,
  `update_time` DATETIME NULL,
  `create_by` BIGINT NULL,
  `update_by` BIGINT NULL,
  `deleted` TINYINT DEFAULT 0,
  `status` TINYINT DEFAULT 1,
  `sort_order` INT DEFAULT 0,
  `remark` VARCHAR(500) NULL,
  `student_code` VARCHAR(20) NOT NULL,
  `student_name` VARCHAR(50) NOT NULL,
  `gender` INT NULL,
  `birth_date` DATE NULL,
  `id_card` VARCHAR(18) NULL,
  `class_id` BIGINT NOT NULL,
  `guardian_name` VARCHAR(50) NULL,
  `guardian_phone` VARCHAR(20) NULL,
  `guardian_relation` VARCHAR(20) NULL,
  `address` VARCHAR(200) NULL,
  `user_id` BIGINT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_student_student_code` (`student_code`),
  UNIQUE KEY `uk_sys_student_id_card` (`id_card`),
  KEY `idx_sys_student_class_id` (`class_id`),
  KEY `idx_sys_student_user_id` (`user_id`),
  KEY `idx_sys_student_deleted_status` (`deleted`, `status`),
  CONSTRAINT `fk_sys_student_class` FOREIGN KEY (`class_id`) REFERENCES `sys_class` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_sys_student_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- 课程表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_course` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `create_time` DATETIME NULL,
  `update_time` DATETIME NULL,
  `create_by` BIGINT NULL,
  `update_by` BIGINT NULL,
  `deleted` TINYINT DEFAULT 0,
  `status` TINYINT DEFAULT 1,
  `sort_order` INT DEFAULT 0,
  `remark` VARCHAR(500) NULL,
  `course_code` VARCHAR(20) NOT NULL,
  `course_name` VARCHAR(50) NOT NULL,
  `stage` INT NOT NULL,
  `course_type` INT NOT NULL,
  `credit` DECIMAL(3,1) NULL,
  `full_score` DECIMAL(5,2) NULL,
  `score_type` INT NULL,
  `pass_score` DECIMAL(5,2) NULL,
  `good_score` DECIMAL(5,2) NULL,
  `excellent_score` DECIMAL(5,2) NULL,
  `description` VARCHAR(500) NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_course_course_code` (`course_code`),
  KEY `idx_sys_course_deleted_status` (`deleted`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- 考试信息表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `exam_info` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `create_time` DATETIME NULL,
  `update_time` DATETIME NULL,
  `create_by` BIGINT NULL,
  `update_by` BIGINT NULL,
  `deleted` TINYINT DEFAULT 0,
  `status` TINYINT DEFAULT 1,
  `sort_order` INT DEFAULT 0,
  `remark` VARCHAR(500) NULL,
  `exam_code` VARCHAR(20) NOT NULL,
  `exam_name` VARCHAR(100) NOT NULL,
  `school_year` VARCHAR(9) NOT NULL,
  `semester` INT NOT NULL,
  `exam_type` INT NOT NULL,
  `start_date` DATE NOT NULL,
  `end_date` DATE NOT NULL,
  `grade_ids` VARCHAR(200) NULL,
  `course_ids` VARCHAR(200) NULL,
  `description` VARCHAR(500) NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_exam_info_exam_code` (`exam_code`),
  KEY `idx_exam_info_deleted_status` (`deleted`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- 成绩表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `exam_score` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `create_time` DATETIME NULL,
  `update_time` DATETIME NULL,
  `create_by` BIGINT NULL,
  `update_by` BIGINT NULL,
  `deleted` TINYINT DEFAULT 0,
  `status` TINYINT DEFAULT 1,
  `sort_order` INT DEFAULT 0,
  `remark` VARCHAR(500) NULL,
  `exam_id` BIGINT NOT NULL,
  `student_id` BIGINT NOT NULL,
  `course_id` BIGINT NOT NULL,
  `score` DECIMAL(5,2) NULL,
  `absent` TINYINT DEFAULT 0,
  `comment` VARCHAR(200) NULL,
  `class_rank` INT NULL,
  `grade_rank` INT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_exam_score_exam_id` (`exam_id`),
  KEY `idx_exam_score_student_id` (`student_id`),
  KEY `idx_exam_score_course_id` (`course_id`),
  KEY `idx_exam_score_deleted_status` (`deleted`, `status`),
  CONSTRAINT `fk_exam_score_exam` FOREIGN KEY (`exam_id`) REFERENCES `exam_info` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_exam_score_student` FOREIGN KEY (`student_id`) REFERENCES `sys_student` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_exam_score_course` FOREIGN KEY (`course_id`) REFERENCES `sys_course` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- 教师班级课程关系表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_teacher_class_course` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `create_time` DATETIME NULL,
  `update_time` DATETIME NULL,
  `create_by` BIGINT NULL,
  `update_by` BIGINT NULL,
  `deleted` TINYINT DEFAULT 0,
  `status` TINYINT DEFAULT 1,
  `sort_order` INT DEFAULT 0,
  `remark` VARCHAR(500) NULL,
  `teacher_id` BIGINT NOT NULL,
  `class_id` BIGINT NOT NULL,
  `course_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_teacher_class_course` (`teacher_id`, `class_id`, `course_id`),
  KEY `idx_tcc_class_id` (`class_id`),
  KEY `idx_tcc_course_id` (`course_id`),
  KEY `idx_tcc_deleted_status` (`deleted`, `status`),
  CONSTRAINT `fk_tcc_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `sys_teacher` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_tcc_class` FOREIGN KEY (`class_id`) REFERENCES `sys_class` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_tcc_course` FOREIGN KEY (`course_id`) REFERENCES `sys_course` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- 系统日志表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `log_type` INT NOT NULL,
  `operation` VARCHAR(100) NULL,
  `method` VARCHAR(100) NULL,
  `params` LONGTEXT NULL,
  `result` LONGTEXT NULL,
  `ip` VARCHAR(50) NULL,
  `user_agent` VARCHAR(500) NULL,
  `user_id` BIGINT NULL,
  `username` VARCHAR(50) NULL,
  `execution_time` BIGINT NULL,
  `status` TINYINT DEFAULT 1,
  `create_time` DATETIME NOT NULL,
  `remark` VARCHAR(500) NULL,
  PRIMARY KEY (`id`),
  KEY `idx_sys_log_create_time` (`create_time`),
  KEY `idx_sys_log_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- 消息通知表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_message` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `create_time` DATETIME NULL,
  `update_time` DATETIME NULL,
  `create_by` BIGINT NULL,
  `update_by` BIGINT NULL,
  `deleted` TINYINT DEFAULT 0,
  `sort_order` INT DEFAULT 0,
  `remark` VARCHAR(500) NULL,
  `title` VARCHAR(200) NOT NULL,
  `content` LONGTEXT NOT NULL,
  `message_type` INT NOT NULL,
  `sender_id` BIGINT NULL,
  `sender_name` VARCHAR(50) NULL,
  `target_type` INT NOT NULL,
  `target_ids` VARCHAR(500) NULL,
  `send_method` INT NOT NULL,
  `send_time` DATETIME NULL,
  `status` TINYINT DEFAULT 0,
  `read_count` INT DEFAULT 0,
  `total_count` INT DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_sys_message_sender_id` (`sender_id`),
  KEY `idx_sys_message_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- 消息接收表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_message_receive` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `message_id` BIGINT NOT NULL,
  `receiver_id` BIGINT NOT NULL,
  `receiver_name` VARCHAR(50) NULL,
  `is_read` TINYINT DEFAULT 0,
  `read_time` DATETIME NULL,
  `create_time` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_message_receive_message_id` (`message_id`),
  KEY `idx_message_receive_receiver_id` (`receiver_id`),
  KEY `idx_message_receive_is_read` (`is_read`),
  CONSTRAINT `fk_message_receive_message` FOREIGN KEY (`message_id`) REFERENCES `sys_message` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_message_receive_receiver` FOREIGN KEY (`receiver_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;
