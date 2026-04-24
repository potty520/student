package com.school.grade.entity;

import com.school.grade.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * 年级实体类
 * 
 * @author Qoder
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_grade")
public class Grade extends BaseEntity {

    /**
     * 年级编码
     */
    @Column(name = "grade_code", nullable = false, length = 20, unique = true)
    private String gradeCode;

    /**
     * 年级名称
     */
    @Column(name = "grade_name", nullable = false, length = 50)
    private String gradeName;

    /**
     * 学段 1:小学 2:初中 3:高中
     */
    @Column(name = "stage", nullable = false)
    private Integer stage;

    /**
     * 学年 (格式: 2023-2024)
     */
    @Column(name = "school_year", nullable = false, length = 9)
    private String schoolYear;

    /**
     * 年级级别 (小学:1-6, 初中:7-9, 高中:10-12)
     */
    @Column(name = "grade_level", nullable = false)
    private Integer gradeLevel;
}