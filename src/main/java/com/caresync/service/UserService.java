package com.caresync.service;

import com.caresync.entity.User;
import com.caresync.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    // =========================================================
    // REGISTER USER
    // =========================================================

    @Transactional
    public User registerUser(
            String name,
            String email,
            String password,
            String role) {

        User existingUser =
                userRepository.findByEmail(email);

        if (existingUser != null) {
            throw new RuntimeException(
                    "Email already registered"
            );
        }

        // Default role
        if (role == null || role.trim().isEmpty()) {
            role = "PATIENT";
        }

        role = role.trim().toUpperCase();

        // Only these roles can currently be registered
        if (!role.equals("PATIENT")
                && !role.equals("CAREGIVER")) {

            throw new RuntimeException(
                    "Invalid registration role"
            );
        }

        String hashedPassword =
                passwordEncoder.encode(password);

        User user = new User(
                name,
                email,
                hashedPassword,
                role
        );

        return userRepository.save(user);
    }

    // =========================================================
    // FIND USER
    // =========================================================

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // =========================================================
    // VERIFY PASSWORD
    // =========================================================

    public boolean verifyPassword(
            String rawPassword,
            String hashedPassword) {

        return passwordEncoder.matches(
                rawPassword,
                hashedPassword
        );
    }

    // =========================================================
    // LOGIN
    // =========================================================

    public String loginUser(
            String email,
            String password,
            JwtService jwtService) {

        User user =
                userRepository.findByEmail(email);

        if (user == null) {
            throw new RuntimeException(
                    "Invalid email or password"
            );
        }

        boolean passwordMatches =
                passwordEncoder.matches(
                        password,
                        user.getPassword()
                );

        if (!passwordMatches) {
            throw new RuntimeException(
                    "Invalid email or password"
            );
        }

        // JWT contains the user's actual role
        return jwtService.generateToken(
                user.getEmail(),
                user.getRole()
        );
    }
}