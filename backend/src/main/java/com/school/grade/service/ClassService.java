package com.school.grade.service;

import com.school.grade.common.result.Result;
import com.school.grade.common.PageQuery;
import com.school.grade.entity.SchoolClass;
import com.school.grade.repository.SchoolClassRepository;
import com.school.grade.repository.StudentRepository;
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
 * 班级管理服务类
 * 实现班级的增删改查和相关业务逻辑
 * 
 * @author Qoder
 * @since 2025-08-25
 */
@Service
@Transactional
public class ClassService {

    @Autowired
    private SchoolClassRepository classRepository;
    
    @Autowired
    private StudentRepository studentRepository;

    /**
     * 分页查询班级列表
     */
    public Result<Page<SchoolClass>> getClassList(PageQuery pageQuery, String className, Long gradeId) {
        try {
            Pageable pageable = PageRequest.of(
                pageQuery.getPageNum() - 1, 
                pageQuery.getPageSize(),
                Sort.by(Sort.Direction.ASC, "sortOrder", "className")
            );
            
            Page<SchoolClass> classes;
            if (StringUtils.hasText(className) || gradeId != null) {
                classes = classRepository.findByConditions(className, gradeId, pageable);
            } else {
                classes = classRepository.findByDeletedFalse(pageable);
            }
            
            return Result.success(classes);
        } catch (Exception e) {
            return Result.error("查询班级列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有启用的班级列表
     */
    public Result<List<SchoolClass>> getAllActiveClasses() {
        try {
            List<SchoolClass> classes = classRepository.findByStatusAndDeletedFalseOrderBySortOrderAscClassNameAsc(1);
            return Result.success(classes);
        } catch (Exception e) {
            return Result.error("查询班级列表失败: " + e.getMessage());
        }
    }

    /**
     * 根据年级ID获取班级列表
     */
    public Result<List<SchoolClass>> getClassesByGradeId(Long gradeId) {
        try {
            List<SchoolClass> classes = classRepository.findByGradeIdAndStatusAndDeletedFalseOrderBySortOrderAsc(
                gradeId, 1);
            return Result.success(classes);
        } catch (Exception e) {
            return Result.error("查询班级列表失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取班级详情
     */
    public Result<SchoolClass> getClassById(Long id) {
        try {
            Optional<SchoolClass> schoolClass = classRepository.findByIdAndDeletedFalse(id);
            if (schoolClass.isPresent()) {
                return Result.success(schoolClass.get());
            } else {
                return Result.error("班级不存在");
            }
        } catch (Exception e) {
            return Result.error("查询班级详情失败: " + e.getMessage());
        }
    }

    /**
     * 新增班级
     */
    public Result<SchoolClass> createClass(SchoolClass schoolClass) {
        try {
            // 验证班级编码是否重复
            if (classRepository.existsByClassCodeAndDeletedFalse(schoolClass.getClassCode())) {
                return Result.error("班级编码已存在");
            }
            
            // 验证同年级同名班级是否重复
            if (classRepository.existsByGradeIdAndClassNameAndDeletedFalse(
                    schoolClass.getGradeId(), schoolClass.getClassName())) {
                return Result.error("该年级已存在同名班级");
            }
            
            schoolClass.setCreateTime(LocalDateTime.now());
            schoolClass.setUpdateTime(LocalDateTime.now());
            schoolClass.setDeleted(0); // 0表示未删除
            schoolClass.setStudentCount(0); // 初始学生数为0
            
            SchoolClass savedClass = classRepository.save(schoolClass);
            return Result.success(savedClass);
        } catch (Exception e) {
            return Result.error("新增班级失败: " + e.getMessage());
        }
    }

    /**
     * 更新班级
     */
    public Result<SchoolClass> updateClass(Long id, SchoolClass schoolClass) {
        try {
            Optional<SchoolClass> existingClass = classRepository.findByIdAndDeletedFalse(id);
            if (!existingClass.isPresent()) {
                return Result.error("班级不存在");
            }
            
            SchoolClass classToUpdate = existingClass.get();
            
            // 如果修改了班级编码，检查是否重复
            if (!classToUpdate.getClassCode().equals(schoolClass.getClassCode())) {
                if (classRepository.existsByClassCodeAndDeletedFalse(schoolClass.getClassCode())) {
                    return Result.error("班级编码已存在");
                }
            }
            
            // 如果修改了年级或班级名称，检查是否重复
            if (!classToUpdate.getGradeId().equals(schoolClass.getGradeId()) || 
                !classToUpdate.getClassName().equals(schoolClass.getClassName())) {
                if (classRepository.existsByGradeIdAndClassNameAndDeletedFalse(
                        schoolClass.getGradeId(), schoolClass.getClassName())) {
                    return Result.error("该年级已存在同名班级");
                }
            }
            
            // 更新字段
            classToUpdate.setClassName(schoolClass.getClassName());
            classToUpdate.setClassCode(schoolClass.getClassCode());
            classToUpdate.setGradeId(schoolClass.getGradeId());
            classToUpdate.setHeadTeacherId(schoolClass.getHeadTeacherId());
            classToUpdate.setSortOrder(schoolClass.getSortOrder());
            classToUpdate.setStatus(schoolClass.getStatus());
            classToUpdate.setRemark(schoolClass.getRemark());
            classToUpdate.setUpdateTime(LocalDateTime.now());
            classToUpdate.setUpdateBy(schoolClass.getUpdateBy());
            
            SchoolClass updatedClass = classRepository.save(classToUpdate);
            return Result.success(updatedClass);
        } catch (Exception e) {
            return Result.error("更新班级失败: " + e.getMessage());
        }
    }

    /**
     * 删除班级
     */
    public Result<Void> deleteClass(Long id) {
        try {
            Optional<SchoolClass> schoolClass = classRepository.findByIdAndDeletedFalse(id);
            if (!schoolClass.isPresent()) {
                return Result.error("班级不存在");
            }
            
            // 检查是否有关联的学生
            long studentCount = studentRepository.countByClassIdAndStatusAndDeletedFalse(id, 1);
            if (studentCount > 0) {
                return Result.error("该班级下有学生，无法删除");
            }
            
            // 软删除
            SchoolClass classToDelete = schoolClass.get();
            classToDelete.setDeleted(1); // 1表示已删除
            classToDelete.setUpdateTime(LocalDateTime.now());
            classRepository.save(classToDelete);
            
            return Result.success();
        } catch (Exception e) {
            return Result.error("删除班级失败: " + e.getMessage());
        }
    }

    /**
     * 批量删除班级
     */
    public Result<Void> batchDeleteClasses(List<Long> ids) {
        try {
            for (Long id : ids) {
                Result<Void> result = deleteClass(id);
                if (!result.isSuccess()) {
                    return result;
                }
            }
            return Result.success();
        } catch (Exception e) {
            return Result.error("批量删除班级失败: " + e.getMessage());
        }
    }

    /**
     * 更新班级状态
     */
    public Result<Void> updateClassStatus(Long id, Integer status) {
        try {
            Optional<SchoolClass> schoolClass = classRepository.findByIdAndDeletedFalse(id);
            if (!schoolClass.isPresent()) {
                return Result.error("班级不存在");
            }
            
            SchoolClass classToUpdate = schoolClass.get();
            classToUpdate.setStatus(status);
            classToUpdate.setUpdateTime(LocalDateTime.now());
            classRepository.save(classToUpdate);
            
            return Result.success();
        } catch (Exception e) {
            return Result.error("更新班级状态失败: " + e.getMessage());
        }
    }

    /**
     * 更新班级学生数量
     */
    public Result<Void> updateStudentCount(Long classId) {
        try {
            Optional<SchoolClass> schoolClass = classRepository.findByIdAndDeletedFalse(classId);
            if (!schoolClass.isPresent()) {
                return Result.error("班级不存在");
            }
            
            long studentCount = studentRepository.countByClassIdAndStatusAndDeletedFalse(classId, 1);
            
            SchoolClass classToUpdate = schoolClass.get();
            classToUpdate.setStudentCount((int) studentCount);
            classToUpdate.setUpdateTime(LocalDateTime.now());
            classRepository.save(classToUpdate);
            
            return Result.success();
        } catch (Exception e) {
            return Result.error("更新班级学生数量失败: " + e.getMessage());
        }
    }
}