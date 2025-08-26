package com.school.grade.repository;

import com.school.grade.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 课程Repository
 * 
 * @author Qoder
 * @version 1.0.0
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {

    /**
     * 根据课程编码查找课程
     * 
     * @param courseCode 课程编码
     * @param deleted 删除标记
     * @return 课程信息
     */
    Optional<Course> findByCourseCodeAndDeleted(String courseCode, Integer deleted);

    /**
     * 根据课程编码查找课程（排除指定课程）
     * 
     * @param courseCode 课程编码
     * @param id 排除的课程ID
     * @param deleted 删除标记
     * @return 是否存在
     */
    boolean existsByCourseCodeAndIdNotAndDeleted(String courseCode, Long id, Integer deleted);

    /**
     * 根据学段查询课程列表
     * 
     * @param stage 学段
     * @param deleted 删除标记
     * @return 课程列表
     */
    List<Course> findByStageAndDeletedOrderBySortOrder(Integer stage, Integer deleted);

    /**
     * 根据学段和课程类型查询课程列表
     * 
     * @param stage 学段
     * @param courseType 课程类型
     * @param deleted 删除标记
     * @return 课程列表
     */
    List<Course> findByStageAndCourseTypeAndDeletedOrderBySortOrder(Integer stage, Integer courseType, Integer deleted);

    /**
     * 查询所有启用的课程
     * 
     * @param status 状态
     * @param deleted 删除标记
     * @return 课程列表
     */
    List<Course> findByStatusAndDeletedOrderBySortOrder(Integer status, Integer deleted);

    /**
     * 根据学段和状态查询课程
     * 
     * @param stage 学段
     * @param status 状态
     * @param deleted 删除标记
     * @return 课程列表
     */
    List<Course> findByStageAndStatusAndDeletedOrderBySortOrder(Integer stage, Integer status, Integer deleted);

    // Service层使用的附加方法
    Optional<Course> findByIdAndDeleted(Long id, Integer deleted);
    boolean existsByCourseCodeAndDeleted(String courseCode, Integer deleted);
    List<Course> findByIdInAndDeleted(List<Long> ids, Integer deleted);
    List<Course> findByStageAndStatusAndDeletedOrderBySortOrderAsc(Integer stage, Integer status, Integer deleted);
    List<Course> findByStatusAndDeletedOrderBySortOrderAsc(Integer status, Integer deleted);
    List<Course> findByCourseTypeAndStatusAndDeletedOrderBySortOrderAsc(Integer courseType, Integer status, Integer deleted);
    boolean existsByIdAndDeleted(Long id, Integer deleted);
}