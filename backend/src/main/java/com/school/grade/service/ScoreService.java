package com.school.grade.service;

import com.school.grade.entity.Score;
import com.school.grade.entity.Exam;
import com.school.grade.entity.Student;
import com.school.grade.entity.Course;
import com.school.grade.repository.ScoreRepository;
import com.school.grade.repository.ExamRepository;
import com.school.grade.repository.StudentRepository;
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
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 成绩管理业务逻辑层
 * 
 * @author Qoder
 * @version 1.0.0
 */
@Service
@Transactional
public class ScoreService {

    @Autowired
    private ScoreRepository scoreRepository;

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    /**
     * 分页查询成绩信息
     * 
     * @param page 页码
     * @param size 每页大小
     * @param examId 考试ID
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @param classId 班级ID
     * @return 分页结果
     */
    public Result<PageResult<Score>> getScoreList(int page, int size, Long examId, 
                                                 Long studentId, Long courseId, Long classId) {
        try {
            // 创建分页对象
            Pageable pageable = PageRequest.of(page - 1, size, 
                Sort.by(Sort.Direction.DESC, "createTime"));

            // 构建查询条件
            Specification<Score> spec = (root, query, criteriaBuilder) -> {
                List<Predicate> predicates = new ArrayList<>();
                
                // 软删除条件
                predicates.add(criteriaBuilder.equal(root.get("deleted"), 0));
                
                // 考试查询
                if (examId != null) {
                    predicates.add(criteriaBuilder.equal(root.get("examId"), examId));
                }
                
                // 学生查询
                if (studentId != null) {
                    predicates.add(criteriaBuilder.equal(root.get("studentId"), studentId));
                }
                
                // 课程查询
                if (courseId != null) {
                    predicates.add(criteriaBuilder.equal(root.get("courseId"), courseId));
                }
                
                // 班级查询（通过学生表关联）
                if (classId != null) {
                    predicates.add(criteriaBuilder.equal(
                        root.join("student").get("classId"), classId));
                }
                
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            };

            Page<Score> scorePage = scoreRepository.findAll(spec, pageable);
            
            PageResult<Score> pageResult = new PageResult<>(
                scorePage.getContent(),
                scorePage.getTotalElements(),
                page,
                size
            );

            return Result.success(pageResult);
        } catch (Exception e) {
            return Result.error("查询成绩列表失败：" + e.getMessage());
        }
    }

    /**
     * 根据ID获取成绩信息
     * 
     * @param id 成绩ID
     * @return 成绩信息
     */
    public Result<Score> getScoreById(Long id) {
        try {
            Optional<Score> score = scoreRepository.findByIdAndDeleted(id, 0);
            if (score.isPresent()) {
                return Result.success(score.get());
            } else {
                return Result.error("成绩信息不存在");
            }
        } catch (Exception e) {
            return Result.error("获取成绩信息失败：" + e.getMessage());
        }
    }

    /**
     * 添加成绩
     * 
     * @param score 成绩信息
     * @return 操作结果
     */
    public Result<Score> addScore(Score score) {
        try {
            // 数据验证
            Result<Void> validateResult = validateScore(score);
            if (!validateResult.isSuccess()) {
                return Result.error(validateResult.getMessage());
            }

            // 检查成绩是否已存在
            if (scoreRepository.existsByExamIdAndStudentIdAndCourseIdAndDeleted(
                    score.getExamId(), score.getStudentId(), score.getCourseId(), 0)) {
                return Result.error("该学生在此考试中的该科目成绩已存在");
            }

            Score savedScore = scoreRepository.save(score);
            
            // 计算排名
            calculateRankings(score.getExamId(), score.getCourseId());
            
            return Result.success(savedScore);
        } catch (Exception e) {
            return Result.error("添加成绩失败：" + e.getMessage());
        }
    }

    /**
     * 批量录入成绩
     * 
     * @param scores 成绩列表
     * @return 操作结果
     */
    public Result<List<Score>> batchAddScores(List<Score> scores) {
        try {
            if (CollectionUtils.isEmpty(scores)) {
                return Result.error("成绩列表不能为空");
            }

            // 验证所有成绩数据
            for (Score score : scores) {
                Result<Void> validateResult = validateScore(score);
                if (!validateResult.isSuccess()) {
                    return Result.error("第" + (scores.indexOf(score) + 1) + "条记录：" + validateResult.getMessage());
                }
            }

            // 区分新增和更新：有ID的记录为更新，无ID的为新�?
            List<Score> toSave = new ArrayList<>();
            for (Score score : scores) {
                if (score.getId() != null) {
                    // 更新已有记录
                    Optional<Score> existing = scoreRepository.findByIdAndDeleted(score.getId(), 0);
                    if (existing.isPresent()) {
                        Score s = existing.get();
                        s.setScore(score.getScore());
                        s.setAbsent(score.getAbsent());
                        s.setComment(score.getComment());
                        toSave.add(s);
                    }
                } else {
                    // 新增记录，检查是否重复
                    if (scoreRepository.existsByExamIdAndStudentIdAndCourseIdAndDeleted(
                            score.getExamId(), score.getStudentId(), score.getCourseId(), 0)) {
                        continue; // 跳过已存在的记录
                    }
                    toSave.add(score);
                }
            }

            if (toSave.isEmpty()) {
                return Result.success(toSave);
            }

            List<Score> savedScores = scoreRepository.saveAll(toSave);

            // 批量计算排名
            Set<Long> examIds = toSave.stream().map(Score::getExamId).collect(Collectors.toSet());
            Set<Long> courseIds = toSave.stream().map(Score::getCourseId).collect(Collectors.toSet());

            for (Long examId : examIds) {
                for (Long courseId : courseIds) {
                    calculateRankings(examId, courseId);
                }
            }

            return Result.success(savedScores);
        } catch (Exception e) {
            return Result.error("批量录入成绩失败：" + e.getMessage());
        }
    }

    /**
     * 从Excel导入成绩
     */
    public Result<Map<String, Object>> importScoresFromExcel(Long examId, Long courseId, Long classId, MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (file.isEmpty()) {
                return Result.error("上传文件不能为空");
            }

            String filename = file.getOriginalFilename();
            Workbook workbook;
            if (filename != null && filename.endsWith(".xls")) {
                workbook = new HSSFWorkbook(file.getInputStream());
            } else {
                workbook = new XSSFWorkbook(file.getInputStream());
            }

            Sheet sheet = workbook.getSheetAt(0);
            if (sheet.getLastRowNum() < 1) {
                workbook.close();
                return Result.error("Excel文件无数据");
            }

            // 读取表头
            Row headerRow = sheet.getRow(0);
            Map<String, Integer> colMap = new HashMap<>();
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                Cell cell = headerRow.getCell(i);
                if (cell != null) {
                    String val = cell.getStringCellValue().trim();
                    colMap.put(val, i);
                }
            }

            Integer codeCol = colMap.get("学号");
            Integer nameCol = colMap.get("姓名");
            Integer scoreCol = colMap.get("成绩");
            if (codeCol == null) {
                workbook.close();
                return Result.error("Excel缺少'学号'列");
            }

            List<Score> scores = new ArrayList<>();
            List<Map<String, String>> errors = new ArrayList<>();
            int successCount = 0;

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String studentCode = getCellString(row, codeCol);
                if (studentCode == null || studentCode.isEmpty()) continue;

                // 通过学号查找学生
                Optional<Student> studentOpt = studentRepository.findByStudentCodeAndDeleted(studentCode, 0);
                if (!studentOpt.isPresent()) {
                    Map<String, String> err = new HashMap<>();
                    err.put("row", String.valueOf(i + 1));
                    err.put("info", "学号" + studentCode + "不存在");
                    errors.add(err);
                    continue;
                }
                Student student = studentOpt.get();

                Score score = new Score();
                score.setExamId(examId);
                score.setStudentId(student.getId());
                score.setCourseId(courseId);

                // 解析成绩
                if (scoreCol != null) {
                    String scoreStr = getCellString(row, scoreCol);
                    if (scoreStr != null && !scoreStr.isEmpty() && !scoreStr.contains("缺考")) {
                        try {
                            score.setScore(new BigDecimal(scoreStr.trim()));
                            score.setAbsent(0);
                        } catch (NumberFormatException e) {
                            Map<String, String> err = new HashMap<>();
                            err.put("row", String.valueOf(i + 1));
                            err.put("info", "成绩格式错误: " + scoreStr);
                            errors.add(err);
                            continue;
                        }
                    } else {
                        score.setScore(null);
                        score.setAbsent(1);
                    }
                }

                // 解析备注
                Integer remarkCol = colMap.get("备注");
                if (remarkCol != null) {
                    String remark = getCellString(row, remarkCol);
                    if (remark != null && !remark.isEmpty()) {
                        score.setComment(remark);
                    }
                }

                scores.add(score);
                successCount++;
            }

            workbook.close();

            // 保存成绩
            if (!scores.isEmpty()) {
                Result<List<Score>> saveResult = batchAddScores(scores);
                if (!saveResult.isSuccess()) {
                    return Result.error(saveResult.getMessage());
                }
            }

            result.put("total", scores.size() + errors.size());
            result.put("successCount", successCount);
            result.put("errorCount", errors.size());
            result.put("errors", errors);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("Excel导入失败: " + e.getMessage());
        }
    }

    private String getCellString(Row row, int colIndex) {
        Cell cell = row.getCell(colIndex);
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                double d = cell.getNumericCellValue();
                if (d == Math.floor(d) && !Double.isInfinite(d)) {
                    return String.valueOf((long) d);
                }
                return String.valueOf(d);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return String.valueOf(cell.getNumericCellValue());
                } catch (Exception e) {
                    return cell.getStringCellValue().trim();
                }
            default:
                return null;
        }
    }

    /**
     * 下载成绩导入模板
     */
    public void downloadTemplate(Long examId, Long courseId, Long classId, HttpServletResponse response) throws IOException {
        List<Student> students = studentRepository.findByClassIdAndDeleted(classId, 0);
        Course course = courseRepository.findByIdAndDeleted(courseId, 0).orElse(null);
        Exam exam = examRepository.findByIdAndDeleted(examId, 0).orElse(null);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("成绩导入");

        // 表头
        Row header = sheet.createRow(0);
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        String[] headers = {"学号", "姓名", "成绩", "备注"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // 数据行
        for (int i = 0; i < students.size(); i++) {
            Student s = students.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(s.getStudentCode());
            row.createCell(1).setCellValue(s.getStudentName());
            row.createCell(2).setCellValue("");
            row.createCell(3).setCellValue("");
        }

        for (int i = 0; i < 4; i++) sheet.autoSizeColumn(i);

        String filename = "成绩导入模板";
        if (exam != null) filename += "_" + exam.getExamName();
        if (course != null) filename += "_" + course.getCourseName();
        filename += ".xlsx";

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(filename, "UTF-8").replace("+", "%20"));

        OutputStream os = response.getOutputStream();
        workbook.write(os);
        workbook.close();
        os.flush();
    }

    /**
     * 更新成绩信息
     *
     * @param score 成绩信息
     * @return 操作结果
     */
    public Result<Score> updateScore(Score score) {
        try {
            // 检查成绩是否存在
            Optional<Score> existingScore = scoreRepository.findByIdAndDeleted(score.getId(), 0);
            if (!existingScore.isPresent()) {
                return Result.error("成绩信息不存在");
            }

            // 数据验证
            Result<Void> validateResult = validateScore(score);
            if (!validateResult.isSuccess()) {
                return Result.error(validateResult.getMessage());
            }

            Score savedScore = scoreRepository.save(score);
            
            // 重新计算排名
            calculateRankings(score.getExamId(), score.getCourseId());
            
            return Result.success(savedScore);
        } catch (Exception e) {
            return Result.error("更新成绩信息失败：" + e.getMessage());
        }
    }

    /**
     * 删除成绩（软删除）
     * 
     * @param id 成绩ID
     * @return 操作结果
     */
    public Result<Void> deleteScore(Long id) {
        try {
            Optional<Score> score = scoreRepository.findByIdAndDeleted(id, 0);
            if (!score.isPresent()) {
                return Result.error("成绩信息不存在");
            }

            Score scoreEntity = score.get();
            scoreEntity.setDeleted(1);
            scoreRepository.save(scoreEntity);

            // 重新计算排名
            calculateRankings(scoreEntity.getExamId(), scoreEntity.getCourseId());

            return Result.success();
        } catch (Exception e) {
            return Result.error("删除成绩失败：" + e.getMessage());
        }
    }

    /**
     * 根据考试和课程获取成绩列表
     * 
     * @param examId 考试ID
     * @param courseId 课程ID
     * @return 成绩列表
     */
    public Result<List<Score>> getScoresByExamAndCourse(Long examId, Long courseId) {
        try {
            List<Score> scores = scoreRepository.findByExamIdAndCourseIdAndDeletedOrderByScoreDesc(
                examId, courseId, 0);
            return Result.success(scores);
        } catch (Exception e) {
            return Result.error("获取成绩列表失败：" + e.getMessage());
        }
    }

    /**
     * 根据学生和考试获取成绩列表
     * 
     * @param studentId 学生ID
     * @param examId 考试ID
     * @return 成绩列表
     */
    public Result<List<Score>> getScoresByStudentAndExam(Long studentId, Long examId) {
        try {
            List<Score> scores = scoreRepository.findByStudentIdAndExamIdAndDeleted(studentId, examId, 0);
            return Result.success(scores);
        } catch (Exception e) {
            return Result.error("获取学生成绩失败：" + e.getMessage());
        }
    }

    /**
     * 成绩统计分析
     * 
     * @param examId 考试ID
     * @param courseId 课程ID
     * @param classId 班级ID（可选）
     * @return 统计结果
     */
    public Result<Map<String, Object>> getScoreStatistics(Long examId, Long courseId, Long classId) {
        try {
            List<Score> scores;
            
            if (classId != null) {
                // 班级统计
                scores = scoreRepository.findByExamIdAndCourseIdAndClassIdAndDeleted(
                    examId, courseId, classId, 0);
            } else {
                // 年级统计
                scores = scoreRepository.findByExamIdAndCourseIdAndDeletedOrderByScoreDesc(
                    examId, courseId, 0);
            }

            if (scores.isEmpty()) {
                return Result.error("没有找到相关成绩数据");
            }

            Map<String, Object> statistics = calculateStatistics(scores, courseId);
            return Result.success(statistics);
        } catch (Exception e) {
            return Result.error("获取成绩统计失败：" + e.getMessage());
        }
    }

    /**
     * 计算排名
     * 
     * @param examId 考试ID
     * @param courseId 课程ID
     */
    private void calculateRankings(Long examId, Long courseId) {
        try {
            // 获取班级排名
            List<Score> classScores = scoreRepository.findByExamIdAndCourseIdAndDeletedOrderByScoreDesc(
                examId, courseId, 0);
            
            Map<Long, List<Score>> classScoreMap = classScores.stream()
                .collect(Collectors.groupingBy(score -> {
                    // 这里需要通过学生获取班级ID，简化处理
                    Optional<Student> student = studentRepository.findById(score.getStudentId());
                    return student.map(Student::getClassId).orElse(0L);
                }));

            // 计算班级排名
            classScoreMap.forEach((classId, scoreList) -> {
                scoreList.sort((s1, s2) -> {
                    if (s1.getScore() == null && s2.getScore() == null) return 0;
                    if (s1.getScore() == null) return 1;
                    if (s2.getScore() == null) return -1;
                    return s2.getScore().compareTo(s1.getScore());
                });
                
                for (int i = 0; i < scoreList.size(); i++) {
                    Score score = scoreList.get(i);
                    score.setClassRank(i + 1);
                }
            });

            // 计算年级排名
            for (int i = 0; i < classScores.size(); i++) {
                Score score = classScores.get(i);
                score.setGradeRank(i + 1);
            }

            scoreRepository.saveAll(classScores);
        } catch (Exception e) {
            // 记录日志，但不抛出异常影响主业务
            e.printStackTrace();
        }
    }

    /**
     * 计算统计数据
     * 
     * @param scores 成绩列表
     * @param courseId 课程ID
     * @return 统计结果
     */
    private Map<String, Object> calculateStatistics(List<Score> scores, Long courseId) {
        Map<String, Object> statistics = new HashMap<>();
        
        // 获取课程信息
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        if (!courseOpt.isPresent()) {
            return statistics;
        }
        
        Course course = courseOpt.get();
        BigDecimal passScore = course.getPassScore();
        BigDecimal goodScore = course.getGoodScore();
        BigDecimal excellentScore = course.getExcellentScore();

        // 基本统计
        int totalCount = scores.size();
        int validCount = (int) scores.stream().filter(s -> s.getScore() != null && s.getAbsent() != null && s.getAbsent() == 0).count();
        int absentCount = (int) scores.stream().filter(s -> s.getAbsent() != null && s.getAbsent() == 1).count();

        statistics.put("totalCount", totalCount);
        statistics.put("validCount", validCount);
        statistics.put("absentCount", absentCount);

        if (validCount == 0) {
            return statistics;
        }

        // 分数统计
        List<BigDecimal> validScores = scores.stream()
            .filter(s -> s.getScore() != null && s.getAbsent() != null && s.getAbsent() == 0)
            .map(Score::getScore)
            .sorted()
            .collect(Collectors.toList());

        BigDecimal maxScore = validScores.get(validScores.size() - 1);
        BigDecimal minScore = validScores.get(0);
        BigDecimal avgScore = validScores.stream()
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .divide(BigDecimal.valueOf(validCount), 2, RoundingMode.HALF_UP);

        statistics.put("maxScore", maxScore);
        statistics.put("minScore", minScore);
        statistics.put("avgScore", avgScore);

        // 三率统计
        if (passScore != null) {
            int passCount = (int) validScores.stream()
                .filter(score -> score.compareTo(passScore) >= 0)
                .count();
            BigDecimal passRate = BigDecimal.valueOf(passCount)
                .divide(BigDecimal.valueOf(validCount), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
            
            statistics.put("passCount", passCount);
            statistics.put("passRate", passRate);
        }

        if (goodScore != null) {
            int goodCount = (int) validScores.stream()
                .filter(score -> score.compareTo(goodScore) >= 0)
                .count();
            BigDecimal goodRate = BigDecimal.valueOf(goodCount)
                .divide(BigDecimal.valueOf(validCount), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
            
            statistics.put("goodCount", goodCount);
            statistics.put("goodRate", goodRate);
        }

        if (excellentScore != null) {
            int excellentCount = (int) validScores.stream()
                .filter(score -> score.compareTo(excellentScore) >= 0)
                .count();
            BigDecimal excellentRate = BigDecimal.valueOf(excellentCount)
                .divide(BigDecimal.valueOf(validCount), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
            
            statistics.put("excellentCount", excellentCount);
            statistics.put("excellentRate", excellentRate);
        }

        return statistics;
    }

    /**
     * 获取学生所有成绩
     * 
     * @param studentId 学生ID
     * @return 成绩列表
     */
    public Result<List<Score>> getStudentAllScores(Long studentId) {
        try {
            List<Score> scores = scoreRepository.findByStudentIdAndExamIdAndDeleted(studentId, null, 0);
            return Result.success(scores);
        } catch (Exception e) {
            return Result.error("获取学生所有成绩失败：" + e.getMessage());
        }
    }

    /**
     * 获取班级成绩列表
     * 
     * @param classId 班级ID
     * @param examId 考试ID
     * @return 成绩列表
     */
    public Result<List<Score>> getClassScoreList(Long classId, Long examId) {
        try {
            List<Score> scores = scoreRepository.findByExamIdAndCourseIdAndClassIdAndDeleted(examId, null, classId, 0);
            return Result.success(scores);
        } catch (Exception e) {
            return Result.error("获取班级成绩列表失败：" + e.getMessage());
        }
    }

    /**
     * 数据验证
     * 
     * @param score 成绩信息
     * @return 验证结果
     */
    private Result<Void> validateScore(Score score) {
        if (score == null) {
            return Result.error("成绩信息不能为空");
        }

        if (score.getExamId() == null) {
            return Result.error("考试ID不能为空");
        }

        if (score.getStudentId() == null) {
            return Result.error("学生ID不能为空");
        }

        if (score.getCourseId() == null) {
            return Result.error("课程ID不能为空");
        }

        // 验证考试是否存在
        if (!examRepository.existsByIdAndDeleted(score.getExamId(), 0)) {
            return Result.error("考试不存在");
        }

        // 验证学生是否存在
        if (!studentRepository.existsByIdAndDeleted(score.getStudentId(), 0)) {
            return Result.error("学生不存在");
        }

        // 验证课程是否存在
        if (!courseRepository.existsByIdAndDeleted(score.getCourseId(), 0)) {
            return Result.error("课程不存在");
        }

        // 验证分数
        if (score.getAbsent() != null && score.getAbsent() == 0 && score.getScore() != null) {
            if (score.getScore().compareTo(BigDecimal.ZERO) < 0) {
                return Result.error("分数不能为负数");
            }
            
            // 获取课程满分进行验证
            Optional<Course> courseOpt = courseRepository.findById(score.getCourseId());
            if (courseOpt.isPresent()) {
                Course course = courseOpt.get();
                if (score.getScore().compareTo(course.getFullScore()) > 0) {
                    return Result.error("分数不能超过满分");
                }
            }
        }

        return Result.success();
    }
}