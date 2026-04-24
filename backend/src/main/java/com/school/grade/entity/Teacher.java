package com.school.grade.entity;

import com.school.grade.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * 教师实体类
 * 
 * @author Qoder
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_teacher")
public class Teacher extends BaseEntity {

    /**
     * 教师工号
     */
    @Column(name = "teacher_code", nullable = false, length = 20, unique = true)
    private String teacherCode;

    /**
     * 教师姓名
     */
    @Column(name = "teacher_name", nullable = false, length = 50)
    private String teacherName;

    /**
     * 性别 1:男 2:女
     */
    @Column(name = "gender")
    private Integer gender;

    /**
     * 出生日期
     */
    @Column(name = "birth_date")
    private java.time.LocalDate birthDate;

    /**
     * 身份证号
     */
    @Column(name = "id_card", length = 18, unique = true)
    private String idCard;

    /**
     * 手机号
     */
    @Column(name = "mobile", length = 20)
    private String mobile;

    /**
     * 邮箱
     */
    @Column(name = "email", length = 100)
    private String email;

    /**
     * 职称
     */
    @Column(name = "position", length = 50)
    private String position;

    /**
     * 学历
     */
    @Column(name = "education", length = 50)
    private String education;

    /**
     * 毕业院校
     */
    @Column(name = "graduate_school", length = 100)
    private String graduateSchool;

    /**
     * 入职日期
     */
    @Column(name = "hire_date")
    private java.time.LocalDate hireDate;

    /**
     * 用户ID（关联用户表）
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 关联的用户信息
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
}