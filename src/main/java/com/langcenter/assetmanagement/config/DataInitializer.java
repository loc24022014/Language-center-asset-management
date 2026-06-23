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
        if (roleRepository.count() == 0) {
            roleRepository.save(Role.builder().roleName("ADMIN").build());
            roleRepository.save(Role.builder().roleName("MANAGER").build());
            roleRepository.save(Role.builder().roleName("TEACHER").build());
            roleRepository.save(Role.builder().roleName("STAFF").build());
        }

        if (userRepository.count() == 0) {
            Role adminRole = roleRepository.findByRoleName("ADMIN").orElseThrow();
            Role managerRole = roleRepository.findByRoleName("MANAGER").orElseThrow();
            Role teacherRole = roleRepository.findByRoleName("TEACHER").orElseThrow();

            userRepository.save(User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .fullName("Administrator")
                    .email("admin@langcenter.edu.vn")
                    .role(adminRole)
                    .isActive(true)
                    .build());

            userRepository.save(User.builder()
                    .username("manager")
                    .password(passwordEncoder.encode("manager123"))
                    .fullName("Quản lý trung tâm")
                    .email("manager@langcenter.edu.vn")
                    .role(managerRole)
                    .isActive(true)
                    .build());

            userRepository.save(User.builder()
                    .username("teacher1")
                    .password(passwordEncoder.encode("teacher123"))
                    .fullName("Giáo viên 1")
                    .email("teacher1@langcenter.edu.vn")
                    .role(teacherRole)
                    .isActive(true)
                    .build());
        }
    }
}
