-- =============================================
-- 学生成绩管理系统 - 基础数据脚本
-- 执行前请先运行 init.sql
-- =============================================

SET NAMES utf8mb4;
USE `student_grade_system`;

-- ----------------------------
-- 角色初始化
-- ----------------------------
INSERT INTO `sys_role`
(`create_time`,`update_time`,`create_by`,`update_by`,`deleted`,`status`,`sort_order`,`remark`,
 `role_code`,`role_name`,`description`)
SELECT NOW(), NOW(), 1, 1, 0, 1, 1, '系统内置', 'ADMIN', '管理员', '系统管理员角色'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_role` WHERE `role_code`='ADMIN' AND `deleted`=0);

INSERT INTO `sys_role`
(`create_time`,`update_time`,`create_by`,`update_by`,`deleted`,`status`,`sort_order`,`remark`,
 `role_code`,`role_name`,`description`)
SELECT NOW(), NOW(), 1, 1, 0, 1, 2, '系统内置', 'TEACHER', '教师', '教师角色'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_role` WHERE `role_code`='TEACHER' AND `deleted`=0);

INSERT INTO `sys_role`
(`create_time`,`update_time`,`create_by`,`update_by`,`deleted`,`status`,`sort_order`,`remark`,
 `role_code`,`role_name`,`description`)
SELECT NOW(), NOW(), 1, 1, 0, 1, 3, '系统内置', 'student', '学生', '学生角色'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_role` WHERE `role_code`='student' AND `deleted`=0);

-- ----------------------------
-- 权限初始化（简化版）
-- ----------------------------
INSERT INTO `sys_permission`
(`create_time`,`update_time`,`create_by`,`update_by`,`deleted`,`status`,`sort_order`,`remark`,
 `permission_code`,`permission_name`,`permission_type`,`parent_id`,`path`,`component`,`perms`,`icon`,`is_frame`,`is_cache`,`description`)
SELECT NOW(), NOW(), 1, 1, 0, 1, 1, '系统内置',
       'SYSTEM:ALL', '系统管理', 1, 0, '/system', 'Layout', 'system:*:*', 'Setting', 0, 0, '系统管理菜单'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `permission_code`='SYSTEM:ALL' AND `deleted`=0);

INSERT INTO `sys_permission`
(`create_time`,`update_time`,`create_by`,`update_by`,`deleted`,`status`,`sort_order`,`remark`,
 `permission_code`,`permission_name`,`permission_type`,`parent_id`,`path`,`component`,`perms`,`icon`,`is_frame`,`is_cache`,`description`)
SELECT NOW(), NOW(), 1, 1, 0, 1, 2, '系统内置',
       'BASIC:ALL', '基础信息', 1, 0, '/basic', 'Layout', 'basic:*:*', 'School', 0, 0, '基础信息菜单'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `permission_code`='BASIC:ALL' AND `deleted`=0);

INSERT INTO `sys_permission`
(`create_time`,`update_time`,`create_by`,`update_by`,`deleted`,`status`,`sort_order`,`remark`,
 `permission_code`,`permission_name`,`permission_type`,`parent_id`,`path`,`component`,`perms`,`icon`,`is_frame`,`is_cache`,`description`)
SELECT NOW(), NOW(), 1, 1, 0, 1, 3, '系统内置',
       'GRADE:ALL', '成绩管理', 1, 0, '/grade', 'Layout', 'grade:*:*', 'DataLine', 0, 0, '成绩管理菜单'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `permission_code`='GRADE:ALL' AND `deleted`=0);

-- 管理员角色绑定所有权限
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT r.id, p.id
FROM `sys_role` r
JOIN `sys_permission` p ON p.deleted = 0
WHERE r.role_code = 'ADMIN'
  AND r.deleted = 0
  AND NOT EXISTS (
      SELECT 1 FROM `sys_role_permission` rp
      WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

-- ----------------------------
-- 基础年级、班级、课程
-- ----------------------------
INSERT INTO `sys_grade`
(`create_time`,`update_time`,`create_by`,`update_by`,`deleted`,`status`,`sort_order`,`remark`,
 `grade_code`,`grade_name`,`stage`,`school_year`,`grade_level`)
SELECT NOW(), NOW(), 1, 1, 0, 1, 1, '演示数据',
       'G7-2025', '七年级', 2, '2025-2026', 7
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_grade` WHERE `grade_code`='G7-2025' AND `deleted`=0);

INSERT INTO `sys_grade`
(`create_time`,`update_time`,`create_by`,`update_by`,`deleted`,`status`,`sort_order`,`remark`,
 `grade_code`,`grade_name`,`stage`,`school_year`,`grade_level`)
SELECT NOW(), NOW(), 1, 1, 0, 1, 2, '演示数据',
       'G8-2025', '八年级', 2, '2025-2026', 8
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_grade` WHERE `grade_code`='G8-2025' AND `deleted`=0);

INSERT INTO `sys_course`
(`create_time`,`update_time`,`create_by`,`update_by`,`deleted`,`status`,`sort_order`,`remark`,
 `course_code`,`course_name`,`stage`,`course_type`,`credit`,`full_score`,`score_type`,`pass_score`,`good_score`,`excellent_score`,`description`)
SELECT NOW(), NOW(), 1, 1, 0, 1, 1, '演示数据',
       'C-CH-001', '语文', 2, 1, 2.0, 100.00, 1, 60.00, 80.00, 90.00, '初中语文'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_course` WHERE `course_code`='C-CH-001' AND `deleted`=0);

INSERT INTO `sys_course`
(`create_time`,`update_time`,`create_by`,`update_by`,`deleted`,`status`,`sort_order`,`remark`,
 `course_code`,`course_name`,`stage`,`course_type`,`credit`,`full_score`,`score_type`,`pass_score`,`good_score`,`excellent_score`,`description`)
SELECT NOW(), NOW(), 1, 1, 0, 1, 2, '演示数据',
       'C-MA-001', '数学', 2, 1, 2.0, 100.00, 1, 60.00, 80.00, 90.00, '初中数学'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_course` WHERE `course_code`='C-MA-001' AND `deleted`=0);

INSERT INTO `sys_course`
(`create_time`,`update_time`,`create_by`,`update_by`,`deleted`,`status`,`sort_order`,`remark`,
 `course_code`,`course_name`,`stage`,`course_type`,`credit`,`full_score`,`score_type`,`pass_score`,`good_score`,`excellent_score`,`description`)
SELECT NOW(), NOW(), 1, 1, 0, 1, 3, '演示数据',
       'C-EN-001', '英语', 2, 1, 2.0, 100.00, 1, 60.00, 80.00, 90.00, '初中英语'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_course` WHERE `course_code`='C-EN-001' AND `deleted`=0);

-- ----------------------------
-- 教师、班级、学生演示数据
-- ----------------------------
INSERT INTO `sys_teacher`
(`create_time`,`update_time`,`create_by`,`update_by`,`deleted`,`status`,`sort_order`,`remark`,
 `teacher_code`,`teacher_name`,`gender`,`birth_date`,`mobile`,`email`,`position`,`education`,`graduate_school`,`hire_date`)
SELECT NOW(), NOW(), 1, 1, 0, 1, 1, '演示数据',
       'T2025001', '张老师', 1, '1988-05-06', '13900000001', 'zhang@school.com', '语文教师', '本科', '师范大学', '2015-09-01'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_teacher` WHERE `teacher_code`='T2025001' AND `deleted`=0);

INSERT INTO `sys_teacher`
(`create_time`,`update_time`,`create_by`,`update_by`,`deleted`,`status`,`sort_order`,`remark`,
 `teacher_code`,`teacher_name`,`gender`,`birth_date`,`mobile`,`email`,`position`,`education`,`graduate_school`,`hire_date`)
SELECT NOW(), NOW(), 1, 1, 0, 1, 2, '演示数据',
       'T2025002', '李老师', 2, '1990-03-16', '13900000002', 'li@school.com', '数学教师', '硕士', '师范大学', '2017-09-01'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_teacher` WHERE `teacher_code`='T2025002' AND `deleted`=0);

INSERT INTO `sys_class`
(`create_time`,`update_time`,`create_by`,`update_by`,`deleted`,`status`,`sort_order`,`remark`,
 `class_code`,`class_name`,`grade_id`,`head_teacher_id`,`student_count`)
SELECT NOW(), NOW(), 1, 1, 0, 1, 1, '演示数据',
       'C7-01', '七年级1班',
       (SELECT id FROM `sys_grade` WHERE `grade_code`='G7-2025' AND `deleted`=0 LIMIT 1),
       (SELECT id FROM `sys_teacher` WHERE `teacher_code`='T2025001' AND `deleted`=0 LIMIT 1),
       2
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_class` WHERE `class_code`='C7-01' AND `deleted`=0);

INSERT INTO `sys_class`
(`create_time`,`update_time`,`create_by`,`update_by`,`deleted`,`status`,`sort_order`,`remark`,
 `class_code`,`class_name`,`grade_id`,`head_teacher_id`,`student_count`)
SELECT NOW(), NOW(), 1, 1, 0, 1, 2, '演示数据',
       'C8-01', '八年级1班',
       (SELECT id FROM `sys_grade` WHERE `grade_code`='G8-2025' AND `deleted`=0 LIMIT 1),
       (SELECT id FROM `sys_teacher` WHERE `teacher_code`='T2025002' AND `deleted`=0 LIMIT 1),
       1
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_class` WHERE `class_code`='C8-01' AND `deleted`=0);

INSERT INTO `sys_student`
(`create_time`,`update_time`,`create_by`,`update_by`,`deleted`,`status`,`sort_order`,`remark`,
 `student_code`,`student_name`,`gender`,`birth_date`,`class_id`,`guardian_name`,`guardian_phone`,`guardian_relation`,`address`)
SELECT NOW(), NOW(), 1, 1, 0, 1, 1, '演示数据',
       'S2025001', '王小明', 1, '2012-04-11',
       (SELECT id FROM `sys_class` WHERE `class_code`='C7-01' AND `deleted`=0 LIMIT 1),
       '王建国', '13800000001', '父子', 'XX市XX区'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_student` WHERE `student_code`='S2025001' AND `deleted`=0);

INSERT INTO `sys_student`
(`create_time`,`update_time`,`create_by`,`update_by`,`deleted`,`status`,`sort_order`,`remark`,
 `student_code`,`student_name`,`gender`,`birth_date`,`class_id`,`guardian_name`,`guardian_phone`,`guardian_relation`,`address`)
SELECT NOW(), NOW(), 1, 1, 0, 1, 2, '演示数据',
       'S2025002', '李小红', 2, '2012-08-23',
       (SELECT id FROM `sys_class` WHERE `class_code`='C7-01' AND `deleted`=0 LIMIT 1),
       '李梅', '13800000002', '母女', 'XX市XX区'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_student` WHERE `student_code`='S2025002' AND `deleted`=0);

INSERT INTO `sys_student`
(`create_time`,`update_time`,`create_by`,`update_by`,`deleted`,`status`,`sort_order`,`remark`,
 `student_code`,`student_name`,`gender`,`birth_date`,`class_id`,`guardian_name`,`guardian_phone`,`guardian_relation`,`address`)
SELECT NOW(), NOW(), 1, 1, 0, 1, 3, '演示数据',
       'S2025003', '赵小强', 1, '2011-12-02',
       (SELECT id FROM `sys_class` WHERE `class_code`='C8-01' AND `deleted`=0 LIMIT 1),
       '赵强', '13800000003', '父子', 'XX市XX区'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_student` WHERE `student_code`='S2025003' AND `deleted`=0);

-- ----------------------------
-- 任课关系
-- ----------------------------
INSERT INTO `sys_teacher_class_course`
(`create_time`,`update_time`,`create_by`,`update_by`,`deleted`,`status`,`sort_order`,`remark`,`teacher_id`,`class_id`,`course_id`)
SELECT NOW(), NOW(), 1, 1, 0, 1, 1, '演示数据',
       t.id, c.id, co.id
FROM `sys_teacher` t, `sys_class` c, `sys_course` co
WHERE t.teacher_code='T2025001'
  AND c.class_code='C7-01'
  AND co.course_code='C-CH-001'
  AND NOT EXISTS (
      SELECT 1 FROM `sys_teacher_class_course` x
      WHERE x.teacher_id=t.id AND x.class_id=c.id AND x.course_id=co.id AND x.deleted=0
  );

INSERT INTO `sys_teacher_class_course`
(`create_time`,`update_time`,`create_by`,`update_by`,`deleted`,`status`,`sort_order`,`remark`,`teacher_id`,`class_id`,`course_id`)
SELECT NOW(), NOW(), 1, 1, 0, 1, 2, '演示数据',
       t.id, c.id, co.id
FROM `sys_teacher` t, `sys_class` c, `sys_course` co
WHERE t.teacher_code='T2025002'
  AND c.class_code='C7-01'
  AND co.course_code='C-MA-001'
  AND NOT EXISTS (
      SELECT 1 FROM `sys_teacher_class_course` x
      WHERE x.teacher_id=t.id AND x.class_id=c.id AND x.course_id=co.id AND x.deleted=0
  );

-- ----------------------------
-- 考试与成绩演示数据
-- ----------------------------
INSERT INTO `exam_info`
(`create_time`,`update_time`,`create_by`,`update_by`,`deleted`,`status`,`sort_order`,`remark`,
 `exam_code`,`exam_name`,`school_year`,`semester`,`exam_type`,`start_date`,`end_date`,`grade_ids`,`course_ids`,`description`)
SELECT NOW(), NOW(), 1, 1, 0, 1, 1, '演示数据',
       'EX2025M01', '2025学年上学期第一次月考', '2025-2026', 1, 1, '2025-10-20', '2025-10-21',
       (SELECT CAST(id AS CHAR) FROM `sys_grade` WHERE `grade_code`='G7-2025' AND `deleted`=0 LIMIT 1),
       (SELECT GROUP_CONCAT(id) FROM `sys_course` WHERE `course_code` IN ('C-CH-001','C-MA-001','C-EN-001') AND `deleted`=0),
       '七年级月考'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `exam_info` WHERE `exam_code`='EX2025M01' AND `deleted`=0);

INSERT INTO `exam_score`
(`create_time`,`update_time`,`create_by`,`update_by`,`deleted`,`status`,`sort_order`,`remark`,
 `exam_id`,`student_id`,`course_id`,`score`,`absent`,`comment`,`class_rank`,`grade_rank`)
SELECT NOW(), NOW(), 1, 1, 0, 1, 0, '演示数据',
       e.id, s.id, c.id, 88.00, 0, '表现良好', NULL, NULL
FROM `exam_info` e, `sys_student` s, `sys_course` c
WHERE e.exam_code='EX2025M01'
  AND s.student_code='S2025001'
  AND c.course_code='C-CH-001'
  AND NOT EXISTS (
      SELECT 1 FROM `exam_score` x
      WHERE x.exam_id=e.id AND x.student_id=s.id AND x.course_id=c.id AND x.deleted=0
  );

INSERT INTO `exam_score`
(`create_time`,`update_time`,`create_by`,`update_by`,`deleted`,`status`,`sort_order`,`remark`,
 `exam_id`,`student_id`,`course_id`,`score`,`absent`,`comment`,`class_rank`,`grade_rank`)
SELECT NOW(), NOW(), 1, 1, 0, 1, 0, '演示数据',
       e.id, s.id, c.id, 92.00, 0, '优秀', NULL, NULL
FROM `exam_info` e, `sys_student` s, `sys_course` c
WHERE e.exam_code='EX2025M01'
  AND s.student_code='S2025002'
  AND c.course_code='C-CH-001'
  AND NOT EXISTS (
      SELECT 1 FROM `exam_score` x
      WHERE x.exam_id=e.id AND x.student_id=s.id AND x.course_id=c.id AND x.deleted=0
  );

-- ----------------------------
-- 提示
-- ----------------------------
-- 1) 管理员账号建议使用后端 DataInitializer 自动创建/更新（application.yml: system.admin.*）
-- 2) 若需重置基础数据，可先 TRUNCATE 业务表后重新执行 data.sql
