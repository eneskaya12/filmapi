package org.example.filmapi.initializer;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.filmapi.model.entity.User;
import org.example.filmapi.model.enums.Role;
import org.example.filmapi.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Order(1)
public class UserDataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() == 0) {
            User admin = User.builder()
                    .fullname("Admin User")
                    .email("admin@test.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .build();

            User user = User.builder()
                    .fullname("Test User")
                    .email("user@test.com")
                    .password(passwordEncoder.encode("user123"))
                    .role(Role.USER)
                    .build();

            userRepository.saveAll(List.of(admin, user));
        }
    }
}
