package com.school.grade.entity;

import com.school.grade.common.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * 班级实体类
 * 
 * @author Qoder
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_class")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SchoolClass extends BaseEntity {

    /**
     * 班级编码
     */
    @Column(name = "class_code", nullable = false, length = 20, unique = true)
    private String classCode;

    /**
     * 班级名称
     */
    @Column(name = "class_name", nullable = false, length = 50)
    private String className;

    /**
     * 年级ID
     */
    @Column(name = "grade_id", nullable = false)
    private Long gradeId;

    /**
     * 班主任ID
     */
    @Column(name = "head_teacher_id")
    private Long headTeacherId;

    /**
     * 班级人数
     */
    @Column(name = "student_count", columnDefinition = "INT DEFAULT 0")
    private Integer studentCount = 0;

    /**
     * 关联的年级信息
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_id", insertable = false, updatable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Grade grade;
}