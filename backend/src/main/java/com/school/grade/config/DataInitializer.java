package com.school.grade.config;

import com.school.grade.entity.User;
import com.school.grade.entity.Role;
import com.school.grade.repository.UserRepository;
import com.school.grade.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

/**
 * 数据初始化组件
 * 
 * @author Qoder
 * @version 1.0.0
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${system.admin.username:admin}")
    private String adminUsername;

    @Value("${system.admin.password:admin123}")
    private String adminPassword;

    @Value("${system.admin.email:admin@school.com}")
    private String adminEmail;

    @Override
    public void run(String... args) throws Exception {
        initializeRoles();
        initializeAdminUser();
    }

    /**
     * 初始化角色数据
     */
    private void initializeRoles() {
        // 创建管理员角色
        if (!roleRepository.findByRoleCodeAndDeleted("ADMIN", 0).isPresent()) {
            Role adminRole = new Role();
            adminRole.setRoleName("管理员");
            adminRole.setRoleCode("ADMIN");
            adminRole.setDescription("系统管理员角色");
            adminRole.setStatus(1);
            adminRole.setSortOrder(1);
            adminRole.setDeleted(0);
            adminRole.setCreateTime(LocalDateTime.now());
            adminRole.setUpdateTime(LocalDateTime.now());
            roleRepository.save(adminRole);
            System.out.println("初始化管理员角色成功");
        }

        // 创建教师角色
        if (!roleRepository.findByRoleCodeAndDeleted("TEACHER", 0).isPresent()) {
            Role teacherRole = new Role();
            teacherRole.setRoleName("教师");
            teacherRole.setRoleCode("TEACHER");
            teacherRole.setDescription("教师角色");
            teacherRole.setStatus(1);
            teacherRole.setSortOrder(2);
            teacherRole.setDeleted(0);
            teacherRole.setCreateTime(LocalDateTime.now());
            teacherRole.setUpdateTime(LocalDateTime.now());
            roleRepository.save(teacherRole);
            System.out.println("初始化教师角色成功");
        }

        // 创建学生角色
        if (!roleRepository.findByRoleCodeAndDeleted("student", 0).isPresent()) {
            Role studentRole = new Role();
            studentRole.setRoleName("学生");
            studentRole.setRoleCode("student");
            studentRole.setDescription("学生角色");
            studentRole.setStatus(1);
            studentRole.setDeleted(0);
            studentRole.setCreateTime(LocalDateTime.now());
            studentRole.setUpdateTime(LocalDateTime.now());
            roleRepository.save(studentRole);
            System.out.println("初始化学生角色成功");
        }
    }

    /**
     * 初始化管理员用户
     */
    private void initializeAdminUser() {
        // 检查是否已存在管理员用户
        Optional<User> existingAdmin = userRepository.findByUsernameAndDeleted(adminUsername, 0);
        if (existingAdmin.isPresent()) {
            // 更新现有管理员用户的密码
            User adminUser = existingAdmin.get();
            adminUser.setPassword(passwordEncoder.encode(adminPassword));
            adminUser.setUpdateTime(LocalDateTime.now());
            userRepository.save(adminUser);
            System.out.println("管理员用户已存在，更新密码: " + adminUsername + "/" + adminPassword);
            return;
        }

        // 获取管理员角色
        Optional<Role> adminRoleOpt = roleRepository.findByRoleCodeAndDeleted("ADMIN", 0);
        if (!adminRoleOpt.isPresent()) {
            System.err.println("管理员角色不存在，无法创建管理员用户");
            return;
        }

        Role adminRole = adminRoleOpt.get();

        // 创建管理员用户
        User adminUser = new User();
        adminUser.setUsername(adminUsername);
        adminUser.setPassword(passwordEncoder.encode(adminPassword));
        adminUser.setRealName("系统管理员");
        adminUser.setEmail(adminEmail);
        adminUser.setPhone("13800138000");
        adminUser.setGender(1); // 1-男性
        adminUser.setStatus(1); // 1-启用
        adminUser.setDeleted(0); // 0-未删除
        adminUser.setRoles(new HashSet<>(Arrays.asList(adminRole)));
        adminUser.setCreateTime(LocalDateTime.now());
        adminUser.setUpdateTime(LocalDateTime.now());

        userRepository.save(adminUser);
        System.out.println("初始化管理员用户成功: " + adminUsername + "/" + adminPassword);
    }
}