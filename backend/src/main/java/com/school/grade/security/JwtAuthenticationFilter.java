package com.school.grade.security;

import com.school.grade.entity.Permission;
import com.school.grade.entity.Role;
import com.school.grade.entity.User;
import com.school.grade.repository.UserRepository;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String auth = request.getHeader("Authorization");
        if (StringUtils.hasText(auth) && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);
            try {
                Claims claims = jwtUtil.parseClaims(token);
                String userIdStr = claims.getSubject();
                if (StringUtils.hasText(userIdStr) && SecurityContextHolder.getContext().getAuthentication() == null) {
                    String username = (String) claims.get("username");
                    if (!StringUtils.hasText(username)) {
                        filterChain.doFilter(request, response);
                        return;
                    }

                    // 带角色和权限一次性查询，避免懒加载导致鉴权上下文无法建立
                    Optional<User> userOpt = userRepository.findByUsernameWithRolesAndPermissions(username);
                    if (userOpt.isPresent()) {
                        User user = userOpt.get();

                        Set<GrantedAuthority> authorities = new HashSet<>();
                        // 角色 -> ROLE_xxx
                        if (user.getRoles() != null) {
                            for (Role role : user.getRoles()) {
                                if (role != null && StringUtils.hasText(role.getRoleCode())) {
                                    authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleCode()));
                                }
                                if (role != null && role.getPermissions() != null) {
                                    for (Permission p : role.getPermissions()) {
                                        if (p != null && StringUtils.hasText(p.getPerms())) {
                                            authorities.add(new SimpleGrantedAuthority(p.getPerms()));
                                        }
                                    }
                                }
                            }
                        }

                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(username, null, authorities);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            } catch (Exception ignored) {
                // token 无效则按未登录处理
            }
        }

        filterChain.doFilter(request, response);
    }
}

