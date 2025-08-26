package com.school.grade.service;

import com.school.grade.entity.Teacher;
import com.school.grade.repository.TeacherRepository;
import com.school.grade.repository.UserRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

/**
 * 教师管理业务逻辑层
 * 
 * @author Qoder
 * @version 1.0.0
 */
@Service
@Transactional
public class TeacherService {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 分页查询教师信息
     * 
     * @param page 页码
     * @param size 每页大小
     * @param teacherName 教师姓名（模糊查询）
     * @param teacherCode 教师工号（精确查询）
     * @param position 职位
     * @param status 状态
     * @return 分页结果
     */
    public Result<PageResult<Teacher>> getTeacherList(int page, int size, String teacherName, 
                                                     String teacherCode, String position, Integer status) {
        try {
            // 创建分页对象
            Pageable pageable = PageRequest.of(page - 1, size, 
                Sort.by(Sort.Direction.DESC, "createTime"));

            // 构建查询条件
            Specification<Teacher> spec = (root, query, criteriaBuilder) -> {
                List<Predicate> predicates = new ArrayList<>();
                
                // 软删除条件
                predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
                
                // 教师姓名模糊查询
                if (StringUtils.hasText(teacherName)) {
                    predicates.add(criteriaBuilder.like(root.get("teacherName"), "%" + teacherName + "%"));
                }
                
                // 教师工号精确查询
                if (StringUtils.hasText(teacherCode)) {
                    predicates.add(criteriaBuilder.equal(root.get("teacherCode"), teacherCode));
                }
                
                // 职位查询
                if (StringUtils.hasText(position)) {
                    predicates.add(criteriaBuilder.like(root.get("position"), "%" + position + "%"));
                }
                
                // 状态查询
                if (status != null) {
                    predicates.add(criteriaBuilder.equal(root.get("status"), status));
                }
                
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            };

            Page<Teacher> teacherPage = teacherRepository.findAll(spec, pageable);
            
            PageResult<Teacher> pageResult = new PageResult<>(
                teacherPage.getContent(),
                teacherPage.getTotalElements(),
                page,
                size
            );

            return Result.success(pageResult);
        } catch (Exception e) {
            return Result.error("查询教师列表失败：" + e.getMessage());
        }
    }

    /**
     * 根据ID获取教师信息
     * 
     * @param id 教师ID
     * @return 教师信息
     */
    public Result<Teacher> getTeacherById(Long id) {
        try {
            Optional<Teacher> teacher = teacherRepository.findByIdAndDeleted(id, 0);
            if (teacher.isPresent()) {
                return Result.success(teacher.get());
            } else {
                return Result.error("教师信息不存在");
            }
        } catch (Exception e) {
            return Result.error("获取教师信息失败：" + e.getMessage());
        }
    }

    /**
     * 添加教师
     * 
     * @param teacher 教师信息
     * @return 操作结果
     */
    public Result<Teacher> addTeacher(Teacher teacher) {
        try {
            // 数据验证
            Result<Void> validateResult = validateTeacher(teacher);
            if (!validateResult.isSuccess()) {
                return Result.error(validateResult.getMessage());
            }

            // 检查教师工号是否已存在
            if (teacherRepository.existsByTeacherCodeAndDeleted(teacher.getTeacherCode(), 0)) {
                return Result.error("教师工号已存在");
            }

            // 验证关联用户ID（如果提供）
            if (teacher.getUserId() != null) {
                if (!userRepository.existsByIdAndDeleted(teacher.getUserId(), 0)) {
                    return Result.error("关联用户不存在");
                }
                
                // 检查该用户是否已被其他教师关联
                if (teacherRepository.existsByUserIdAndDeleted(teacher.getUserId(), 0)) {
                    return Result.error("该用户已被其他教师关联");
                }
            }

            // 设置默认值
            if (teacher.getStatus() == null) {
                teacher.setStatus(1); // 默认在职状态
            }

            Teacher savedTeacher = teacherRepository.save(teacher);
            return Result.success(savedTeacher);
        } catch (Exception e) {
            return Result.error("添加教师失败：" + e.getMessage());
        }
    }

    /**
     * 更新教师信息
     * 
     * @param teacher 教师信息
     * @return 操作结果
     */
    public Result<Teacher> updateTeacher(Teacher teacher) {
        try {
            // 检查教师是否存在
            Optional<Teacher> existingTeacher = teacherRepository.findByIdAndDeleted(teacher.getId(), 0);
            if (!existingTeacher.isPresent()) {
                return Result.error("教师信息不存在");
            }

            // 数据验证
            Result<Void> validateResult = validateTeacher(teacher);
            if (!validateResult.isSuccess()) {
                return Result.error(validateResult.getMessage());
            }

            // 检查教师工号是否被其他教师占用
            Optional<Teacher> duplicateTeacher = teacherRepository.findByTeacherCodeAndDeleted(teacher.getTeacherCode(), 0);
            if (duplicateTeacher.isPresent() && !duplicateTeacher.get().getId().equals(teacher.getId())) {
                return Result.error("教师工号已被其他教师使用");
            }

            // 验证关联用户ID（如果提供）
            if (teacher.getUserId() != null) {
                if (!userRepository.existsByIdAndDeleted(teacher.getUserId(), 0)) {
                    return Result.error("关联用户不存在");
                }
                
                // 检查该用户是否已被其他教师关联
                Optional<Teacher> userBoundTeacher = teacherRepository.findByUserIdAndDeleted(teacher.getUserId(), 0);
                if (userBoundTeacher.isPresent() && !userBoundTeacher.get().getId().equals(teacher.getId())) {
                    return Result.error("该用户已被其他教师关联");
                }
            }

            Teacher savedTeacher = teacherRepository.save(teacher);
            return Result.success(savedTeacher);
        } catch (Exception e) {
            return Result.error("更新教师信息失败：" + e.getMessage());
        }
    }

    /**
     * 删除教师（软删除）
     * 
     * @param id 教师ID
     * @return 操作结果
     */
    public Result<Void> deleteTeacher(Long id) {
        try {
            Optional<Teacher> teacher = teacherRepository.findByIdAndDeleted(id, 0);
            if (!teacher.isPresent()) {
                return Result.error("教师信息不存在");
            }

            Teacher teacherEntity = teacher.get();
            teacherEntity.setDeleted(1); // 1表示已删除
            teacherRepository.save(teacherEntity);

            return Result.success();
        } catch (Exception e) {
            return Result.error("删除教师失败：" + e.getMessage());
        }
    }

    /**
     * 批量删除教师
     * 
     * @param ids 教师ID列表
     * @return 操作结果
     */
    public Result<Void> batchDeleteTeachers(List<Long> ids) {
        try {
            List<Teacher> teachers = teacherRepository.findByIdInAndDeleted(ids, 0);
            if (teachers.isEmpty()) {
                return Result.error("未找到要删除的教师");
            }

            teachers.forEach(teacher -> teacher.setDeleted(1)); // 1表示已删除
            teacherRepository.saveAll(teachers);

            return Result.success();
        } catch (Exception e) {
            return Result.error("批量删除教师失败：" + e.getMessage());
        }
    }

    /**
     * 更新教师状态
     * 
     * @param id 教师ID
     * @param status 新状态
     * @return 操作结果
     */
    public Result<Void> updateTeacherStatus(Long id, Integer status) {
        try {
            Optional<Teacher> teacher = teacherRepository.findByIdAndDeleted(id, 0);
            if (!teacher.isPresent()) {
                return Result.error("教师信息不存在");
            }

            // 验证状态值
            if (status < 0 || status > 1) {
                return Result.error("无效的状态值");
            }

            Teacher teacherEntity = teacher.get();
            teacherEntity.setStatus(status);
            teacherRepository.save(teacherEntity);

            return Result.success();
        } catch (Exception e) {
            return Result.error("更新教师状态失败：" + e.getMessage());
        }
    }

    /**
     * 获取所有在职教师（用于下拉选择）
     * 
     * @return 教师列表
     */
    public Result<List<Teacher>> getActiveTeachers() {
        try {
            List<Teacher> teachers = teacherRepository.findByStatusAndDeletedOrderByTeacherNameAsc(1, 0);
            return Result.success(teachers);
        } catch (Exception e) {
            return Result.error("获取在职教师列表失败：" + e.getMessage());
        }
    }

    /**
     * 根据职位获取教师列表
     * 
     * @param position 职位
     * @return 教师列表
     */
    public Result<List<Teacher>> getTeachersByPosition(String position) {
        try {
            List<Teacher> teachers = teacherRepository.findByPositionContainingAndStatusAndDeletedOrderByTeacherNameAsc(
                position, 1, 0);
            return Result.success(teachers);
        } catch (Exception e) {
            return Result.error("根据职位获取教师列表失败：" + e.getMessage());
        }
    }

    /**
     * 数据验证
     * 
     * @param teacher 教师信息
     * @return 验证结果
     */
    private Result<Void> validateTeacher(Teacher teacher) {
        if (teacher == null) {
            return Result.error("教师信息不能为空");
        }

        if (!StringUtils.hasText(teacher.getTeacherCode())) {
            return Result.error("教师工号不能为空");
        }

        if (teacher.getTeacherCode().length() > 20) {
            return Result.error("教师工号长度不能超过20个字符");
        }

        if (!StringUtils.hasText(teacher.getTeacherName())) {
            return Result.error("教师姓名不能为空");
        }

        if (teacher.getTeacherName().length() > 50) {
            return Result.error("教师姓名长度不能超过50个字符");
        }

        // 验证身份证号格式（如果提供）
        if (StringUtils.hasText(teacher.getIdCard()) && teacher.getIdCard().length() != 18) {
            return Result.error("身份证号格式不正确");
        }

        // 验证手机号格式（如果提供）
        if (StringUtils.hasText(teacher.getPhone()) && 
            !teacher.getPhone().matches("^1[3-9]\\d{9}$")) {
            return Result.error("手机号格式不正确");
        }

        // 验证邮箱格式（如果提供）
        if (StringUtils.hasText(teacher.getEmail()) && 
            !teacher.getEmail().matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$")) {
            return Result.error("邮箱格式不正确");
        }

        return Result.success();
    }
}