package com.school.grade.repository;

import com.school.grade.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 角色Repository
 * 
 * @author Qoder
 * @version 1.0.0
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {

    /**
     * 根据角色编码查找角色
     * 
     * @param roleCode 角色编码
     * @param deleted 删除标记
     * @return 角色信息
     */
    Optional<Role> findByRoleCodeAndDeleted(String roleCode, Integer deleted);

    /**
     * 根据角色编码查找角色（排除指定角色）
     * 
     * @param roleCode 角色编码
     * @param id 排除的角色ID
     * @param deleted 删除标记
     * @return 是否存在
     */
    boolean existsByRoleCodeAndIdNotAndDeleted(String roleCode, Long id, Integer deleted);

    /**
     * 查询所有启用的角色
     * 
     * @param status 状态
     * @param deleted 删除标记
     * @return 角色列表
     */
    List<Role> findByStatusAndDeletedOrderBySortOrder(Integer status, Integer deleted);

    /**
     * 查询角色及其权限信息
     * 
     * @param roleId 角色ID
     * @return 角色信息
     */
    @Query("SELECT r FROM Role r LEFT JOIN FETCH r.permissions p WHERE r.id = :roleId AND r.deleted = 0")
    Optional<Role> findByIdWithPermissions(@Param("roleId") Long roleId);

    /**
     * 根据用户ID查询角色列表
     * 
     * @param userId 用户ID
     * @return 角色列表
     */
    @Query("SELECT r FROM User u JOIN u.roles r WHERE u.id = :userId AND r.deleted = 0")
    List<Role> findByUserId(@Param("userId") Long userId);

    /**
     * 根据ID和删除标记查找角色
     */
    Optional<Role> findByIdAndDeleted(Long id, Integer deleted);

    /**
     * 检查角色编码是否已存在
     */
    boolean existsByRoleCodeAndDeleted(String roleCode, Integer deleted);

    /**
     * 根据ID列表和删除标记查找角色
     */
    List<Role> findByIdInAndDeleted(List<Long> ids, Integer deleted);

    /**
     * 根据状态和删除标记查询角色列表
     */
    List<Role> findByStatusAndDeletedOrderBySortOrderAsc(Integer status, Integer deleted);

    /**
     * 删除角色权限关联 - 暂时禁用，使用Entity关系来处理
     */
    // @Modifying
    // @Query("DELETE FROM RolePermission rp WHERE rp.roleId = :roleId")
    // void deleteRolePermissions(@Param("roleId") Long roleId);
}