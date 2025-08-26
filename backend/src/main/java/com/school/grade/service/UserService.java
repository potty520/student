package com.school.grade.service;

import com.school.grade.entity.User;
import com.school.grade.entity.Role;
import com.school.grade.repository.UserRepository;
import com.school.grade.repository.RoleRepository;
import com.school.grade.common.result.Result;
import com.school.grade.common.result.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * 用户管理业务逻辑层
 * 
 * @author Qoder
 * @version 1.0.0
 */
@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 密码复杂度正则表达式
    private static final String PASSWORD_PATTERN = 
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{8,}$";

    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

    /**
     * 分页查询用户信息
     * 
     * @param page 页码
     * @param size 每页大小
     * @param username 用户名（模糊查询）
     * @param realName 真实姓名（模糊查询）
     * @param status 状态
     * @return 分页结果
     */
    public Result<PageResult<User>> getUserList(int page, int size, String username, 
                                               String realName, Integer status) {
        try {
            // 创建分页对象
            Pageable pageable = PageRequest.of(page - 1, size, 
                Sort.by(Sort.Direction.DESC, "createTime"));

            // 构建查询条件
            Specification<User> spec = (root, query, criteriaBuilder) -> {
                List<Predicate> predicates = new ArrayList<>();
                
                // 软删除条件
                predicates.add(criteriaBuilder.equal(root.get("deleted"), 0));
                
                // 用户名模糊查询
                if (StringUtils.hasText(username)) {
                    predicates.add(criteriaBuilder.like(root.get("username"), "%" + username + "%"));
                }
                
                // 真实姓名模糊查询
                if (StringUtils.hasText(realName)) {
                    predicates.add(criteriaBuilder.like(root.get("realName"), "%" + realName + "%"));
                }
                
                // 状态查询
                if (status != null) {
                    predicates.add(criteriaBuilder.equal(root.get("status"), status));
                }
                
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            };

            Page<User> userPage = userRepository.findAll(spec, pageable);
            
            // 隐藏密码信息
            userPage.getContent().forEach(user -> user.setPassword(null));
            
            PageResult<User> pageResult = new PageResult<>(
                userPage.getContent(),
                userPage.getTotalElements(),
                page,
                size
            );

            return Result.success(pageResult);
        } catch (Exception e) {
            return Result.error("查询用户列表失败：" + e.getMessage());
        }
    }

    /**
     * 根据ID获取用户信息
     * 
     * @param id 用户ID
     * @return 用户信息
     */
    public Result<User> getUserById(Long id) {
        try {
            Optional<User> user = userRepository.findByIdAndDeleted(id, 0);
            if (user.isPresent()) {
                User userEntity = user.get();
                // 隐藏密码信息
                userEntity.setPassword(null);
                return Result.success(userEntity);
            } else {
                return Result.error("用户信息不存在");
            }
        } catch (Exception e) {
            return Result.error("获取用户信息失败：" + e.getMessage());
        }
    }

    /**
     * 添加用户
     * 
     * @param user 用户信息
     * @return 操作结果
     */
    public Result<User> addUser(User user) {
        try {
            // 数据验证
            Result<Void> validateResult = validateUser(user, true);
            if (!validateResult.isSuccess()) {
                return Result.error(validateResult.getMessage());
            }

            // 检查用户名是否已存在
            if (userRepository.existsByUsernameAndDeleted(user.getUsername(), 0)) {
                return Result.error("用户名已存在");
            }

            // 加密密码
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            // 设置默认值
            if (user.getStatus() == null) {
                user.setStatus(1); // 默认启用状态
            }

            User savedUser = userRepository.save(user);
            
            // 隐藏密码信息
            savedUser.setPassword(null);
            return Result.success(savedUser);
        } catch (Exception e) {
            return Result.error("添加用户失败：" + e.getMessage());
        }
    }

    /**
     * 更新用户信息
     * 
     * @param user 用户信息
     * @return 操作结果
     */
    public Result<User> updateUser(User user) {
        try {
            // 检查用户是否存在
            Optional<User> existingUser = userRepository.findByIdAndDeleted(user.getId(), 0);
            if (!existingUser.isPresent()) {
                return Result.error("用户信息不存在");
            }

            // 数据验证（更新时不验证密码）
            Result<Void> validateResult = validateUser(user, false);
            if (!validateResult.isSuccess()) {
                return Result.error(validateResult.getMessage());
            }

            // 检查用户名是否被其他用户占用
            Optional<User> duplicateUser = userRepository.findByUsernameAndDeleted(user.getUsername(), 0);
            if (duplicateUser.isPresent() && !duplicateUser.get().getId().equals(user.getId())) {
                return Result.error("用户名已被其他用户使用");
            }

            User existing = existingUser.get();
            
            // 更新允许修改的字段
            existing.setUsername(user.getUsername());
            existing.setRealName(user.getRealName());
            existing.setGender(user.getGender());
            existing.setPhone(user.getPhone());
            existing.setEmail(user.getEmail());
            existing.setAvatar(user.getAvatar());
            existing.setStatus(user.getStatus());
            existing.setRemark(user.getRemark());

            User savedUser = userRepository.save(existing);
            
            // 隐藏密码信息
            savedUser.setPassword(null);
            return Result.success(savedUser);
        } catch (Exception e) {
            return Result.error("更新用户信息失败：" + e.getMessage());
        }
    }

    /**
     * 删除用户（软删除）
     * 
     * @param id 用户ID
     * @return 操作结果
     */
    public Result<Void> deleteUser(Long id) {
        try {
            Optional<User> user = userRepository.findByIdAndDeleted(id, 0);
            if (!user.isPresent()) {
                return Result.error("用户信息不存在");
            }

            User userEntity = user.get();
            
            // 检查是否为系统管理员
            if ("admin".equals(userEntity.getUsername())) {
                return Result.error("系统管理员不能被删除");
            }

            userEntity.setDeleted(1);
            userRepository.save(userEntity);

            return Result.success();
        } catch (Exception e) {
            return Result.error("删除用户失败：" + e.getMessage());
        }
    }

    /**
     * 批量删除用户
     * 
     * @param ids 用户ID列表
     * @return 操作结果
     */
    public Result<Void> batchDeleteUsers(List<Long> ids) {
        try {
            List<User> users = userRepository.findByIdInAndDeleted(ids, 0);
            if (users.isEmpty()) {
                return Result.error("未找到要删除的用户");
            }

            // 检查是否包含系统管理员
            boolean hasAdmin = users.stream().anyMatch(user -> "admin".equals(user.getUsername()));
            if (hasAdmin) {
                return Result.error("不能删除系统管理员");
            }

            users.forEach(user -> user.setDeleted(1));
            userRepository.saveAll(users);

            return Result.success();
        } catch (Exception e) {
            return Result.error("批量删除用户失败：" + e.getMessage());
        }
    }

    /**
     * 更新用户状态
     * 
     * @param id 用户ID
     * @param status 新状态
     * @return 操作结果
     */
    public Result<Void> updateUserStatus(Long id, Integer status) {
        try {
            Optional<User> user = userRepository.findByIdAndDeleted(id, 0);
            if (!user.isPresent()) {
                return Result.error("用户信息不存在");
            }

            // 验证状态值
            if (status < 0 || status > 1) {
                return Result.error("无效的状态值");
            }

            User userEntity = user.get();
            
            // 检查是否为系统管理员
            if ("admin".equals(userEntity.getUsername()) && status == 0) {
                return Result.error("不能禁用系统管理员");
            }

            userEntity.setStatus(status);
            userRepository.save(userEntity);

            return Result.success();
        } catch (Exception e) {
            return Result.error("更新用户状态失败：" + e.getMessage());
        }
    }

    /**
     * 重置用户密码
     * 
     * @param id 用户ID
     * @param newPassword 新密码
     * @return 操作结果
     */
    public Result<Void> resetPassword(Long id, String newPassword) {
        try {
            Optional<User> user = userRepository.findByIdAndDeleted(id, 0);
            if (!user.isPresent()) {
                return Result.error("用户信息不存在");
            }

            // 验证密码复杂度
            if (!isValidPassword(newPassword)) {
                return Result.error("密码必须包含大小写字母和数字，且长度不少于8位");
            }

            User userEntity = user.get();
            userEntity.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(userEntity);

            return Result.success();
        } catch (Exception e) {
            return Result.error("重置密码失败：" + e.getMessage());
        }
    }

    /**
     * 修改密码
     * 
     * @param id 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 操作结果
     */
    public Result<Void> changePassword(Long id, String oldPassword, String newPassword) {
        try {
            Optional<User> user = userRepository.findByIdAndDeleted(id, 0);
            if (!user.isPresent()) {
                return Result.error("用户信息不存在");
            }

            User userEntity = user.get();
            
            // 验证旧密码
            if (!passwordEncoder.matches(oldPassword, userEntity.getPassword())) {
                return Result.error("旧密码不正确");
            }

            // 验证新密码复杂度
            if (!isValidPassword(newPassword)) {
                return Result.error("密码必须包含大小写字母和数字，且长度不少于8位");
            }

            userEntity.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(userEntity);

            return Result.success();
        } catch (Exception e) {
            return Result.error("修改密码失败：" + e.getMessage());
        }
    }

    /**
     * 更新最后登录信息
     * 
     * @param id 用户ID
     * @param loginIp 登录IP
     * @return 操作结果
     */
    public Result<Void> updateLastLogin(Long id, String loginIp) {
        try {
            Optional<User> user = userRepository.findByIdAndDeleted(id, 0);
            if (!user.isPresent()) {
                return Result.error("用户信息不存在");
            }

            User userEntity = user.get();
            userEntity.setLastLoginTime(LocalDateTime.now());
            userEntity.setLastLoginIp(loginIp);
            userRepository.save(userEntity);

            return Result.success();
        } catch (Exception e) {
            return Result.error("更新登录信息失败：" + e.getMessage());
        }
    }

    /**
     * 验证用户登录
     * 
     * @param username 用户名
     * @param password 密码
     * @return 验证结果
     */
    public Result<User> validateLogin(String username, String password) {
        try {
            // 根据用户名查找用户
            Optional<User> userOpt = userRepository.findByUsernameAndDeleted(username, 0);
            if (!userOpt.isPresent()) {
                return Result.error("用户名或密码错误");
            }
            
            User user = userOpt.get();
            
            // 检查用户状态
            if (user.getStatus() == 0) {
                return Result.error("账户已被禁用");
            }
            
            // 验证密码
            if (!passwordEncoder.matches(password, user.getPassword())) {
                return Result.error("用户名或密码错误");
            }
            
            return Result.success(user);
        } catch (Exception e) {
            return Result.error("登录验证失败：" + e.getMessage());
        }
    }

    /**
     * 根据用户名获取用户信息
     * 
     * @param username 用户名
     * @return 用户信息
     */
    public Result<User> getUserByUsername(String username) {
        try {
            Optional<User> user = userRepository.findByUsernameAndDeleted(username, 0);
            if (user.isPresent()) {
                return Result.success(user.get());
            } else {
                return Result.error("用户不存在");
            }
        } catch (Exception e) {
            return Result.error("获取用户信息失败：" + e.getMessage());
        }
    }

    /**
     * 数据验证
     * 
     * @param user 用户信息
     * @param isCreate 是否为创建操作
     * @return 验证结果
     */
    private Result<Void> validateUser(User user, boolean isCreate) {
        if (user == null) {
            return Result.error("用户信息不能为空");
        }

        if (!StringUtils.hasText(user.getUsername())) {
            return Result.error("用户名不能为空");
        }

        if (user.getUsername().length() < 3 || user.getUsername().length() > 50) {
            return Result.error("用户名长度必须在3-50个字符之间");
        }

        if (!user.getUsername().matches("^[a-zA-Z0-9_]+$")) {
            return Result.error("用户名只能包含字母、数字和下划线");
        }

        if (isCreate) {
            if (!StringUtils.hasText(user.getPassword())) {
                return Result.error("密码不能为空");
            }

            if (!isValidPassword(user.getPassword())) {
                return Result.error("密码必须包含大小写字母和数字，且长度不少于8位");
            }
        }

        if (!StringUtils.hasText(user.getRealName())) {
            return Result.error("真实姓名不能为空");
        }

        if (user.getRealName().length() > 50) {
            return Result.error("真实姓名长度不能超过50个字符");
        }

        // 验证手机号格式（如果提供）
        if (StringUtils.hasText(user.getPhone()) && 
            !user.getPhone().matches("^1[3-9]\\d{9}$")) {
            return Result.error("手机号格式不正确");
        }

        // 验证邮箱格式（如果提供）
        if (StringUtils.hasText(user.getEmail()) && 
            !user.getEmail().matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$")) {
            return Result.error("邮箱格式不正确");
        }

        return Result.success();
    }

    /**
     * 验证密码复杂度
     * 
     * @param password 密码
     * @return 是否有效
     */
    private boolean isValidPassword(String password) {
        return StringUtils.hasText(password) && pattern.matcher(password).matches();
    }
}