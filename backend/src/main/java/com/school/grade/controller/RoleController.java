package com.school.grade.controller;

import com.school.grade.entity.Role;
import com.school.grade.entity.Permission;
import com.school.grade.service.RoleService;
import com.school.grade.common.result.Result;
import com.school.grade.common.result.PageResult;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 角色管理控制�?
 * 
 * @author Qoder
 * @version 1.0.0
 */
@Tag(name = "角色管理")
@RestController
@RequestMapping("/api/roles")
@CrossOrigin(origins = "*")
public class RoleController {

    @Autowired
    private RoleService roleService;

    /**
     * 分页查询角色列表
     */
    @Operation(summary = "分页查询角色列表")
    @GetMapping("/list")
    public Result<PageResult<Role>> getRoleList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "角色名称") @RequestParam(required = false) String roleName,
            @Parameter(description = "角色编码") @RequestParam(required = false) String roleCode,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        
        return roleService.getRoleList(page, size, roleName, roleCode, status);
    }

    /**
     * 根据ID获取角色信息
     */
    @Operation(summary = "根据ID获取角色信息")
    @GetMapping("/{id}")
    public Result<Role> getRoleById(@Parameter(description = "角色ID") @PathVariable Long id) {
        return roleService.getRoleById(id);
    }

    /**
     * 添加角色
     */
    @Operation(summary = "添加角色")
    @PostMapping
    public Result<Role> addRole(@Parameter(description = "角色信息") @Valid @RequestBody Role role) {
        return roleService.addRole(role);
    }

    /**
     * 更新角色信息
     */
    @Operation(summary = "更新角色信息")
    @PutMapping("/{id}")
    public Result<Role> updateRole(
            @Parameter(description = "角色ID") @PathVariable Long id,
            @Parameter(description = "角色信息") @Valid @RequestBody Role role) {
        role.setId(id);
        return roleService.updateRole(role);
    }

    /**
     * 删除角色
     */
    @Operation(summary = "删除角色")
    @DeleteMapping("/{id}")
    public Result<Void> deleteRole(@Parameter(description = "角色ID") @PathVariable Long id) {
        return roleService.deleteRole(id);
    }

    /**
     * 批量删除角色
     */
    @Operation(summary = "批量删除角色")
    @DeleteMapping("/batch")
    public Result<Void> batchDeleteRoles(@Parameter(description = "角色ID列表") @RequestBody List<Long> ids) {
        return roleService.batchDeleteRoles(ids);
    }

    /**
     * 更新角色状�?
     */
    @Operation(summary = "更新角色状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateRoleStatus(
            @Parameter(description = "角色ID") @PathVariable Long id,
            @Parameter(description = "状态") @RequestParam Integer status) {
        return roleService.updateRoleStatus(id, status);
    }

    /**
     * 获取角色权限列表
     */
    @Operation(summary = "获取角色权限列表")
    @GetMapping("/{id}/permissions")
    public Result<List<Permission>> getRolePermissions(@Parameter(description = "角色ID") @PathVariable Long id) {
        return roleService.getRolePermissions(id);
    }

    /**
     * 分配角色权限
     */
    @Operation(summary = "分配角色权限")
    @PostMapping("/{id}/permissions")
    public Result<Void> assignRolePermissions(
            @Parameter(description = "角色ID") @PathVariable Long id,
            @Parameter(description = "权限ID列表") @RequestBody List<Long> permissionIds) {
        return roleService.assignRolePermissions(id, permissionIds);
    }

    /**
     * 获取所有启用的角色（用于下拉选择�?
     */
    @Operation(summary = "获取所有启用的角色")
    @GetMapping("/active")
    public Result<List<Role>> getActiveRoles() {
        return roleService.getActiveRoles();
    }

    /**
     * 更新角色排序
     */
    @Operation(summary = "更新角色排序")
    @PutMapping("/{id}/sort")
    public Result<Void> updateRoleSortOrder(
            @Parameter(description = "角色ID") @PathVariable Long id,
            @Parameter(description = "排序") @RequestParam Integer sortOrder) {
        return roleService.updateRoleSortOrder(id, sortOrder);
    }
}
