package com.school.grade.entity;

import com.school.grade.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 权限实体类
 * 
 * @author Qoder
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_permission")
public class Permission extends BaseEntity {

    /**
     * 权限编码
     */
    @NotBlank(message = "权限编码不能为空")
    @Column(name = "permission_code", unique = true, nullable = false, length = 100)
    private String permissionCode;

    /**
     * 权限名称
     */
    @NotBlank(message = "权限名称不能为空")
    @Column(name = "permission_name", nullable = false, length = 50)
    private String permissionName;

    /**
     * 权限类型 1:菜单 2:按钮 3:接口
     */
    @NotNull(message = "权限类型不能为空")
    @Column(name = "permission_type", nullable = false, columnDefinition = "TINYINT")
    private Integer permissionType;

    /**
     * 父权限ID
     */
    @Column(name = "parent_id")
    private Long parentId;

    /**
     * 权限路径
     */
    @Column(name = "permission_path", length = 200)
    private String permissionPath;

    /**
     * 图标
     */
    @Column(name = "icon", length = 50)
    private String icon;

    /**
     * 排序
     */
    @Column(name = "sort_order", columnDefinition = "INT DEFAULT 0")
    private Integer sortOrder = 0;

    /**
     * 状态 0:禁用 1:启用
     */
    @NotNull(message = "状态不能为空")
    @Column(name = "status", nullable = false, columnDefinition = "TINYINT DEFAULT 1")
    private Integer status = 1;

    /**
     * 子权限
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private List<Permission> children;
}