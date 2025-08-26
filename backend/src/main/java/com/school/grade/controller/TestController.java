package com.school.grade.controller;

import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 系统测试控制�?
 * 
 * @author Qoder
 * @version 1.0.0
 */
@RestController
@RequestMapping("/test")
@CrossOrigin(origins = "*")
public class TestController {

    /**
     * 健康检查接�?
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "ok");
        result.put("message", "学生成绩管理系统运行正常");
        result.put("timestamp", System.currentTimeMillis());
        return result;
    }

    /**
     * 系统信息接口
     */
    @GetMapping("/info")
    public Map<String, Object> info() {
        Map<String, Object> result = new HashMap<>();
        result.put("system", "学生成绩管理系统");
        result.put("version", "1.0.0");
        result.put("description", "中小学学生成绩管理系统");
        result.put("framework", "Spring Boot 2.7");
        return result;
    }

    /**
     * 简单的回显接口
     */
    @PostMapping("/echo")
    public Map<String, Object> echo(@RequestBody Map<String, Object> data) {
        Map<String, Object> result = new HashMap<>();
        result.put("received", data);
        result.put("timestamp", System.currentTimeMillis());
        return result;
    }
}
