package com.school.grade.service;

import com.school.grade.entity.Exam;
import com.school.grade.repository.ExamRepository;
import com.school.grade.repository.GradeRepository;
import com.school.grade.repository.CourseRepository;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 考试管理业务逻辑层
 * 
 * @author Qoder
 * @version 1.0.0
 */
@Service
@Transactional
public class ExamService {

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private CourseRepository courseRepository;

    /**
     * 分页查询考试信息
     * 
     * @param page 页码
     * @param size 每页大小
     * @param examName 考试名称（模糊查询）
     * @param examType 考试类型
     * @param schoolYear 学年
     * @param semester 学期
     * @param status 状态
     * @return 分页结果
     */
    public Result<PageResult<Exam>> getExamList(int page, int size, String examName, 
                                               Integer examType, String schoolYear, Integer semester, Integer status) {
        try {
            // 创建分页对象
            Pageable pageable = PageRequest.of(page - 1, size, 
                Sort.by(Sort.Direction.DESC, "startDate", "createTime"));

            // 构建查询条件
            Specification<Exam> spec = (root, query, criteriaBuilder) -> {
                List<Predicate> predicates = new ArrayList<>();
                
                // 软删除条件
                predicates.add(criteriaBuilder.equal(root.get("deleted"), 0));
                
                // 考试名称模糊查询
                if (StringUtils.hasText(examName)) {
                    predicates.add(criteriaBuilder.like(root.get("examName"), "%" + examName + "%"));
                }
                
                // 考试类型查询
                if (examType != null) {
                    predicates.add(criteriaBuilder.equal(root.get("examType"), examType));
                }
                
                // 学年查询
                if (StringUtils.hasText(schoolYear)) {
                    predicates.add(criteriaBuilder.equal(root.get("schoolYear"), schoolYear));
                }
                
                // 学期查询
                if (semester != null) {
                    predicates.add(criteriaBuilder.equal(root.get("semester"), semester));
                }
                
                // 状态查询
                if (status != null) {
                    predicates.add(criteriaBuilder.equal(root.get("status"), status));
                }
                
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            };

            Page<Exam> examPage = examRepository.findAll(spec, pageable);
            
            PageResult<Exam> pageResult = new PageResult<>(
                examPage.getContent(),
                examPage.getTotalElements(),
                page,
                size
            );

            return Result.success(pageResult);
        } catch (Exception e) {
            return Result.error("查询考试列表失败：" + e.getMessage());
        }
    }

    /**
     * 根据ID获取考试信息
     * 
     * @param id 考试ID
     * @return 考试信息
     */
    public Result<Exam> getExamById(Long id) {
        try {
            Optional<Exam> exam = examRepository.findByIdAndDeleted(id, 0);
            if (exam.isPresent()) {
                return Result.success(exam.get());
            } else {
                return Result.error("考试信息不存在");
            }
        } catch (Exception e) {
            return Result.error("获取考试信息失败：" + e.getMessage());
        }
    }

    /**
     * 添加考试
     * 
     * @param exam 考试信息
     * @return 操作结果
     */
    public Result<Exam> addExam(Exam exam) {
        try {
            // 数据验证
            Result<Void> validateResult = validateExam(exam);
            if (!validateResult.isSuccess()) {
                return Result.error(validateResult.getMessage());
            }

            // 检查考试编码是否已存在
            if (examRepository.existsByExamCodeAndDeleted(exam.getExamCode(), 0)) {
                return Result.error("考试编码已存在");
            }

            // 验证年级是否存在
            Result<Void> gradeValidateResult = validateGradeIds(exam.getGradeIds());
            if (!gradeValidateResult.isSuccess()) {
                return Result.error(gradeValidateResult.getMessage());
            }

            // 验证课程是否存在
            Result<Void> courseValidateResult = validateCourseIds(exam.getCourseIds());
            if (!courseValidateResult.isSuccess()) {
                return Result.error(courseValidateResult.getMessage());
            }

            // 设置默认值
            if (exam.getStatus() == null) {
                exam.setStatus(0); // 默认未开始状态
            }

            Exam savedExam = examRepository.save(exam);
            return Result.success(savedExam);
        } catch (Exception e) {
            return Result.error("添加考试失败：" + e.getMessage());
        }
    }

    /**
     * 更新考试信息
     * 
     * @param exam 考试信息
     * @return 操作结果
     */
    public Result<Exam> updateExam(Exam exam) {
        try {
            // 检查考试是否存在
            Optional<Exam> existingExam = examRepository.findByIdAndDeleted(exam.getId(), 0);
            if (!existingExam.isPresent()) {
                return Result.error("考试信息不存在");
            }

            // 数据验证
            Result<Void> validateResult = validateExam(exam);
            if (!validateResult.isSuccess()) {
                return Result.error(validateResult.getMessage());
            }

            // 检查考试编码是否被其他考试占用
            Optional<Exam> duplicateExam = examRepository.findByExamCodeAndDeleted(exam.getExamCode(), 0);
            if (duplicateExam.isPresent() && !duplicateExam.get().getId().equals(exam.getId())) {
                return Result.error("考试编码已被其他考试使用");
            }

            // 验证年级是否存在
            Result<Void> gradeValidateResult = validateGradeIds(exam.getGradeIds());
            if (!gradeValidateResult.isSuccess()) {
                return Result.error(gradeValidateResult.getMessage());
            }

            // 验证课程是否存在
            Result<Void> courseValidateResult = validateCourseIds(exam.getCourseIds());
            if (!courseValidateResult.isSuccess()) {
                return Result.error(courseValidateResult.getMessage());
            }

            Exam savedExam = examRepository.save(exam);
            return Result.success(savedExam);
        } catch (Exception e) {
            return Result.error("更新考试信息失败：" + e.getMessage());
        }
    }

    /**
     * 删除考试（软删除）
     * 
     * @param id 考试ID
     * @return 操作结果
     */
    public Result<Void> deleteExam(Long id) {
        try {
            Optional<Exam> exam = examRepository.findByIdAndDeleted(id, 0);
            if (!exam.isPresent()) {
                return Result.error("考试信息不存在");
            }

            Exam examEntity = exam.get();
            // 检查考试状态，如果已开始或已结束，不允许删除
            if (examEntity.getStatus() != null && examEntity.getStatus() > 0) {
                return Result.error("考试已开始或已结束，不允许删除");
            }

            examEntity.setDeleted(1);
            examRepository.save(examEntity);

            return Result.success();
        } catch (Exception e) {
            return Result.error("删除考试失败：" + e.getMessage());
        }
    }

    /**
     * 批量删除考试
     * 
     * @param ids 考试ID列表
     * @return 操作结果
     */
    public Result<Void> batchDeleteExams(List<Long> ids) {
        try {
            List<Exam> exams = examRepository.findByIdInAndDeleted(ids, 0);
            if (exams.isEmpty()) {
                return Result.error("未找到要删除的考试");
            }

            // 检查是否有已开始的考试
            List<Exam> activeExams = exams.stream()
                .filter(exam -> exam.getStatus() != null && exam.getStatus() > 0)
                .collect(Collectors.toList());
            
            if (!activeExams.isEmpty()) {
                return Result.error("存在已开始或已结束的考试，不允许删除");
            }

            exams.forEach(exam -> exam.setDeleted(1));
            examRepository.saveAll(exams);

            return Result.success();
        } catch (Exception e) {
            return Result.error("批量删除考试失败：" + e.getMessage());
        }
    }

    /**
     * 更新考试状态
     * 
     * @param id 考试ID
     * @param status 新状态
     * @return 操作结果
     */
    public Result<Void> updateExamStatus(Long id, Integer status) {
        try {
            Optional<Exam> exam = examRepository.findByIdAndDeleted(id, 0);
            if (!exam.isPresent()) {
                return Result.error("考试信息不存在");
            }

            // 验证状态值
            if (status < 0 || status > 3) {
                return Result.error("无效的状态值");
            }

            Exam examEntity = exam.get();
            
            // 状态流转验证
            if (!isValidStatusTransition(examEntity.getStatus(), status)) {
                return Result.error("无效的状态流转");
            }

            examEntity.setStatus(status);
            examRepository.save(examEntity);

            return Result.success();
        } catch (Exception e) {
            return Result.error("更新考试状态失败：" + e.getMessage());
        }
    }

    /**
     * 根据学年和学期获取考试列表
     * 
     * @param schoolYear 学年
     * @param semester 学期
     * @return 考试列表
     */
    public Result<List<Exam>> getExamsBySchoolYearAndSemester(String schoolYear, Integer semester) {
        try {
            List<Exam> exams = examRepository.findBySchoolYearAndSemesterAndDeletedOrderByStartDateAsc(
                schoolYear, semester, 0);
            return Result.success(exams);
        } catch (Exception e) {
            return Result.error("获取考试列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取当前进行中的考试
     * 
     * @return 考试列表
     */
    public Result<List<Exam>> getCurrentExams() {
        try {
            LocalDate today = LocalDate.now();
            List<Exam> exams = examRepository.findCurrentExams(today, 0);
            return Result.success(exams);
        } catch (Exception e) {
            return Result.error("获取当前考试列表失败：" + e.getMessage());
        }
    }

    /**
     * 数据验证
     * 
     * @param exam 考试信息
     * @return 验证结果
     */
    private Result<Void> validateExam(Exam exam) {
        if (exam == null) {
            return Result.error("考试信息不能为空");
        }

        if (!StringUtils.hasText(exam.getExamCode())) {
            return Result.error("考试编码不能为空");
        }

        if (exam.getExamCode().length() > 50) {
            return Result.error("考试编码长度不能超过50个字符");
        }

        if (!StringUtils.hasText(exam.getExamName())) {
            return Result.error("考试名称不能为空");
        }

        if (exam.getExamName().length() > 100) {
            return Result.error("考试名称长度不能超过100个字符");
        }

        if (exam.getExamType() == null) {
            return Result.error("考试类型不能为空");
        }

        if (exam.getExamType() < 1 || exam.getExamType() > 5) {
            return Result.error("考试类型值必须在1-5之间");
        }

        if (!StringUtils.hasText(exam.getSchoolYear())) {
            return Result.error("学年不能为空");
        }

        if (exam.getSemester() == null) {
            return Result.error("学期不能为空");
        }

        if (exam.getSemester() < 1 || exam.getSemester() > 2) {
            return Result.error("学期值必须为1或2");
        }

        if (exam.getStartDate() == null) {
            return Result.error("考试开始日期不能为空");
        }

        if (exam.getEndDate() == null) {
            return Result.error("考试结束日期不能为空");
        }

        if (exam.getStartDate().isAfter(exam.getEndDate())) {
            return Result.error("考试开始日期不能晚于结束日期");
        }

        if (!StringUtils.hasText(exam.getGradeIds())) {
            return Result.error("参考年级不能为空");
        }

        if (!StringUtils.hasText(exam.getCourseIds())) {
            return Result.error("考试科目不能为空");
        }

        return Result.success();
    }

    /**
     * 验证年级ID
     * 
     * @param gradeIds 年级ID字符串
     * @return 验证结果
     */
    private Result<Void> validateGradeIds(String gradeIds) {
        try {
            List<Long> ids = Arrays.stream(gradeIds.split(","))
                .map(String::trim)
                .map(Long::valueOf)
                .collect(Collectors.toList());
            
            for (Long gradeId : ids) {
                if (!gradeRepository.existsByIdAndDeleted(gradeId, 0)) {
                    return Result.error("年级ID " + gradeId + " 不存在");
                }
            }
            
            return Result.success();
        } catch (NumberFormatException e) {
            return Result.error("年级ID格式不正确");
        }
    }

    /**
     * 验证课程ID
     * 
     * @param courseIds 课程ID字符串
     * @return 验证结果
     */
    private Result<Void> validateCourseIds(String courseIds) {
        try {
            List<Long> ids = Arrays.stream(courseIds.split(","))
                .map(String::trim)
                .map(Long::valueOf)
                .collect(Collectors.toList());
            
            for (Long courseId : ids) {
                if (!courseRepository.existsByIdAndDeleted(courseId, 0)) {
                    return Result.error("课程ID " + courseId + " 不存在");
                }
            }
            
            return Result.success();
        } catch (NumberFormatException e) {
            return Result.error("课程ID格式不正确");
        }
    }

    /**
     * 验证状态流转是否有效
     * 
     * @param currentStatus 当前状态
     * @param newStatus 新状态
     * @return 是否有效
     */
    private boolean isValidStatusTransition(Integer currentStatus, Integer newStatus) {
        if (currentStatus == null) {
            currentStatus = 0;
        }
        
        // 状态流转规则：0:未开始 -> 1:进行中 -> 2:已结束 -> 3:已发布
        switch (currentStatus) {
            case 0: // 未开始 -> 进行中
                return newStatus == 1;
            case 1: // 进行中 -> 已结束
                return newStatus == 2;
            case 2: // 已结束 -> 已发布
                return newStatus == 3;
            case 3: // 已发布，不能再变更
                return false;
            default:
                return false;
        }
    }
}