package com.school.grade.repository;

import com.school.grade.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户Repository
 * 
 * @author Qoder
 * @version 1.0.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    /**
     * 根据用户名查找用户
     * 
     * @param username 用户名
     * @return 用户信息
     */
    Optional<User> findByUsernameAndDeleted(String username, Integer deleted);

    /**
     * 检查用户名是否已存在
     */
    boolean existsByUsernameAndDeleted(String username, Integer deleted);

    /**
     * 根据ID列表和删除标记查找用户
     */
    List<User> findByIdInAndDeleted(List<Long> ids, Integer deleted);

    /**
     * 根据用户名查找用户（排除指定用户）
     * 
     * @param username 用户名
     * @param id 排除的用户ID
     * @param deleted 删除标记
     * @return 是否存在
     */
    boolean existsByUsernameAndIdNotAndDeleted(String username, Long id, Integer deleted);

    /**
     * 根据手机号查找用户
     * 
     * @param phone 手机号
     * @param deleted 删除标记
     * @return 用户信息
     */
    Optional<User> findByPhoneAndDeleted(String phone, Integer deleted);

    /**
     * 根据邮箱查找用户
     * 
     * @param email 邮箱
     * @param deleted 删除标记
     * @return 用户信息
     */
    Optional<User> findByEmailAndDeleted(String email, Integer deleted);

    /**
     * 查询用户及其角色信息
     * 
     * @param username 用户名
     * @return 用户信息
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles r WHERE u.username = :username AND u.deleted = 0")
    Optional<User> findByUsernameWithRoles(@Param("username") String username);

    /**
     * 统计用户数量
     * 
     * @param deleted 删除标记
     * @return 用户数量
     */
    long countByDeleted(Integer deleted);

    /**
     * 根据ID和删除标记查找用户
     */
    Optional<User> findByIdAndDeleted(Long id, Integer deleted);

    /**
     * 按状态和删除标记查询用户列表
     */
    List<User> findByStatusAndDeleted(Integer status, Integer deleted);

    /**
     * 根据角色ID查询用户列表
     */
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.id = :roleId AND u.status = :status AND u.deleted = :deleted")
    List<User> findByRoleIdAndStatusAndDeleted(@Param("roleId") Long roleId, @Param("status") Integer status, @Param("deleted") Integer deleted);

    /**
     * 检查用户ID是否存在（boolean版本）
     */
    boolean existsByIdAndDeleted(Long id, boolean deleted);

    /**
     * 检查用户ID是否存在（Integer版本）
     */
    boolean existsByIdAndDeleted(Long id, Integer deleted);

    /**
     * 根据班级ID查询用户列表（教师）
     * 查询与指定班级相关的教师用户（班主任和任课教师）
     */
    @Query("SELECT DISTINCT u FROM User u " +
           "JOIN Teacher t ON u.id = t.userId " +
           "WHERE (t.id IN (SELECT c.headTeacherId FROM SchoolClass c WHERE c.id = :classId) " +
           "   OR t.id IN (SELECT tcc.teacherId FROM TeacherClassCourse tcc WHERE tcc.classId = :classId)) " +
           "AND u.status = :status AND u.deleted = :deleted")
    List<User> findByClassIdAndStatusAndDeleted(@Param("classId") Long classId, @Param("status") Integer status, @Param("deleted") Integer deleted);
}