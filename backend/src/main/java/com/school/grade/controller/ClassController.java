package com.school.grade.controller;

import com.school.grade.common.result.Result;
import com.school.grade.common.PageQuery;
import com.school.grade.entity.SchoolClass;
import com.school.grade.service.ClassService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 班级管理控制器
 * 提供班级相关的REST API接口
 * 
 * @author Qoder
 * @since 2025-08-25
 */
@Tag(name = "班级管理", description = "班级管理相关接口")
@RestController
@RequestMapping("/api/basic/class")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ClassController {

    @Autowired
    private ClassService classService;

    /**
     * 分页查询班级列表
     */
    @Operation(summary = "分页查询班级列表", description = "支持按班级名称和年级进行搜索")
    @GetMapping("/list")
    public Result<Page<SchoolClass>> getClassList(
            @Parameter(description = "分页参数") @Valid PageQuery pageQuery,
            @Parameter(description = "班级名称") @RequestParam(required = false) String className,
            @Parameter(description = "年级ID") @RequestParam(required = false) Long gradeId) {
        return classService.getClassList(pageQuery, className, gradeId);
    }

    /**
     * 获取所有启用的班级列表
     */
    @Operation(summary = "获取所有启用的班级列表")
    @GetMapping("/all")
    public Result<List<SchoolClass>> getAllActiveClasses() {
        return classService.getAllActiveClasses();
    }

    /**
     * 根据年级ID获取班级列表
     */
    @Operation(summary = "根据年级ID获取班级列表")
    @GetMapping("/grade/{gradeId}")
    public Result<List<SchoolClass>> getClassesByGradeId(
            @Parameter(description = "年级ID") @PathVariable Long gradeId) {
        return classService.getClassesByGradeId(gradeId);
    }

    /**
     * 根据ID获取班级详情
     */
    @Operation(summary = "根据ID获取班级详情")
    @GetMapping("/{id}")
    public Result<SchoolClass> getClassById(
            @Parameter(description = "班级ID") @PathVariable Long id) {
        return classService.getClassById(id);
    }

    /**
     * 新增班级
     */
    @Operation(summary = "新增班级")
    @PostMapping
    public Result<SchoolClass> createClass(
            @Parameter(description = "班级信息") @Valid @RequestBody SchoolClass schoolClass) {
        return classService.createClass(schoolClass);
    }

    /**
     * 更新班级
     */
    @Operation(summary = "更新班级")
    @PutMapping("/{id}")
    public Result<SchoolClass> updateClass(
            @Parameter(description = "班级ID") @PathVariable Long id,
            @Parameter(description = "班级信息") @Valid @RequestBody SchoolClass schoolClass) {
        return classService.updateClass(id, schoolClass);
    }

    /**
     * 删除班级
     */
    @Operation(summary = "删除班级")
    @DeleteMapping("/{id}")
    public Result<Void> deleteClass(
            @Parameter(description = "班级ID") @PathVariable Long id) {
        return classService.deleteClass(id);
    }

    /**
     * 批量删除班级
     */
    @Operation(summary = "批量删除班级")
    @DeleteMapping("/batch")
    public Result<Void> batchDeleteClasses(
            @Parameter(description = "班级ID列表") @RequestBody List<Long> ids) {
        return classService.batchDeleteClasses(ids);
    }

    /**
     * 更新班级状态
     */
    @Operation(summary = "更新班级状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateClassStatus(
            @Parameter(description = "班级ID") @PathVariable Long id,
            @Parameter(description = "状态") @RequestParam Integer status) {
        return classService.updateClassStatus(id, status);
    }

    /**
     * 更新班级学生数量
     */
    @Operation(summary = "更新班级学生数量")
    @PutMapping("/{id}/student-count")
    public Result<Void> updateStudentCount(
            @Parameter(description = "班级ID") @PathVariable Long id) {
        return classService.updateStudentCount(id);
    }
}
