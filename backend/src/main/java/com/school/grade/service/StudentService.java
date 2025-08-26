package com.school.grade.service;

import com.school.grade.entity.Student;
import com.school.grade.repository.StudentRepository;
import com.school.grade.repository.SchoolClassRepository;
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
import java.time.format.DateTimeParseException;

/**
 * 学生管理业务逻辑层
 * 
 * @author Qoder
 * @version 1.0.0
 */
@Service
@Transactional
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SchoolClassRepository classRepository;

    /**
     * 分页查询学生信息
     * 
     * @param page 页码
     * @param size 每页大小
     * @param studentName 学生姓名（模糊查询）
     * @param studentCode 学号（精确查询）
     * @param classId 班级ID
     * @param status 状态
     * @return 分页结果
     */
    public Result<PageResult<Student>> getStudentList(int page, int size, String studentName, 
                                                     String studentCode, Long classId, Integer status) {
        try {
            // 创建分页对象
            Pageable pageable = PageRequest.of(page - 1, size, 
                Sort.by(Sort.Direction.DESC, "createTime"));

            // 构建查询条件
            Specification<Student> spec = (root, query, criteriaBuilder) -> {
                List<Predicate> predicates = new ArrayList<>();
                
                // 软删除条件
                predicates.add(criteriaBuilder.equal(root.get("deleted"), 0));
                
                // 学生姓名模糊查询
                if (StringUtils.hasText(studentName)) {
                    predicates.add(criteriaBuilder.like(root.get("studentName"), "%" + studentName + "%"));
                }
                
                // 学号精确查询
                if (StringUtils.hasText(studentCode)) {
                    predicates.add(criteriaBuilder.equal(root.get("studentCode"), studentCode));
                }
                
                // 班级查询
                if (classId != null) {
                    predicates.add(criteriaBuilder.equal(root.get("classId"), classId));
                }
                
                // 状态查询
                if (status != null) {
                    predicates.add(criteriaBuilder.equal(root.get("status"), status));
                }
                
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            };

            Page<Student> studentPage = studentRepository.findAll(spec, pageable);
            
            PageResult<Student> pageResult = new PageResult<>(
                studentPage.getContent(),
                studentPage.getTotalElements(),
                page,
                size
            );

            return Result.success(pageResult);
        } catch (Exception e) {
            return Result.error("查询学生列表失败：" + e.getMessage());
        }
    }

    /**
     * 根据ID获取学生信息
     * 
     * @param id 学生ID
     * @return 学生信息
     */
    public Result<Student> getStudentById(Long id) {
        try {
            Optional<Student> student = studentRepository.findByIdAndDeleted(id, 0);
            if (student.isPresent()) {
                return Result.success(student.get());
            } else {
                return Result.error("学生信息不存在");
            }
        } catch (Exception e) {
            return Result.error("获取学生信息失败：" + e.getMessage());
        }
    }

    /**
     * 根据学号获取学生信息
     * 
     * @param studentCode 学号
     * @return 学生信息
     */
    public Result<Student> getStudentByCode(String studentCode) {
        try {
            Optional<Student> student = studentRepository.findByStudentCodeAndDeleted(studentCode, 0);
            if (student.isPresent()) {
                return Result.success(student.get());
            } else {
                return Result.error("学生信息不存在");
            }
        } catch (Exception e) {
            return Result.error("获取学生信息失败：" + e.getMessage());
        }
    }

    /**
     * 添加学生
     * 
     * @param student 学生信息
     * @return 操作结果
     */
    public Result<Student> addStudent(Student student) {
        try {
            // 数据验证
            Result<Void> validateResult = validateStudent(student);
            if (!validateResult.isSuccess()) {
                return Result.error(validateResult.getMessage());
            }

            // 检查学号是否已存在
            if (studentRepository.existsByStudentCodeAndDeleted(student.getStudentCode(), 0)) {
                return Result.error("学号已存在");
            }

            // 验证班级是否存在
            if (!classRepository.existsByIdAndDeleted(student.getClassId(), 0)) {
                return Result.error("所属班级不存在");
            }

            // 设置默认值
            if (student.getStatus() == null) {
                student.setStatus(1); // 默认在读状态
            }

            Student savedStudent = studentRepository.save(student);
            return Result.success(savedStudent);
        } catch (Exception e) {
            return Result.error("添加学生失败：" + e.getMessage());
        }
    }

    /**
     * 更新学生信息
     * 
     * @param student 学生信息
     * @return 操作结果
     */
    public Result<Student> updateStudent(Student student) {
        try {
            // 检查学生是否存在
            Optional<Student> existingStudent = studentRepository.findByIdAndDeleted(student.getId(), 0);
            if (!existingStudent.isPresent()) {
                return Result.error("学生信息不存在");
            }

            // 数据验证
            Result<Void> validateResult = validateStudent(student);
            if (!validateResult.isSuccess()) {
                return Result.error(validateResult.getMessage());
            }

            // 检查学号是否被其他学生占用
            Optional<Student> duplicateStudent = studentRepository.findByStudentCodeAndDeleted(student.getStudentCode(), 0);
            if (duplicateStudent.isPresent() && !duplicateStudent.get().getId().equals(student.getId())) {
                return Result.error("学号已被其他学生使用");
            }

            // 验证班级是否存在
            if (!classRepository.existsByIdAndDeleted(student.getClassId(), 0)) {
                return Result.error("所属班级不存在");
            }

            Student savedStudent = studentRepository.save(student);
            return Result.success(savedStudent);
        } catch (Exception e) {
            return Result.error("更新学生信息失败：" + e.getMessage());
        }
    }

    /**
     * 删除学生（软删除）
     * 
     * @param id 学生ID
     * @return 操作结果
     */
    public Result<Void> deleteStudent(Long id) {
        try {
            Optional<Student> student = studentRepository.findByIdAndDeleted(id, 0);
            if (!student.isPresent()) {
                return Result.error("学生信息不存在");
            }

            Student studentEntity = student.get();
            studentEntity.setDeleted(1);
            studentRepository.save(studentEntity);

            return Result.success();
        } catch (Exception e) {
            return Result.error("删除学生失败：" + e.getMessage());
        }
    }

    /**
     * 批量删除学生
     * 
     * @param ids 学生ID列表
     * @return 操作结果
     */
    public Result<Void> batchDeleteStudents(List<Long> ids) {
        try {
            List<Student> students = studentRepository.findByIdInAndDeleted(ids, 0);
            if (students.isEmpty()) {
                return Result.error("未找到要删除的学生");
            }

            students.forEach(student -> student.setDeleted(1));
            studentRepository.saveAll(students);

            return Result.success();
        } catch (Exception e) {
            return Result.error("批量删除学生失败：" + e.getMessage());
        }
    }

    /**
     * 更新学生状态
     * 
     * @param id 学生ID
     * @param status 新状态
     * @return 操作结果
     */
    public Result<Void> updateStudentStatus(Long id, Integer status) {
        try {
            Optional<Student> student = studentRepository.findByIdAndDeleted(id, 0);
            if (!student.isPresent()) {
                return Result.error("学生信息不存在");
            }

            // 验证状态值
            if (status < 0 || status > 3) {
                return Result.error("无效的状态值");
            }

            Student studentEntity = student.get();
            studentEntity.setStatus(status);
            studentRepository.save(studentEntity);

            return Result.success();
        } catch (Exception e) {
            return Result.error("更新学生状态失败：" + e.getMessage());
        }
    }

    /**
     * 根据班级ID获取学生列表
     * 
     * @param classId 班级ID
     * @return 学生列表
     */
    public Result<List<Student>> getStudentsByClassId(Long classId) {
        try {
            List<Student> students = studentRepository.findByClassIdAndDeleted(classId, 0);
            return Result.success(students);
        } catch (Exception e) {
            return Result.error("获取班级学生列表失败：" + e.getMessage());
        }
    }

    /**
     * 数据验证
     * 
     * @param student 学生信息
     * @return 验证结果
     */
    private Result<Void> validateStudent(Student student) {
        if (student == null) {
            return Result.error("学生信息不能为空");
        }

        if (!StringUtils.hasText(student.getStudentCode())) {
            return Result.error("学号不能为空");
        }

        if (student.getStudentCode().length() > 20) {
            return Result.error("学号长度不能超过20个字符");
        }

        if (!StringUtils.hasText(student.getStudentName())) {
            return Result.error("学生姓名不能为空");
        }

        if (student.getStudentName().length() > 50) {
            return Result.error("学生姓名长度不能超过50个字符");
        }

        if (student.getClassId() == null) {
            return Result.error("所属班级不能为空");
        }

        // 验证身份证号格式（如果提供）
        if (StringUtils.hasText(student.getIdCard()) && student.getIdCard().length() != 18) {
            return Result.error("身份证号格式不正确");
        }

        // 验证手机号格式（如果提供）
        if (StringUtils.hasText(student.getGuardianPhone()) && 
            !student.getGuardianPhone().matches("^1[3-9]\\d{9}$")) {
            return Result.error("监护人手机号格式不正确");
        }

        return Result.success();
    }
}