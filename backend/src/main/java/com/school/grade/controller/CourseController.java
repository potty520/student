package com.school.grade.controller;

import com.school.grade.entity.Course;
import com.school.grade.service.CourseService;
import com.school.grade.common.result.Result;
import com.school.grade.common.result.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 课程管理控制器
 * 
 * @author Qoder
 * @version 1.0.0
 */
@Tag(name = "课程管理")
@RestController
@RequestMapping("/api/courses")
@CrossOrigin(origins = "*")
public class CourseController {

    @Autowired
    private CourseService courseService;

    /**
     * 分页查询课程列表
     */
    @Operation(summary = "分页查询课程列表")
    @GetMapping("/list")
    public Result<PageResult<Course>> getCourseList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "课程名称") @RequestParam(required = false) String courseName,
            @Parameter(description = "课程编码") @RequestParam(required = false) String courseCode,
            @Parameter(description = "学段") @RequestParam(required = false) Integer stage,
            @Parameter(description = "课程类型") @RequestParam(required = false) Integer courseType,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        
        return courseService.getCourseList(page, size, courseName, courseCode, stage, courseType, status);
    }

    /**
     * 根据ID获取课程信息
     */
    @Operation(summary = "根据ID获取课程信息")
    @GetMapping("/{id}")
    public Result<Course> getCourseById(@Parameter(description = "课程ID") @PathVariable Long id) {
        return courseService.getCourseById(id);
    }

    /**
     * 添加课程
     */
    @Operation(summary = "添加课程")
    @PostMapping
    public Result<Course> addCourse(@Parameter(description = "课程信息") @Valid @RequestBody Course course) {
        return courseService.addCourse(course);
    }

    /**
     * 更新课程信息
     */
    @Operation(summary = "更新课程信息")
    @PutMapping("/{id}")
    public Result<Course> updateCourse(
            @Parameter(description = "课程ID") @PathVariable Long id,
            @Parameter(description = "课程信息") @Valid @RequestBody Course course) {
        course.setId(id);
        return courseService.updateCourse(course);
    }

    /**
     * 删除课程
     */
    @Operation(summary = "删除课程")
    @DeleteMapping("/{id}")
    public Result<Void> deleteCourse(@Parameter(description = "课程ID") @PathVariable Long id) {
        return courseService.deleteCourse(id);
    }

    /**
     * 批量删除课程
     */
    @Operation(summary = "批量删除课程")
    @DeleteMapping("/batch")
    public Result<Void> batchDeleteCourses(@Parameter(description = "课程ID列表") @RequestBody List<Long> ids) {
        return courseService.batchDeleteCourses(ids);
    }

    /**
     * 更新课程状态
     */
    @Operation(summary = "更新课程状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateCourseStatus(
            @Parameter(description = "课程ID") @PathVariable Long id,
            @Parameter(description = "状态") @RequestParam Integer status) {
        return courseService.updateCourseStatus(id, status);
    }

    /**
     * 根据学段获取课程列表
     */
    @Operation(summary = "根据学段获取课程列表")
    @GetMapping("/stage/{stage}")
    public Result<List<Course>> getCoursesByStage(@Parameter(description = "学段") @PathVariable Integer stage) {
        return courseService.getCoursesByStage(stage);
    }

    /**
     * 获取所有启用的课程（用于下拉选择）
     */
    @Operation(summary = "获取所有启用的课程")
    @GetMapping("/active")
    public Result<List<Course>> getActiveCourses() {
        return courseService.getActiveCourses();
    }

    /**
     * 根据课程类型获取课程列表
     */
    @Operation(summary = "根据课程类型获取课程列表")
    @GetMapping("/type/{courseType}")
    public Result<List<Course>> getCoursesByType(@Parameter(description = "课程类型") @PathVariable Integer courseType) {
        return courseService.getCoursesByType(courseType);
    }

    /**
     * 获取主科课程列表
     */
    @Operation(summary = "获取主科课程列表")
    @GetMapping("/main-courses")
    public Result<List<Course>> getMainCourses() {
        return courseService.getCoursesByType(1);
    }

    /**
     * 获取副科课程列表
     */
    @Operation(summary = "获取副科课程列表")
    @GetMapping("/minor-courses")
    public Result<List<Course>> getMinorCourses() {
        return courseService.getCoursesByType(2);
    }

    /**
     * 更新课程排序
     */
    @Operation(summary = "更新课程排序")
    @PutMapping("/{id}/sort")
    public Result<Void> updateCourseSortOrder(
            @Parameter(description = "课程ID") @PathVariable Long id,
            @Parameter(description = "排序") @RequestParam Integer sortOrder) {
        return courseService.updateCourseSortOrder(id, sortOrder);
    }

    /**
     * 根据课程编码获取课程信息
     */
    @Operation(summary = "根据课程编码获取课程信息")
    @GetMapping("/code/{courseCode}")
    public Result<PageResult<Course>> getCourseByCode(@Parameter(description = "课程编码") @PathVariable String courseCode) {
        return courseService.getCourseList(1, 1, null, courseCode, null, null, null);
    }
}