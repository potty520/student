package com.school.grade.entity;

import com.school.grade.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
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
@Table(name = "exam_score")
public class Score extends BaseEntity {

    /**
     * 考试ID
     */
    @Column(name = "exam_id", nullable = false)
    private Long examId;

    /**
     * 学生ID
     */
    @Column(name = "student_id", nullable = false)
    private Long studentId;

    /**
     * 课程ID
     */
    @Column(name = "course_id", nullable = false)
    private Long courseId;

    /**
     * 分数
     */
    @Column(name = "score", precision = 5, scale = 2)
    private BigDecimal score;

    /**
     * 是否缺考 0:否 1:是
     */
    @Column(name = "absent", columnDefinition = "TINYINT DEFAULT 0")
    private Integer absent = 0;

    /**
     * 评语
     */
    @Column(name = "comment", length = 200)
    private String comment;

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
     * 关联的考试信息
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", insertable = false, updatable = false)
    private Exam exam;

    /**
     * 关联的学生信息
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", insertable = false, updatable = false)
    private Student student;

    /**
     * 关联的课程信息
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", insertable = false, updatable = false)
    private Course course;
}