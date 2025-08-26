package com.school.grade.entity;

import com.school.grade.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * 教师任课关系实体类
 * 
 * @author Qoder
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "teacher_class_course",
       uniqueConstraints = @UniqueConstraint(columnNames = {"teacher_id", "class_id", "course_id"}))
public class TeacherClassCourse extends BaseEntity {

    /**
     * 教师ID
     */
    @NotNull(message = "教师ID不能为空")
    @Column(name = "teacher_id", nullable = false)
    private Long teacherId;

    /**
     * 教师
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", insertable = false, updatable = false)
    private Teacher teacher;

    /**
     * 班级ID
     */
    @NotNull(message = "班级ID不能为空")
    @Column(name = "class_id", nullable = false)
    private Long classId;

    /**
     * 班级
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", insertable = false, updatable = false)
    private SchoolClass schoolClass;

    /**
     * 课程ID
     */
    @NotNull(message = "课程ID不能为空")
    @Column(name = "course_id", nullable = false)
    private Long courseId;

    /**
     * 课程
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", insertable = false, updatable = false)
    private Course course;

    /**
     * 学年 (如：2024-2025)
     */
    @Column(name = "school_year", length = 20)
    private String schoolYear;

    /**
     * 学期 1:第一学期 2:第二学期
     */
    @Column(name = "semester", columnDefinition = "TINYINT")
    private Integer semester;

    /**
     * 状态 0:停用 1:启用
     */
    @NotNull(message = "状态不能为空")
    @Column(name = "status", nullable = false, columnDefinition = "TINYINT DEFAULT 1")
    private Integer status = 1;
}