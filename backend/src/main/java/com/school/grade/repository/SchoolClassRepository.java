package com.school.grade.repository;

import com.school.grade.entity.SchoolClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 班级Repository
 * 
 * @author Qoder
 * @version 1.0.0
 */
@Repository
public interface SchoolClassRepository extends JpaRepository<SchoolClass, Long>, JpaSpecificationExecutor<SchoolClass> {

    /**
     * 根据班级编码查找班级
     * 
     * @param classCode 班级编码
     * @param deleted 删除标记
     * @return 班级信息
     */
    Optional<SchoolClass> findByClassCodeAndDeleted(String classCode, Integer deleted);

    /**
     * 根据班级编码查找班级（排除指定班级）
     * 
     * @param classCode 班级编码
     * @param id 排除的班级ID
     * @param deleted 删除标记
     * @return 是否存在
     */
    boolean existsByClassCodeAndIdNotAndDeleted(String classCode, Long id, Integer deleted);

    /**
     * 根据年级ID查询班级列表
     * 
     * @param gradeId 年级ID
     * @param deleted 删除标记
     * @return 班级列表
     */
    List<SchoolClass> findByGradeIdAndDeletedOrderBySortOrder(Long gradeId, Integer deleted);

    /**
     * 根据班主任ID查询班级
     * 
     * @param headTeacherId 班主任ID
     * @param deleted 删除标记
     * @return 班级列表
     */
    List<SchoolClass> findByHeadTeacherIdAndDeleted(Long headTeacherId, Integer deleted);

    /**
     * 查询所有启用的班级
     * 
     * @param status 状态
     * @param deleted 删除标记
     * @return 班级列表
     */
    List<SchoolClass> findByStatusAndDeletedOrderBySortOrder(Integer status, Integer deleted);

    /**
     * 查询班级及年级信息
     * 
     * @param classId 班级ID
     * @return 班级信息
     */
    @Query("SELECT c FROM SchoolClass c LEFT JOIN FETCH c.grade WHERE c.id = :classId AND c.deleted = 0")
    Optional<SchoolClass> findByIdWithGrade(@Param("classId") Long classId);

    /**
     * 根据教师ID查询任教班级
     * 
     * @param teacherId 教师ID
     * @return 班级列表
     */
    @Query("SELECT DISTINCT c FROM SchoolClass c " +
           "JOIN TeacherClassCourse tcc ON c.id = tcc.classId " +
           "WHERE tcc.teacherId = :teacherId AND c.deleted = 0 AND tcc.deleted = 0")
    List<SchoolClass> findByTeacherId(@Param("teacherId") Long teacherId);

    // Service层使用的附加方法
    Optional<SchoolClass> findByIdAndDeletedFalse(Long id);
    boolean existsByClassCodeAndDeletedFalse(String classCode);
    boolean existsByGradeIdAndClassNameAndDeletedFalse(Long gradeId, String className);
    org.springframework.data.domain.Page<SchoolClass> findByDeletedFalse(org.springframework.data.domain.Pageable pageable);
    
    /**
     * 根据条件查询班级列表
     */
    @Query("SELECT c FROM SchoolClass c WHERE c.deleted = 0 " +
           "AND (:keyword IS NULL OR c.className LIKE %:keyword% OR c.classCode LIKE %:keyword%) " +
           "AND (:gradeId IS NULL OR c.gradeId = :gradeId)")
    org.springframework.data.domain.Page<SchoolClass> findByConditions(@Param("keyword") String keyword, 
                                                                       @Param("gradeId") Long gradeId, 
                                                                       org.springframework.data.domain.Pageable pageable);
    
    List<SchoolClass> findByStatusAndDeletedFalseOrderBySortOrderAscClassNameAsc(Integer status);
    List<SchoolClass> findByGradeIdAndStatusAndDeletedFalseOrderBySortOrderAsc(Long gradeId, Integer status);
    boolean existsByIdAndDeleted(Long id, Integer deleted);
}