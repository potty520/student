package com.school.grade.entity;

import com.school.grade.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 成绩实体类
 * 
 * @author Qoder
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "school_score",
       uniqueConstraints = @UniqueConstraint(columnNames = {"exam_id", "student_id", "course_id"}))
public class Score extends BaseEntity {

    /**
     * 考试ID
     */
    @NotNull(message = "考试ID不能为空")
    @Column(name = "exam_id", nullable = false)
    private Long examId;

    /**
     * 考试
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", insertable = false, updatable = false)
    private Exam exam;

    /**
     * 学生ID
     */
    @NotNull(message = "学生ID不能为空")
    @Column(name = "student_id", nullable = false)
    private Long studentId;

    /**
     * 学生
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", insertable = false, updatable = false)
    private Student student;

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
     * 分数
     */
    @Column(name = "score", precision = 5, scale = 1)
    private BigDecimal score;

    /**
     * 等级 (A/B/C/D等)
     */
    @Column(name = "grade_level", length = 10)
    private String gradeLevel;

    /**
     * 班级排名
     */
    @Column(name = "class_rank")
    private Integer classRank;

    /**
     * 年级排名
     */
    @Column(name = "grade_rank")
    private Integer gradeRank;

    /**
     * 是否缺考 0:正常 1:缺考
     */
    @Column(name = "absent", columnDefinition = "TINYINT DEFAULT 0")
    private Integer absent = 0;

    /**
     * 录入教师ID
     */
    @Column(name = "teacher_id")
    private Long teacherId;

    /**
     * 录入教师
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", insertable = false, updatable = false)
    private Teacher teacher;
}