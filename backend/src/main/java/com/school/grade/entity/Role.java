package com.school.grade.entity;

import com.school.grade.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * 角色实体类
 * 
 * @author Qoder
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"permissions"})
@Entity
@Table(name = "sys_role")
public class Role extends BaseEntity {

    /**
     * 角色编码
     */
    @Column(name = "role_code", nullable = false, length = 50, unique = true)
    private String roleCode;

    /**
     * 角色名称
     */
    @Column(name = "role_name", nullable = false, length = 50)
    private String roleName;

    /**
     * 角色描述
     */
    @Column(name = "description", length = 200)
    private String description;

    /**
     * 关联的权限集合
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "sys_role_permission",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions = new HashSet<>();
}