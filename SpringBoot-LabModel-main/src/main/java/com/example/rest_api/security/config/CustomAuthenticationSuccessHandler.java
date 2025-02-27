package com.example.rest_api.security.config;

import com.example.rest_api.database.users.model.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    Logger logger = LoggerFactory.getLogger(CustomAuthenticationSuccessHandler.class);

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        AuthenticationSuccessHandler.super.onAuthenticationSuccess(request, response, chain, authentication);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        if (authorities.contains(new SimpleGrantedAuthority(Role.ADMIN.name()))) {
            logger.info("redirecting to admin endpoint");
            response.sendRedirect("/admin/dashboard");
        } else if (authorities.contains(new SimpleGrantedAuthority(Role.USER.name()))) {
            logger.info("redirecting to home endpoint");
            response.sendRedirect("/user/home");
        } else if (authorities.contains(new SimpleGrantedAuthority(Role.USER.name()))) {
            logger.info("Redirecting to user home");
            response.sendRedirect("/user/home");
        } else {
            response.sendRedirect("/login?error=true"); // Default fallback
        }
    }
}
