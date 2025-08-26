package com.school.grade.controller;

import com.school.grade.common.result.Result;
import com.school.grade.common.PageQuery;
import com.school.grade.entity.Grade;
import com.school.grade.service.GradeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 年级管理控制�?
 * 提供年级相关的REST API接口
 * 
 * @author Qoder
 * @since 2025-08-25
 */
@Tag(name = "年级管理", description = "年级管理相关接口")
@RestController
@RequestMapping("/api/basic/grade")
@CrossOrigin(origins = "*", maxAge = 3600)
public class GradeController {

    @Autowired
    private GradeService gradeService;

    /**
     * 分页查询年级列表
     */
    @Operation(summary = "分页查询年级列表", description = "支持按年级名称和学年进行搜索")
    @GetMapping("/list")
    public Result<Page<Grade>> getGradeList(
            @Parameter(description = "分页参数") @Valid PageQuery pageQuery,
            @Parameter(description = "年级名称") @RequestParam(required = false) String gradeName,
            @Parameter(description = "学年") @RequestParam(required = false) String schoolYear) {
        return gradeService.getGradeList(pageQuery, gradeName, schoolYear);
    }

    /**
     * 获取所有启用的年级列表
     */
    @Operation(summary = "获取所有启用的年级列表")
    @GetMapping("/all")
    public Result<List<Grade>> getAllActiveGrades() {
        return gradeService.getAllActiveGrades();
    }

    /**
     * 根据ID获取年级详情
     */
    @Operation(summary = "根据ID获取年级详情")
    @GetMapping("/{id}")
    public Result<Grade> getGradeById(
            @Parameter(description = "年级ID") @PathVariable Long id) {
        return gradeService.getGradeById(id);
    }

    /**
     * 新增年级
     */
    @Operation(summary = "新增年级")
    @PostMapping
    public Result<Grade> createGrade(
            @Parameter(description = "年级信息") @Valid @RequestBody Grade grade) {
        return gradeService.createGrade(grade);
    }

    /**
     * 更新年级
     */
    @Operation(summary = "更新年级")
    @PutMapping("/{id}")
    public Result<Grade> updateGrade(
            @Parameter(description = "年级ID") @PathVariable Long id,
            @Parameter(description = "年级信息") @Valid @RequestBody Grade grade) {
        return gradeService.updateGrade(id, grade);
    }

    /**
     * 删除年级
     */
    @Operation(summary = "删除年级")
    @DeleteMapping("/{id}")
    public Result<Void> deleteGrade(
            @Parameter(description = "年级ID") @PathVariable Long id) {
        return gradeService.deleteGrade(id);
    }

    /**
     * 批量删除年级
     */
    @Operation(summary = "批量删除年级")
    @DeleteMapping("/batch")
    public Result<Void> batchDeleteGrades(
            @Parameter(description = "年级ID列表") @RequestBody List<Long> ids) {
        return gradeService.batchDeleteGrades(ids);
    }

    /**
     * 更新年级状态
     */
    @Operation(summary = "更新年级状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateGradeStatus(
            @Parameter(description = "年级ID") @PathVariable Long id,
            @Parameter(description = "状态") @RequestParam Integer status) {
        return gradeService.updateGradeStatus(id, status);
    }

    /**
     * 根据学年获取年级列表
     */
    @Operation(summary = "根据学年获取年级列表")
    @GetMapping("/school-year/{schoolYear}")
    public Result<List<Grade>> getGradesBySchoolYear(
            @Parameter(description = "学年") @PathVariable String schoolYear) {
        return gradeService.getGradesBySchoolYear(schoolYear);
    }
}
