package com.school.grade.repository;

import com.school.grade.entity.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 成绩Repository
 * 
 * @author Qoder
 * @version 1.0.0
 */
@Repository
public interface ScoreRepository extends JpaRepository<Score, Long>, JpaSpecificationExecutor<Score> {

    /**
     * 根据考试、学生、课程查找成绩
     * 
     * @param examId 考试ID
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @param deleted 删除标记
     * @return 成绩信息
     */
    Optional<Score> findByExamIdAndStudentIdAndCourseIdAndDeleted(Long examId, Long studentId, Long courseId, Integer deleted);

    /**
     * 根据考试ID查询成绩列表
     * 
     * @param examId 考试ID
     * @param deleted 删除标记
     * @return 成绩列表
     */
    List<Score> findByExamIdAndDeleted(Long examId, Integer deleted);

    /**
     * 根据学生ID查询成绩列表
     * 
     * @param studentId 学生ID
     * @param deleted 删除标记
     * @return 成绩列表
     */
    @Query("SELECT s FROM Score s LEFT JOIN FETCH s.exam e LEFT JOIN FETCH s.course c WHERE s.studentId = :studentId AND s.deleted = :deleted ORDER BY e.startDate DESC")
    List<Score> findByStudentIdWithExamAndCourse(@Param("studentId") Long studentId, @Param("deleted") Integer deleted);

    /**
     * 根据考试ID和课程ID查询成绩列表
     * 
     * @param examId 考试ID
     * @param courseId 课程ID
     * @param deleted 删除标记
     * @return 成绩列表
     */
    @Query("SELECT s FROM Score s LEFT JOIN FETCH s.student st WHERE s.examId = :examId AND s.courseId = :courseId AND s.deleted = :deleted ORDER BY s.score DESC")
    List<Score> findByExamIdAndCourseIdWithStudent(@Param("examId") Long examId, @Param("courseId") Long courseId, @Param("deleted") Integer deleted);

    /**
     * 根据班级ID和考试ID查询成绩列表
     * 
     * @param classId 班级ID
     * @param examId 考试ID
     * @param deleted 删除标记
     * @return 成绩列表
     */
    @Query("SELECT s FROM Score s " +
           "JOIN s.student st " +
           "WHERE st.classId = :classId AND s.examId = :examId AND s.deleted = :deleted AND st.deleted = 0")
    List<Score> findByClassIdAndExamId(@Param("classId") Long classId, @Param("examId") Long examId, @Param("deleted") Integer deleted);

    /**
     * 计算班级某科目平均分
     * 
     * @param classId 班级ID
     * @param examId 考试ID
     * @param courseId 课程ID
     * @return 平均分
     */
    @Query("SELECT AVG(s.score) FROM Score s " +
           "JOIN s.student st " +
           "WHERE st.classId = :classId AND s.examId = :examId AND s.courseId = :courseId " +
           "AND s.absent = 0 AND s.deleted = 0 AND st.deleted = 0")
    BigDecimal calculateClassAverageScore(@Param("classId") Long classId, @Param("examId") Long examId, @Param("courseId") Long courseId);

    /**
     * 计算年级某科目平均分
     * 
     * @param gradeId 年级ID
     * @param examId 考试ID
     * @param courseId 课程ID
     * @return 平均分
     */
    @Query("SELECT AVG(s.score) FROM Score s " +
           "JOIN s.student st " +
           "JOIN st.schoolClass sc " +
           "WHERE sc.gradeId = :gradeId AND s.examId = :examId AND s.courseId = :courseId " +
           "AND s.absent = 0 AND s.deleted = 0 AND st.deleted = 0 AND sc.deleted = 0")
    BigDecimal calculateGradeAverageScore(@Param("gradeId") Long gradeId, @Param("examId") Long examId, @Param("courseId") Long courseId);

    /**
     * 统计班级某科目各分数段人数
     * 
     * @param classId 班级ID
     * @param examId 考试ID
     * @param courseId 课程ID
     * @param minScore 最低分数
     * @param maxScore 最高分数
     * @return 人数
     */
    @Query("SELECT COUNT(s) FROM Score s " +
           "JOIN s.student st " +
           "WHERE st.classId = :classId AND s.examId = :examId AND s.courseId = :courseId " +
           "AND s.score >= :minScore AND s.score < :maxScore " +
           "AND s.absent = 0 AND s.deleted = 0 AND st.deleted = 0")
    long countByClassAndScoreRange(@Param("classId") Long classId, @Param("examId") Long examId, 
                                   @Param("courseId") Long courseId, @Param("minScore") BigDecimal minScore, 
                                   @Param("maxScore") BigDecimal maxScore);

    /**
     * 查询学生总分和排名
     * 
     * @param examId 考试ID
     * @param studentId 学生ID
     * @return 学生总分信息
     */
    @Query("SELECT SUM(s.score) as totalScore FROM Score s " +
           "WHERE s.examId = :examId AND s.studentId = :studentId AND s.absent = 0 AND s.deleted = 0")
    BigDecimal calculateStudentTotalScore(@Param("examId") Long examId, @Param("studentId") Long studentId);

    /**
     * 查询班级成绩排名
     * 
     * @param examId 考试ID
     * @param classId 班级ID
     * @return 排名列表
     */
    @Query("SELECT s.studentId, SUM(s.score) as totalScore FROM Score s " +
           "JOIN s.student st " +
           "WHERE s.examId = :examId AND st.classId = :classId AND s.absent = 0 AND s.deleted = 0 AND st.deleted = 0 " +
           "GROUP BY s.studentId ORDER BY totalScore DESC")
    List<Object[]> findClassRanking(@Param("examId") Long examId, @Param("classId") Long classId);

    /**
     * 根据教师ID查询需要录入的成绩
     * 
     * @param teacherId 教师ID
     * @param examId 考试ID
     * @return 成绩列表
     */
    @Query("SELECT s FROM Score s " +
           "JOIN TeacherClassCourse tcc ON s.courseId = tcc.courseId " +
           "JOIN s.student st ON st.classId = tcc.classId " +
           "WHERE tcc.teacherId = :teacherId AND s.examId = :examId " +
           "AND s.deleted = 0 AND tcc.deleted = 0 AND st.deleted = 0")
    List<Score> findByTeacherIdAndExamId(@Param("teacherId") Long teacherId, @Param("examId") Long examId);

    /**
     * 统计已录入成绩数量
     * 
     * @param examId 考试ID
     * @param courseId 课程ID
     * @param classId 班级ID
     * @return 已录入数量
     */
    @Query("SELECT COUNT(s) FROM Score s " +
           "JOIN s.student st " +
           "WHERE s.examId = :examId AND s.courseId = :courseId AND st.classId = :classId " +
           "AND s.score IS NOT NULL AND s.deleted = 0 AND st.deleted = 0")
    long countEnteredScores(@Param("examId") Long examId, @Param("courseId") Long courseId, @Param("classId") Long classId);

    // Service层使用的附加方法
    Optional<Score> findByIdAndDeleted(Long id, Integer deleted);
    boolean existsByExamIdAndStudentIdAndCourseIdAndDeleted(Long examId, Long studentId, Long courseId, Integer deleted);
    List<Score> findByExamIdAndCourseIdAndDeletedOrderByScoreDesc(Long examId, Long courseId, Integer deleted);
    List<Score> findByStudentIdAndExamIdAndDeleted(Long studentId, Long examId, Integer deleted);
    
    /**
     * 根据考试、课程、班级查询成绩列表（通过学生关联）
     * 
     * @param examId 考试ID
     * @param courseId 课程ID
     * @param classId 班级ID
     * @param deleted 删除标记
     * @return 成绩列表
     */
    @Query("SELECT s FROM Score s JOIN s.student st WHERE s.examId = :examId AND s.courseId = :courseId AND st.classId = :classId AND s.deleted = :deleted AND st.deleted = 0 ORDER BY s.score DESC")
    List<Score> findByExamIdAndCourseIdAndClassIdAndDeleted(@Param("examId") Long examId, @Param("courseId") Long courseId, @Param("classId") Long classId, @Param("deleted") Integer deleted);
}