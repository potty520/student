package com.school.grade.controller;

import com.school.grade.entity.User;
import com.school.grade.entity.Role;
import com.school.grade.entity.Permission;
import com.school.grade.repository.UserRepository;
import com.school.grade.service.UserService;
import com.school.grade.common.result.Result;
import com.school.grade.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Autowired
    private UserRepository userRepository;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

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

        // 重新查询：带角色与权限
        Optional<User> withPermsOpt = userRepository.findByUsernameWithRolesAndPermissions(user.getUsername());
        User withPerms = withPermsOpt.orElse(user);

        // 生成JWT
        JwtUtil jwtUtil = new JwtUtil(jwtSecret, jwtExpiration);
        String token = jwtUtil.generateToken(withPerms.getId(), withPerms.getUsername());

        // 构建返回数据（与前端 store 结构一致）
        Map<String, Object> data = new HashMap<>();
        withPerms.setPassword(null);
        data.put("token", token);
        data.put("userInfo", withPerms);

        List<String> roleCodes = new ArrayList<>();
        Set<String> perms = new HashSet<>();
        if (withPerms.getRoles() != null) {
            for (Role role : withPerms.getRoles()) {
                if (role != null && role.getRoleCode() != null) {
                    roleCodes.add(role.getRoleCode());
                }
                if (role != null && role.getPermissions() != null) {
                    for (Permission p : role.getPermissions()) {
                        if (p != null && p.getPerms() != null && !p.getPerms().trim().isEmpty()) {
                            perms.add(p.getPerms().trim());
                        }
                    }
                }
            }
        }

        // ADMIN 给通配（与当前前端约定兼容）
        if (roleCodes.contains("ADMIN")) {
            perms.add("*:*:*");
        }

        data.put("roles", roleCodes);
        data.put("permissions", perms.stream().sorted().collect(Collectors.toList()));
        
        return Result.success(data);
    }

    /**
     * 用户登出
     */
    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        // JWT为无状态，前端清理token即可
        return Result.success();
    }

    /**
     * 获取当前用户信息
     */
    @Operation(summary = "获取当前用户信息")
    @GetMapping("/current")
    public Result<Map<String, Object>> getCurrentUser() {
        // 通过JWT filter 已把 username 放入 SecurityContext principal
        Object principal = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication() == null
                ? null
                : org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal == null) {
            return Result.error("未登录");
        }

        String username = String.valueOf(principal);
        Optional<User> withPermsOpt = userRepository.findByUsernameWithRolesAndPermissions(username);
        if (!withPermsOpt.isPresent()) {
            return Result.error("用户不存在");
        }

        User withPerms = withPermsOpt.get();
        withPerms.setPassword(null);

        List<String> roleCodes = new ArrayList<>();
        Set<String> perms = new HashSet<>();
        if (withPerms.getRoles() != null) {
            for (Role role : withPerms.getRoles()) {
                if (role != null && role.getRoleCode() != null) {
                    roleCodes.add(role.getRoleCode());
                }
                if (role != null && role.getPermissions() != null) {
                    for (Permission p : role.getPermissions()) {
                        if (p != null && p.getPerms() != null && !p.getPerms().trim().isEmpty()) {
                            perms.add(p.getPerms().trim());
                        }
                    }
                }
            }
        }
        if (roleCodes.contains("ADMIN")) {
            perms.add("*:*:*");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("userInfo", withPerms);
        data.put("roles", roleCodes);
        data.put("permissions", perms.stream().sorted().collect(Collectors.toList()));
        return Result.success(data);
    }

    /**
     * 修改密码
     */
    @Operation(summary = "修改密码")
    @PostMapping("/changePassword")
    public Result<Void> changePassword(
            @Parameter(description = "修改密码信息") @RequestBody Map<String, String> passwordData,
            HttpServletRequest request) {

        Object principal = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication() == null
                ? null
                : org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal == null) {
            return Result.error("未登录");
        }

        String username = String.valueOf(principal);
        Optional<User> u = userRepository.findByUsernameAndDeleted(username, 0);
        if (!u.isPresent()) {
            return Result.error("用户不存在");
        }

        String oldPassword = passwordData.get("oldPassword");
        String newPassword = passwordData.get("newPassword");

        return userService.changePassword(u.get().getId(), oldPassword, newPassword);
    }

    /**
     * 刷新token
     */
    @Operation(summary = "刷新token")
    @PostMapping("/refresh")
    public Result<Map<String, Object>> refreshToken() {
        Object principal = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication() == null
                ? null
                : org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal == null) {
            return Result.error("未登录");
        }

        String username = String.valueOf(principal);
        Optional<User> withPermsOpt = userRepository.findByUsernameWithRolesAndPermissions(username);
        if (!withPermsOpt.isPresent()) {
            return Result.error("用户不存在");
        }

        User user = withPermsOpt.get();
        JwtUtil jwtUtil = new JwtUtil(jwtSecret, jwtExpiration);
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
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