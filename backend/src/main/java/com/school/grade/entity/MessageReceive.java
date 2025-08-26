package com.school.grade.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 消息接收实体类
 * 
 * @author Qoder
 * @version 1.0.0
 */
@Data
@Entity
@Table(name = "sys_message_receive")
public class MessageReceive {

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 消息ID
     */
    @Column(name = "message_id", nullable = false)
    private Long messageId;

    /**
     * 接收人ID
     */
    @Column(name = "receiver_id", nullable = false)
    private Long receiverId;

    /**
     * 接收人姓名
     */
    @Column(name = "receiver_name", length = 50)
    private String receiverName;

    /**
     * 是否已读 0:未读 1:已读
     */
    @Column(name = "is_read", nullable = false, columnDefinition = "TINYINT DEFAULT 0")
    private Integer isRead = 0;

    /**
     * 阅读时间
     */
    @Column(name = "read_time")
    private LocalDateTime readTime;

    /**
     * 创建时间
     */
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    /**
     * 消息信息（不映射到数据库）
     */
    @Transient
    private Message message;

    /**
     * 获取是否已读描述
     */
    @Transient
    public String getIsReadText() {
        if (isRead == null) return "";
        return isRead == 1 ? "已读" : "未读";
    }

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
    }
}