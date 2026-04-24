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

## 打包构建

### 后端打包
```bash
cd backend
mvn -DskipTests clean package
```

产物路径：
`backend/target/grade-management-1.0.0.jar`

### 前端打包
```bash
cd frontend
npm run build
```

产物路径：
`frontend/dist/`

## 生产部署（无域名，使用服务器IP）

以下示例基于当前部署信息：
- 服务器IP：`64.83.43.205`
- 前端目录：`/opt/student/dist`
- Nginx端口：`8088`（非默认80/443）
- 后端端口：`8081`

### 1) 后端后台启动（推荐 systemd）

将后端 jar 上传到服务器，例如：
`/opt/student/backend/grade-management-1.0.0.jar`

创建服务文件：
```bash
sudo tee /etc/systemd/system/student-grade.service > /dev/null <<'EOF'
[Unit]
Description=Student Grade Backend
After=network.target

[Service]
Type=simple
User=root
WorkingDirectory=/opt/student/backend
ExecStart=/usr/bin/java -jar /opt/student/backend/grade-management-1.0.0.jar --server.port=8081
SuccessExitStatus=143
Restart=always
RestartSec=5

[Install]
WantedBy=multi-user.target
EOF
```

启动并设置开机自启：
```bash
sudo systemctl daemon-reload
sudo systemctl enable student-grade
sudo systemctl start student-grade
```

查看状态与日志：
```bash
sudo systemctl status student-grade
sudo journalctl -u student-grade -f
```

### 2) Nginx 配置（conf.d 独立文件）

建议新建：
`/etc/nginx/conf.d/student-grade.conf`

```nginx
server {
    listen 8088;
    server_name 64.83.43.205;

    root /opt/student/dist;
    index index.html;

    # Vue history 路由支持
    location / {
        try_files $uri $uri/ /index.html;
    }

    # 后端 API 代理
    location /api/ {
        proxy_pass http://127.0.0.1:8081/api/;
        proxy_http_version 1.1;

        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

### 3) Nginx 主配置注意事项

`/etc/nginx/nginx.conf` 中：
- 只在 `http {}` 块内保留 `include /etc/nginx/conf.d/*.conf;`
- 不要在顶层（`http {}` 外）写该 include

否则会出现报错：
`"server" directive is not allowed here`

### 4) 重载 Nginx
```bash
sudo nginx -t
sudo systemctl reload nginx
```

### 5) 端口放行

确保安全组/防火墙放行：
- `8088`（前端访问）
- `8081`（后端服务；若仅本机代理可不对公网放行）

### 6) 访问地址

前端入口：
`http://64.83.43.205:8088`

## 数据库配置

请在 `backend/src/main/resources/application.yml` 中配置数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/student_grade_system
    username: your_username
    password: your_password
```

## 开发团队

由AI助手Qoder协助开发

## 许可证

MIT License