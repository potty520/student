# 中小学学生成绩管理系统

## 项目概述

本系统是一个基于Vue + Spring Boot + MySQL的中小学学生成绩管理系统，旨在提高学校教务管理效率，为教师、学生和家长提供便捷的成绩管理和查询服务。

## 技术栈

- **前端**: Vue 3 + Element Plus + Vue Router + Axios
- **后端**: Spring Boot 2.7 + Spring Security + Spring Data JPA + MySQL
- **数据库**: MySQL 5.7.44

## 项目结构

```
学生成绩/
├── backend/           # Spring Boot后端项目
├── frontend/          # Vue前端项目
├── sql/              # 数据库脚本
├── docs/             # 项目文档
└── README.md         # 项目说明
```

## 主要功能模块

### 1. 基础信息管理模块 (FM)
- FM01: 年级班级管理
- FM02: 学生信息管理  
- FM03: 教师信息管理
- FM04: 课程科目管理

### 2. 考务与成绩管理模块 (GM)
- GM01: 考试管理
- GM02: 成绩录入
- GM03: 成绩计算与统计
- GM04: 成绩分析报告

### 3. 系统与权限管理模块 (SM)
- SM01: 用户权限管理 (RBAC)
- SM02: 系统日志
- SM03: 数据安全

### 4. 门户与通知模块 (PM)
- PM01: 角色门户
- PM02: 消息通知

## 系统角色

- **系统管理员**: 系统维护、用户权限分配
- **教务人员**: 年级班级课程管理、考试安排
- **班主任**: 管理本班学生、查看班级成绩
- **科任老师**: 录入成绩、查看教学分析
- **校领导**: 查看全校统计分析
- **学生**: 查看个人成绩
- **家长**: 查看子女成绩

## 快速开始

### 环境要求
- JDK 8+
- Node.js 14+
- MySQL 5.7+

### 后端启动
```bash
cd backend
mvn spring-boot:run
```

### 前端启动
```bash
cd frontend  
npm install
npm run dev
```

### 数据库初始化
```bash
mysql -u root -p < sql/init.sql
```

## 数据库配置

- 服务器: 1.95.34.240:3306
- 数据库: student_grade_system
- 用户名: root
- 密码: Bonc@1997#!

## 开发团队

由AI助手Qoder协助开发

## 许可证

MIT License