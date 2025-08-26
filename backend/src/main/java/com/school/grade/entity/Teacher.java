package com.school.grade.entity;

import com.school.grade.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * 教师实体类
 * 
 * @author Qoder
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "school_teacher")
public class Teacher extends BaseEntity {

    /**
     * 教师工号
     */
    @NotBlank(message = "教师工号不能为空")
    @Column(name = "teacher_code", unique = true, nullable = false, length = 20)
    private String teacherCode;

    /**
     * 教师姓名
     */
    @NotBlank(message = "教师姓名不能为空")
    @Column(name = "teacher_name", nullable = false, length = 50)
    private String teacherName;

    /**
     * 性别 0:女 1:男
     */
    @Column(name = "gender", columnDefinition = "TINYINT DEFAULT 1")
    private Integer gender;

    /**
     * 身份证号
     */
    @Column(name = "id_card", length = 18)
    private String idCard;

    /**
     * 手机号
     */
    @Column(name = "phone", length = 11)
    private String phone;

    /**
     * 邮箱
     */
    @Column(name = "email", length = 100)
    private String email;

    /**
     * 出生日期
     */
    @Column(name = "birth_date")
    private LocalDate birthDate;

    /**
     * 入职日期
     */
    @Column(name = "hire_date")
    private LocalDate hireDate;

    /**
     * 职位
     */
    @Column(name = "position", length = 50)
    private String position;

    /**
     * 学历
     */
    @Column(name = "education", length = 20)
    private String education;

    /**
     * 专业
     */
    @Column(name = "major", length = 50)
    private String major;

    /**
     * 关联用户ID
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 关联用户
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    /**
     * 状态 0:离职 1:在职
     */
    @NotNull(message = "状态不能为空")
    @Column(name = "status", nullable = false, columnDefinition = "TINYINT DEFAULT 1")
    private Integer status = 1;
}