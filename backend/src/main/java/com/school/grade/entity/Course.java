package com.school.grade.entity;

import com.school.grade.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 课程科目实体类
 * 
 * @author Qoder
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "school_course")
public class Course extends BaseEntity {

    /**
     * 课程编码
     */
    @NotBlank(message = "课程编码不能为空")
    @Column(name = "course_code", unique = true, nullable = false, length = 20)
    private String courseCode;

    /**
     * 课程名称
     */
    @NotBlank(message = "课程名称不能为空")
    @Column(name = "course_name", nullable = false, length = 50)
    private String courseName;

    /**
     * 学段 1:小学 2:初中 3:高中
     */
    @NotNull(message = "学段不能为空")
    @Column(name = "stage", nullable = false, columnDefinition = "TINYINT")
    private Integer stage;

    /**
     * 科目类型 1:主科 2:副科
     */
    @NotNull(message = "科目类型不能为空")
    @Column(name = "course_type", nullable = false, columnDefinition = "TINYINT")
    private Integer courseType;

    /**
     * 满分分值
     */
    @NotNull(message = "满分分值不能为空")
    @Column(name = "full_score", nullable = false, precision = 5, scale = 1)
    private BigDecimal fullScore;

    /**
     * 及格分数
     */
    @Column(name = "pass_score", precision = 5, scale = 1)
    private BigDecimal passScore;

    /**
     * 良好分数
     */
    @Column(name = "good_score", precision = 5, scale = 1)
    private BigDecimal goodScore;

    /**
     * 优秀分数
     */
    @Column(name = "excellent_score", precision = 5, scale = 1)
    private BigDecimal excellentScore;

    /**
     * 计分方式 1:百分制 2:等级制 3:120分制
     */
    @NotNull(message = "计分方式不能为空")
    @Column(name = "score_type", nullable = false, columnDefinition = "TINYINT")
    private Integer scoreType;

    /**
     * 排序
     */
    @Column(name = "sort_order", columnDefinition = "INT DEFAULT 0")
    private Integer sortOrder = 0;

    /**
     * 状态 0:禁用 1:启用
     */
    @NotNull(message = "状态不能为空")
    @Column(name = "status", nullable = false, columnDefinition = "TINYINT DEFAULT 1")
    private Integer status = 1;
}