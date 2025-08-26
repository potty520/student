package com.school.grade.repository;

import com.school.grade.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 年级Repository
 * 
 * @author Qoder
 * @version 1.0.0
 */
@Repository
public interface GradeRepository extends JpaRepository<Grade, Long>, JpaSpecificationExecutor<Grade> {

    /**
     * 根据年级编码查找年级
     * 
     * @param gradeCode 年级编码
     * @param deleted 删除标记
     * @return 年级信息
     */
    Optional<Grade> findByGradeCodeAndDeleted(String gradeCode, Integer deleted);

    /**
     * 根据年级编码查找年级（排除指定年级）
     * 
     * @param gradeCode 年级编码
     * @param id 排除的年级ID
     * @param deleted 删除标记
     * @return 是否存在
     */
    boolean existsByGradeCodeAndIdNotAndDeleted(String gradeCode, Long id, Integer deleted);

    /**
     * 根据学段查询年级列表
     * 
     * @param stage 学段
     * @param deleted 删除标记
     * @return 年级列表
     */
    List<Grade> findByStageAndDeletedOrderBySortOrder(Integer stage, Integer deleted);

    /**
     * 根据学年查询年级列表
     * 
     * @param schoolYear 学年
     * @param deleted 删除标记
     * @return 年级列表
     */
    List<Grade> findBySchoolYearAndDeletedOrderBySortOrder(String schoolYear, Integer deleted);

    /**
     * 查询所有启用的年级
     * 
     * @param status 状态
     * @param deleted 删除标记
     * @return 年级列表
     */
    List<Grade> findByStatusAndDeletedOrderBySortOrder(Integer status, Integer deleted);

    /**
     * 根据ID和删除标记查找年级
     */
    Optional<Grade> findByIdAndDeleted(Long id, Integer deleted);

    /**
     * 检查年级编码是否已存在
     */
    boolean existsByGradeCodeAndDeleted(String gradeCode, Integer deleted);

    /**
     * 检查学年和年级级别组合是否已存在
     */
    boolean existsBySchoolYearAndGradeLevelAndDeleted(String schoolYear, Integer gradeLevel, Integer deleted);

    /**
     * 统计年级下的班级数量
     */
    @Query("SELECT COUNT(c) FROM SchoolClass c WHERE c.gradeId = :gradeId AND c.deleted = 0")
    long countClassesByGradeId(@Param("gradeId") Long gradeId);

    /**
     * 按学年和状态查询年级
     */
    List<Grade> findBySchoolYearAndStatusAndDeletedOrderByGradeLevelAsc(String schoolYear, Integer status, Integer deleted);

    // Service层使用的附加方法
    Optional<Grade> findByIdAndDeletedFalse(Long id);
    boolean existsByGradeCodeAndDeletedFalse(String gradeCode);
    boolean existsBySchoolYearAndGradeLevelAndDeletedFalse(String schoolYear, Integer gradeLevel);
    List<Grade> findByDeletedFalse(org.springframework.data.domain.Pageable pageable);
    
    /**
     * 根据条件查询年级列表
     */
    @Query("SELECT g FROM Grade g WHERE g.deleted = 0 " +
           "AND (:keyword IS NULL OR g.gradeName LIKE %:keyword% OR g.gradeCode LIKE %:keyword%) " +
           "AND (:schoolYear IS NULL OR g.schoolYear = :schoolYear)")
    org.springframework.data.domain.Page<Grade> findByConditions(@Param("keyword") String keyword, 
                                                                 @Param("schoolYear") String schoolYear, 
                                                                 org.springframework.data.domain.Pageable pageable);
    
    List<Grade> findByStatusAndDeletedFalseOrderBySortOrderAscGradeLevelAsc(Integer status);
    List<Grade> findBySchoolYearAndStatusAndDeletedFalseOrderByGradeLevelAsc(String schoolYear, Integer status);
    boolean existsByIdAndDeleted(Long id, Integer deleted);
}