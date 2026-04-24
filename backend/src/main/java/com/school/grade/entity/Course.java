package com.school.grade.entity;

import com.school.grade.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * 课程实体类
 * 
 * @author Qoder
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_course")
public class Course extends BaseEntity {

    /**
     * 课程编码
     */
    @Column(name = "course_code", nullable = false, length = 20, unique = true)
    private String courseCode;

    /**
     * 课程名称
     */
    @Column(name = "course_name", nullable = false, length = 50)
    private String courseName;

    /**
     * 学段 1:小学 2:初中 3:高中
     */
    @Column(name = "stage", nullable = false)
    private Integer stage;

    /**
     * 课程类型 1:必修 2:选修
     */
    @Column(name = "course_type", nullable = false)
    private Integer courseType;

    /**
     * 学分
     */
    @Column(name = "credit", precision = 3, scale = 1)
    private BigDecimal credit;

    /**
     * 满分分值
     */
    @Column(name = "full_score", precision = 5, scale = 2)
    private BigDecimal fullScore;

    /**
     * 计分方式 1:百分制 2:等级制 3:五分制
     */
    @Column(name = "score_type")
    private Integer scoreType;

    /**
     * 及格分数线
     */
    @Column(name = "pass_score", precision = 5, scale = 2)
    private BigDecimal passScore;

    /**
     * 良好分数线
     */
    @Column(name = "good_score", precision = 5, scale = 2)
    private BigDecimal goodScore;

    /**
     * 优秀分数线
     */
    @Column(name = "excellent_score", precision = 5, scale = 2)
    private BigDecimal excellentScore;

    /**
     * 课程描述
     */
    @Column(name = "description", length = 500)
    private String description;
}