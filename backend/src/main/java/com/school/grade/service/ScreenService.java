package com.school.grade.service;

import com.school.grade.entity.Course;
import com.school.grade.entity.Exam;
import com.school.grade.entity.SchoolClass;
import com.school.grade.entity.Score;
import com.school.grade.entity.Student;
import com.school.grade.repository.CourseRepository;
import com.school.grade.repository.ExamRepository;
import com.school.grade.repository.SchoolClassRepository;
import com.school.grade.repository.ScoreRepository;
import com.school.grade.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ScreenService {

    @Autowired
    private ScoreRepository scoreRepository;

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private SchoolClassRepository schoolClassRepository;

    public Map<String, Object> getScreenData(Long examId, Long courseId) {
        Map<String, Object> result = new HashMap<>();

        // Overview - always available
        result.put("overview", buildOverview());

        // Exam-dependent sections
        if (examId != null && courseId != null) {
            List<Score> scores = scoreRepository.findByExamIdAndCourseIdAndDeletedOrderByScoreDesc(examId, courseId, 0);
            List<Score> validScores = scores.stream()
                    .filter(s -> s.getAbsent() != 1 && s.getScore() != null)
                    .collect(Collectors.toList());

            Course course = courseRepository.findByIdAndDeleted(courseId, 0).orElse(null);
            if (course != null) {
                result.put("scoreDistribution", buildScoreDistribution(validScores));
                result.put("rateAnalysis", buildRateAnalysis(validScores, course));
            }

            result.put("classComparison", buildClassComparison(examId, courseId));
            result.put("topStudents", buildTopStudents(examId, courseId));
        }

        // Recent trend - needs courseId only
        if (courseId != null) {
            result.put("recentTrend", buildRecentTrend(courseId));
        }

        return result;
    }

    private Map<String, Object> buildOverview() {
        Map<String, Object> overview = new HashMap<>();
        overview.put("totalStudents", studentRepository.count());
        overview.put("totalExams", examRepository.count());
        overview.put("totalClasses", schoolClassRepository.count());
        overview.put("totalCourses", courseRepository.count());
        return overview;
    }

    private List<Map<String, Object>> buildScoreDistribution(List<Score> validScores) {
        int[] buckets = new int[5];
        String[] labels = {"0-59", "60-69", "70-79", "80-89", "90-100"};

        for (Score s : validScores) {
            BigDecimal score = s.getScore();
            if (score == null) continue;
            int v = score.intValue();
            if (v < 60) buckets[0]++;
            else if (v < 70) buckets[1]++;
            else if (v < 80) buckets[2]++;
            else if (v < 90) buckets[3]++;
            else buckets[4]++;
        }

        List<Map<String, Object>> distribution = new ArrayList<>();
        for (int i = 0; i < buckets.length; i++) {
            Map<String, Object> seg = new HashMap<>();
            seg.put("segment", i);
            seg.put("label", labels[i]);
            seg.put("count", buckets[i]);
            distribution.add(seg);
        }
        return distribution;
    }

    private Map<String, Object> buildRateAnalysis(List<Score> validScores, Course course) {
        Map<String, Object> rates = new HashMap<>();
        int total = validScores.size();
        rates.put("totalCount", total);

        if (total == 0) {
            rates.put("passCount", 0);
            rates.put("goodCount", 0);
            rates.put("excellentCount", 0);
            rates.put("passRate", BigDecimal.ZERO);
            rates.put("goodRate", BigDecimal.ZERO);
            rates.put("excellentRate", BigDecimal.ZERO);
            return rates;
        }

        BigDecimal passLine = course.getPassScore() != null ? course.getPassScore() : new BigDecimal("60");
        BigDecimal goodLine = course.getGoodScore() != null ? course.getGoodScore() : new BigDecimal("80");
        BigDecimal excellentLine = course.getExcellentScore() != null ? course.getExcellentScore() : new BigDecimal("90");

        int passCount = 0, goodCount = 0, excellentCount = 0;
        for (Score s : validScores) {
            BigDecimal sc = s.getScore();
            if (sc == null) continue;
            if (sc.compareTo(passLine) >= 0) passCount++;
            if (sc.compareTo(goodLine) >= 0) goodCount++;
            if (sc.compareTo(excellentLine) >= 0) excellentCount++;
        }

        rates.put("passCount", passCount);
        rates.put("goodCount", goodCount);
        rates.put("excellentCount", excellentCount);
        rates.put("passRate", BigDecimal.valueOf(passCount * 100.0 / total).setScale(1, RoundingMode.HALF_UP));
        rates.put("goodRate", BigDecimal.valueOf(goodCount * 100.0 / total).setScale(1, RoundingMode.HALF_UP));
        rates.put("excellentRate", BigDecimal.valueOf(excellentCount * 100.0 / total).setScale(1, RoundingMode.HALF_UP));

        // 返回分数线阈值，供前端动态适配仪表盘
        rates.put("fullScore", course.getFullScore() != null ? course.getFullScore() : new BigDecimal("100"));
        rates.put("passScore", passLine);
        rates.put("goodScore", goodLine);
        rates.put("excellentScore", excellentLine);
        return rates;
    }

    private List<Map<String, Object>> buildClassComparison(Long examId, Long courseId) {
        List<Object[]> rows = scoreRepository.findClassScoreComparison(examId, courseId);
        List<Map<String, Object>> comparison = new ArrayList<>();

        for (int i = 0; i < rows.size() && i < 10; i++) {
            Object[] row = rows.get(i);
            Map<String, Object> item = new LinkedHashMap<>();
            Long classId = (Long) row[0];
            item.put("classId", classId);
            item.put("avgScore", row[1] != null ? new BigDecimal(row[1].toString()).setScale(1, RoundingMode.HALF_UP) : BigDecimal.ZERO);
            item.put("maxScore", row[2] != null ? new BigDecimal(row[2].toString()) : BigDecimal.ZERO);
            item.put("minScore", row[3] != null ? new BigDecimal(row[3].toString()) : BigDecimal.ZERO);
            item.put("studentCount", row[4] != null ? ((Number) row[4]).intValue() : 0);

            schoolClassRepository.findByIdAndDeleted(classId, 0).ifPresent(c -> item.put("className", c.getClassName()));
            if (!item.containsKey("className")) item.put("className", "班级" + classId);

            comparison.add(item);
        }
        return comparison;
    }

    private List<Map<String, Object>> buildTopStudents(Long examId, Long courseId) {
        List<Score> scores = scoreRepository.findByExamIdAndCourseIdWithStudent(examId, courseId, 0);
        List<Map<String, Object>> top = new ArrayList<>();

        for (int i = 0; i < scores.size() && i < 10; i++) {
            Score s = scores.get(i);
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("rank", i + 1);
            item.put("score", s.getScore());
            item.put("studentName", s.getStudent() != null ? s.getStudent().getStudentName() : "未知");
            item.put("className", s.getStudent() != null && s.getStudent().getSchoolClass() != null
                    ? s.getStudent().getSchoolClass().getClassName() : "");
            top.add(item);
        }
        return top;
    }

    private List<Map<String, Object>> buildRecentTrend(Long courseId) {
        List<Exam> completedExams = examRepository.findByStatusAndDeletedOrderByStartDateDesc(2, 0);
        if (completedExams.size() > 8) completedExams = completedExams.subList(0, 8);

        List<Long> examIds = completedExams.stream().map(Exam::getId).collect(Collectors.toList());
        List<Map<String, Object>> trend = new ArrayList<>();

        if (examIds.isEmpty()) return trend;

        List<Object[]> rows = scoreRepository.findAvgScoreByExams(courseId, examIds);
        Map<Long, BigDecimal> examAvgMap = new HashMap<>();
        for (Object[] row : rows) {
            examAvgMap.put((Long) row[0], new BigDecimal(row[1].toString()).setScale(1, RoundingMode.HALF_UP));
        }

        for (int i = completedExams.size() - 1; i >= 0; i--) {
            Exam e = completedExams.get(i);
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("examId", e.getId());
            item.put("examName", e.getExamName());
            item.put("examDate", e.getStartDate() != null ? e.getStartDate().toString() : "");
            item.put("avgScore", examAvgMap.getOrDefault(e.getId(), BigDecimal.ZERO));
            trend.add(item);
        }
        return trend;
    }
}
