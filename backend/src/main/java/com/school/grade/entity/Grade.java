package com.school.grade.entity;

import com.school.grade.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 年级实体类
 * 
 * @author Qoder
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "school_grade")
public class Grade extends BaseEntity {

    /**
     * 年级名称
     */
    @NotBlank(message = "年级名称不能为空")
    @Column(name = "grade_name", nullable = false, length = 50)
    private String gradeName;

    /**
     * 年级编码
     */
    @NotBlank(message = "年级编码不能为空")
    @Column(name = "grade_code", unique = true, nullable = false, length = 20)
    private String gradeCode;

    /**
     * 学段 1:小学 2:初中 3:高中
     */
    @NotNull(message = "学段不能为空")
    @Column(name = "stage", nullable = false, columnDefinition = "TINYINT")
    private Integer stage;

    /**
     * 年级序号 (一年级=1, 二年级=2...)
     */
    @NotNull(message = "年级序号不能为空")
    @Column(name = "grade_level", nullable = false)
    private Integer gradeLevel;

    /**
     * 学年 (如：2024-2025)
     */
    @NotBlank(message = "学年不能为空")
    @Column(name = "school_year", nullable = false, length = 20)
    private String schoolYear;

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