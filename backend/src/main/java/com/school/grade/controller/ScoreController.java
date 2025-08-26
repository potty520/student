package com.school.grade.controller;

import com.school.grade.entity.Score;
import com.school.grade.service.ScoreService;
import com.school.grade.common.result.Result;
import com.school.grade.common.result.PageResult;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 成绩管理控制�?
 * 
 * @author Qoder
 * @version 1.0.0
 */
@Tag(name = "成绩管理")
@RestController
@RequestMapping("/api/scores")
@CrossOrigin(origins = "*")
public class ScoreController {

    @Autowired
    private ScoreService scoreService;

    /**
     * 分页查询成绩列表
     */
    @Operation(summary = "分页查询成绩列表")
    @GetMapping("/list")
    public Result<PageResult<Score>> getScoreList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "考试ID") @RequestParam(required = false) Long examId,
            @Parameter(description = "学生ID") @RequestParam(required = false) Long studentId,
            @Parameter(description = "课程ID") @RequestParam(required = false) Long courseId,
            @Parameter(description = "班级ID") @RequestParam(required = false) Long classId) {
        
        return scoreService.getScoreList(page, size, examId, studentId, courseId, classId);
    }

    /**
     * 根据ID获取成绩信息
     */
    @Operation(summary = "根据ID获取成绩信息")
    @GetMapping("/{id}")
    public Result<Score> getScoreById(@Parameter(description = "成绩ID") @PathVariable Long id) {
        return scoreService.getScoreById(id);
    }

    /**
     * 添加成绩
     */
    @Operation(summary = "添加成绩")
    @PostMapping
    public Result<Score> addScore(@Parameter(description = "成绩信息") @Valid @RequestBody Score score) {
        return scoreService.addScore(score);
    }

    /**
     * 批量录入成绩
     */
    @Operation(summary = "批量录入成绩")
    @PostMapping("/batch")
    public Result<List<Score>> batchAddScores(@Parameter(description = "成绩列表") @Valid @RequestBody List<Score> scores) {
        return scoreService.batchAddScores(scores);
    }

    /**
     * 更新成绩信息
     */
    @Operation(summary = "更新成绩信息")
    @PutMapping("/{id}")
    public Result<Score> updateScore(
            @Parameter(description = "成绩ID") @PathVariable Long id,
            @Parameter(description = "成绩信息") @Valid @RequestBody Score score) {
        score.setId(id);
        return scoreService.updateScore(score);
    }

    /**
     * 删除成绩
     */
    @Operation(summary = "删除成绩")
    @DeleteMapping("/{id}")
    public Result<Void> deleteScore(@Parameter(description = "成绩ID") @PathVariable Long id) {
        return scoreService.deleteScore(id);
    }

    /**
     * 根据考试和课程获取成绩列�?
     */
    @Operation(summary = "根据考试和课程获取成绩列表")
    @GetMapping("/exam/{examId}/course/{courseId}")
    public Result<List<Score>> getScoresByExamAndCourse(
            @Parameter(description = "考试ID") @PathVariable Long examId,
            @Parameter(description = "课程ID") @PathVariable Long courseId) {
        return scoreService.getScoresByExamAndCourse(examId, courseId);
    }

    /**
     * 根据学生和考试获取成绩列表
     */
    @Operation(summary = "根据学生和考试获取成绩列表")
    @GetMapping("/student/{studentId}/exam/{examId}")
    public Result<List<Score>> getScoresByStudentAndExam(
            @Parameter(description = "学生ID") @PathVariable Long studentId,
            @Parameter(description = "考试ID") @PathVariable Long examId) {
        return scoreService.getScoresByStudentAndExam(studentId, examId);
    }

    /**
     * 成绩统计分析 - 班级统计
     */
    @Operation(summary = "班级成绩统计分析")
    @GetMapping("/statistics/class")
    public Result<Map<String, Object>> getClassScoreStatistics(
            @Parameter(description = "考试ID") @RequestParam Long examId,
            @Parameter(description = "课程ID") @RequestParam Long courseId,
            @Parameter(description = "班级ID") @RequestParam Long classId) {
        return scoreService.getScoreStatistics(examId, courseId, classId);
    }

    /**
     * 成绩统计分析 - 年级统计
     */
    @Operation(summary = "年级成绩统计分析")
    @GetMapping("/statistics/grade")
    public Result<Map<String, Object>> getGradeScoreStatistics(
            @Parameter(description = "考试ID") @RequestParam Long examId,
            @Parameter(description = "课程ID") @RequestParam Long courseId) {
        return scoreService.getScoreStatistics(examId, courseId, null);
    }

    /**
     * 获取学生个人成绩报告
     */
    @Operation(summary = "获取学生个人成绩报告")
    @GetMapping("/report/student/{studentId}")
    public Result<List<Score>> getStudentScoreReport(
            @Parameter(description = "学生ID") @PathVariable Long studentId,
            @Parameter(description = "考试ID") @RequestParam(required = false) Long examId) {
        
        if (examId != null) {
            return scoreService.getScoresByStudentAndExam(studentId, examId);
        } else {
            // 如果没有指定考试，返回该学生的所有成�?
            return scoreService.getStudentAllScores(studentId);
        }
    }

    /**
     * 获取班级成绩�?
     */
    @Operation(summary = "获取班级成绩")
    @GetMapping("/report/class/{classId}")
    public Result<PageResult<Score>> getClassScoreReport(
            @Parameter(description = "班级ID") @PathVariable Long classId,
            @Parameter(description = "考试ID") @RequestParam(required = false) Long examId,
            @Parameter(description = "课程ID") @RequestParam(required = false) Long courseId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "50") int size) {
        
        return scoreService.getScoreList(page, size, examId, null, courseId, classId);
    }

    /**
     * 获取课程成绩排行�?
     */
    @Operation(summary = "获取课程成绩排行")
    @GetMapping("/ranking/course/{courseId}")
    public Result<List<Score>> getCourseRanking(
            @Parameter(description = "课程ID") @PathVariable Long courseId,
            @Parameter(description = "考试ID") @RequestParam Long examId,
            @Parameter(description = "班级ID") @RequestParam(required = false) Long classId,
            @Parameter(description = "排名类型") @RequestParam(defaultValue = "grade") String rankType) {
        
        return scoreService.getScoresByExamAndCourse(examId, courseId);
    }

    /**
     * 获取考试总分排行�?
     */
    @Operation(summary = "获取考试总分排行")
    @GetMapping("/ranking/exam/{examId}")
    public Result<PageResult<Score>> getExamTotalRanking(
            @Parameter(description = "考试ID") @PathVariable Long examId,
            @Parameter(description = "班级ID") @RequestParam(required = false) Long classId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "50") int size) {
        
        return scoreService.getScoreList(page, size, examId, null, null, classId);
    }

    /**
     * 导出班级成绩
     */
    @Operation(summary = "导出班级成绩")
    @GetMapping("/export/class/{classId}")
    public Result<List<Score>> exportClassScores(
            @Parameter(description = "班级ID") @PathVariable Long classId,
            @Parameter(description = "考试ID") @RequestParam Long examId) {
        
        return scoreService.getClassScoreList(classId, examId);
    }

    /**
     * 导出年级成绩
     */
    @Operation(summary = "导出年级成绩")
    @GetMapping("/export/grade")
    public Result<PageResult<Score>> exportGradeScores(
            @Parameter(description = "考试ID") @RequestParam Long examId,
            @Parameter(description = "课程ID") @RequestParam(required = false) Long courseId) {
        
        return scoreService.getScoreList(1, 10000, examId, null, courseId, null);
    }
}
