-- =============================================
-- 大屏展示测试数据
-- 生成丰富的学生、成绩数据用于大屏展示
-- =============================================

SET NAMES utf8mb4;
USE student_grade_system;

-- 1. 先清理已有的成绩数据
DELETE FROM exam_score WHERE deleted=0;

-- 2. 扩充学生 (每个班级20人)
-- 七年级1班已有 王小明、李小红，再插入18人
INSERT INTO sys_student (student_code, student_name, gender, class_id, status, deleted, create_time) VALUES
('STU2025003','张伟',1,1,1,0,NOW()),
('STU2025004','刘洋',1,1,1,0,NOW()),
('STU2025005','陈静',2,1,1,0,NOW()),
('STU2025006','杨帆',1,1,1,0,NOW()),
('STU2025007','赵敏',2,1,1,0,NOW()),
('STU2025008','周杰',1,1,1,0,NOW()),
('STU2025009','吴婷',2,1,1,0,NOW()),
('STU2025010','郑浩',1,1,1,0,NOW()),
('STU2025011','冯雪',2,1,1,0,NOW()),
('STU2025012','褚亮',1,1,1,0,NOW()),
('STU2025013','卫芳',2,1,1,0,NOW()),
('STU2025014','蒋涛',1,1,1,0,NOW()),
('STU2025015','沈悦',2,1,1,0,NOW()),
('STU2025016','韩冰',2,1,1,0,NOW()),
('STU2025017','朱峰',1,1,1,0,NOW()),
('STU2025018','秦瑶',2,1,1,0,NOW()),
('STU2025019','许辉',1,1,1,0,NOW()),
('STU2025020','何琳',2,1,1,0,NOW());

-- 八年级1班已有 赵小强，再插入19人
INSERT INTO sys_student (student_code, student_name, gender, class_id, status, deleted, create_time) VALUES
('STU2025021','吕鹏',1,2,1,0,NOW()),
('STU2025022','施雨',2,2,1,0,NOW()),
('STU2025023','张明',1,2,1,0,NOW()),
('STU2025024','孔丽',2,2,1,0,NOW()),
('STU2025025','曹磊',1,2,1,0,NOW()),
('STU2025026','严华',2,2,1,0,NOW()),
('STU2025027','华蕊',2,2,1,0,NOW()),
('STU2025028','金鑫',1,2,1,0,NOW()),
('STU2025029','魏然',2,2,1,0,NOW()),
('STU2025030','陶喆',1,2,1,0,NOW()),
('STU2025031','姜文',1,2,1,0,NOW()),
('STU2025032','戚薇',2,2,1,0,NOW()),
('STU2025033','谢安',1,2,1,0,NOW()),
('STU2025034','邹雨',2,2,1,0,NOW()),
('STU2025035','喻言',1,2,1,0,NOW()),
('STU2025036','柏林',2,2,1,0,NOW()),
('STU2025037','水均',1,2,1,0,NOW()),
('STU2025038','窦唯',2,2,1,0,NOW()),
('STU2025039','章宇',1,2,1,0,NOW());

-- 3. 新增班级 (七2班、八2班)
INSERT INTO sys_class (class_code, class_name, grade_id, status, deleted, create_time) VALUES
('C2025003','七年级2班',1,1,0,NOW()),
('C2025004','八年级2班',2,1,0,NOW());

-- 七2班学生
INSERT INTO sys_student (student_code, student_name, gender, class_id, status, deleted, create_time) VALUES
('STU2025040','苏灿',1,3,1,0,NOW()),
('STU2025041','潘悦',2,3,1,0,NOW()),
('STU2025042','葛峰',1,3,1,0,NOW()),
('STU2025043','范琳',2,3,1,0,NOW()),
('STU2025044','彭博',1,3,1,0,NOW()),
('STU2025045','鲁冰',2,3,1,0,NOW()),
('STU2025046','韦达',1,3,1,0,NOW()),
('STU2025047','昌晶',2,3,1,0,NOW()),
('STU2025048','马超',1,3,1,0,NOW()),
('STU2025049','花荣',2,3,1,0,NOW()),
('STU2025050','方芳',2,3,1,0,NOW()),
('STU2025051','袁浩',1,3,1,0,NOW()),
('STU2025052','柳青',2,3,1,0,NOW()),
('STU2025053','丰硕',1,3,1,0,NOW()),
('STU2025054','鲍蕾',2,3,1,0,NOW()),
('STU2025055','史进',1,3,1,0,NOW()),
('STU2025056','唐艺',2,3,1,0,NOW()),
('STU2025057','费翔',1,3,1,0,NOW()),
('STU2025058','廉颇',1,3,1,0,NOW()),
('STU2025059','薛宝',2,3,1,0,NOW());

-- 八2班学生
INSERT INTO sys_student (student_code, student_name, gender, class_id, status, deleted, create_time) VALUES
('STU2025060','贺龙',1,4,1,0,NOW()),
('STU2025061','倪萍',2,4,1,0,NOW()),
('STU2025062','汤圆',1,4,1,0,NOW()),
('STU2025063','滕飞',2,4,1,0,NOW()),
('STU2025064','殷实',1,4,1,0,NOW()),
('STU2025065','罗浩',1,4,1,0,NOW()),
('STU2025066','毕胜',1,4,1,0,NOW()),
('STU2025067','郝帅',2,4,1,0,NOW()),
('STU2025068','邬丽',2,4,1,0,NOW()),
('STU2025069','安静',2,4,1,0,NOW()),
('STU2025070','常乐',1,4,1,0,NOW()),
('STU2025071','傅斌',1,4,1,0,NOW()),
('STU2025072','卞卡',2,4,1,0,NOW()),
('STU2025073','齐天',1,4,1,0,NOW()),
('STU2025074','康健',2,4,1,0,NOW()),
('STU2025075','伍岳',1,4,1,0,NOW()),
('STU2025076','余香',2,4,1,0,NOW()),
('STU2025077','元华',1,4,1,0,NOW()),
('STU2025078','卜凡',2,4,1,0,NOW()),
('STU2025079','顾盼',2,4,1,0,NOW());

-- 4. 设置考试状态：期中已结束(2)，期末已结束(2)，月考进行中(1)
UPDATE exam_info SET status=2 WHERE id=2;
UPDATE exam_info SET status=2 WHERE id=3;

-- 新增更多考试
INSERT INTO exam_info (exam_code, exam_name, exam_type, school_year, semester, start_date, end_date, status, grade_ids, course_ids, deleted, create_time) VALUES
('EXAM202502','2025学年上学期第二次月考','1','2025-2026','1','2025-11-10','2025-11-12',2,'1,2','1,2,3',0,NOW()),
('EXAM202503','2025学年上学期第三次月考','1','2025-2026','1','2025-12-15','2025-12-17',2,'1,2','1,2,3',0,NOW()),
('EXAM202504','2024-2025学年第二学期期中','2','2024-2025','2','2025-04-20','2025-04-22',2,'1,2','1,2,3',0,NOW());

-- 5. 批量生成成绩数据 - 使用存储过程
DELIMITER $$
DROP PROCEDURE IF EXISTS generate_scores$$
CREATE PROCEDURE generate_scores()
BEGIN
    DECLARE done INT DEFAULT 0;
    DECLARE v_student_id BIGINT;
    DECLARE v_exam_id BIGINT;
    DECLARE v_course_id BIGINT;
    DECLARE v_class_id BIGINT;
    DECLARE v_base_score INT;
    DECLARE v_rand INT;
    DECLARE v_score DECIMAL(5,2);

    DECLARE student_cursor CURSOR FOR
        SELECT id, class_id FROM sys_student WHERE deleted=0;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done=1;

    OPEN student_cursor;

    student_loop: LOOP
        FETCH student_cursor INTO v_student_id, v_class_id;
        IF done THEN LEAVE student_loop; END IF;

        -- 每个学生参加所有考试的所有科目
        -- 考试 1,2,3,4,5,6  (6个考试)
        SET v_exam_id = 1;
        WHILE v_exam_id <= 6 DO
            -- 科目 1=语文, 2=数学, 3=英语
            SET v_course_id = 1;
            WHILE v_course_id <= 3 DO
                -- 根据班级设置不同的基础分以产生区别
                IF v_course_id = 1 THEN SET v_base_score = CASE v_class_id WHEN 1 THEN 78 WHEN 2 THEN 82 WHEN 3 THEN 75 WHEN 4 THEN 80 ELSE 77 END;
                ELSEIF v_course_id = 2 THEN SET v_base_score = CASE v_class_id WHEN 1 THEN 72 WHEN 2 THEN 76 WHEN 3 THEN 70 WHEN 4 THEN 74 ELSE 73 END;
                ELSE SET v_base_score = CASE v_class_id WHEN 1 THEN 76 WHEN 2 THEN 80 WHEN 3 THEN 73 WHEN 4 THEN 78 ELSE 77 END;
                END IF;

                -- 加入考试难度因子 (越后面考试越难)
                IF v_exam_id <= 2 THEN SET v_base_score = v_base_score + 2;
                ELSEIF v_exam_id >= 5 THEN SET v_base_score = v_base_score - 3;
                END IF;

                -- 随机偏移 ±18
                SET v_rand = FLOOR(RAND() * 37) - 18;
                SET v_score = v_base_score + v_rand;

                -- 限制分数范围 15-100
                IF v_score > 100 THEN SET v_score = 95 + FLOOR(RAND()*6); END IF;
                IF v_score < 15 THEN SET v_score = 15 + FLOOR(RAND()*10); END IF;

                -- 约5%的概率缺考
                IF RAND() < 0.05 THEN
                    INSERT INTO exam_score (exam_id, student_id, course_id, score, absent, deleted, create_time)
                    VALUES (v_exam_id, v_student_id, v_course_id, NULL, 1, 0, NOW());
                ELSE
                    INSERT INTO exam_score (exam_id, student_id, course_id, score, absent, deleted, create_time)
                    VALUES (v_exam_id, v_student_id, v_course_id, v_score, 0, 0, NOW());
                END IF;

                SET v_course_id = v_course_id + 1;
            END WHILE;
            SET v_exam_id = v_exam_id + 1;
        END WHILE;
    END LOOP;
    CLOSE student_cursor;
END$$
DELIMITER ;

-- 执行存储过程生成成绩
CALL generate_scores();

-- 清理存储过程
DROP PROCEDURE IF EXISTS generate_scores;

-- 验证数据量
SELECT 'Students' AS item, COUNT(*) AS total FROM sys_student WHERE deleted=0
UNION ALL
SELECT 'Classes', COUNT(*) FROM sys_class WHERE deleted=0
UNION ALL
SELECT 'Exams', COUNT(*) FROM exam_info WHERE deleted=0
UNION ALL
SELECT 'Scores', COUNT(*) FROM exam_score WHERE deleted=0
UNION ALL
SELECT 'Completed Exams', COUNT(*) FROM exam_info WHERE deleted=0 AND status=2;
