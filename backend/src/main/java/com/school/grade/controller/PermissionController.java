package com.school.grade.controller;

import com.school.grade.entity.Permission;
import com.school.grade.service.PermissionService;
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
 * 权限管理控制�?
 * 
 * @author Qoder
 * @version 1.0.0
 */
@Tag(name = "权限管理")
@RestController
@RequestMapping("/api/permissions")
@CrossOrigin(origins = "*")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    /**
     * 分页查询权限列表
     */
    @Operation(summary = "分页查询权限列表")
    @GetMapping("/list")
    public Result<PageResult<Permission>> getPermissionList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "权限名称") @RequestParam(required = false) String permissionName,
            @Parameter(description = "权限编码") @RequestParam(required = false) String permissionCode,
            @Parameter(description = "权限类型") @RequestParam(required = false) Integer permissionType,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        
        return permissionService.getPermissionList(page, size, permissionName, permissionCode, permissionType, status);
    }

    /**
     * 根据ID获取权限信息
     */
    @Operation(summary = "根据ID获取权限信息")
    @GetMapping("/{id}")
    public Result<Permission> getPermissionById(@Parameter(description = "权限ID") @PathVariable Long id) {
        return permissionService.getPermissionById(id);
    }

    /**
     * 添加权限
     */
    @Operation(summary = "添加权限")
    @PostMapping
    public Result<Permission> addPermission(@Parameter(description = "权限信息") @Valid @RequestBody Permission permission) {
        return permissionService.addPermission(permission);
    }

    /**
     * 更新权限信息
     */
    @Operation(summary = "更新权限信息")
    @PutMapping("/{id}")
    public Result<Permission> updatePermission(
            @Parameter(description = "权限ID") @PathVariable Long id,
            @Parameter(description = "权限信息") @Valid @RequestBody Permission permission) {
        permission.setId(id);
        return permissionService.updatePermission(permission);
    }

    /**
     * 删除权限
     */
    @Operation(summary = "删除权限")
    @DeleteMapping("/{id}")
    public Result<Void> deletePermission(@Parameter(description = "权限ID") @PathVariable Long id) {
        return permissionService.deletePermission(id);
    }

    /**
     * 批量删除权限
     */
    @Operation(summary = "批量删除权限")
    @DeleteMapping("/batch")
    public Result<Void> batchDeletePermissions(@Parameter(description = "权限ID列表") @RequestBody List<Long> ids) {
        return permissionService.batchDeletePermissions(ids);
    }

    /**
     * 更新权限状�?
     */
    @Operation(summary = "更新权限状态")
    @PutMapping("/{id}/status")
    public Result<Void> updatePermissionStatus(
            @Parameter(description = "权限ID") @PathVariable Long id,
            @Parameter(description = "状态") @RequestParam Integer status) {
        return permissionService.updatePermissionStatus(id, status);
    }

    /**
     * 获取权限树结�?
     */
    @Operation(summary = "获取权限树结构")
    @GetMapping("/tree")
    public Result<List<Permission>> getPermissionTree() {
        return permissionService.getPermissionTree();
    }

    /**
     * 根据父ID获取子权限列�?
     */
    @Operation(summary = "根据父ID获取子权限列表")
    @GetMapping("/children/{parentId}")
    public Result<List<Permission>> getChildPermissions(@Parameter(description = "父权限ID") @PathVariable Long parentId) {
        return permissionService.getChildPermissions(parentId);
    }

    /**
     * 获取所有启用的权限（用于下拉选择�?
     */
    @Operation(summary = "获取所有启用的权限")
    @GetMapping("/active")
    public Result<List<Permission>> getActivePermissions() {
        return permissionService.getActivePermissions();
    }

    /**
     * 更新权限排序
     */
    @Operation(summary = "更新权限排序")
    @PutMapping("/{id}/sort")
    public Result<Void> updatePermissionSortOrder(
            @Parameter(description = "权限ID") @PathVariable Long id,
            @Parameter(description = "排序") @RequestParam Integer sortOrder) {
        return permissionService.updatePermissionSortOrder(id, sortOrder);
    }
}
