package com.school.grade.controller;

import com.school.grade.entity.Student;
import com.school.grade.service.StudentService;
import com.school.grade.common.result.Result;
import com.school.grade.common.result.PageResult;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 学生管理控制�?
 * 
 * @author Qoder
 * @version 1.0.0
 */
@Tag(name = "学生管理")
@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*")
public class StudentController {

    @Autowired
    private StudentService studentService;

    /**
     * 分页查询学生列表
     */
    @Operation(summary = "分页查询学生列表")
    @GetMapping("/list")
    public Result<PageResult<Student>> getStudentList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "学生姓名") @RequestParam(required = false) String studentName,
            @Parameter(description = "学号") @RequestParam(required = false) String studentCode,
            @Parameter(description = "班级ID") @RequestParam(required = false) Long classId,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        
        return studentService.getStudentList(page, size, studentName, studentCode, classId, status);
    }

    /**
     * 根据ID获取学生信息
     */
    @Operation(summary = "根据ID获取学生信息")
    @GetMapping("/{id}")
    public Result<Student> getStudentById(@Parameter(description = "学生ID") @PathVariable Long id) {
        return studentService.getStudentById(id);
    }

    /**
     * 添加学生
     */
    @Operation(summary = "添加学生")
    @PostMapping
    public Result<Student> addStudent(@Parameter(description = "学生信息") @Valid @RequestBody Student student) {
        return studentService.addStudent(student);
    }

    /**
     * 更新学生信息
     */
    @Operation(summary = "更新学生信息")
    @PutMapping("/{id}")
    public Result<Student> updateStudent(
            @Parameter(description = "学生ID") @PathVariable Long id,
            @Parameter(description = "学生信息") @Valid @RequestBody Student student) {
        student.setId(id);
        return studentService.updateStudent(student);
    }

    /**
     * 删除学生
     */
    @Operation(summary = "删除学生")
    @DeleteMapping("/{id}")
    public Result<Void> deleteStudent(@Parameter(description = "学生ID") @PathVariable Long id) {
        return studentService.deleteStudent(id);
    }

    /**
     * 批量删除学生
     */
    @Operation(summary = "批量删除学生")
    @DeleteMapping("/batch")
    public Result<Void> batchDeleteStudents(@Parameter(description = "学生ID列表") @RequestBody List<Long> ids) {
        return studentService.batchDeleteStudents(ids);
    }

    /**
     * 更新学生状�?
     */
    @Operation(summary = "更新学生状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateStudentStatus(
            @Parameter(description = "学生ID") @PathVariable Long id,
            @Parameter(description = "状态") @RequestParam Integer status) {
        return studentService.updateStudentStatus(id, status);
    }

    /**
     * 根据班级ID获取学生列表
     */
    @Operation(summary = "根据班级ID获取学生列表")
    @GetMapping("/class/{classId}")
    public Result<List<Student>> getStudentsByClassId(@Parameter(description = "班级ID") @PathVariable Long classId) {
        return studentService.getStudentsByClassId(classId);
    }

    /**
     * 获取所有在读学生（用于下拉选择�?
     */
    @Operation(summary = "获取所有在读学生")
    @GetMapping("/active")
    public Result<PageResult<Student>> getActiveStudents(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "50") int size,
            @Parameter(description = "学生姓名") @RequestParam(required = false) String studentName) {
        
        // 只查询在读状态的学生
        return studentService.getStudentList(page, size, studentName, null, null, 1);
    }

    /**
     * 根据学号获取学生信息
     */
    @Operation(summary = "根据学号获取学生信息")
    @GetMapping("/code/{studentCode}")
    public Result<Student> getStudentByCode(@Parameter(description = "学号") @PathVariable String studentCode) {
        return studentService.getStudentByCode(studentCode);
    }
}
