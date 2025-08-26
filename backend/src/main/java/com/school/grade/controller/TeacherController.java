package com.school.grade.controller;

import com.school.grade.entity.Teacher;
import com.school.grade.service.TeacherService;
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
 * 教师管理控制�?
 * 
 * @author Qoder
 * @version 1.0.0
 */
@Tag(name = "教师管理")
@RestController
@RequestMapping("/api/teachers")
@CrossOrigin(origins = "*")
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    /**
     * 分页查询教师列表
     */
    @Operation(summary = "分页查询教师列表")
    @GetMapping("/list")
    public Result<PageResult<Teacher>> getTeacherList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "教师姓名") @RequestParam(required = false) String teacherName,
            @Parameter(description = "教师工号") @RequestParam(required = false) String teacherCode,
            @Parameter(description = "职位") @RequestParam(required = false) String position,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        
        return teacherService.getTeacherList(page, size, teacherName, teacherCode, position, status);
    }

    /**
     * 根据ID获取教师信息
     */
    @Operation(summary = "根据ID获取教师信息")
    @GetMapping("/{id}")
    public Result<Teacher> getTeacherById(@Parameter(description = "教师ID") @PathVariable Long id) {
        return teacherService.getTeacherById(id);
    }

    /**
     * 添加教师
     */
    @Operation(summary = "添加教师")
    @PostMapping
    public Result<Teacher> addTeacher(@Parameter(description = "教师信息") @Valid @RequestBody Teacher teacher) {
        return teacherService.addTeacher(teacher);
    }

    /**
     * 更新教师信息
     */
    @Operation(summary = "更新教师信息")
    @PutMapping("/{id}")
    public Result<Teacher> updateTeacher(
            @Parameter(description = "教师ID") @PathVariable Long id,
            @Parameter(description = "教师信息") @Valid @RequestBody Teacher teacher) {
        teacher.setId(id);
        return teacherService.updateTeacher(teacher);
    }

    /**
     * 删除教师
     */
    @Operation(summary = "删除教师")
    @DeleteMapping("/{id}")
    public Result<Void> deleteTeacher(@Parameter(description = "教师ID") @PathVariable Long id) {
        return teacherService.deleteTeacher(id);
    }

    /**
     * 批量删除教师
     */
    @Operation(summary = "批量删除教师")
    @DeleteMapping("/batch")
    public Result<Void> batchDeleteTeachers(@Parameter(description = "教师ID列表") @RequestBody List<Long> ids) {
        return teacherService.batchDeleteTeachers(ids);
    }

    /**
     * 更新教师状�?
     */
    @Operation(summary = "更新教师状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateTeacherStatus(
            @Parameter(description = "教师ID") @PathVariable Long id,
            @Parameter(description = "状态") @RequestParam Integer status) {
        return teacherService.updateTeacherStatus(id, status);
    }

    /**
     * 获取所有在职教师（用于下拉选择�?
     */
    @Operation(summary = "获取所有在职教师")
    @GetMapping("/active")
    public Result<List<Teacher>> getActiveTeachers() {
        return teacherService.getActiveTeachers();
    }

    /**
     * 根据职位获取教师列表
     */
    @Operation(summary = "根据职位获取教师列表")
    @GetMapping("/position/{position}")
    public Result<List<Teacher>> getTeachersByPosition(@Parameter(description = "职位") @PathVariable String position) {
        return teacherService.getTeachersByPosition(position);
    }

    /**
     * 获取班主任列�?
     */
    @Operation(summary = "获取班主任列表")
    @GetMapping("/head-teachers")
    public Result<List<Teacher>> getHeadTeachers() {
        return teacherService.getTeachersByPosition("班主任");
    }

    /**
     * 根据教师工号获取教师信息
     */
    @Operation(summary = "根据教师工号获取教师信息")
    @GetMapping("/code/{teacherCode}")
    public Result<PageResult<Teacher>> getTeacherByCode(@Parameter(description = "教师工号") @PathVariable String teacherCode) {
        return teacherService.getTeacherList(1, 1, null, teacherCode, null, null);
    }
}
