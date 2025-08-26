package com.school.grade.service;

import com.school.grade.entity.SystemLog;
import com.school.grade.repository.SystemLogRepository;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统日志业务逻辑层
 * 
 * @author Qoder
 * @version 1.0.0
 */
@Service
@Transactional
public class SystemLogService {

    @Autowired
    private SystemLogRepository systemLogRepository;

    /**
     * 分页查询系统日志
     * 
     * @param page 页码
     * @param size 每页大小
     * @param logType 日志类型
     * @param username 用户名
     * @param status 状态
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分页结果
     */
    public Result<PageResult<SystemLog>> getSystemLogList(int page, int size, Integer logType, 
                                                         String username, Integer status, 
                                                         LocalDateTime startTime, LocalDateTime endTime) {
        try {
            // 创建分页对象
            Pageable pageable = PageRequest.of(page - 1, size, 
                Sort.by(Sort.Direction.DESC, "createTime"));

            // 构建查询条件
            Specification<SystemLog> spec = (root, query, criteriaBuilder) -> {
                List<Predicate> predicates = new ArrayList<>();
                
                // 日志类型查询
                if (logType != null) {
                    predicates.add(criteriaBuilder.equal(root.get("logType"), logType));
                }
                
                // 用户名模糊查询
                if (StringUtils.hasText(username)) {
                    predicates.add(criteriaBuilder.like(root.get("username"), "%" + username + "%"));
                }
                
                // 状态查询
                if (status != null) {
                    predicates.add(criteriaBuilder.equal(root.get("status"), status));
                }
                
                // 时间范围查询
                if (startTime != null) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"), startTime));
                }
                if (endTime != null) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createTime"), endTime));
                }
                
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            };

            Page<SystemLog> logPage = systemLogRepository.findAll(spec, pageable);
            
            PageResult<SystemLog> pageResult = new PageResult<>(
                logPage.getContent(),
                logPage.getTotalElements(),
                page,
                size
            );

            return Result.success(pageResult);
        } catch (Exception e) {
            return Result.error("查询系统日志失败：" + e.getMessage());
        }
    }

    /**
     * 记录系统日志
     * 
     * @param systemLog 日志信息
     * @return 操作结果
     */
    public Result<SystemLog> saveSystemLog(SystemLog systemLog) {
        try {
            SystemLog savedLog = systemLogRepository.save(systemLog);
            return Result.success(savedLog);
        } catch (Exception e) {
            return Result.error("保存系统日志失败：" + e.getMessage());
        }
    }

    /**
     * 记录登录日志
     * 
     * @param userId 用户ID
     * @param username 用户名
     * @param ip IP地址
     * @param userAgent 用户代理
     * @param success 是否成功
     * @param remark 备注
     * @return 操作结果
     */
    public Result<Void> recordLoginLog(Long userId, String username, String ip, String userAgent, 
                                      boolean success, String remark) {
        try {
            SystemLog log = new SystemLog();
            log.setLogType(1); // 登录日志
            log.setOperation("用户登录");
            log.setUserId(userId);
            log.setUsername(username);
            log.setIp(ip);
            log.setUserAgent(userAgent);
            log.setStatus(success ? 1 : 0);
            log.setRemark(remark);
            
            systemLogRepository.save(log);
            return Result.success();
        } catch (Exception e) {
            return Result.error("记录登录日志失败：" + e.getMessage());
        }
    }

    /**
     * 记录操作日志
     * 
     * @param userId 用户ID
     * @param username 用户名
     * @param operation 操作内容
     * @param method 请求方法
     * @param params 请求参数
     * @param result 操作结果
     * @param ip IP地址
     * @param executionTime 执行时间
     * @return 操作结果
     */
    public Result<Void> recordOperationLog(Long userId, String username, String operation, 
                                          String method, String params, String result, 
                                          String ip, Long executionTime) {
        try {
            SystemLog log = new SystemLog();
            log.setLogType(2); // 操作日志
            log.setOperation(operation);
            log.setMethod(method);
            log.setParams(params);
            log.setResult(result);
            log.setUserId(userId);
            log.setUsername(username);
            log.setIp(ip);
            log.setExecutionTime(executionTime);
            log.setStatus(1);
            
            systemLogRepository.save(log);
            return Result.success();
        } catch (Exception e) {
            return Result.error("记录操作日志失败：" + e.getMessage());
        }
    }

    /**
     * 获取日志统计信息
     * 
     * @return 统计信息
     */
    public Result<Map<String, Object>> getLogStatistics() {
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            // 今日日志总数
            Long todayTotal = systemLogRepository.countTodayLogs();
            statistics.put("todayTotal", todayTotal);
            
            // 今日错误日志数
            Long todayErrors = systemLogRepository.countTodayErrorLogs();
            statistics.put("todayErrors", todayErrors);
            
            // 今日登录日志数
            Long todayLogins = systemLogRepository.countTodayLoginLogs();
            statistics.put("todayLogins", todayLogins);
            
            // 各类型日志统计
            List<Object[]> logTypeCounts = systemLogRepository.countByLogType();
            Map<String, Long> typeStatistics = new HashMap<>();
            for (Object[] row : logTypeCounts) {
                Integer logType = (Integer) row[0];
                Long count = (Long) row[1];
                String typeName = getLogTypeName(logType);
                typeStatistics.put(typeName, count);
            }
            statistics.put("typeStatistics", typeStatistics);
            
            return Result.success(statistics);
        } catch (Exception e) {
            return Result.error("获取日志统计失败：" + e.getMessage());
        }
    }

    /**
     * 获取最近的错误日志
     * 
     * @param size 获取数量
     * @return 错误日志列表
     */
    public Result<List<SystemLog>> getRecentErrorLogs(int size) {
        try {
            Pageable pageable = PageRequest.of(0, size);
            Page<SystemLog> logPage = systemLogRepository.findRecentErrorLogs(pageable);
            return Result.success(logPage.getContent());
        } catch (Exception e) {
            return Result.error("获取错误日志失败：" + e.getMessage());
        }
    }

    /**
     * 清理指定日期之前的日志
     * 
     * @param beforeDate 指定日期
     * @return 操作结果
     */
    public Result<Integer> cleanLogsBeforeDate(LocalDateTime beforeDate) {
        try {
            int deletedCount = systemLogRepository.deleteLogsBeforeDate(beforeDate);
            return Result.success(deletedCount);
        } catch (Exception e) {
            return Result.error("清理日志失败：" + e.getMessage());
        }
    }

    /**
     * 获取日志类型名称
     * 
     * @param logType 日志类型
     * @return 类型名称
     */
    private String getLogTypeName(Integer logType) {
        if (logType == null) return "未知";
        switch (logType) {
            case 1: return "登录日志";
            case 2: return "操作日志";
            case 3: return "错误日志";
            case 4: return "系统日志";
            default: return "未知类型";
        }
    }
}