package com.school.grade.service;

import com.school.grade.entity.Message;
import com.school.grade.entity.MessageReceive;
import com.school.grade.entity.User;
import com.school.grade.repository.MessageRepository;
import com.school.grade.repository.MessageReceiveRepository;
import com.school.grade.repository.UserRepository;
import com.school.grade.repository.RoleRepository;
import com.school.grade.repository.SchoolClassRepository;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 消息通知业务逻辑层
 * 
 * @author Qoder
 * @version 1.0.0
 */
@Service
@Transactional
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MessageReceiveRepository messageReceiveRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private SchoolClassRepository classRepository;

    /**
     * 分页查询消息列表
     * 
     * @param page 页码
     * @param size 每页大小
     * @param title 消息标题（模糊查询）
     * @param messageType 消息类型
     * @param status 状态
     * @param senderId 发送人ID
     * @return 分页结果
     */
    public Result<PageResult<Message>> getMessageList(int page, int size, String title, 
                                                     Integer messageType, Integer status, Long senderId) {
        try {
            // 创建分页对象
            Pageable pageable = PageRequest.of(page - 1, size, 
                Sort.by(Sort.Direction.DESC, "sendTime", "createTime"));

            // 构建查询条件
            Specification<Message> spec = (root, query, criteriaBuilder) -> {
                List<Predicate> predicates = new ArrayList<>();
                
                // 软删除条件
                predicates.add(criteriaBuilder.equal(root.get("deleted"), 0));
                
                // 消息标题模糊查询
                if (StringUtils.hasText(title)) {
                    predicates.add(criteriaBuilder.like(root.get("title"), "%" + title + "%"));
                }
                
                // 消息类型查询
                if (messageType != null) {
                    predicates.add(criteriaBuilder.equal(root.get("messageType"), messageType));
                }
                
                // 状态查询
                if (status != null) {
                    predicates.add(criteriaBuilder.equal(root.get("status"), status));
                }
                
                // 发送人查询
                if (senderId != null) {
                    predicates.add(criteriaBuilder.equal(root.get("senderId"), senderId));
                }
                
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            };

            Page<Message> messagePage = messageRepository.findAll(spec, pageable);
            
            PageResult<Message> pageResult = new PageResult<>(
                messagePage.getContent(),
                messagePage.getTotalElements(),
                page,
                size
            );

            return Result.success(pageResult);
        } catch (Exception e) {
            return Result.error("查询消息列表失败：" + e.getMessage());
        }
    }

    /**
     * 根据ID获取消息信息
     * 
     * @param id 消息ID
     * @return 消息信息
     */
    public Result<Message> getMessageById(Long id) {
        try {
            Optional<Message> message = messageRepository.findByIdAndDeleted(id, 0);
            if (message.isPresent()) {
                return Result.success(message.get());
            } else {
                return Result.error("消息信息不存在");
            }
        } catch (Exception e) {
            return Result.error("获取消息信息失败：" + e.getMessage());
        }
    }

    /**
     * 创建消息（草稿状态）
     * 
     * @param message 消息信息
     * @return 操作结果
     */
    public Result<Message> createMessage(Message message) {
        try {
            // 数据验证
            Result<Void> validateResult = validateMessage(message);
            if (!validateResult.isSuccess()) {
                return Result.error(validateResult.getMessage());
            }

            // 设置默认值
            message.setStatus(0); // 草稿状态
            message.setReadCount(0);
            message.setTotalCount(0);

            Message savedMessage = messageRepository.save(message);
            return Result.success(savedMessage);
        } catch (Exception e) {
            return Result.error("创建消息失败：" + e.getMessage());
        }
    }

    /**
     * 更新消息信息
     * 
     * @param message 消息信息
     * @return 操作结果
     */
    public Result<Message> updateMessage(Message message) {
        try {
            // 检查消息是否存在
            Optional<Message> existingMessage = messageRepository.findByIdAndDeleted(message.getId(), 0);
            if (!existingMessage.isPresent()) {
                return Result.error("消息信息不存在");
            }

            Message existing = existingMessage.get();
            
            // 只有草稿状态的消息才能修改
            if (existing.getStatus() != 0) {
                return Result.error("只有草稿状态的消息才能修改");
            }

            // 数据验证
            Result<Void> validateResult = validateMessage(message);
            if (!validateResult.isSuccess()) {
                return Result.error(validateResult.getMessage());
            }

            Message savedMessage = messageRepository.save(message);
            return Result.success(savedMessage);
        } catch (Exception e) {
            return Result.error("更新消息信息失败：" + e.getMessage());
        }
    }

    /**
     * 发送消息
     * 
     * @param id 消息ID
     * @return 操作结果
     */
    public Result<Void> sendMessage(Long id) {
        try {
            Optional<Message> message = messageRepository.findByIdAndDeleted(id, 0);
            if (!message.isPresent()) {
                return Result.error("消息信息不存在");
            }

            Message messageEntity = message.get();
            
            // 只有草稿状态的消息才能发送
            if (messageEntity.getStatus() != 0) {
                return Result.error("只有草稿状态的消息才能发送");
            }

            // 获取接收人列表
            Result<List<Long>> receiversResult = getReceivers(messageEntity);
            if (!receiversResult.isSuccess()) {
                return Result.error(receiversResult.getMessage());
            }

            List<Long> receiverIds = receiversResult.getData();
            if (receiverIds.isEmpty()) {
                return Result.error("没有找到接收人");
            }

            // 创建接收记录
            List<MessageReceive> receives = new ArrayList<>();
            for (Long receiverId : receiverIds) {
                Optional<User> userOpt = userRepository.findByIdAndDeleted(receiverId, 0);
                if (userOpt.isPresent()) {
                    MessageReceive receive = new MessageReceive();
                    receive.setMessageId(id);
                    receive.setReceiverId(receiverId);
                    receive.setReceiverName(userOpt.get().getUsername());
                    receive.setIsRead(0);
                    receives.add(receive);
                }
            }

            messageReceiveRepository.saveAll(receives);

            // 更新消息状态
            messageEntity.setStatus(1); // 已发送
            messageEntity.setSendTime(LocalDateTime.now());
            messageEntity.setTotalCount(receives.size());
            messageEntity.setReadCount(0);
            messageRepository.save(messageEntity);

            return Result.success();
        } catch (Exception e) {
            return Result.error("发送消息失败：" + e.getMessage());
        }
    }

    /**
     * 删除消息（软删除）
     * 
     * @param id 消息ID
     * @return 操作结果
     */
    public Result<Void> deleteMessage(Long id) {
        try {
            Optional<Message> message = messageRepository.findByIdAndDeleted(id, 0);
            if (!message.isPresent()) {
                return Result.error("消息信息不存在");
            }

            Message messageEntity = message.get();
            messageEntity.setDeleted(1);
            messageRepository.save(messageEntity);

            return Result.success();
        } catch (Exception e) {
            return Result.error("删除消息失败：" + e.getMessage());
        }
    }

    /**
     * 获取用户接收的消息列表
     * 
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @param isRead 是否已读
     * @return 消息列表
     */
    public Result<PageResult<MessageReceive>> getUserMessages(Long userId, int page, int size, Integer isRead) {
        try {
            Pageable pageable = PageRequest.of(page - 1, size);
            
            Page<MessageReceive> receivePage;
            if (isRead != null) {
                if (isRead == 0) {
                    receivePage = messageReceiveRepository.findUnreadMessagesWithDetails(userId, pageable);
                } else {
                    receivePage = messageReceiveRepository.findReadMessagesWithDetails(userId, pageable);
                }
            } else {
                receivePage = messageReceiveRepository.findByReceiverIdOrderByCreateTimeDesc(userId, pageable);
            }

            // 填充消息详情
            List<MessageReceive> receives = receivePage.getContent();
            for (MessageReceive receive : receives) {
                Optional<Message> messageOpt = messageRepository.findByIdAndDeleted(receive.getMessageId(), 0);
                if (messageOpt.isPresent()) {
                    receive.setMessage(messageOpt.get());
                }
            }

            PageResult<MessageReceive> pageResult = new PageResult<>(
                receives,
                receivePage.getTotalElements(),
                page,
                size
            );

            return Result.success(pageResult);
        } catch (Exception e) {
            return Result.error("获取用户消息失败：" + e.getMessage());
        }
    }

    /**
     * 标记消息为已读
     * 
     * @param messageId 消息ID
     * @param userId 用户ID
     * @return 操作结果
     */
    public Result<Void> markAsRead(Long messageId, Long userId) {
        try {
            int updatedRows = messageReceiveRepository.markAsRead(messageId, userId);
            if (updatedRows > 0) {
                // 更新消息的已读人数
                updateMessageReadCount(messageId);
                return Result.success();
            } else {
                return Result.error("标记失败，可能消息已读或不存在");
            }
        } catch (Exception e) {
            return Result.error("标记消息已读失败：" + e.getMessage());
        }
    }

    /**
     * 标记用户所有消息为已读
     * 
     * @param userId 用户ID
     * @return 操作结果
     */
    public Result<Void> markAllAsRead(Long userId) {
        try {
            messageReceiveRepository.markAllAsReadByUser(userId);
            return Result.success();
        } catch (Exception e) {
            return Result.error("标记所有消息已读失败：" + e.getMessage());
        }
    }

    /**
     * 获取用户未读消息数量
     * 
     * @param userId 用户ID
     * @return 未读消息数量
     */
    public Result<Long> getUnreadCount(Long userId) {
        try {
            Long count = messageReceiveRepository.countUnreadByUser(userId);
            return Result.success(count);
        } catch (Exception e) {
            return Result.error("获取未读消息数量失败：" + e.getMessage());
        }
    }

    /**
     * 获取最近的系统通知
     * 
     * @param size 获取数量
     * @return 系统通知列表
     */
    public Result<List<Message>> getRecentSystemNotices(int size) {
        try {
            Pageable pageable = PageRequest.of(0, size);
            Page<Message> messagePage = messageRepository.findRecentSystemNotices(pageable);
            return Result.success(messagePage.getContent());
        } catch (Exception e) {
            return Result.error("获取系统通知失败：" + e.getMessage());
        }
    }

    /**
     * 获取接收人列表
     * 
     * @param message 消息
     * @return 接收人ID列表
     */
    private Result<List<Long>> getReceivers(Message message) {
        List<Long> receiverIds = new ArrayList<>();
        
        try {
            switch (message.getTargetType()) {
                case 1: // 全部用户
                    List<User> allUsers = userRepository.findByStatusAndDeleted(1, 0);
                    receiverIds = allUsers.stream().map(User::getId).collect(Collectors.toList());
                    break;
                
                case 2: // 按角色
                    if (StringUtils.hasText(message.getTargetIds())) {
                        List<String> roleIds = Arrays.asList(message.getTargetIds().split(","));
                        for (String roleIdStr : roleIds) {
                            Long roleId = Long.valueOf(roleIdStr.trim());
                            List<User> roleUsers = userRepository.findByRoleIdAndStatusAndDeleted(roleId, 1, 0);
                            receiverIds.addAll(roleUsers.stream().map(User::getId).collect(Collectors.toList()));
                        }
                    }
                    break;
                
                case 3: // 按班级
                    if (StringUtils.hasText(message.getTargetIds())) {
                        List<String> classIds = Arrays.asList(message.getTargetIds().split(","));
                        for (String classIdStr : classIds) {
                            Long classId = Long.valueOf(classIdStr.trim());
                            // 获取班级的班主任和任课教师
                            List<User> classUsers = userRepository.findByClassIdAndStatusAndDeleted(classId, 1, 0);
                            receiverIds.addAll(classUsers.stream().map(User::getId).collect(Collectors.toList()));
                        }
                    }
                    break;
                
                case 4: // 指定个人
                    if (StringUtils.hasText(message.getTargetIds())) {
                        List<String> userIds = Arrays.asList(message.getTargetIds().split(","));
                        for (String userIdStr : userIds) {
                            receiverIds.add(Long.valueOf(userIdStr.trim()));
                        }
                    }
                    break;
                
                default:
                    return Result.error("无效的接收对象类型");
            }
            
            // 去重
            receiverIds = receiverIds.stream().distinct().collect(Collectors.toList());
            
            return Result.success(receiverIds);
        } catch (Exception e) {
            return Result.error("获取接收人列表失败：" + e.getMessage());
        }
    }

    /**
     * 更新消息的已读人数
     * 
     * @param messageId 消息ID
     */
    private void updateMessageReadCount(Long messageId) {
        try {
            Long readCount = messageReceiveRepository.countReadByMessage(messageId);
            Optional<Message> messageOpt = messageRepository.findByIdAndDeleted(messageId, 0);
            if (messageOpt.isPresent()) {
                Message message = messageOpt.get();
                message.setReadCount(readCount.intValue());
                messageRepository.save(message);
            }
        } catch (Exception e) {
            // 忽略更新失败的情况
        }
    }

    /**
     * 数据验证
     * 
     * @param message 消息信息
     * @return 验证结果
     */
    private Result<Void> validateMessage(Message message) {
        if (message == null) {
            return Result.error("消息信息不能为空");
        }

        if (!StringUtils.hasText(message.getTitle())) {
            return Result.error("消息标题不能为空");
        }

        if (message.getTitle().length() > 200) {
            return Result.error("消息标题长度不能超过200个字符");
        }

        if (!StringUtils.hasText(message.getContent())) {
            return Result.error("消息内容不能为空");
        }

        if (message.getMessageType() == null) {
            return Result.error("消息类型不能为空");
        }

        if (message.getMessageType() < 1 || message.getMessageType() > 4) {
            return Result.error("消息类型值必须在1-4之间");
        }

        if (message.getTargetType() == null) {
            return Result.error("接收对象类型不能为空");
        }

        if (message.getTargetType() < 1 || message.getTargetType() > 4) {
            return Result.error("接收对象类型值必须在1-4之间");
        }

        if (message.getTargetType() > 1 && !StringUtils.hasText(message.getTargetIds())) {
            return Result.error("接收对象ID不能为空");
        }

        if (message.getSendMethod() == null) {
            return Result.error("发送方式不能为空");
        }

        if (message.getSendMethod() < 1 || message.getSendMethod() > 4) {
            return Result.error("发送方式值必须在1-4之间");
        }

        return Result.success();
    }
}