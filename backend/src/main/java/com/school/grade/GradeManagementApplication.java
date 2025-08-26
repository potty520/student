package com.school.grade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 学生成绩管理系统启动类
 * 
 * @author Qoder
 * @version 1.0.0
 */
@SpringBootApplication
public class GradeManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(GradeManagementApplication.class, args);
        System.out.println("=======================================================");
        System.out.println("        学生成绩管理系统启动成功!");
        System.out.println("        访问地址: http://localhost:8080");
        System.out.println("=======================================================");
    }
}