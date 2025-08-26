package com.school.grade.entity;

import com.school.grade.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * 学生实体类
 * 
 * @author Qoder
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "school_student")
public class Student extends BaseEntity {

    /**
     * 学号
     */
    @NotBlank(message = "学号不能为空")
    @Column(name = "student_code", unique = true, nullable = false, length = 20)
    private String studentCode;

    /**
     * 学生姓名
     */
    @NotBlank(message = "学生姓名不能为空")
    @Column(name = "student_name", nullable = false, length = 50)
    private String studentName;

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
     * 出生日期
     */
    @Column(name = "birth_date")
    private LocalDate birthDate;

    /**
     * 入学日期
     */
    @Column(name = "enrollment_date")
    private LocalDate enrollmentDate;

    /**
     * 所属班级ID
     */
    @NotNull(message = "所属班级不能为空")
    @Column(name = "class_id", nullable = false)
    private Long classId;

    /**
     * 所属班级
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", insertable = false, updatable = false)
    private SchoolClass schoolClass;

    /**
     * 监护人姓名
     */
    @Column(name = "guardian_name", length = 50)
    private String guardianName;

    /**
     * 监护人关系
     */
    @Column(name = "guardian_relation", length = 20)
    private String guardianRelation;

    /**
     * 监护人电话
     */
    @Column(name = "guardian_phone", length = 11)
    private String guardianPhone;

    /**
     * 家庭住址
     */
    @Column(name = "address", length = 200)
    private String address;

    /**
     * 关联用户ID (可选，学生可以有登录账号)
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
     * 状态 0:休学 1:在读 2:毕业 3:转学
     */
    @NotNull(message = "状态不能为空")
    @Column(name = "status", nullable = false, columnDefinition = "TINYINT DEFAULT 1")
    private Integer status = 1;
}