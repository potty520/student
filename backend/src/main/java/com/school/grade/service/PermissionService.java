package com.school.grade.service;

import com.school.grade.entity.Permission;
import com.school.grade.repository.PermissionRepository;
import com.school.grade.common.result.Result;
import com.school.grade.common.result.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 权限管理业务逻辑层
 * 
 * @author Qoder
 * @version 1.0.0
 */
@Service
@Transactional
public class PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    /**
     * 分页查询权限信息
     * 
     * @param page 页码
     * @param size 每页大小
     * @param permissionName 权限名称（模糊查询）
     * @param permissionCode 权限编码（模糊查询）
     * @param permissionType 权限类型
     * @param status 状态
     * @return 分页结果
     */
    public Result<PageResult<Permission>> getPermissionList(int page, int size, String permissionName, 
                                                           String permissionCode, Integer permissionType, Integer status) {
        try {
            // 创建分页对象
            Pageable pageable = PageRequest.of(page - 1, size, 
                Sort.by(Sort.Direction.ASC, "sortOrder", "createTime"));

            // 构建查询条件
            Specification<Permission> spec = (root, query, criteriaBuilder) -> {
                List<Predicate> predicates = new ArrayList<>();
                
                // 软删除条件
                predicates.add(criteriaBuilder.equal(root.get("deleted"), 0));
                
                // 权限名称模糊查询
                if (StringUtils.hasText(permissionName)) {
                    predicates.add(criteriaBuilder.like(root.get("permissionName"), "%" + permissionName + "%"));
                }
                
                // 权限编码模糊查询
                if (StringUtils.hasText(permissionCode)) {
                    predicates.add(criteriaBuilder.like(root.get("permissionCode"), "%" + permissionCode + "%"));
                }
                
                // 权限类型查询
                if (permissionType != null) {
                    predicates.add(criteriaBuilder.equal(root.get("permissionType"), permissionType));
                }
                
                // 状态查询
                if (status != null) {
                    predicates.add(criteriaBuilder.equal(root.get("status"), status));
                }
                
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            };

            Page<Permission> permissionPage = permissionRepository.findAll(spec, pageable);
            
            PageResult<Permission> pageResult = new PageResult<>(
                permissionPage.getContent(),
                permissionPage.getTotalElements(),
                page,
                size
            );

            return Result.success(pageResult);
        } catch (Exception e) {
            return Result.error("查询权限列表失败：" + e.getMessage());
        }
    }

    /**
     * 根据ID获取权限信息
     * 
     * @param id 权限ID
     * @return 权限信息
     */
    public Result<Permission> getPermissionById(Long id) {
        try {
            Optional<Permission> permission = permissionRepository.findByIdAndDeleted(id, 0);
            if (permission.isPresent()) {
                return Result.success(permission.get());
            } else {
                return Result.error("权限信息不存在");
            }
        } catch (Exception e) {
            return Result.error("获取权限信息失败：" + e.getMessage());
        }
    }

    /**
     * 添加权限
     * 
     * @param permission 权限信息
     * @return 操作结果
     */
    public Result<Permission> addPermission(Permission permission) {
        try {
            // 数据验证
            Result<Void> validateResult = validatePermission(permission);
            if (!validateResult.isSuccess()) {
                return Result.error(validateResult.getMessage());
            }

            // 检查权限编码是否已存在
            if (permissionRepository.existsByPermissionCodeAndDeleted(permission.getPermissionCode(), 0)) {
                return Result.error("权限编码已存在");
            }

            // 验证父权限是否存在
            if (permission.getParentId() != null && permission.getParentId() > 0) {
                if (!permissionRepository.existsByIdAndDeleted(permission.getParentId(), 0)) {
                    return Result.error("父权限不存在");
                }
            }

            // 设置默认值
            if (permission.getStatus() == null) {
                permission.setStatus(1); // 默认启用状态
            }
            if (permission.getSortOrder() == null) {
                permission.setSortOrder(0);
            }
            if (permission.getParentId() == null) {
                permission.setParentId(0L); // 顶级权限
            }

            Permission savedPermission = permissionRepository.save(permission);
            return Result.success(savedPermission);
        } catch (Exception e) {
            return Result.error("添加权限失败：" + e.getMessage());
        }
    }

    /**
     * 更新权限信息
     * 
     * @param permission 权限信息
     * @return 操作结果
     */
    public Result<Permission> updatePermission(Permission permission) {
        try {
            // 检查权限是否存在
            Optional<Permission> existingPermission = permissionRepository.findByIdAndDeleted(permission.getId(), 0);
            if (!existingPermission.isPresent()) {
                return Result.error("权限信息不存在");
            }

            // 数据验证
            Result<Void> validateResult = validatePermission(permission);
            if (!validateResult.isSuccess()) {
                return Result.error(validateResult.getMessage());
            }

            // 检查权限编码是否被其他权限占用
            Optional<Permission> duplicatePermission = permissionRepository.findByPermissionCodeAndDeleted(permission.getPermissionCode(), 0);
            if (duplicatePermission.isPresent() && !duplicatePermission.get().getId().equals(permission.getId())) {
                return Result.error("权限编码已被其他权限使用");
            }

            // 验证父权限是否存在（不能设置自己为父权限）
            if (permission.getParentId() != null && permission.getParentId() > 0) {
                if (permission.getParentId().equals(permission.getId())) {
                    return Result.error("不能设置自己为父权限");
                }
                if (!permissionRepository.existsByIdAndDeleted(permission.getParentId(), 0)) {
                    return Result.error("父权限不存在");
                }
            }

            // 检查是否会形成循环引用
            if (permission.getParentId() != null && permission.getParentId() > 0) {
                if (isCircularReference(permission.getId(), permission.getParentId())) {
                    return Result.error("不能设置子权限为父权限，会形成循环引用");
                }
            }

            Permission savedPermission = permissionRepository.save(permission);
            return Result.success(savedPermission);
        } catch (Exception e) {
            return Result.error("更新权限信息失败：" + e.getMessage());
        }
    }

    /**
     * 删除权限（软删除）
     * 
     * @param id 权限ID
     * @return 操作结果
     */
    public Result<Void> deletePermission(Long id) {
        try {
            Optional<Permission> permission = permissionRepository.findByIdAndDeleted(id, 0);
            if (!permission.isPresent()) {
                return Result.error("权限信息不存在");
            }

            // 检查是否有子权限
            List<Permission> childPermissions = permissionRepository.findByParentIdAndDeleted(id, 0);
            if (!childPermissions.isEmpty()) {
                return Result.error("该权限下还有子权限，不能删除");
            }

            Permission permissionEntity = permission.get();
            permissionEntity.setDeleted(1);
            permissionRepository.save(permissionEntity);

            return Result.success();
        } catch (Exception e) {
            return Result.error("删除权限失败：" + e.getMessage());
        }
    }

    /**
     * 批量删除权限
     * 
     * @param ids 权限ID列表
     * @return 操作结果
     */
    public Result<Void> batchDeletePermissions(List<Long> ids) {
        try {
            List<Permission> permissions = permissionRepository.findByIdInAndDeleted(ids, 0);
            if (permissions.isEmpty()) {
                return Result.error("未找到要删除的权限");
            }

            // 检查是否有子权限
            for (Permission permission : permissions) {
                List<Permission> childPermissions = permissionRepository.findByParentIdAndDeleted(permission.getId(), 0);
                if (!childPermissions.isEmpty()) {
                    return Result.error("权限【" + permission.getPermissionName() + "】下还有子权限，不能删除");
                }
            }

            permissions.forEach(permission -> permission.setDeleted(1));
            permissionRepository.saveAll(permissions);

            return Result.success();
        } catch (Exception e) {
            return Result.error("批量删除权限失败：" + e.getMessage());
        }
    }

    /**
     * 更新权限状态
     * 
     * @param id 权限ID
     * @param status 新状态
     * @return 操作结果
     */
    public Result<Void> updatePermissionStatus(Long id, Integer status) {
        try {
            Optional<Permission> permission = permissionRepository.findByIdAndDeleted(id, 0);
            if (!permission.isPresent()) {
                return Result.error("权限信息不存在");
            }

            // 验证状态值
            if (status < 0 || status > 1) {
                return Result.error("无效的状态值");
            }

            Permission permissionEntity = permission.get();
            permissionEntity.setStatus(status);
            permissionRepository.save(permissionEntity);

            return Result.success();
        } catch (Exception e) {
            return Result.error("更新权限状态失败：" + e.getMessage());
        }
    }

    /**
     * 获取权限树结构
     * 
     * @return 权限树
     */
    public Result<List<Permission>> getPermissionTree() {
        try {
            List<Permission> allPermissions = permissionRepository.findByDeletedOrderBySortOrderAsc(0);
            List<Permission> tree = buildPermissionTree(allPermissions, 0L);
            return Result.success(tree);
        } catch (Exception e) {
            return Result.error("获取权限树失败：" + e.getMessage());
        }
    }

    /**
     * 根据父ID获取子权限列表
     * 
     * @param parentId 父权限ID
     * @return 子权限列表
     */
    public Result<List<Permission>> getChildPermissions(Long parentId) {
        try {
            List<Permission> permissions = permissionRepository.findByParentIdAndDeletedOrderBySortOrderAsc(parentId, 0);
            return Result.success(permissions);
        } catch (Exception e) {
            return Result.error("获取子权限列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取所有启用的权限
     * 
     * @return 权限列表
     */
    public Result<List<Permission>> getActivePermissions() {
        try {
            List<Permission> permissions = permissionRepository.findByStatusAndDeletedOrderBySortOrderAsc(1, 0);
            return Result.success(permissions);
        } catch (Exception e) {
            return Result.error("获取启用权限列表失败：" + e.getMessage());
        }
    }

    /**
     * 更新权限排序
     * 
     * @param id 权限ID
     * @param sortOrder 排序号
     * @return 操作结果
     */
    public Result<Void> updatePermissionSortOrder(Long id, Integer sortOrder) {
        try {
            Optional<Permission> permission = permissionRepository.findByIdAndDeleted(id, 0);
            if (!permission.isPresent()) {
                return Result.error("权限信息不存在");
            }

            Permission permissionEntity = permission.get();
            permissionEntity.setSortOrder(sortOrder);
            permissionRepository.save(permissionEntity);

            return Result.success();
        } catch (Exception e) {
            return Result.error("更新权限排序失败：" + e.getMessage());
        }
    }

    /**
     * 构建权限树
     * 
     * @param allPermissions 所有权限
     * @param parentId 父权限ID
     * @return 权限树
     */
    private List<Permission> buildPermissionTree(List<Permission> allPermissions, Long parentId) {
        return allPermissions.stream()
            .filter(permission -> parentId.equals(permission.getParentId()))
            .map(permission -> {
                List<Permission> children = buildPermissionTree(allPermissions, permission.getId());
                permission.setChildren(children);
                return permission;
            })
            .collect(Collectors.toList());
    }

    /**
     * 检查是否存在循环引用
     * 
     * @param currentId 当前权限ID
     * @param parentId 父权限ID
     * @return 是否存在循环引用
     */
    private boolean isCircularReference(Long currentId, Long parentId) {
        if (parentId == null || parentId <= 0) {
            return false;
        }
        
        Optional<Permission> parent = permissionRepository.findByIdAndDeleted(parentId, 0);
        if (!parent.isPresent()) {
            return false;
        }
        
        Permission parentPermission = parent.get();
        if (currentId.equals(parentPermission.getParentId())) {
            return true;
        }
        
        return isCircularReference(currentId, parentPermission.getParentId());
    }

    /**
     * 数据验证
     * 
     * @param permission 权限信息
     * @return 验证结果
     */
    private Result<Void> validatePermission(Permission permission) {
        if (permission == null) {
            return Result.error("权限信息不能为空");
        }

        if (!StringUtils.hasText(permission.getPermissionCode())) {
            return Result.error("权限编码不能为空");
        }

        if (permission.getPermissionCode().length() > 100) {
            return Result.error("权限编码长度不能超过100个字符");
        }

        if (!StringUtils.hasText(permission.getPermissionName())) {
            return Result.error("权限名称不能为空");
        }

        if (permission.getPermissionName().length() > 50) {
            return Result.error("权限名称长度不能超过50个字符");
        }

        if (permission.getPermissionType() == null) {
            return Result.error("权限类型不能为空");
        }

        if (permission.getPermissionType() < 1 || permission.getPermissionType() > 3) {
            return Result.error("权限类型值必须在1-3之间");
        }

        return Result.success();
    }
}