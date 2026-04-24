package com.school.grade.entity;

import com.school.grade.common.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * 学生实体类
 * 
 * @author Qoder
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_student")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Student extends BaseEntity {

    /**
     * 学号
     */
    @Column(name = "student_code", nullable = false, length = 20, unique = true)
    private String studentCode;

    /**
     * 学生姓名
     */
    @Column(name = "student_name", nullable = false, length = 50)
    private String studentName;

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
     * 班级ID
     */
    @Column(name = "class_id", nullable = false)
    private Long classId;

    /**
     * 监护人姓名
     */
    @Column(name = "guardian_name", length = 50)
    private String guardianName;

    /**
     * 监护人电话
     */
    @Column(name = "guardian_phone", length = 20)
    private String guardianPhone;

    /**
     * 监护人关系
     */
    @Column(name = "guardian_relation", length = 20)
    private String guardianRelation;

    /**
     * 家庭住址
     */
    @Column(name = "address", length = 200)
    private String address;

    /**
     * 用户ID（关联用户表）
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 关联的班级信息
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", insertable = false, updatable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private SchoolClass schoolClass;
}