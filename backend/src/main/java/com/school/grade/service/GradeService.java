package com.school.grade.service;

import com.school.grade.common.result.Result;
import com.school.grade.common.PageQuery;
import com.school.grade.entity.Grade;
import com.school.grade.repository.GradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 年级管理服务类
 * 实现年级的增删改查和相关业务逻辑
 * 
 * @author Qoder
 * @since 2025-08-25
 */
@Service
@Transactional
public class GradeService {

    @Autowired
    private GradeRepository gradeRepository;

    /**
     * 分页查询年级列表
     */
    public Result<Page<Grade>> getGradeList(PageQuery pageQuery, String gradeName, String schoolYear) {
        try {
            Pageable pageable = PageRequest.of(
                pageQuery.getPageNum() - 1, 
                pageQuery.getPageSize(),
                Sort.by(Sort.Direction.ASC, "sortOrder", "gradeLevel")
            );
            
            Page<Grade> grades;
            if (StringUtils.hasText(gradeName) || StringUtils.hasText(schoolYear)) {
                // TODO: 实现自定义查询方法，暂时使用基础查询
                grades = gradeRepository.findAll(pageable);
            } else {
                grades = gradeRepository.findAll(pageable);
            }
            
            return Result.success(grades);
        } catch (Exception e) {
            return Result.error("查询年级列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有启用的年级列表
     */
    public Result<List<Grade>> getAllActiveGrades() {
        try {
            List<Grade> grades = gradeRepository.findByStatusAndDeletedFalseOrderBySortOrderAscGradeLevelAsc(1);
            return Result.success(grades);
        } catch (Exception e) {
            return Result.error("查询年级列表失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取年级详情
     */
    public Result<Grade> getGradeById(Long id) {
        try {
            Optional<Grade> grade = gradeRepository.findByIdAndDeletedFalse(id);
            if (grade.isPresent()) {
                return Result.success(grade.get());
            } else {
                return Result.error("年级不存在");
            }
        } catch (Exception e) {
            return Result.error("查询年级详情失败: " + e.getMessage());
        }
    }

    /**
     * 新增年级
     */
    public Result<Grade> createGrade(Grade grade) {
        try {
            // 验证年级编码是否重复
            if (gradeRepository.existsByGradeCodeAndDeletedFalse(grade.getGradeCode())) {
                return Result.error("年级编码已存在");
            }
            
            // 验证同学年同年级是否重复
            if (gradeRepository.existsBySchoolYearAndGradeLevelAndDeletedFalse(
                    grade.getSchoolYear(), grade.getGradeLevel())) {
                return Result.error("该学年该年级已存在");
            }
            
            grade.setCreateTime(LocalDateTime.now());
            grade.setUpdateTime(LocalDateTime.now());
            grade.setDeleted(0); // 0表示未删除
            
            Grade savedGrade = gradeRepository.save(grade);
            return Result.success(savedGrade);
        } catch (Exception e) {
            return Result.error("新增年级失败: " + e.getMessage());
        }
    }

    /**
     * 更新年级
     */
    public Result<Grade> updateGrade(Long id, Grade grade) {
        try {
            Optional<Grade> existingGrade = gradeRepository.findByIdAndDeletedFalse(id);
            if (!existingGrade.isPresent()) {
                return Result.error("年级不存在");
            }
            
            Grade gradeToUpdate = existingGrade.get();
            
            // 如果修改了年级编码，检查是否重复
            if (!gradeToUpdate.getGradeCode().equals(grade.getGradeCode())) {
                if (gradeRepository.existsByGradeCodeAndDeletedFalse(grade.getGradeCode())) {
                    return Result.error("年级编码已存在");
                }
            }
            
            // 如果修改了学年或年级序号，检查是否重复
            if (!gradeToUpdate.getSchoolYear().equals(grade.getSchoolYear()) || 
                !gradeToUpdate.getGradeLevel().equals(grade.getGradeLevel())) {
                if (gradeRepository.existsBySchoolYearAndGradeLevelAndDeletedFalse(
                        grade.getSchoolYear(), grade.getGradeLevel())) {
                    return Result.error("该学年该年级已存在");
                }
            }
            
            // 更新字段
            gradeToUpdate.setGradeName(grade.getGradeName());
            gradeToUpdate.setGradeCode(grade.getGradeCode());
            gradeToUpdate.setStage(grade.getStage());
            gradeToUpdate.setGradeLevel(grade.getGradeLevel());
            gradeToUpdate.setSchoolYear(grade.getSchoolYear());
            gradeToUpdate.setSortOrder(grade.getSortOrder());
            gradeToUpdate.setStatus(grade.getStatus());
            gradeToUpdate.setRemark(grade.getRemark());
            gradeToUpdate.setUpdateTime(LocalDateTime.now());
            gradeToUpdate.setUpdateBy(grade.getUpdateBy());
            
            Grade updatedGrade = gradeRepository.save(gradeToUpdate);
            return Result.success(updatedGrade);
        } catch (Exception e) {
            return Result.error("更新年级失败: " + e.getMessage());
        }
    }

    /**
     * 删除年级
     */
    public Result<Void> deleteGrade(Long id) {
        try {
            Optional<Grade> grade = gradeRepository.findByIdAndDeletedFalse(id);
            if (!grade.isPresent()) {
                return Result.error("年级不存在");
            }
            
            // 检查是否有关联的班级
            long classCount = gradeRepository.countClassesByGradeId(id);
            if (classCount > 0) {
                return Result.error("该年级下有班级，无法删除");
            }
            
            // 软删除
            Grade gradeToDelete = grade.get();
            gradeToDelete.setDeleted(1); // 1表示已删除
            gradeToDelete.setUpdateTime(LocalDateTime.now());
            gradeRepository.save(gradeToDelete);
            
            return Result.success();
        } catch (Exception e) {
            return Result.error("删除年级失败: " + e.getMessage());
        }
    }

    /**
     * 批量删除年级
     */
    public Result<Void> batchDeleteGrades(List<Long> ids) {
        try {
            for (Long id : ids) {
                Result<Void> result = deleteGrade(id);
                if (!result.isSuccess()) {
                    return result;
                }
            }
            return Result.success();
        } catch (Exception e) {
            return Result.error("批量删除年级失败: " + e.getMessage());
        }
    }

    /**
     * 更新年级状态
     */
    public Result<Void> updateGradeStatus(Long id, Integer status) {
        try {
            Optional<Grade> grade = gradeRepository.findByIdAndDeletedFalse(id);
            if (!grade.isPresent()) {
                return Result.error("年级不存在");
            }
            
            Grade gradeToUpdate = grade.get();
            gradeToUpdate.setStatus(status);
            gradeToUpdate.setUpdateTime(LocalDateTime.now());
            gradeRepository.save(gradeToUpdate);
            
            return Result.success();
        } catch (Exception e) {
            return Result.error("更新年级状态失败: " + e.getMessage());
        }
    }

    /**
     * 根据学年获取年级列表
     */
    public Result<List<Grade>> getGradesBySchoolYear(String schoolYear) {
        try {
            List<Grade> grades = gradeRepository.findBySchoolYearAndStatusAndDeletedFalseOrderByGradeLevelAsc(
                schoolYear, 1);
            return Result.success(grades);
        } catch (Exception e) {
            return Result.error("查询年级列表失败: " + e.getMessage());
        }
    }
}