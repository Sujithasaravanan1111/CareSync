package com.caresync.security;

import com.caresync.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String token = null;

        /*
         * 1. First check Authorization header.
         *
         * This keeps all your existing API requests working.
         */
        String authorizationHeader =
                request.getHeader("Authorization");

        if (authorizationHeader != null &&
                authorizationHeader.startsWith("Bearer ")) {

            token = authorizationHeader
                    .substring(7)
                    .trim();
        }

        /*
         * 2. If no Authorization header exists,
         *    check the CareSync JWT cookie.
         *
         * This is needed for Thymeleaf page navigation.
         */
        if (token == null) {

            Cookie[] cookies = request.getCookies();

            if (cookies != null) {

                for (Cookie cookie : cookies) {

                    if ("caresyncToken".equals(cookie.getName())) {

                        token = cookie.getValue();
                        break;
                    }
                }
            }
        }

        /*
         * 3. No token.
         *
         * Allow request to continue.
         * Individual controllers decide whether
         * authentication is required.
         */
        if (token == null || token.isBlank()) {

            filterChain.doFilter(request, response);
            return;
        }

        /*
         * 4. Validate and extract user information.
         */
        try {

            String email =
                    jwtService.extractEmail(token);

            request.setAttribute(
                    "authenticatedUser",
                    email
            );

            /*
             * Extract role.
             */
            try {

                String role =
                        jwtService.extractRole(token);

                request.setAttribute(
                        "authenticatedRole",
                        role
                );

            } catch (Exception ignored) {
                // Role is optional for this filter.
            }

            filterChain.doFilter(request, response);

        } catch (Exception e) {

            /*
             * Invalid/expired JWT.
             */
            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
            );

            response.setContentType(
                    "text/plain;charset=UTF-8"
            );

            response.getWriter().write(
                    "JWT ERROR: " +
                    e.getClass().getSimpleName() +
                    " - " +
                    e.getMessage()
            );
        }
    }
}