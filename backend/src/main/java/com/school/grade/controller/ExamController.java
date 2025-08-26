package com.school.grade.controller;

import com.school.grade.entity.Exam;
import com.school.grade.service.ExamService;
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
 * 考试管理控制器
 * 
 * @author Qoder
 * @version 1.0.0
 */
@Tag(name = "考试管理")
@RestController
@RequestMapping("/api/exams")
@CrossOrigin(origins = "*")
public class ExamController {

    @Autowired
    private ExamService examService;

    /**
     * 分页查询考试列表
     */
    @Operation(summary = "分页查询考试列表")
    @GetMapping("/list")
    public Result<PageResult<Exam>> getExamList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "考试名称") @RequestParam(required = false) String examName,
            @Parameter(description = "考试类型") @RequestParam(required = false) Integer examType,
            @Parameter(description = "学年") @RequestParam(required = false) String schoolYear,
            @Parameter(description = "学期") @RequestParam(required = false) Integer semester,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        
        return examService.getExamList(page, size, examName, examType, schoolYear, semester, status);
    }

    /**
     * 根据ID获取考试信息
     */
    @Operation(summary = "根据ID获取考试信息")
    @GetMapping("/{id}")
    public Result<Exam> getExamById(@Parameter(description = "考试ID") @PathVariable Long id) {
        return examService.getExamById(id);
    }

    /**
     * 添加考试
     */
    @Operation(summary = "添加考试")
    @PostMapping
    public Result<Exam> addExam(@Parameter(description = "考试信息") @Valid @RequestBody Exam exam) {
        return examService.addExam(exam);
    }

    /**
     * 更新考试信息
     */
    @Operation(summary = "更新考试信息")
    @PutMapping("/{id}")
    public Result<Exam> updateExam(
            @Parameter(description = "考试ID") @PathVariable Long id,
            @Parameter(description = "考试信息") @Valid @RequestBody Exam exam) {
        exam.setId(id);
        return examService.updateExam(exam);
    }

    /**
     * 删除考试
     */
    @Operation(summary = "删除考试")
    @DeleteMapping("/{id}")
    public Result<Void> deleteExam(@Parameter(description = "考试ID") @PathVariable Long id) {
        return examService.deleteExam(id);
    }

    /**
     * 批量删除考试
     */
    @Operation(summary = "批量删除考试")
    @DeleteMapping("/batch")
    public Result<Void> batchDeleteExams(@Parameter(description = "考试ID列表") @RequestBody List<Long> ids) {
        return examService.batchDeleteExams(ids);
    }

    /**
     * 更新考试状态
     */
    @Operation(summary = "更新考试状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateExamStatus(
            @Parameter(description = "考试ID") @PathVariable Long id,
            @Parameter(description = "状态") @RequestParam Integer status) {
        return examService.updateExamStatus(id, status);
    }

    /**
     * 根据学年和学期获取考试列表
     */
    @Operation(summary = "根据学年和学期获取考试列表")
    @GetMapping("/school-year/{schoolYear}/semester/{semester}")
    public Result<List<Exam>> getExamsBySchoolYearAndSemester(
            @Parameter(description = "学年") @PathVariable String schoolYear,
            @Parameter(description = "学期") @PathVariable Integer semester) {
        return examService.getExamsBySchoolYearAndSemester(schoolYear, semester);
    }

    /**
     * 获取当前进行中的考试
     */
    @Operation(summary = "获取当前进行中的考试")
    @GetMapping("/current")
    public Result<List<Exam>> getCurrentExams() {
        return examService.getCurrentExams();
    }

    /**
     * 开始考试
     */
    @Operation(summary = "开始考试")
    @PutMapping("/{id}/start")
    public Result<Void> startExam(@Parameter(description = "考试ID") @PathVariable Long id) {
        return examService.updateExamStatus(id, 1);
    }

    /**
     * 结束考试
     */
    @Operation(summary = "结束考试")
    @PutMapping("/{id}/finish")
    public Result<Void> finishExam(@Parameter(description = "考试ID") @PathVariable Long id) {
        return examService.updateExamStatus(id, 2);
    }

    /**
     * 发布考试成绩
     */
    @Operation(summary = "发布考试成绩")
    @PutMapping("/{id}/publish")
    public Result<Void> publishExam(@Parameter(description = "考试ID") @PathVariable Long id) {
        return examService.updateExamStatus(id, 3);
    }

    /**
     * 获取即将到来的考试
     */
    @Operation(summary = "获取即将到来的考试")
    @GetMapping("/upcoming")
    public Result<PageResult<Exam>> getUpcomingExams(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        
        // 查询未开始状态的考试
        return examService.getExamList(page, size, null, null, null, null, 0);
    }

    /**
     * 获取已结束的考试
     */
    @Operation(summary = "获取已结束的考试")
    @GetMapping("/finished")
    public Result<PageResult<Exam>> getFinishedExams(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "学年") @RequestParam(required = false) String schoolYear,
            @Parameter(description = "学期") @RequestParam(required = false) Integer semester) {
        
        // 查询已结束状态的考试
        return examService.getExamList(page, size, null, null, schoolYear, semester, 2);
    }
}