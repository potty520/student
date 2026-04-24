package com.school.grade.entity;

import com.school.grade.common.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 权限实体类
 * 
 * @author Qoder
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"roles", "children"})
@Entity
@Table(name = "sys_permission")
public class Permission extends BaseEntity {

    /**
     * 权限编码
     */
    @Column(name = "permission_code", nullable = false, length = 100, unique = true)
    private String permissionCode;

    /**
     * 权限名称
     */
    @Column(name = "permission_name", nullable = false, length = 100)
    private String permissionName;

    /**
     * 权限类型 1:菜单 2:按钮 3:接口
     */
    @Column(name = "permission_type", nullable = false)
    private Integer permissionType;

    /**
     * 父权限ID
     */
    @Column(name = "parent_id")
    private Long parentId;

    /**
     * 路由地址
     */
    @Column(name = "path", length = 200)
    private String path;

    /**
     * 组件路径
     */
    @Column(name = "component", length = 200)
    private String component;

    /**
     * 权限标识
     */
    @Column(name = "perms", length = 100)
    private String perms;

    /**
     * 图标
     */
    @Column(name = "icon", length = 50)
    private String icon;

    /**
     * 是否外链 0:否 1:是
     */
    @Column(name = "is_frame", columnDefinition = "TINYINT DEFAULT 0")
    private Integer isFrame = 0;

    /**
     * 是否缓存 0:否 1:是
     */
    @Column(name = "is_cache", columnDefinition = "TINYINT DEFAULT 0")
    private Integer isCache = 0;

    /**
     * 权限描述
     */
    @Column(name = "description", length = 200)
    private String description;

    /**
     * 关联的角色集合
     */
    @JsonIgnore
    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    private Set<Role> roles = new HashSet<>();

    /**
     * 权限树子节点（运行时组装，不落库）
     */
    @Transient
    private List<Permission> children = new ArrayList<>();
}