package com.school.grade.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 系统日志实体类
 * 
 * @author Qoder
 * @version 1.0.0
 */
@Data
@Entity
@Table(name = "sys_log")
public class SystemLog {

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 日志类型 1:登录 2:操作 3:错误 4:系统
     */
    @Column(name = "log_type", nullable = false)
    private Integer logType;

    /**
     * 操作内容
     */
    @Column(name = "operation", length = 100)
    private String operation;

    /**
     * 请求方法
     */
    @Column(name = "method", length = 100)
    private String method;

    /**
     * 请求参数
     */
    @Lob
    @Column(name = "params")
    private String params;

    /**
     * 操作结果
     */
    @Lob
    @Column(name = "result")
    private String result;

    /**
     * IP地址
     */
    @Column(name = "ip", length = 50)
    private String ip;

    /**
     * 用户代理
     */
    @Column(name = "user_agent", length = 500)
    private String userAgent;

    /**
     * 操作用户ID
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 用户名
     */
    @Column(name = "username", length = 50)
    private String username;

    /**
     * 执行时间(毫秒)
     */
    @Column(name = "execution_time")
    private Long executionTime;

    /**
     * 状态 0:失败 1:成功
     */
    @Column(name = "status", nullable = false, columnDefinition = "TINYINT DEFAULT 1")
    private Integer status = 1;

    /**
     * 创建时间
     */
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    /**
     * 备注
     */
    @Column(name = "remark", length = 500)
    private String remark;

    /**
     * 获取日志类型描述
     */
    @Transient
    public String getLogTypeText() {
        if (logType == null) return "";
        switch (logType) {
            case 1: return "登录日志";
            case 2: return "操作日志";
            case 3: return "错误日志";
            case 4: return "系统日志";
            default: return "未知类型";
        }
    }

    /**
     * 获取状态描述
     */
    @Transient
    public String getStatusText() {
        if (status == null) return "";
        return status == 1 ? "成功" : "失败";
    }

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
    }
}