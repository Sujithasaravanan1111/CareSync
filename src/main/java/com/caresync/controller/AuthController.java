package com.caresync.controller;

import com.caresync.entity.User;
import com.caresync.service.JwtService;
import com.caresync.service.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(
            UserService userService,
            JwtService jwtService) {

        this.userService = userService;
        this.jwtService = jwtService;
    }

    // =========================================================
    // REGISTER
    // =========================================================

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegistrationRequest request) {

        try {

            User user = userService.registerUser(
                    request.getName(),
                    request.getEmail(),
                    request.getPassword(),
                    request.getRole()
            );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("Registration successful");

        } catch (RuntimeException e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    // =========================================================
    // LOGIN
    // =========================================================

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request,
            HttpServletResponse response) {

        try {

            String token = userService.loginUser(
                    request.getEmail(),
                    request.getPassword(),
                    jwtService
            );

            Cookie cookie = new Cookie(
                    "caresyncToken",
                    token
            );

            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(24 * 60 * 60);

            response.addCookie(cookie);

            return ResponseEntity.ok(
                    new LoginResponse(token)
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(e.getMessage());
        }
    }

    // =========================================================
    // LOGIN REQUEST
    // =========================================================

    public static class LoginRequest {

        private String email;
        private String password;

        public LoginRequest() {
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    // =========================================================
    // LOGIN RESPONSE
    // =========================================================

    public static class LoginResponse {

        private String token;

        public LoginResponse(String token) {
            this.token = token;
        }

        public String getToken() {
            return token;
        }
    }

    // =========================================================
    // REGISTRATION REQUEST
    // =========================================================

    public static class RegistrationRequest {

        private String name;
        private String email;
        private String password;
        private String role;

        public RegistrationRequest() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }

    // =========================================================
    // PROFILE
    // =========================================================

    @GetMapping("/profile")
    public ResponseEntity<?> profile(
            @RequestAttribute(
                    value = "authenticatedUser",
                    required = false)
            String email) {

        if (email == null) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication required");
        }

        User user = userService.findByEmail(email);

        if (user == null) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("User not found");
        }

        return ResponseEntity.ok(
                "Welcome " + user.getName()
                        + ". Your email is "
                        + user.getEmail()
        );
    }

    // =========================================================
    // PATIENT AREA
    // =========================================================

    @GetMapping("/patient-area")
    public ResponseEntity<?> patientArea(
            @RequestAttribute(
                    value = "authenticatedRole",
                    required = false)
            String role) {

        if (role == null) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication required");
        }

        if (!"PATIENT".equalsIgnoreCase(role)) {

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Access denied");
        }

        return ResponseEntity.ok(
                "Welcome to the Patient Area"
        );
    }

    // =========================================================
    // CAREGIVER AREA
    // =========================================================

    @GetMapping("/caregiver-area")
    public ResponseEntity<?> caregiverArea(
            @RequestAttribute(
                    value = "authenticatedRole",
                    required = false)
            String role) {

        if (role == null) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication required");
        }

        if (!"CAREGIVER".equalsIgnoreCase(role)) {

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(
                            "Access denied. "
                                    + "Caregiver role required."
                    );
        }

        return ResponseEntity.ok(
                "Welcome to the Caregiver Area"
        );
    }

    // =========================================================
    // DOCTOR AREA
    // =========================================================

    @GetMapping("/doctor-area")
    public ResponseEntity<?> doctorArea(
            @RequestAttribute(
                    value = "authenticatedRole",
                    required = false)
            String role) {

        if (role == null) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication required");
        }

        if (!"DOCTOR".equalsIgnoreCase(role)) {

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(
                            "Access denied. "
                                    + "Doctor role required."
                    );
        }

        return ResponseEntity.ok(
                "Welcome to the Doctor Area"
        );
    }

    // =========================================================
    // ADMIN AREA
    // =========================================================

    @GetMapping("/admin-area")
    public ResponseEntity<?> adminArea(
            @RequestAttribute(
                    value = "authenticatedRole",
                    required = false)
            String role) {

        if (role == null) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication required");
        }

        if (!"ADMIN".equalsIgnoreCase(role)) {

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(
                            "Access denied. "
                                    + "Admin role required."
                    );
        }

        return ResponseEntity.ok(
                "Welcome to the Admin Area"
        );
    }
}