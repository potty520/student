package com.school.grade.service;

import com.school.grade.entity.Role;
import com.school.grade.entity.Permission;
import com.school.grade.repository.RoleRepository;
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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 角色管理业务逻辑层
 * 
 * @author Qoder
 * @version 1.0.0
 */
@Service
@Transactional
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    /**
     * 分页查询角色信息
     * 
     * @param page 页码
     * @param size 每页大小
     * @param roleName 角色名称（模糊查询）
     * @param roleCode 角色编码（模糊查询）
     * @param status 状态
     * @return 分页结果
     */
    public Result<PageResult<Role>> getRoleList(int page, int size, String roleName, 
                                               String roleCode, Integer status) {
        try {
            // 创建分页对象
            Pageable pageable = PageRequest.of(page - 1, size, 
                Sort.by(Sort.Direction.ASC, "sortOrder", "createTime"));

            // 构建查询条件
            Specification<Role> spec = (root, query, criteriaBuilder) -> {
                List<Predicate> predicates = new ArrayList<>();
                
                // 软删除条件
                predicates.add(criteriaBuilder.equal(root.get("deleted"), 0));
                
                // 角色名称模糊查询
                if (StringUtils.hasText(roleName)) {
                    predicates.add(criteriaBuilder.like(root.get("roleName"), "%" + roleName + "%"));
                }
                
                // 角色编码模糊查询
                if (StringUtils.hasText(roleCode)) {
                    predicates.add(criteriaBuilder.like(root.get("roleCode"), "%" + roleCode + "%"));
                }
                
                // 状态查询
                if (status != null) {
                    predicates.add(criteriaBuilder.equal(root.get("status"), status));
                }
                
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            };

            Page<Role> rolePage = roleRepository.findAll(spec, pageable);
            
            PageResult<Role> pageResult = new PageResult<>(
                rolePage.getContent(),
                rolePage.getTotalElements(),
                page,
                size
            );

            return Result.success(pageResult);
        } catch (Exception e) {
            return Result.error("查询角色列表失败：" + e.getMessage());
        }
    }

    /**
     * 根据ID获取角色信息
     * 
     * @param id 角色ID
     * @return 角色信息
     */
    public Result<Role> getRoleById(Long id) {
        try {
            Optional<Role> role = roleRepository.findByIdAndDeleted(id, 0);
            if (role.isPresent()) {
                return Result.success(role.get());
            } else {
                return Result.error("角色信息不存在");
            }
        } catch (Exception e) {
            return Result.error("获取角色信息失败：" + e.getMessage());
        }
    }

    /**
     * 添加角色
     * 
     * @param role 角色信息
     * @return 操作结果
     */
    public Result<Role> addRole(Role role) {
        try {
            // 数据验证
            Result<Void> validateResult = validateRole(role);
            if (!validateResult.isSuccess()) {
                return Result.error(validateResult.getMessage());
            }

            // 检查角色编码是否已存在
            if (roleRepository.existsByRoleCodeAndDeleted(role.getRoleCode(), 0)) {
                return Result.error("角色编码已存在");
            }

            // 设置默认值
            if (role.getStatus() == null) {
                role.setStatus(1); // 默认启用状态
            }
            if (role.getSortOrder() == null) {
                role.setSortOrder(0);
            }

            Role savedRole = roleRepository.save(role);
            return Result.success(savedRole);
        } catch (Exception e) {
            return Result.error("添加角色失败：" + e.getMessage());
        }
    }

    /**
     * 更新角色信息
     * 
     * @param role 角色信息
     * @return 操作结果
     */
    public Result<Role> updateRole(Role role) {
        try {
            // 检查角色是否存在
            Optional<Role> existingRole = roleRepository.findByIdAndDeleted(role.getId(), 0);
            if (!existingRole.isPresent()) {
                return Result.error("角色信息不存在");
            }

            // 数据验证
            Result<Void> validateResult = validateRole(role);
            if (!validateResult.isSuccess()) {
                return Result.error(validateResult.getMessage());
            }

            // 检查角色编码是否被其他角色占用
            Optional<Role> duplicateRole = roleRepository.findByRoleCodeAndDeleted(role.getRoleCode(), 0);
            if (duplicateRole.isPresent() && !duplicateRole.get().getId().equals(role.getId())) {
                return Result.error("角色编码已被其他角色使用");
            }

            Role existing = existingRole.get();
            
            // 检查系统内置角色
            if ("ADMIN".equals(existing.getRoleCode()) && !role.getRoleCode().equals("ADMIN")) {
                return Result.error("不能修改系统管理员角色编码");
            }

            Role savedRole = roleRepository.save(role);
            return Result.success(savedRole);
        } catch (Exception e) {
            return Result.error("更新角色信息失败：" + e.getMessage());
        }
    }

    /**
     * 删除角色（软删除）
     * 
     * @param id 角色ID
     * @return 操作结果
     */
    public Result<Void> deleteRole(Long id) {
        try {
            Optional<Role> role = roleRepository.findByIdAndDeleted(id, 0);
            if (!role.isPresent()) {
                return Result.error("角色信息不存在");
            }

            Role roleEntity = role.get();
            
            // 检查是否为系统内置角色
            if ("ADMIN".equals(roleEntity.getRoleCode())) {
                return Result.error("不能删除系统管理员角色");
            }

            // 检查是否有用户在使用该角色
            // 这里可以添加用户角色关联检查逻辑

            roleEntity.setDeleted(1);
            roleRepository.save(roleEntity);

            return Result.success();
        } catch (Exception e) {
            return Result.error("删除角色失败：" + e.getMessage());
        }
    }

    /**
     * 批量删除角色
     * 
     * @param ids 角色ID列表
     * @return 操作结果
     */
    public Result<Void> batchDeleteRoles(List<Long> ids) {
        try {
            List<Role> roles = roleRepository.findByIdInAndDeleted(ids, 0);
            if (roles.isEmpty()) {
                return Result.error("未找到要删除的角色");
            }

            // 检查是否包含系统内置角色
            boolean hasSystemRole = roles.stream()
                .anyMatch(role -> "ADMIN".equals(role.getRoleCode()));
            if (hasSystemRole) {
                return Result.error("不能删除系统内置角色");
            }

            roles.forEach(role -> role.setDeleted(1));
            roleRepository.saveAll(roles);

            return Result.success();
        } catch (Exception e) {
            return Result.error("批量删除角色失败：" + e.getMessage());
        }
    }

    /**
     * 更新角色状态
     * 
     * @param id 角色ID
     * @param status 新状态
     * @return 操作结果
     */
    public Result<Void> updateRoleStatus(Long id, Integer status) {
        try {
            Optional<Role> role = roleRepository.findByIdAndDeleted(id, 0);
            if (!role.isPresent()) {
                return Result.error("角色信息不存在");
            }

            // 验证状态值
            if (status < 0 || status > 1) {
                return Result.error("无效的状态值");
            }

            Role roleEntity = role.get();
            
            // 检查是否为系统管理员角色
            if ("ADMIN".equals(roleEntity.getRoleCode()) && status == 0) {
                return Result.error("不能禁用系统管理员角色");
            }

            roleEntity.setStatus(status);
            roleRepository.save(roleEntity);

            return Result.success();
        } catch (Exception e) {
            return Result.error("更新角色状态失败：" + e.getMessage());
        }
    }

    /**
     * 获取角色权限列表
     * 
     * @param roleId 角色ID
     * @return 权限列表
     */
    public Result<List<Permission>> getRolePermissions(Long roleId) {
        try {
            Optional<Role> role = roleRepository.findByIdAndDeleted(roleId, 0);
            if (!role.isPresent()) {
                return Result.error("角色信息不存在");
            }

            List<Permission> permissions = permissionRepository.findByRoleId(roleId);
            return Result.success(permissions);
        } catch (Exception e) {
            return Result.error("获取角色权限失败：" + e.getMessage());
        }
    }

    /**
     * 分配角色权限
     * 
     * @param roleId 角色ID
     * @param permissionIds 权限ID列表
     * @return 操作结果
     */
    public Result<Void> assignRolePermissions(Long roleId, List<Long> permissionIds) {
        try {
            Optional<Role> role = roleRepository.findByIdAndDeleted(roleId, 0);
            if (!role.isPresent()) {
                return Result.error("角色信息不存在");
            }

            // 验证权限是否存在
            if (permissionIds != null && !permissionIds.isEmpty()) {
                for (Long permissionId : permissionIds) {
                    if (!permissionRepository.existsByIdAndDeleted(permissionId, 0)) {
                        return Result.error("权限ID " + permissionId + " 不存在");
                    }
                }
            }

            // 使用JPA实体关系来处理角色权限关联
            Role roleEntity = role.get();
            
            if (permissionIds == null || permissionIds.isEmpty()) {
                // 清空所有权限
                roleEntity.setPermissions(new HashSet<>());
            } else {
                // 查询权限实体并设置关联
                List<Permission> permissions = permissionRepository.findByIdInAndDeleted(permissionIds, 0);
                roleEntity.setPermissions(new HashSet<>(permissions));
            }
            
            roleRepository.save(roleEntity);
            return Result.success();
        } catch (Exception e) {
            return Result.error("分配角色权限失败：" + e.getMessage());
        }
    }

    /**
     * 获取所有启用的角色
     * 
     * @return 角色列表
     */
    public Result<List<Role>> getActiveRoles() {
        try {
            List<Role> roles = roleRepository.findByStatusAndDeletedOrderBySortOrderAsc(1, 0);
            return Result.success(roles);
        } catch (Exception e) {
            return Result.error("获取启用角色列表失败：" + e.getMessage());
        }
    }

    /**
     * 更新角色排序
     * 
     * @param id 角色ID
     * @param sortOrder 排序号
     * @return 操作结果
     */
    public Result<Void> updateRoleSortOrder(Long id, Integer sortOrder) {
        try {
            Optional<Role> role = roleRepository.findByIdAndDeleted(id, 0);
            if (!role.isPresent()) {
                return Result.error("角色信息不存在");
            }

            Role roleEntity = role.get();
            roleEntity.setSortOrder(sortOrder);
            roleRepository.save(roleEntity);

            return Result.success();
        } catch (Exception e) {
            return Result.error("更新角色排序失败：" + e.getMessage());
        }
    }

    /**
     * 数据验证
     * 
     * @param role 角色信息
     * @return 验证结果
     */
    private Result<Void> validateRole(Role role) {
        if (role == null) {
            return Result.error("角色信息不能为空");
        }

        if (!StringUtils.hasText(role.getRoleCode())) {
            return Result.error("角色编码不能为空");
        }

        if (role.getRoleCode().length() > 50) {
            return Result.error("角色编码长度不能超过50个字符");
        }

        if (!role.getRoleCode().matches("^[A-Z0-9_]+$")) {
            return Result.error("角色编码只能包含大写字母、数字和下划线");
        }

        if (!StringUtils.hasText(role.getRoleName())) {
            return Result.error("角色名称不能为空");
        }

        if (role.getRoleName().length() > 50) {
            return Result.error("角色名称长度不能超过50个字符");
        }

        return Result.success();
    }
}