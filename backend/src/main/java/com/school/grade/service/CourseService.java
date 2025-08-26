package com.school.grade.service;

import com.school.grade.entity.Course;
import com.school.grade.repository.CourseRepository;
import com.school.grade.common.result.Result;
import com.school.grade.common.result.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 课程管理业务逻辑层
 * 
 * @author Qoder
 * @version 1.0.0
 */
@Service
@Transactional
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    /**
     * 分页查询课程信息
     * 
     * @param page 页码
     * @param size 每页大小
     * @param courseName 课程名称（模糊查询）
     * @param courseCode 课程编码（精确查询）
     * @param stage 学段
     * @param courseType 课程类型
     * @param status 状态
     * @return 分页结果
     */
    public Result<PageResult<Course>> getCourseList(int page, int size, String courseName, 
                                                   String courseCode, Integer stage, Integer courseType, Integer status) {
        try {
            // 创建分页对象
            Pageable pageable = PageRequest.of(page - 1, size, 
                Sort.by(Sort.Direction.ASC, "sortOrder", "createTime"));

            // 构建查询条件
            Specification<Course> spec = (root, query, criteriaBuilder) -> {
                List<Predicate> predicates = new ArrayList<>();
                
                // 软删除条件
                predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
                
                // 课程名称模糊查询
                if (StringUtils.hasText(courseName)) {
                    predicates.add(criteriaBuilder.like(root.get("courseName"), "%" + courseName + "%"));
                }
                
                // 课程编码精确查询
                if (StringUtils.hasText(courseCode)) {
                    predicates.add(criteriaBuilder.equal(root.get("courseCode"), courseCode));
                }
                
                // 学段查询
                if (stage != null) {
                    predicates.add(criteriaBuilder.equal(root.get("stage"), stage));
                }
                
                // 课程类型查询
                if (courseType != null) {
                    predicates.add(criteriaBuilder.equal(root.get("courseType"), courseType));
                }
                
                // 状态查询
                if (status != null) {
                    predicates.add(criteriaBuilder.equal(root.get("status"), status));
                }
                
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            };

            Page<Course> coursePage = courseRepository.findAll(spec, pageable);
            
            PageResult<Course> pageResult = new PageResult<>(
                coursePage.getContent(),
                coursePage.getTotalElements(),
                page,
                size
            );

            return Result.success(pageResult);
        } catch (Exception e) {
            return Result.error("查询课程列表失败：" + e.getMessage());
        }
    }

    /**
     * 根据ID获取课程信息
     * 
     * @param id 课程ID
     * @return 课程信息
     */
    public Result<Course> getCourseById(Long id) {
        try {
            Optional<Course> course = courseRepository.findByIdAndDeleted(id, 0);
            if (course.isPresent()) {
                return Result.success(course.get());
            } else {
                return Result.error("课程信息不存在");
            }
        } catch (Exception e) {
            return Result.error("获取课程信息失败：" + e.getMessage());
        }
    }

    /**
     * 添加课程
     * 
     * @param course 课程信息
     * @return 操作结果
     */
    public Result<Course> addCourse(Course course) {
        try {
            // 数据验证
            Result<Void> validateResult = validateCourse(course);
            if (!validateResult.isSuccess()) {
                return Result.error(validateResult.getMessage());
            }

            // 检查课程编码是否已存在
            if (courseRepository.existsByCourseCodeAndDeleted(course.getCourseCode(), 0)) {
                return Result.error("课程编码已存在");
            }

            // 设置默认值
            if (course.getStatus() == null) {
                course.setStatus(1); // 默认启用状态
            }
            if (course.getSortOrder() == null) {
                course.setSortOrder(0);
            }

            Course savedCourse = courseRepository.save(course);
            return Result.success(savedCourse);
        } catch (Exception e) {
            return Result.error("添加课程失败：" + e.getMessage());
        }
    }

    /**
     * 更新课程信息
     * 
     * @param course 课程信息
     * @return 操作结果
     */
    public Result<Course> updateCourse(Course course) {
        try {
            // 检查课程是否存在
            Optional<Course> existingCourse = courseRepository.findByIdAndDeleted(course.getId(), 0);
            if (!existingCourse.isPresent()) {
                return Result.error("课程信息不存在");
            }

            // 数据验证
            Result<Void> validateResult = validateCourse(course);
            if (!validateResult.isSuccess()) {
                return Result.error(validateResult.getMessage());
            }

            // 检查课程编码是否被其他课程占用
            Optional<Course> duplicateCourse = courseRepository.findByCourseCodeAndDeleted(course.getCourseCode(), 0);
            if (duplicateCourse.isPresent() && !duplicateCourse.get().getId().equals(course.getId())) {
                return Result.error("课程编码已被其他课程使用");
            }

            Course savedCourse = courseRepository.save(course);
            return Result.success(savedCourse);
        } catch (Exception e) {
            return Result.error("更新课程信息失败：" + e.getMessage());
        }
    }

    /**
     * 删除课程（软删除）
     * 
     * @param id 课程ID
     * @return 操作结果
     */
    public Result<Void> deleteCourse(Long id) {
        try {
            Optional<Course> course = courseRepository.findByIdAndDeleted(id, 0);
            if (!course.isPresent()) {
                return Result.error("课程信息不存在");
            }

            Course courseEntity = course.get();
            courseEntity.setDeleted(1); // 1表示已删除
            courseRepository.save(courseEntity);

            return Result.success();
        } catch (Exception e) {
            return Result.error("删除课程失败：" + e.getMessage());
        }
    }

    /**
     * 批量删除课程
     * 
     * @param ids 课程ID列表
     * @return 操作结果
     */
    public Result<Void> batchDeleteCourses(List<Long> ids) {
        try {
            List<Course> courses = courseRepository.findByIdInAndDeleted(ids, 0);
            if (courses.isEmpty()) {
                return Result.error("未找到要删除的课程");
            }

            courses.forEach(course -> course.setDeleted(1)); // 1表示已删除
            courseRepository.saveAll(courses);

            return Result.success();
        } catch (Exception e) {
            return Result.error("批量删除课程失败：" + e.getMessage());
        }
    }

    /**
     * 更新课程状态
     * 
     * @param id 课程ID
     * @param status 新状态
     * @return 操作结果
     */
    public Result<Void> updateCourseStatus(Long id, Integer status) {
        try {
            Optional<Course> course = courseRepository.findByIdAndDeleted(id, 0);
            if (!course.isPresent()) {
                return Result.error("课程信息不存在");
            }

            // 验证状态值
            if (status < 0 || status > 1) {
                return Result.error("无效的状态值");
            }

            Course courseEntity = course.get();
            courseEntity.setStatus(status);
            courseRepository.save(courseEntity);

            return Result.success();
        } catch (Exception e) {
            return Result.error("更新课程状态失败：" + e.getMessage());
        }
    }

    /**
     * 根据学段获取课程列表
     * 
     * @param stage 学段
     * @return 课程列表
     */
    public Result<List<Course>> getCoursesByStage(Integer stage) {
        try {
            List<Course> courses = courseRepository.findByStageAndStatusAndDeletedOrderBySortOrderAsc(stage, 1, 0);
            return Result.success(courses);
        } catch (Exception e) {
            return Result.error("根据学段获取课程列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取所有启用的课程（用于下拉选择）
     * 
     * @return 课程列表
     */
    public Result<List<Course>> getActiveCourses() {
        try {
            List<Course> courses = courseRepository.findByStatusAndDeletedOrderBySortOrderAsc(1, 0);
            return Result.success(courses);
        } catch (Exception e) {
            return Result.error("获取启用课程列表失败：" + e.getMessage());
        }
    }

    /**
     * 根据课程类型获取课程列表
     * 
     * @param courseType 课程类型
     * @return 课程列表
     */
    public Result<List<Course>> getCoursesByType(Integer courseType) {
        try {
            List<Course> courses = courseRepository.findByCourseTypeAndStatusAndDeletedOrderBySortOrderAsc(
                courseType, 1, 0);
            return Result.success(courses);
        } catch (Exception e) {
            return Result.error("根据课程类型获取课程列表失败：" + e.getMessage());
        }
    }

    /**
     * 更新课程排序
     * 
     * @param id 课程ID
     * @param sortOrder 排序号
     * @return 操作结果
     */
    public Result<Void> updateCourseSortOrder(Long id, Integer sortOrder) {
        try {
            Optional<Course> course = courseRepository.findByIdAndDeleted(id, 0);
            if (!course.isPresent()) {
                return Result.error("课程信息不存在");
            }

            Course courseEntity = course.get();
            courseEntity.setSortOrder(sortOrder);
            courseRepository.save(courseEntity);

            return Result.success();
        } catch (Exception e) {
            return Result.error("更新课程排序失败：" + e.getMessage());
        }
    }

    /**
     * 数据验证
     * 
     * @param course 课程信息
     * @return 验证结果
     */
    private Result<Void> validateCourse(Course course) {
        if (course == null) {
            return Result.error("课程信息不能为空");
        }

        if (!StringUtils.hasText(course.getCourseCode())) {
            return Result.error("课程编码不能为空");
        }

        if (course.getCourseCode().length() > 20) {
            return Result.error("课程编码长度不能超过20个字符");
        }

        if (!StringUtils.hasText(course.getCourseName())) {
            return Result.error("课程名称不能为空");
        }

        if (course.getCourseName().length() > 50) {
            return Result.error("课程名称长度不能超过50个字符");
        }

        if (course.getStage() == null) {
            return Result.error("学段不能为空");
        }

        if (course.getStage() < 1 || course.getStage() > 3) {
            return Result.error("学段值必须在1-3之间");
        }

        if (course.getCourseType() == null) {
            return Result.error("课程类型不能为空");
        }

        if (course.getCourseType() < 1 || course.getCourseType() > 2) {
            return Result.error("课程类型值必须在1-2之间");
        }

        if (course.getFullScore() == null) {
            return Result.error("满分分值不能为空");
        }

        if (course.getFullScore().compareTo(BigDecimal.ZERO) <= 0) {
            return Result.error("满分分值必须大于0");
        }

        if (course.getScoreType() == null) {
            return Result.error("计分方式不能为空");
        }

        if (course.getScoreType() < 1 || course.getScoreType() > 3) {
            return Result.error("计分方式值必须在1-3之间");
        }

        // 验证各分数线设置
        if (course.getPassScore() != null && 
            course.getPassScore().compareTo(course.getFullScore()) > 0) {
            return Result.error("及格分数不能大于满分");
        }

        if (course.getGoodScore() != null && 
            course.getGoodScore().compareTo(course.getFullScore()) > 0) {
            return Result.error("良好分数不能大于满分");
        }

        if (course.getExcellentScore() != null && 
            course.getExcellentScore().compareTo(course.getFullScore()) > 0) {
            return Result.error("优秀分数不能大于满分");
        }

        return Result.success();
    }
}