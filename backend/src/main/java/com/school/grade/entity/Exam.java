package com.school.grade.entity;

import com.school.grade.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
@Table(name = "school_exam")
public class Exam extends BaseEntity {

    /**
     * 考试编码
     */
    @NotBlank(message = "考试编码不能为空")
    @Column(name = "exam_code", unique = true, nullable = false, length = 50)
    private String examCode;

    /**
     * 考试名称
     */
    @NotBlank(message = "考试名称不能为空")
    @Column(name = "exam_name", nullable = false, length = 100)
    private String examName;

    /**
     * 考试类型 1:月考 2:期中考试 3:期末考试 4:模拟考试 5:其他
     */
    @NotNull(message = "考试类型不能为空")
    @Column(name = "exam_type", nullable = false, columnDefinition = "TINYINT")
    private Integer examType;

    /**
     * 学年 (如：2024-2025)
     */
    @NotBlank(message = "学年不能为空")
    @Column(name = "school_year", nullable = false, length = 20)
    private String schoolYear;

    /**
     * 学期 1:第一学期 2:第二学期
     */
    @NotNull(message = "学期不能为空")
    @Column(name = "semester", nullable = false, columnDefinition = "TINYINT")
    private Integer semester;

    /**
     * 考试开始日期
     */
    @NotNull(message = "考试开始日期不能为空")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    /**
     * 考试结束日期
     */
    @NotNull(message = "考试结束日期不能为空")
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    /**
     * 参考年级(多个年级用逗号分隔，如：1,2,3)
     */
    @NotBlank(message = "参考年级不能为空")
    @Column(name = "grade_ids", nullable = false, length = 100)
    private String gradeIds;

    /**
     * 考试科目(多个科目用逗号分隔，如：1,2,3)
     */
    @NotBlank(message = "考试科目不能为空")
    @Column(name = "course_ids", nullable = false, length = 100)
    private String courseIds;

    /**
     * 考试描述
     */
    @Column(name = "description", length = 500)
    private String description;

    /**
     * 状态 0:未开始 1:进行中 2:已结束 3:已发布
     */
    @NotNull(message = "状态不能为空")
    @Column(name = "status", nullable = false, columnDefinition = "TINYINT DEFAULT 0")
    private Integer status = 0;
}