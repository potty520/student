-- 基础数据初始化脚本
-- 作者: Qoder
-- 版本: 1.0.0

USE `student_grade_system`;

-- 初始化权限数据
INSERT INTO `sys_permission` (`permission_code`, `permission_name`, `permission_type`, `parent_id`, `permission_path`, `icon`, `sort_order`, `status`) VALUES
-- 一级菜单
('sys:manage', '系统管理', 1, NULL, '/system', 'Setting', 1, 1),
('basic:manage', '基础信息管理', 1, NULL, '/basic', 'Document', 2, 1),
('grade:manage', '成绩管理', 1, NULL, '/grade', 'DataAnalysis', 3, 1),
('report:manage', '报表分析', 1, NULL, '/report', 'PieChart', 4, 1),

-- 系统管理子菜单
('sys:user', '用户管理', 1, 1, '/system/user', 'User', 101, 1),
('sys:role', '角色管理', 1, 1, '/system/role', 'UserFilled', 102, 1),
('sys:permission', '权限管理', 1, 1, '/system/permission', 'Lock', 103, 1),

-- 基础信息管理子菜单
('basic:grade', '年级管理', 1, 2, '/basic/grade', 'School', 201, 1),
('basic:class', '班级管理', 1, 2, '/basic/class', 'House', 202, 1),
('basic:teacher', '教师管理', 1, 2, '/basic/teacher', 'Avatar', 203, 1),
('basic:student', '学生管理', 1, 2, '/basic/student', 'UserFilled', 204, 1),
('basic:course', '课程管理', 1, 2, '/basic/course', 'Reading', 205, 1),

-- 成绩管理子菜单
('grade:exam', '考试管理', 1, 3, '/grade/exam', 'Document', 301, 1),
('grade:score', '成绩录入', 1, 3, '/grade/score', 'Edit', 302, 1),
('grade:statistics', '成绩统计', 1, 3, '/grade/statistics', 'DataAnalysis', 303, 1),

-- 报表分析子菜单
('report:student', '学生成绩报告', 1, 4, '/report/student', 'User', 401, 1),
('report:class', '班级成绩报告', 1, 4, '/report/class', 'House', 402, 1),
('report:teacher', '教学分析报告', 1, 4, '/report/teacher', 'Avatar', 403, 1),

-- 按钮权限
('sys:user:add', '添加用户', 2, 5, NULL, NULL, 1001, 1),
('sys:user:edit', '编辑用户', 2, 5, NULL, NULL, 1002, 1),
('sys:user:delete', '删除用户', 2, 5, NULL, NULL, 1003, 1),
('sys:user:reset', '重置密码', 2, 5, NULL, NULL, 1004, 1),

('basic:grade:add', '添加年级', 2, 9, NULL, NULL, 2001, 1),
('basic:grade:edit', '编辑年级', 2, 9, NULL, NULL, 2002, 1),
('basic:grade:delete', '删除年级', 2, 9, NULL, NULL, 2003, 1),

('basic:student:add', '添加学生', 2, 12, NULL, NULL, 2041, 1),
('basic:student:edit', '编辑学生', 2, 12, NULL, NULL, 2042, 1),
('basic:student:delete', '删除学生', 2, 12, NULL, NULL, 2043, 1),
('basic:student:import', '导入学生', 2, 12, NULL, NULL, 2044, 1),

('grade:score:entry', '录入成绩', 2, 16, NULL, NULL, 3021, 1),
('grade:score:edit', '修改成绩', 2, 16, NULL, NULL, 3022, 1),
('grade:score:import', '导入成绩', 2, 16, NULL, NULL, 3023, 1);

-- 初始化角色数据
INSERT INTO `sys_role` (`role_code`, `role_name`, `description`, `sort_order`, `status`) VALUES
('ADMIN', '系统管理员', '系统最高权限管理员', 1, 1),
('ACADEMIC_ADMIN', '教务管理员', '教务处管理员，负责教务管理', 2, 1),
('HEAD_TEACHER', '班主任', '班主任角色，管理本班学生', 3, 1),
('TEACHER', '任课教师', '任课教师角色，录入成绩', 4, 1),
('PRINCIPAL', '校长', '校长角色，查看统计报表', 5, 1),
('STUDENT', '学生', '学生角色，查看个人成绩', 6, 1),
('PARENT', '家长', '家长角色，查看子女成绩', 7, 1);

-- 初始化管理员用户 (密码: admin123)
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `gender`, `phone`, `email`, `status`) VALUES
('admin', '$2a$10$7JB720yubVSOfvVWdTuXJe2QW1H.RQK0J0n3MhAG..eiVUOjV9cZu', '系统管理员', 1, '13800138000', 'admin@school.com', 1);

-- 管理员角色关联
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (1, 1);

-- 管理员权限关联 (给管理员所有权限)
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) 
SELECT 1, id FROM `sys_permission`;

-- 初始化年级数据
INSERT INTO `school_grade` (`grade_name`, `grade_code`, `stage`, `grade_level`, `school_year`, `sort_order`, `status`) VALUES
('一年级', 'G2024_1', 1, 1, '2024-2025', 1, 1),
('二年级', 'G2024_2', 1, 2, '2024-2025', 2, 1),
('三年级', 'G2024_3', 1, 3, '2024-2025', 3, 1),
('四年级', 'G2024_4', 1, 4, '2024-2025', 4, 1),
('五年级', 'G2024_5', 1, 5, '2024-2025', 5, 1),
('六年级', 'G2024_6', 1, 6, '2024-2025', 6, 1);

-- 初始化课程数据
INSERT INTO `school_course` (`course_code`, `course_name`, `stage`, `course_type`, `full_score`, `pass_score`, `good_score`, `excellent_score`, `score_type`, `sort_order`, `status`) VALUES
-- 小学课程
('PRIMARY_CHINESE', '语文', 1, 1, 100.0, 60.0, 70.0, 85.0, 1, 1, 1),
('PRIMARY_MATH', '数学', 1, 1, 100.0, 60.0, 70.0, 85.0, 1, 2, 1),
('PRIMARY_ENGLISH', '英语', 1, 1, 100.0, 60.0, 70.0, 85.0, 1, 3, 1),
('PRIMARY_SCIENCE', '科学', 1, 2, 100.0, 60.0, 70.0, 85.0, 1, 4, 1),
('PRIMARY_MORAL', '道德与法治', 1, 2, 100.0, 60.0, 70.0, 85.0, 1, 5, 1),
('PRIMARY_PE', '体育', 1, 2, 100.0, 60.0, 70.0, 85.0, 1, 6, 1),
('PRIMARY_MUSIC', '音乐', 1, 2, 100.0, 60.0, 70.0, 85.0, 1, 7, 1),
('PRIMARY_ART', '美术', 1, 2, 100.0, 60.0, 70.0, 85.0, 1, 8, 1);

-- 初始化示例教师数据
INSERT INTO `school_teacher` (`teacher_code`, `teacher_name`, `gender`, `phone`, `email`, `hire_date`, `position`, `status`) VALUES
('T001', '张老师', 0, '13812345678', 'zhang@school.com', '2020-09-01', '语文教师', 1),
('T002', '李老师', 1, '13823456789', 'li@school.com', '2019-09-01', '数学教师', 1),
('T003', '王老师', 0, '13834567890', 'wang@school.com', '2021-09-01', '英语教师', 1);

-- 初始化示例班级数据
INSERT INTO `school_class` (`class_name`, `class_code`, `grade_id`, `head_teacher_id`, `sort_order`, `status`) VALUES
('一年级1班', 'C2024_1_1', 1, 1, 1, 1),
('一年级2班', 'C2024_1_2', 1, 2, 2, 1),
('二年级1班', 'C2024_2_1', 2, 3, 3, 1);

-- 初始化示例学生数据
INSERT INTO `school_student` (`student_code`, `student_name`, `gender`, `birth_date`, `enrollment_date`, `class_id`, `guardian_name`, `guardian_relation`, `guardian_phone`, `status`) VALUES
('S2024001', '张小明', 1, '2018-03-15', '2024-09-01', 1, '张父', '父亲', '13900000001', 1),
('S2024002', '李小红', 0, '2018-05-20', '2024-09-01', 1, '李母', '母亲', '13900000002', 1),
('S2024003', '王小强', 1, '2018-07-10', '2024-09-01', 2, '王父', '父亲', '13900000003', 1);