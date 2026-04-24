package com.school.grade.entity;

import com.school.grade.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 用户实体类
 * 
 * @author Qoder
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_user")
public class User extends BaseEntity {

    /**
     * 用户名
     */
    @Column(name = "username", nullable = false, length = 50, unique = true)
    private String username;

    /**
     * 密码
     */
    @Column(name = "password", nullable = false, length = 100)
    private String password;

    /**
     * 真实姓名
     */
    @Column(name = "real_name", length = 50)
    private String realName;

    /**
     * 性别 1:男 2:女
     */
    @Column(name = "gender")
    private Integer gender;

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
     * 头像URL
     */
    @Column(name = "avatar", length = 200)
    private String avatar;

    /**
     * 最后登录时间
     */
    @Column(name = "last_login_time")
    private LocalDateTime lastLoginTime;

    /**
     * 最后登录IP
     */
    @Column(name = "last_login_ip", length = 50)
    private String lastLoginIp;

    /**
     * 登录次数
     */
    @Column(name = "login_count", columnDefinition = "INT DEFAULT 0")
    private Integer loginCount = 0;

    /**
     * 关联的角色集合
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "sys_user_role",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    /**
     * 兼容历史业务层命名（phone <-> mobile）
     */
    public String getPhone() {
        return this.mobile;
    }

    public void setPhone(String phone) {
        this.mobile = phone;
    }
}