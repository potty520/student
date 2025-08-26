package com.school.grade.repository;

import com.school.grade.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 考试Repository
 * 
 * @author Qoder
 * @version 1.0.0
 */
@Repository
public interface ExamRepository extends JpaRepository<Exam, Long>, JpaSpecificationExecutor<Exam> {

    /**
     * 根据考试编码查找考试
     * 
     * @param examCode 考试编码
     * @param deleted 删除标记
     * @return 考试信息
     */
    Optional<Exam> findByExamCodeAndDeleted(String examCode, Integer deleted);

    /**
     * 根据考试编码查找考试（排除指定考试）
     * 
     * @param examCode 考试编码
     * @param id 排除的考试ID
     * @param deleted 删除标记
     * @return 是否存在
     */
    boolean existsByExamCodeAndIdNotAndDeleted(String examCode, Long id, Integer deleted);

    /**
     * 根据学年查询考试列表
     * 
     * @param schoolYear 学年
     * @param deleted 删除标记
     * @return 考试列表
     */
    List<Exam> findBySchoolYearAndDeleted(String schoolYear, Integer deleted);

    /**
     * 根据学年和学期查询考试列表
     * 
     * @param schoolYear 学年
     * @param semester 学期
     * @param deleted 删除标记
     * @return 考试列表
     */
    List<Exam> findBySchoolYearAndSemesterAndDeleted(String schoolYear, Integer semester, Integer deleted);

    /**
     * 根据考试类型查询考试列表
     * 
     * @param examType 考试类型
     * @param deleted 删除标记
     * @return 考试列表
     */
    List<Exam> findByExamTypeAndDeleted(Integer examType, Integer deleted);

    /**
     * 根据状态查询考试列表
     * 
     * @param status 状态
     * @param deleted 删除标记
     * @return 考试列表
     */
    List<Exam> findByStatusAndDeletedOrderByStartDateDesc(Integer status, Integer deleted);

    /**
     * 查询指定日期范围内的考试
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param deleted 删除标记
     * @return 考试列表
     */
    @Query("SELECT e FROM Exam e WHERE e.startDate >= :startDate AND e.endDate <= :endDate AND e.deleted = :deleted ORDER BY e.startDate")
    List<Exam> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("deleted") Integer deleted);

    /**
     * 查询进行中的考试
     * 
     * @param currentDate 当前日期
     * @param deleted 删除标记
     * @return 考试列表
     */
    @Query("SELECT e FROM Exam e WHERE e.startDate <= :currentDate AND e.endDate >= :currentDate AND e.deleted = :deleted")
    List<Exam> findOngoingExams(@Param("currentDate") LocalDate currentDate, @Param("deleted") Integer deleted);

    /**
     * 根据年级ID查询相关考试
     * 
     * @param gradeId 年级ID
     * @param deleted 删除标记
     * @return 考试列表
     */
    @Query("SELECT e FROM Exam e WHERE FIND_IN_SET(:gradeId, e.gradeIds) > 0 AND e.deleted = :deleted ORDER BY e.startDate DESC")
    List<Exam> findByGradeId(@Param("gradeId") Long gradeId, @Param("deleted") Integer deleted);

    /**
     * 根据课程ID查询相关考试
     * 
     * @param courseId 课程ID
     * @param deleted 删除标记
     * @return 考试列表
     */
    @Query("SELECT e FROM Exam e WHERE FIND_IN_SET(:courseId, e.courseIds) > 0 AND e.deleted = :deleted ORDER BY e.startDate DESC")
    List<Exam> findByCourseId(@Param("courseId") Long courseId, @Param("deleted") Integer deleted);

    // Service层使用的附加方法
    Optional<Exam> findByIdAndDeleted(Long id, Integer deleted);
    boolean existsByExamCodeAndDeleted(String examCode, Integer deleted);
    List<Exam> findByIdInAndDeleted(List<Long> ids, Integer deleted);
    List<Exam> findBySchoolYearAndSemesterAndDeletedOrderByStartDateAsc(String schoolYear, Integer semester, Integer deleted);
    
    /**
     * 查询当前进行中的考试
     */
    @Query("SELECT e FROM Exam e WHERE e.startDate <= :currentDate AND e.endDate >= :currentDate AND e.deleted = :deleted AND e.status = 1")
    List<Exam> findCurrentExams(@Param("currentDate") LocalDate currentDate, @Param("deleted") Integer deleted);
    
    boolean existsByIdAndDeleted(Long id, Integer deleted);
}