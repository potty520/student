package com.school.grade.controller;

import com.school.grade.common.result.Result;
import com.school.grade.service.ScreenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "数据大屏")
@RestController
@RequestMapping("/screen")
@CrossOrigin(origins = "*")
public class ScreenController {

    @Autowired
    private ScreenService screenService;

    @Operation(summary = "获取大屏展示数据")
    @GetMapping("/data")
    public Result<Map<String, Object>> getScreenData(
            @Parameter(description = "考试ID") @RequestParam(required = false) Long examId,
            @Parameter(description = "课程ID") @RequestParam(required = false) Long courseId) {
        return Result.success(screenService.getScreenData(examId, courseId));
    }
}
