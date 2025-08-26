package com.school.grade.repository;

import com.school.grade.entity.TeacherClassCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 教师任课关系Repository
 * 
 * @author Qoder
 * @version 1.0.0
 */
@Repository
public interface TeacherClassCourseRepository extends JpaRepository<TeacherClassCourse, Long>, JpaSpecificationExecutor<TeacherClassCourse> {

    /**
     * 根据教师、班级、课程查找任课关系
     * 
     * @param teacherId 教师ID
     * @param classId 班级ID
     * @param courseId 课程ID
     * @param deleted 删除标记
     * @return 任课关系
     */
    Optional<TeacherClassCourse> findByTeacherIdAndClassIdAndCourseIdAndDeleted(Long teacherId, Long classId, Long courseId, Integer deleted);

    /**
     * 根据教师ID查询任课关系
     * 
     * @param teacherId 教师ID
     * @param deleted 删除标记
     * @return 任课关系列表
     */
    @Query("SELECT tcc FROM TeacherClassCourse tcc " +
           "LEFT JOIN FETCH tcc.schoolClass sc " +
           "LEFT JOIN FETCH tcc.course c " +
           "WHERE tcc.teacherId = :teacherId AND tcc.deleted = :deleted")
    List<TeacherClassCourse> findByTeacherIdWithClassAndCourse(@Param("teacherId") Long teacherId, @Param("deleted") Integer deleted);

    /**
     * 根据班级ID查询任课关系
     * 
     * @param classId 班级ID
     * @param deleted 删除标记
     * @return 任课关系列表
     */
    @Query("SELECT tcc FROM TeacherClassCourse tcc " +
           "LEFT JOIN FETCH tcc.teacher t " +
           "LEFT JOIN FETCH tcc.course c " +
           "WHERE tcc.classId = :classId AND tcc.deleted = :deleted")
    List<TeacherClassCourse> findByClassIdWithTeacherAndCourse(@Param("classId") Long classId, @Param("deleted") Integer deleted);

    /**
     * 根据课程ID查询任课关系
     * 
     * @param courseId 课程ID
     * @param deleted 删除标记
     * @return 任课关系列表
     */
    @Query("SELECT tcc FROM TeacherClassCourse tcc " +
           "LEFT JOIN FETCH tcc.teacher t " +
           "LEFT JOIN FETCH tcc.schoolClass sc " +
           "WHERE tcc.courseId = :courseId AND tcc.deleted = :deleted")
    List<TeacherClassCourse> findByCourseIdWithTeacherAndClass(@Param("courseId") Long courseId, @Param("deleted") Integer deleted);

    /**
     * 根据学年和学期查询任课关系
     * 
     * @param schoolYear 学年
     * @param semester 学期
     * @param deleted 删除标记
     * @return 任课关系列表
     */
    List<TeacherClassCourse> findBySchoolYearAndSemesterAndDeleted(String schoolYear, Integer semester, Integer deleted);

    /**
     * 根据教师和学年学期查询任课关系
     * 
     * @param teacherId 教师ID
     * @param schoolYear 学年
     * @param semester 学期
     * @param deleted 删除标记
     * @return 任课关系列表
     */
    List<TeacherClassCourse> findByTeacherIdAndSchoolYearAndSemesterAndDeleted(Long teacherId, String schoolYear, Integer semester, Integer deleted);

    /**
     * 根据班级和学年学期查询任课关系
     * 
     * @param classId 班级ID
     * @param schoolYear 学年
     * @param semester 学期
     * @param deleted 删除标记
     * @return 任课关系列表
     */
    List<TeacherClassCourse> findByClassIdAndSchoolYearAndSemesterAndDeleted(Long classId, String schoolYear, Integer semester, Integer deleted);

    /**
     * 检查教师是否任教指定班级的指定课程
     * 
     * @param teacherId 教师ID
     * @param classId 班级ID
     * @param courseId 课程ID
     * @param status 状态
     * @param deleted 删除标记
     * @return 是否存在
     */
    boolean existsByTeacherIdAndClassIdAndCourseIdAndStatusAndDeleted(Long teacherId, Long classId, Long courseId, Integer status, Integer deleted);

    /**
     * 根据状态查询任课关系
     * 
     * @param status 状态
     * @param deleted 删除标记
     * @return 任课关系列表
     */
    List<TeacherClassCourse> findByStatusAndDeleted(Integer status, Integer deleted);
}