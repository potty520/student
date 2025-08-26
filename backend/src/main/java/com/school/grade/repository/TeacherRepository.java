package com.school.grade.repository;

import com.school.grade.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 教师Repository
 * 
 * @author Qoder
 * @version 1.0.0
 */
@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long>, JpaSpecificationExecutor<Teacher> {

    /**
     * 根据教师工号查找教师
     * 
     * @param teacherCode 教师工号
     * @param deleted 删除标记
     * @return 教师信息
     */
    Optional<Teacher> findByTeacherCodeAndDeleted(String teacherCode, Integer deleted);

    /**
     * 根据教师工号查找教师（排除指定教师）
     * 
     * @param teacherCode 教师工号
     * @param id 排除的教师ID
     * @param deleted 删除标记
     * @return 是否存在
     */
    boolean existsByTeacherCodeAndIdNotAndDeleted(String teacherCode, Long id, Integer deleted);

    /**
     * 根据用户ID查找教师
     * 
     * @param userId 用户ID
     * @param deleted 删除标记
     * @return 教师信息
     */
    Optional<Teacher> findByUserIdAndDeleted(Long userId, Integer deleted);

    /**
     * 根据身份证号查找教师
     * 
     * @param idCard 身份证号
     * @param deleted 删除标记
     * @return 教师信息
     */
    Optional<Teacher> findByIdCardAndDeleted(String idCard, Integer deleted);

    /**
     * 查询所有在职教师
     * 
     * @param status 状态
     * @param deleted 删除标记
     * @return 教师列表
     */
    List<Teacher> findByStatusAndDeleted(Integer status, Integer deleted);

    /**
     * 查询教师及用户信息
     * 
     * @param teacherId 教师ID
     * @return 教师信息
     */
    @Query("SELECT t FROM Teacher t LEFT JOIN FETCH t.user WHERE t.id = :teacherId AND t.deleted = 0")
    Optional<Teacher> findByIdWithUser(@Param("teacherId") Long teacherId);

    /**
     * 根据班级ID查询班主任
     * 
     * @param classId 班级ID
     * @return 教师信息
     */
    @Query("SELECT t FROM Teacher t " +
           "JOIN SchoolClass c ON t.id = c.headTeacherId " +
           "WHERE c.id = :classId AND t.deleted = 0 AND c.deleted = 0")
    Optional<Teacher> findHeadTeacherByClassId(@Param("classId") Long classId);

    /**
     * 根据课程ID和班级ID查询任课教师
     * 
     * @param courseId 课程ID
     * @param classId 班级ID
     * @return 教师列表
     */
    @Query("SELECT t FROM Teacher t " +
           "JOIN TeacherClassCourse tcc ON t.id = tcc.teacherId " +
           "WHERE tcc.courseId = :courseId AND tcc.classId = :classId " +
           "AND t.deleted = 0 AND tcc.deleted = 0")
    List<Teacher> findByCourseIdAndClassId(@Param("courseId") Long courseId, @Param("classId") Long classId);

    // Service层使用的附加方法
    Optional<Teacher> findByIdAndDeleted(Long id, Integer deleted);
    boolean existsByTeacherCodeAndDeleted(String teacherCode, Integer deleted);
    boolean existsByUserIdAndDeleted(Long userId, Integer deleted);
    List<Teacher> findByIdInAndDeleted(List<Long> ids, Integer deleted);
    List<Teacher> findByStatusAndDeletedOrderByTeacherNameAsc(Integer status, Integer deleted);
    List<Teacher> findByPositionContainingAndStatusAndDeletedOrderByTeacherNameAsc(String position, Integer status, Integer deleted);
}