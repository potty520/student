package com.school.grade.repository;

import com.school.grade.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 学生Repository
 * 
 * @author Qoder
 * @version 1.0.0
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long>, JpaSpecificationExecutor<Student> {

    /**
     * 根据学号查找学生
     * 
     * @param studentCode 学号
     * @param deleted 删除标记
     * @return 学生信息
     */
    Optional<Student> findByStudentCodeAndDeleted(String studentCode, Integer deleted);

    /**
     * 根据学号查找学生（排除指定学生）
     * 
     * @param studentCode 学号
     * @param id 排除的学生ID
     * @param deleted 删除标记
     * @return 是否存在
     */
    boolean existsByStudentCodeAndIdNotAndDeleted(String studentCode, Long id, Integer deleted);

    /**
     * 根据用户ID查找学生
     * 
     * @param userId 用户ID
     * @param deleted 删除标记
     * @return 学生信息
     */
    Optional<Student> findByUserIdAndDeleted(Long userId, Integer deleted);

    /**
     * 根据身份证号查找学生
     * 
     * @param idCard 身份证号
     * @param deleted 删除标记
     * @return 学生信息
     */
    Optional<Student> findByIdCardAndDeleted(String idCard, Integer deleted);

    /**
     * 根据班级ID查询学生列表
     * 
     * @param classId 班级ID
     * @param deleted 删除标记
     * @return 学生列表
     */
    List<Student> findByClassIdAndDeleted(Long classId, Integer deleted);

    /**
     * 根据班级ID和状态查询学生列表
     * 
     * @param classId 班级ID
     * @param status 状态
     * @param deleted 删除标记
     * @return 学生列表
     */
    List<Student> findByClassIdAndStatusAndDeleted(Long classId, Integer status, Integer deleted);

    /**
     * 查询学生及班级信息
     * 
     * @param studentId 学生ID
     * @return 学生信息
     */
    @Query("SELECT s FROM Student s LEFT JOIN FETCH s.schoolClass sc LEFT JOIN FETCH sc.grade WHERE s.id = :studentId AND s.deleted = 0")
    Optional<Student> findByIdWithClass(@Param("studentId") Long studentId);

    /**
     * 根据监护人电话查询学生
     * 
     * @param guardianPhone 监护人电话
     * @param deleted 删除标记
     * @return 学生列表
     */
    List<Student> findByGuardianPhoneAndDeleted(String guardianPhone, Integer deleted);

    /**
     * 统计班级学生数量
     * 
     * @param classId 班级ID
     * @param status 学生状态
     * @param deleted 删除标记
     * @return 学生数量
     */
    long countByClassIdAndStatusAndDeleted(Long classId, Integer status, Integer deleted);

    /**
     * 根据年级ID查询学生列表
     * 
     * @param gradeId 年级ID
     * @return 学生列表
     */
    @Query("SELECT s FROM Student s " +
           "JOIN s.schoolClass c " +
           "WHERE c.gradeId = :gradeId AND s.deleted = 0 AND c.deleted = 0")
    List<Student> findByGradeId(@Param("gradeId") Long gradeId);

    /**
     * 根据年级ID统计学生数量
     * 
     * @param gradeId 年级ID
     * @param status 学生状态
     * @return 学生数量
     */
    @Query("SELECT COUNT(s) FROM Student s " +
           "JOIN s.schoolClass c " +
           "WHERE c.gradeId = :gradeId AND s.status = :status AND s.deleted = 0 AND c.deleted = 0")
    long countByGradeIdAndStatus(@Param("gradeId") Long gradeId, @Param("status") Integer status);

    /**
     * 根据ID和删除标记查找学生
     */
    Optional<Student> findByIdAndDeleted(Long id, Integer deleted);

    /**
     * 检查学号是否已存在
     */
    boolean existsByStudentCodeAndDeleted(String studentCode, Integer deleted);

    /**
     * 根据ID列表和删除标记查找学生
     */
    List<Student> findByIdInAndDeleted(List<Long> ids, Integer deleted);

    // Service层使用的附加方法
    long countByClassIdAndStatusAndDeletedFalse(Long classId, Integer status);
    boolean existsByIdAndDeleted(Long id, Integer deleted);
}