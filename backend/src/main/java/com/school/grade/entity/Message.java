package com.school.grade.entity;

import com.school.grade.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 消息通知实体类
 * 
 * @author Qoder
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_message")
public class Message extends BaseEntity {

    /**
     * 消息标题
     */
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    /**
     * 消息内容
     */
    @Lob
    @Column(name = "content", nullable = false)
    private String content;

    /**
     * 消息类型 1:系统通知 2:成绩发布 3:考试通知 4:家长会通知
     */
    @Column(name = "message_type", nullable = false)
    private Integer messageType;

    /**
     * 发送人ID
     */
    @Column(name = "sender_id")
    private Long senderId;

    /**
     * 发送人姓名
     */
    @Column(name = "sender_name", length = 50)
    private String senderName;

    /**
     * 接收对象类型 1:全部 2:角色 3:班级 4:个人
     */
    @Column(name = "target_type", nullable = false)
    private Integer targetType;

    /**
     * 接收对象ID列表
     */
    @Column(name = "target_ids", length = 500)
    private String targetIds;

    /**
     * 发送方式 1:站内信 2:短信 3:微信 4:邮件
     */
    @Column(name = "send_method", nullable = false)
    private Integer sendMethod;

    /**
     * 发送时间
     */
    @Column(name = "send_time")
    private LocalDateTime sendTime;

    /**
     * 状态 0:草稿 1:已发送 2:发送失败
     */
    @Column(name = "status", nullable = false, columnDefinition = "TINYINT DEFAULT 0")
    private Integer status = 0;

    /**
     * 已读人数
     */
    @Column(name = "read_count", columnDefinition = "INT DEFAULT 0")
    private Integer readCount = 0;

    /**
     * 总接收人数
     */
    @Column(name = "total_count", columnDefinition = "INT DEFAULT 0")
    private Integer totalCount = 0;

    /**
     * 消息接收记录（不映射到数据库）
     */
    @Transient
    private List<MessageReceive> receives;

    /**
     * 获取消息类型描述
     */
    @Transient
    public String getMessageTypeText() {
        if (messageType == null) return "";
        switch (messageType) {
            case 1: return "系统通知";
            case 2: return "成绩发布";
            case 3: return "考试通知";
            case 4: return "家长会通知";
            default: return "未知类型";
        }
    }

    /**
     * 获取接收对象类型描述
     */
    @Transient
    public String getTargetTypeText() {
        if (targetType == null) return "";
        switch (targetType) {
            case 1: return "全部用户";
            case 2: return "按角色";
            case 3: return "按班级";
            case 4: return "指定个人";
            default: return "未知类型";
        }
    }

    /**
     * 获取发送方式描述
     */
    @Transient
    public String getSendMethodText() {
        if (sendMethod == null) return "";
        switch (sendMethod) {
            case 1: return "站内信";
            case 2: return "短信";
            case 3: return "微信";
            case 4: return "邮件";
            default: return "未知方式";
        }
    }

    /**
     * 获取状态描述
     */
    @Transient
    public String getStatusText() {
        if (status == null) return "";
        switch (status) {
            case 0: return "草稿";
            case 1: return "已发送";
            case 2: return "发送失败";
            default: return "未知状态";
        }
    }
}