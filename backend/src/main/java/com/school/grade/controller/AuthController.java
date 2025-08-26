package com.school.grade.controller;

import com.school.grade.entity.User;
import com.school.grade.service.UserService;
import com.school.grade.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 * 
 * @author Qoder
 * @version 1.0.0
 */
@Tag(name = "认证管理", description = "用户登录、登出等认证相关接口")
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     */
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(
            @Parameter(description = "登录信息") @RequestBody Map<String, String> loginData,
            HttpServletRequest request) {
        
        String username = loginData.get("username");
        String password = loginData.get("password");
        
        if (username == null || username.trim().isEmpty()) {
            return Result.error("用户名不能为空");
        }
        
        if (password == null || password.trim().isEmpty()) {
            return Result.error("密码不能为空");
        }
        
        // 验证用户登录
        Result<User> loginResult = userService.validateLogin(username, password);
        if (!loginResult.isSuccess()) {
            return Result.error(loginResult.getMessage());
        }
        
        User user = loginResult.getData();
        
        // 更新最后登录信息
        String loginIp = getClientIpAddress(request);
        userService.updateLastLogin(user.getId(), loginIp);
        
        // 创建会话
        HttpSession session = request.getSession();
        session.setAttribute("user", user);
        session.setAttribute("userId", user.getId());
        session.setAttribute("username", user.getUsername());
        
        // 构建返回数据
        Map<String, Object> data = new HashMap<>();
        data.put("token", session.getId());
        data.put("userInfo", user);
        data.put("permissions", new String[]{"*:*:*"}); // 简化权限处理
        data.put("roles", new String[]{"admin"});
        
        return Result.success(data);
    }

    /**
     * 用户登出
     */
    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return Result.success();
    }

    /**
     * 获取当前用户信息
     */
    @Operation(summary = "获取当前用户信息")
    @GetMapping("/current")
    public Result<User> getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return Result.error("未登录");
        }
        
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.error("会话已过期");
        }
        
        return Result.success(user);
    }

    /**
     * 修改密码
     */
    @Operation(summary = "修改密码")
    @PostMapping("/changePassword")
    public Result<Void> changePassword(
            @Parameter(description = "修改密码信息") @RequestBody Map<String, String> passwordData,
            HttpServletRequest request) {
        
        HttpSession session = request.getSession(false);
        if (session == null) {
            return Result.error("未登录");
        }
        
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return Result.error("会话已过期");
        }
        
        String oldPassword = passwordData.get("oldPassword");
        String newPassword = passwordData.get("newPassword");
        
        return userService.changePassword(userId, oldPassword, newPassword);
    }

    /**
     * 刷新token
     */
    @Operation(summary = "刷新token")
    @PostMapping("/refresh")
    public Result<Map<String, Object>> refreshToken(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return Result.error("未登录");
        }
        
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.error("会话已过期");
        }
        
        Map<String, Object> data = new HashMap<>();
        data.put("token", session.getId());
        data.put("userInfo", user);
        
        return Result.success(data);
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0];
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}