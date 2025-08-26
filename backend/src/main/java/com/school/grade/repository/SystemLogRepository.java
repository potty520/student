package com.school.grade.repository;

import com.school.grade.entity.SystemLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统日志数据访问层
 * 
 * @author Qoder
 * @version 1.0.0
 */
@Repository
public interface SystemLogRepository extends JpaRepository<SystemLog, Long>, JpaSpecificationExecutor<SystemLog> {

    /**
     * 根据日志类型查询日志列表
     */
    Page<SystemLog> findByLogTypeOrderByCreateTimeDesc(Integer logType, Pageable pageable);

    /**
     * 根据用户ID查询日志列表
     */
    Page<SystemLog> findByUserIdOrderByCreateTimeDesc(Long userId, Pageable pageable);

    /**
     * 根据用户名查询日志列表
     */
    Page<SystemLog> findByUsernameOrderByCreateTimeDesc(String username, Pageable pageable);

    /**
     * 根据状态查询日志列表
     */
    Page<SystemLog> findByStatusOrderByCreateTimeDesc(Integer status, Pageable pageable);

    /**
     * 查询指定时间范围内的日志
     */
    @Query("SELECT l FROM SystemLog l WHERE l.createTime BETWEEN :startTime AND :endTime ORDER BY l.createTime DESC")
    Page<SystemLog> findByTimeBetween(@Param("startTime") LocalDateTime startTime, 
                                     @Param("endTime") LocalDateTime endTime, 
                                     Pageable pageable);

    /**
     * 统计各类型日志数量
     */
    @Query("SELECT l.logType, COUNT(l) FROM SystemLog l GROUP BY l.logType")
    List<Object[]> countByLogType();

    /**
     * 统计今日日志数量
     */
    @Query("SELECT COUNT(l) FROM SystemLog l WHERE DATE(l.createTime) = CURRENT_DATE")
    Long countTodayLogs();

    /**
     * 统计今日错误日志数量
     */
    @Query("SELECT COUNT(l) FROM SystemLog l WHERE DATE(l.createTime) = CURRENT_DATE AND l.logType = 3")
    Long countTodayErrorLogs();

    /**
     * 统计今日登录日志数量
     */
    @Query("SELECT COUNT(l) FROM SystemLog l WHERE DATE(l.createTime) = CURRENT_DATE AND l.logType = 1")
    Long countTodayLoginLogs();

    /**
     * 获取最近的错误日志
     */
    @Query("SELECT l FROM SystemLog l WHERE l.logType = 3 ORDER BY l.createTime DESC")
    Page<SystemLog> findRecentErrorLogs(Pageable pageable);

    /**
     * 删除指定日期之前的日志
     */
    @Query("DELETE FROM SystemLog l WHERE l.createTime < :beforeDate")
    int deleteLogsBeforeDate(@Param("beforeDate") LocalDateTime beforeDate);
}