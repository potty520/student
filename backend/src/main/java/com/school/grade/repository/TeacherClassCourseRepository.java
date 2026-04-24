package com.school.grade.repository;

import com.school.grade.entity.Course;
import com.school.grade.entity.SchoolClass;
import com.school.grade.entity.Teacher;
import com.school.grade.entity.TeacherClassCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 教师班级课程关联Repository
 * 
 * @author Qoder
 * @version 1.0.0
 */
@Repository
public interface TeacherClassCourseRepository extends JpaRepository<TeacherClassCourse, Long>, JpaSpecificationExecutor<TeacherClassCourse> {

    /**
     * 根据教师ID查询任教信息
     * 
     * @param teacherId 教师ID
     * @param deleted 删除标记
     * @return 任教信息列表
     */
    List<TeacherClassCourse> findByTeacherIdAndDeleted(Long teacherId, Integer deleted);

    /**
     * 根据班级ID查询任教信息
     * 
     * @param classId 班级ID
     * @param deleted 删除标记
     * @return 任教信息列表
     */
    List<TeacherClassCourse> findByClassIdAndDeleted(Long classId, Integer deleted);

    /**
     * 根据课程ID查询任教信息
     * 
     * @param courseId 课程ID
     * @param deleted 删除标记
     * @return 任教信息列表
     */
    List<TeacherClassCourse> findByCourseIdAndDeleted(Long courseId, Integer deleted);

    /**
     * 根据教师ID、班级ID和课程ID查询任教信息
     * 
     * @param teacherId 教师ID
     * @param classId 班级ID
     * @param courseId 课程ID
     * @param deleted 删除标记
     * @return 任教信息
     */
    Optional<TeacherClassCourse> findByTeacherIdAndClassIdAndCourseIdAndDeleted(Long teacherId, Long classId, Long courseId, Integer deleted);

    /**
     * 根据教师ID和班级ID查询任教信息
     * 
     * @param teacherId 教师ID
     * @param classId 班级ID
     * @param deleted 删除标记
     * @return 任教信息列表
     */
    List<TeacherClassCourse> findByTeacherIdAndClassIdAndDeleted(Long teacherId, Long classId, Integer deleted);

    /**
     * 根据教师ID和课程ID查询任教信息
     * 
     * @param teacherId 教师ID
     * @param courseId 课程ID
     * @param deleted 删除标记
     * @return 任教信息列表
     */
    List<TeacherClassCourse> findByTeacherIdAndCourseIdAndDeleted(Long teacherId, Long courseId, Integer deleted);

    /**
     * 查询指定班级的任课教师列表
     * 
     * @param classId 班级ID
     * @return 教师列表
     */
    @Query("SELECT DISTINCT t FROM Teacher t " +
           "JOIN TeacherClassCourse tcc ON t.id = tcc.teacherId " +
           "WHERE tcc.classId = :classId AND t.deleted = 0 AND tcc.deleted = 0")
    List<Teacher> findTeachersByClassId(@Param("classId") Long classId);

    /**
     * 查询指定课程的任课教师列表
     * 
     * @param courseId 课程ID
     * @return 教师列表
     */
    @Query("SELECT DISTINCT t FROM Teacher t " +
           "JOIN TeacherClassCourse tcc ON t.id = tcc.teacherId " +
           "WHERE tcc.courseId = :courseId AND t.deleted = 0 AND tcc.deleted = 0")
    List<Teacher> findTeachersByCourseId(@Param("courseId") Long courseId);

    /**
     * 查询指定教师任教的班级列表
     * 
     * @param teacherId 教师ID
     * @return 班级列表
     */
    @Query("SELECT DISTINCT c FROM SchoolClass c " +
           "JOIN TeacherClassCourse tcc ON c.id = tcc.classId " +
           "WHERE tcc.teacherId = :teacherId AND c.deleted = 0 AND tcc.deleted = 0")
    List<SchoolClass> findClassesByTeacherId(@Param("teacherId") Long teacherId);

    /**
     * 查询指定教师任教的课程列表
     * 
     * @param teacherId 教师ID
     * @return 课程列表
     */
    @Query("SELECT DISTINCT c FROM Course c " +
           "JOIN TeacherClassCourse tcc ON c.id = tcc.courseId " +
           "WHERE tcc.teacherId = :teacherId AND c.deleted = 0 AND tcc.deleted = 0")
    List<Course> findCoursesByTeacherId(@Param("teacherId") Long teacherId);

    // Service层使用的附加方法
    Optional<TeacherClassCourse> findByIdAndDeleted(Long id, Integer deleted);
    boolean existsByTeacherIdAndClassIdAndCourseIdAndDeleted(Long teacherId, Long classId, Long courseId, Integer deleted);
    List<TeacherClassCourse> findByIdInAndDeleted(List<Long> ids, Integer deleted);
    boolean existsByIdAndDeleted(Long id, Integer deleted);
}