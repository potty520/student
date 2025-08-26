package com.school.grade.repository;

import com.school.grade.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 消息通知数据访问层
 * 
 * @author Qoder
 * @version 1.0.0
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Long>, JpaSpecificationExecutor<Message> {

    /**
     * 根据ID和删除标记查询消息
     */
    Optional<Message> findByIdAndDeleted(Long id, Integer deleted);

    /**
     * 根据发送人ID查询消息列表
     */
    List<Message> findBySenderIdAndDeletedOrderByCreateTimeDesc(Long senderId, Integer deleted);

    /**
     * 根据消息类型查询消息列表
     */
    List<Message> findByMessageTypeAndDeletedOrderByCreateTimeDesc(Integer messageType, Integer deleted);

    /**
     * 根据状态查询消息列表
     */
    List<Message> findByStatusAndDeletedOrderByCreateTimeDesc(Integer status, Integer deleted);

    /**
     * 查询指定时间范围内的消息
     */
    @Query("SELECT m FROM Message m WHERE m.sendTime BETWEEN :startTime AND :endTime AND m.deleted = :deleted ORDER BY m.sendTime DESC")
    List<Message> findByTimeBetween(@Param("startTime") LocalDateTime startTime, 
                                   @Param("endTime") LocalDateTime endTime, 
                                   @Param("deleted") Integer deleted);

    /**
     * 查询用户接收的消息（根据接收对象类型和目标ID）
     */
    @Query("SELECT m FROM Message m WHERE m.deleted = 0 AND m.status = 1 AND " +
           "((m.targetType = 1) OR " +
           "(m.targetType = 4 AND FIND_IN_SET(:userId, m.targetIds) > 0)) " +
           "ORDER BY m.sendTime DESC")
    Page<Message> findUserMessages(@Param("userId") Long userId, Pageable pageable);

    /**
     * 查询角色接收的消息
     */
    @Query("SELECT m FROM Message m WHERE m.deleted = 0 AND m.status = 1 AND " +
           "m.targetType = 2 AND FIND_IN_SET(:roleId, m.targetIds) > 0 " +
           "ORDER BY m.sendTime DESC")
    Page<Message> findRoleMessages(@Param("roleId") Long roleId, Pageable pageable);

    /**
     * 查询班级接收的消息
     */
    @Query("SELECT m FROM Message m WHERE m.deleted = 0 AND m.status = 1 AND " +
           "m.targetType = 3 AND FIND_IN_SET(:classId, m.targetIds) > 0 " +
           "ORDER BY m.sendTime DESC")
    Page<Message> findClassMessages(@Param("classId") Long classId, Pageable pageable);

    /**
     * 统计草稿消息数量
     */
    @Query("SELECT COUNT(m) FROM Message m WHERE m.senderId = :senderId AND m.status = 0 AND m.deleted = 0")
    Long countDraftsByUser(@Param("senderId") Long senderId);

    /**
     * 统计已发送消息数量
     */
    @Query("SELECT COUNT(m) FROM Message m WHERE m.senderId = :senderId AND m.status = 1 AND m.deleted = 0")
    Long countSentByUser(@Param("senderId") Long senderId);

    /**
     * 查询最近的系统通知
     */
    @Query("SELECT m FROM Message m WHERE m.messageType = 1 AND m.status = 1 AND m.deleted = 0 " +
           "ORDER BY m.sendTime DESC")
    Page<Message> findRecentSystemNotices(Pageable pageable);
}