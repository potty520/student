package com.school.grade.entity;

import com.school.grade.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * 教师班级课程关联实体类
 * 
 * @author Qoder
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_teacher_class_course")
public class TeacherClassCourse extends BaseEntity {

    /**
     * 教师ID
     */
    @Column(name = "teacher_id", nullable = false)
    private Long teacherId;

    /**
     * 班级ID
     */
    @Column(name = "class_id", nullable = false)
    private Long classId;

    /**
     * 课程ID
     */
    @Column(name = "course_id", nullable = false)
    private Long courseId;

    /**
     * 关联的教师信息
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", insertable = false, updatable = false)
    private Teacher teacher;

    /**
     * 关联的班级信息
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", insertable = false, updatable = false)
    private SchoolClass schoolClass;

    /**
     * 关联的课程信息
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", insertable = false, updatable = false)
    private Course course;
}