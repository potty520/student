package com.school.grade.entity;

import com.school.grade.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * 考试实体类
 * 
 * @author Qoder
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "exam_info")
public class Exam extends BaseEntity {

    /**
     * 考试编码
     */
    @Column(name = "exam_code", nullable = false, length = 20, unique = true)
    private String examCode;

    /**
     * 考试名称
     */
    @Column(name = "exam_name", nullable = false, length = 100)
    private String examName;

    /**
     * 学年 (格式: 2023-2024)
     */
    @Column(name = "school_year", nullable = false, length = 9)
    private String schoolYear;

    /**
     * 学期 1:上学期 2:下学期
     */
    @Column(name = "semester", nullable = false)
    private Integer semester;

    /**
     * 考试类型 1:月考 2:期中考试 3:期末考试 4:模拟考试
     */
    @Column(name = "exam_type", nullable = false)
    private Integer examType;

    /**
     * 开始日期
     */
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    /**
     * 结束日期
     */
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    /**
     * 年级ID列表 (逗号分隔)
     */
    @Column(name = "grade_ids", length = 200)
    private String gradeIds;

    /**
     * 课程ID列表 (逗号分隔)
     */
    @Column(name = "course_ids", length = 200)
    private String courseIds;

    /**
     * 考试描述
     */
    @Column(name = "description", length = 500)
    private String description;
}