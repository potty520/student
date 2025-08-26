package com.school.grade.controller;

import com.school.grade.entity.User;
import com.school.grade.service.UserService;
import com.school.grade.common.result.Result;
import com.school.grade.common.result.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户管理控制器
 * 
 * @author Qoder
 * @version 1.0.0
 */
@Tag(name = "用户管理", description = "用户增删改查、状态管理等接口")
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 分页查询用户列表
     */
    @Operation(summary = "分页查询用户列表")
    @GetMapping("/list")
    public Result<PageResult<User>> getUserList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "用户名") @RequestParam(required = false) String username,
            @Parameter(description = "真实姓名") @RequestParam(required = false) String realName,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        return userService.getUserList(page, size, username, realName, status);
    }

    /**
     * 根据ID获取用户信息
     */
    @Operation(summary = "根据ID获取用户信息")
    @GetMapping("/{id}")
    public Result<User> getUserById(@Parameter(description = "用户ID") @PathVariable Long id) {
        return userService.getUserById(id);
    }

    /**
     * 添加用户
     */
    @Operation(summary = "添加用户")
    @PostMapping
    public Result<User> addUser(@Parameter(description = "用户信息") @Valid @RequestBody User user) {
        return userService.addUser(user);
    }

    /**
     * 更新用户信息
     */
    @Operation(summary = "更新用户信息")
    @PutMapping("/{id}")
    public Result<User> updateUser(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Parameter(description = "用户信息") @Valid @RequestBody User user) {
        user.setId(id);
        return userService.updateUser(user);
    }

    /**
     * 删除用户
     */
    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@Parameter(description = "用户ID") @PathVariable Long id) {
        return userService.deleteUser(id);
    }

    /**
     * 批量删除用户
     */
    @Operation(summary = "批量删除用户")
    @DeleteMapping("/batch")
    public Result<Void> batchDeleteUsers(@Parameter(description = "用户ID列表") @RequestBody List<Long> ids) {
        return userService.batchDeleteUsers(ids);
    }

    /**
     * 更新用户状态
     */
    @Operation(summary = "更新用户状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateUserStatus(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Parameter(description = "状态") @RequestParam Integer status) {
        return userService.updateUserStatus(id, status);
    }

    /**
     * 启用用户
     */
    @Operation(summary = "启用用户")
    @PutMapping("/{id}/enable")
    public Result<Void> enableUser(@Parameter(description = "用户ID") @PathVariable Long id) {
        return userService.updateUserStatus(id, 1);
    }

    /**
     * 禁用用户
     */
    @Operation(summary = "禁用用户")
    @PutMapping("/{id}/disable")
    public Result<Void> disableUser(@Parameter(description = "用户ID") @PathVariable Long id) {
        return userService.updateUserStatus(id, 0);
    }

    /**
     * 重置用户密码
     */
    @Operation(summary = "重置用户密码")
    @PutMapping("/{id}/reset-password")
    public Result<Void> resetPassword(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Parameter(description = "新密码") @RequestBody Map<String, String> passwordData) {
        String newPassword = passwordData.get("newPassword");
        if (newPassword == null || newPassword.trim().isEmpty()) {
            return Result.error("新密码不能为空");
        }
        return userService.resetPassword(id, newPassword);
    }

    /**
     * 修改密码
     */
    @Operation(summary = "修改密码")
    @PutMapping("/{id}/change-password")
    public Result<Void> changePassword(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @RequestBody Map<String, String> passwordData) {
        String oldPassword = passwordData.get("oldPassword");
        String newPassword = passwordData.get("newPassword");
        
        if (oldPassword == null || oldPassword.trim().isEmpty()) {
            return Result.error("原密码不能为空");
        }
        
        if (newPassword == null || newPassword.trim().isEmpty()) {
            return Result.error("新密码不能为空");
        }
        
        return userService.changePassword(id, oldPassword, newPassword);
    }

    /**
     * 根据用户名获取用户信息
     */
    @Operation(summary = "根据用户名获取用户信息")
    @GetMapping("/username/{username}")
    public Result<User> getUserByUsername(@Parameter(description = "用户名") @PathVariable String username) {
        return userService.getUserByUsername(username);
    }

    /**
     * 更新最后登录信息
     */
    @Operation(summary = "更新最后登录信息")
    @PutMapping("/{id}/last-login")
    public Result<Void> updateLastLogin(
            @Parameter(description = "用户ID") @PathVariable Long id,
            HttpServletRequest request) {
        String loginIp = getClientIpAddress(request);
        return userService.updateLastLogin(id, loginIp);
    }

    /**
     * 获取活跃用户列表
     */
    @Operation(summary = "获取活跃用户列表")
    @GetMapping("/active")
    public Result<PageResult<User>> getActiveUsers(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "50") int size) {
        return userService.getUserList(page, size, null, null, 1);
    }

    /**
     * 检查用户名是否可用
     */
    @Operation(summary = "检查用户名是否可用")
    @GetMapping("/check-username")
    public Result<Boolean> checkUsername(@Parameter(description = "用户名") @RequestParam String username) {
        Result<User> result = userService.getUserByUsername(username);
        return Result.success(!result.isSuccess());
    }

    /**
     * 获取用户统计信息
     */
    @Operation(summary = "获取用户统计信息")
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getUserStatistics() {
        // 这里可以实现用户统计功能
        Map<String, Object> result = new HashMap<>();
        result.put("message", "用户统计功能待实现");
        return Result.success(result);
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }
}
