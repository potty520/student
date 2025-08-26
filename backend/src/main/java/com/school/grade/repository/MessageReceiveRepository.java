package com.school.grade.repository;

import com.school.grade.entity.MessageReceive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 消息接收数据访问层
 * 
 * @author Qoder
 * @version 1.0.0
 */
@Repository
public interface MessageReceiveRepository extends JpaRepository<MessageReceive, Long> {

    /**
     * 根据消息ID和接收人ID查询接收记录
     */
    Optional<MessageReceive> findByMessageIdAndReceiverId(Long messageId, Long receiverId);

    /**
     * 根据消息ID查询所有接收记录
     */
    List<MessageReceive> findByMessageIdOrderByCreateTimeDesc(Long messageId);

    /**
     * 根据接收人ID查询接收记录
     */
    Page<MessageReceive> findByReceiverIdOrderByCreateTimeDesc(Long receiverId, Pageable pageable);

    /**
     * 根据接收人ID和是否已读查询接收记录
     */
    Page<MessageReceive> findByReceiverIdAndIsReadOrderByCreateTimeDesc(Long receiverId, Integer isRead, Pageable pageable);

    /**
     * 统计用户未读消息数量
     */
    @Query("SELECT COUNT(mr) FROM MessageReceive mr WHERE mr.receiverId = :receiverId AND mr.isRead = 0")
    Long countUnreadByUser(@Param("receiverId") Long receiverId);

    /**
     * 统计消息的已读人数
     */
    @Query("SELECT COUNT(mr) FROM MessageReceive mr WHERE mr.messageId = :messageId AND mr.isRead = 1")
    Long countReadByMessage(@Param("messageId") Long messageId);

    /**
     * 统计消息的总接收人数
     */
    @Query("SELECT COUNT(mr) FROM MessageReceive mr WHERE mr.messageId = :messageId")
    Long countTotalByMessage(@Param("messageId") Long messageId);

    /**
     * 标记消息为已读
     */
    @Modifying
    @Query("UPDATE MessageReceive mr SET mr.isRead = 1, mr.readTime = CURRENT_TIMESTAMP " +
           "WHERE mr.messageId = :messageId AND mr.receiverId = :receiverId AND mr.isRead = 0")
    int markAsRead(@Param("messageId") Long messageId, @Param("receiverId") Long receiverId);

    /**
     * 批量标记用户的消息为已读
     */
    @Modifying
    @Query("UPDATE MessageReceive mr SET mr.isRead = 1, mr.readTime = CURRENT_TIMESTAMP " +
           "WHERE mr.receiverId = :receiverId AND mr.isRead = 0")
    int markAllAsReadByUser(@Param("receiverId") Long receiverId);

    /**
     * 删除消息对应的所有接收记录
     */
    @Modifying
    @Query("DELETE FROM MessageReceive mr WHERE mr.messageId = :messageId")
    int deleteByMessageId(@Param("messageId") Long messageId);

    /**
     * 查询用户的未读消息接收记录
     */
    @Query("SELECT mr FROM MessageReceive mr JOIN Message m ON mr.messageId = m.id " +
           "WHERE mr.receiverId = :receiverId AND mr.isRead = 0 AND m.deleted = 0 " +
           "ORDER BY mr.createTime DESC")
    Page<MessageReceive> findUnreadMessagesWithDetails(@Param("receiverId") Long receiverId, Pageable pageable);

    /**
     * 查询用户的已读消息接收记录
     */
    @Query("SELECT mr FROM MessageReceive mr JOIN Message m ON mr.messageId = m.id " +
           "WHERE mr.receiverId = :receiverId AND mr.isRead = 1 AND m.deleted = 0 " +
           "ORDER BY mr.readTime DESC")
    Page<MessageReceive> findReadMessagesWithDetails(@Param("receiverId") Long receiverId, Pageable pageable);
}