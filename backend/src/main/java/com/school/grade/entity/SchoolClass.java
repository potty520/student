package com.school.grade.entity;

import com.school.grade.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 班级实体类
 * 
 * @author Qoder
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "school_class")
public class SchoolClass extends BaseEntity {

    /**
     * 班级名称
     */
    @NotBlank(message = "班级名称不能为空")
    @Column(name = "class_name", nullable = false, length = 50)
    private String className;

    /**
     * 班级编码
     */
    @NotBlank(message = "班级编码不能为空")
    @Column(name = "class_code", unique = true, nullable = false, length = 20)
    private String classCode;

    /**
     * 所属年级ID
     */
    @NotNull(message = "所属年级不能为空")
    @Column(name = "grade_id", nullable = false)
    private Long gradeId;

    /**
     * 所属年级
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_id", insertable = false, updatable = false)
    private Grade grade;

    /**
     * 班主任ID
     */
    @Column(name = "head_teacher_id")
    private Long headTeacherId;

    /**
     * 班主任
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "head_teacher_id", insertable = false, updatable = false)
    private Teacher headTeacher;

    /**
     * 班级人数
     */
    @Column(name = "student_count", columnDefinition = "INT DEFAULT 0")
    private Integer studentCount = 0;

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