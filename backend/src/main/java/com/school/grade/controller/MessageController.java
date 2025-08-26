package com.school.grade.controller;

import com.school.grade.entity.Message;
import com.school.grade.entity.MessageReceive;
import com.school.grade.service.MessageService;
import com.school.grade.common.result.Result;
import com.school.grade.common.result.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 消息管理控制器
 * 
 * @author Qoder
 * @version 1.0.0
 */
@Tag(name = "消息管理", description = "消息发送、接收、查看等接口")
@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*")
public class MessageController {

    @Autowired
    private MessageService messageService;

    /**
     * 分页查询消息列表
     */
    @Operation(summary = "分页查询消息列表")
    @GetMapping("/list")
    public Result<PageResult<Message>> getMessageList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "消息标题") @RequestParam(required = false) String title,
            @Parameter(description = "消息类型") @RequestParam(required = false) Integer messageType,
            @Parameter(description = "发送状态") @RequestParam(required = false) Integer status,
            @Parameter(description = "发送人ID") @RequestParam(required = false) Long senderId) {
        return messageService.getMessageList(page, size, title, messageType, status, senderId);
    }

    /**
     * 根据ID获取消息信息
     */
    @Operation(summary = "根据ID获取消息信息")
    @GetMapping("/{id}")
    public Result<Message> getMessageById(@Parameter(description = "消息ID") @PathVariable Long id) {
        return messageService.getMessageById(id);
    }

    /**
     * 创建消息
     */
    @Operation(summary = "创建消息")
    @PostMapping
    public Result<Message> createMessage(@Parameter(description = "消息信息") @Valid @RequestBody Message message) {
        return messageService.createMessage(message);
    }

    /**
     * 更新消息信息
     */
    @Operation(summary = "更新消息信息")
    @PutMapping("/{id}")
    public Result<Message> updateMessage(
            @Parameter(description = "消息ID") @PathVariable Long id,
            @Parameter(description = "消息信息") @Valid @RequestBody Message message) {
        message.setId(id);
        return messageService.updateMessage(message);
    }

    /**
     * 发送消息
     */
    @Operation(summary = "发送消息")
    @PostMapping("/{id}/send")
    public Result<Void> sendMessage(@Parameter(description = "消息ID") @PathVariable Long id) {
        return messageService.sendMessage(id);
    }

    /**
     * 删除消息
     */
    @Operation(summary = "删除消息")
    @DeleteMapping("/{id}")
    public Result<Void> deleteMessage(@Parameter(description = "消息ID") @PathVariable Long id) {
        return messageService.deleteMessage(id);
    }

    /**
     * 获取用户消息列表
     */
    @Operation(summary = "获取用户消息列表")
    @GetMapping("/user/{userId}")
    public Result<PageResult<MessageReceive>> getUserMessages(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "是否已读") @RequestParam(required = false) Integer isRead) {
        return messageService.getUserMessages(userId, page, size, isRead);
    }

    /**
     * 标记消息为已读
     */
    @Operation(summary = "标记消息为已读")
    @PostMapping("/{messageId}/read/{userId}")
    public Result<Void> markAsRead(
            @Parameter(description = "消息ID") @PathVariable Long messageId,
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        return messageService.markAsRead(messageId, userId);
    }

    /**
     * 标记所有消息为已读
     */
    @Operation(summary = "标记所有消息为已读")
    @PostMapping("/read-all/{userId}")
    public Result<Void> markAllAsRead(@Parameter(description = "用户ID") @PathVariable Long userId) {
        return messageService.markAllAsRead(userId);
    }

    /**
     * 获取未读消息数量
     */
    @Operation(summary = "获取未读消息数量")
    @GetMapping("/unread-count/{userId}")
    public Result<Long> getUnreadCount(@Parameter(description = "用户ID") @PathVariable Long userId) {
        return messageService.getUnreadCount(userId);
    }

    /**
     * 获取最近系统通知
     */
    @Operation(summary = "获取最近系统通知")
    @GetMapping("/system-notices")
    public Result<List<Message>> getRecentSystemNotices(
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "10") int limit) {
        return messageService.getRecentSystemNotices(limit);
    }

    /**
     * 发送系统通知
     */
    @Operation(summary = "发送系统通知")
    @PostMapping("/system-notice")
    public Result<Void> sendSystemNotice(
            @Parameter(description = "标题") @RequestParam String title,
            @Parameter(description = "内容") @RequestParam String content,
            @Parameter(description = "目标类型") @RequestParam Integer targetType,
            @Parameter(description = "目标ID列表") @RequestParam(required = false) String targetIds,
            @Parameter(description = "发送人ID") @RequestParam Long senderId,
            @Parameter(description = "发送人姓名") @RequestParam String senderName) {
        
        try {
            Message message = new Message();
            message.setTitle(title);
            message.setContent(content);
            message.setMessageType(1); // 系统通知
            message.setSenderId(senderId);
            message.setSenderName(senderName);
            message.setTargetType(targetType);
            message.setTargetIds(targetIds);
            message.setSendMethod(1); // 站内信
            
            Result<Message> createResult = messageService.createMessage(message);
            if (createResult.isSuccess()) {
                return messageService.sendMessage(createResult.getData().getId());
            } else {
                return Result.error(createResult.getMessage());
            }
        } catch (Exception e) {
            return Result.error("发送系统通知失败：" + e.getMessage());
        }
    }

    /**
     * 快速发送班级通知
     */
    @Operation(summary = "快速发送班级通知")
    @PostMapping("/class-notice")
    public Result<Void> sendClassNotice(
            @Parameter(description = "标题") @RequestParam String title,
            @Parameter(description = "内容") @RequestParam String content,
            @Parameter(description = "班级ID列表") @RequestParam String classIds,
            @Parameter(description = "发送人ID") @RequestParam Long senderId,
            @Parameter(description = "发送人姓名") @RequestParam String senderName) {
        
        try {
            Message message = new Message();
            message.setTitle(title);
            message.setContent(content);
            message.setMessageType(1); // 系统通知
            message.setSenderId(senderId);
            message.setSenderName(senderName);
            message.setTargetType(3); // 按班级
            message.setTargetIds(classIds);
            message.setSendMethod(1); // 站内信
            
            Result<Message> createResult = messageService.createMessage(message);
            if (createResult.isSuccess()) {
                return messageService.sendMessage(createResult.getData().getId());
            } else {
                return Result.error(createResult.getMessage());
            }
        } catch (Exception e) {
            return Result.error("发送班级通知失败：" + e.getMessage());
        }
    }
}
