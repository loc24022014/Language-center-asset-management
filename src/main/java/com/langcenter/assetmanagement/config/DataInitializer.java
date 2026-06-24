package com.langcenter.assetmanagement.config;

import com.langcenter.assetmanagement.entity.Role;
import com.langcenter.assetmanagement.entity.User;
import com.langcenter.assetmanagement.repository.RoleRepository;
import com.langcenter.assetmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Create Roles if missing
        createRoleIfNotFound("ADMIN");
        createRoleIfNotFound("MANAGER");
        createRoleIfNotFound("TEACHER");
        createRoleIfNotFound("STAFF");

        // Create Users if missing
        createUserIfNotFound("admin", "admin123", "Administrator", "admin@langcenter.edu.vn", "ADMIN");
        createUserIfNotFound("manager", "manager123", "Quản lý trung tâm", "manager@langcenter.edu.vn", "MANAGER");
        createUserIfNotFound("teacher1", "teacher123", "Giáo viên 1", "teacher1@langcenter.edu.vn", "TEACHER");
    }

    private void createRoleIfNotFound(String roleName) {
        if (roleRepository.findByName(roleName).isEmpty()) {
            roleRepository.save(Role.builder().name(roleName).build());
        }
    }

    private void createUserIfNotFound(String username, String password, String fullName, String email, String roleName) {
        if (userRepository.findByUsername(username).isEmpty()) {
            Role role = roleRepository.findByName(roleName).orElseThrow();
            userRepository.save(User.builder()
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .fullName(fullName)
                    .email(email)
                    .role(role)
                    .isActive(true)
                    .build());
        }
    }
}
