package com.school.grade.repository;

import com.school.grade.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 权限Repository
 * 
 * @author Qoder
 * @version 1.0.0
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long>, JpaSpecificationExecutor<Permission> {

    /**
     * 根据权限编码查找权限
     * 
     * @param permissionCode 权限编码
     * @param deleted 删除标记
     * @return 权限信息
     */
    Optional<Permission> findByPermissionCodeAndDeleted(String permissionCode, Integer deleted);

    /**
     * 根据权限编码查找权限（排除指定权限）
     * 
     * @param permissionCode 权限编码
     * @param id 排除的权限ID
     * @param deleted 删除标记
     * @return 是否存在
     */
    boolean existsByPermissionCodeAndIdNotAndDeleted(String permissionCode, Long id, Integer deleted);

    /**
     * 查询顶级权限（菜单）
     * 
     * @param deleted 删除标记
     * @return 权限列表
     */
    List<Permission> findByParentIdIsNullAndDeletedOrderBySortOrder(Integer deleted);

    /**
     * 根据父权限ID查询子权限
     * 
     * @param parentId 父权限ID
     * @param deleted 删除标记
     * @return 权限列表
     */
    List<Permission> findByParentIdAndDeletedOrderBySortOrder(Long parentId, Integer deleted);

    /**
     * 根据权限类型查询权限
     * 
     * @param permissionType 权限类型
     * @param deleted 删除标记
     * @return 权限列表
     */
    List<Permission> findByPermissionTypeAndDeletedOrderBySortOrder(Integer permissionType, Integer deleted);

    /**
     * 根据用户ID查询权限列表
     * 
     * @param userId 用户ID
     * @return 权限列表
     */
    @Query(value = "SELECT DISTINCT p.* FROM sys_permission p " +
           "INNER JOIN sys_role_permission rp ON p.id = rp.permission_id " +
           "INNER JOIN sys_role r ON rp.role_id = r.id " +
           "INNER JOIN sys_user_role ur ON r.id = ur.role_id " +
           "WHERE ur.user_id = :userId AND p.deleted = 0 AND r.deleted = 0 " +
           "ORDER BY p.sort_order", nativeQuery = true)
    List<Permission> findByUserId(@Param("userId") Long userId);

    /**
     * 根据角色ID查询权限列表
     * 
     * @param roleId 角色ID
     * @return 权限列表
     */
    @Query(value = "SELECT p.* FROM sys_permission p INNER JOIN sys_role_permission rp ON p.id = rp.permission_id WHERE rp.role_id = :roleId AND p.deleted = 0 ORDER BY p.sort_order", nativeQuery = true)
    List<Permission> findByRoleId(@Param("roleId") Long roleId);

    /**
     * 查询用户菜单权限
     * 
     * @param userId 用户ID
     * @param permissionType 权限类型
     * @return 权限列表
     */
    @Query(value = "SELECT DISTINCT p.* FROM sys_permission p " +
           "INNER JOIN sys_role_permission rp ON p.id = rp.permission_id " +
           "INNER JOIN sys_role r ON rp.role_id = r.id " +
           "INNER JOIN sys_user_role ur ON r.id = ur.role_id " +
           "WHERE ur.user_id = :userId AND p.permission_type = :permissionType " +
           "AND p.status = 1 AND p.deleted = 0 AND r.deleted = 0 " +
           "ORDER BY p.sort_order", nativeQuery = true)
    List<Permission> findMenuPermissionsByUserId(@Param("userId") Long userId, @Param("permissionType") Integer permissionType);

    /**
     * 根据ID和删除标记查找权限
     */
    Optional<Permission> findByIdAndDeleted(Long id, Integer deleted);

    /**
     * 检查权限编码是否已存在
     */
    boolean existsByPermissionCodeAndDeleted(String permissionCode, Integer deleted);

    /**
     * 根据ID列表和删除标记查找权限
     */
    List<Permission> findByIdInAndDeleted(List<Long> ids, Integer deleted);

    /**
     * 根据删除标记查询权限列表
     */
    List<Permission> findByDeletedOrderBySortOrderAsc(Integer deleted);

    /**
     * 根据父ID和删除标记查询子权限
     */
    List<Permission> findByParentIdAndDeleted(Long parentId, Integer deleted);

    /**
     * 根据父ID和删除标记查询子权限（按排序）
     */
    List<Permission> findByParentIdAndDeletedOrderBySortOrderAsc(Long parentId, Integer deleted);

    /**
     * 根据状态和删除标记查询权限列表
     */
    List<Permission> findByStatusAndDeletedOrderBySortOrderAsc(Integer status, Integer deleted);

    /**
     * 检查权限ID是否存在
     */
    boolean existsByIdAndDeleted(Long id, Integer deleted);
}