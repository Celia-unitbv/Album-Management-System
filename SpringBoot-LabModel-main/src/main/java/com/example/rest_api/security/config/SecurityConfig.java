package com.example.rest_api.security.config;

import com.example.rest_api.service.RoleService;
import com.example.rest_api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final UserService userService;
    private final DynamicAuthorizationManager dynamicAuthorizationManager;
    private final CustomAuthenticationSuccessHandler successHandler;

    @Autowired
    public SecurityConfig(UserService userService, RoleService roleService, DynamicAuthorizationManager dynamicAuthorizationManager, CustomAuthenticationSuccessHandler successHandler) {
        this.userService = userService;
        this.dynamicAuthorizationManager = dynamicAuthorizationManager;
        this.successHandler = successHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        DefaultSecurityFilterChain build = http
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/", "/login/**", "/register", "/oauth2/**").permitAll() // Permite accesul pentru aceste rute
                        .anyRequest().access(dynamicAuthorizationManager) // Orice altă rută este verificată dinamic
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .userInfoEndpoint(userInfo -> userInfo.oidcUserService(userService))
                        .successHandler(successHandler)
                )
                .formLogin(form -> form
                        .loginPage("/login") // Pagină personalizată pentru login
                        .usernameParameter("email") // Mapăm câmpul "email" ca username
                        .passwordParameter("password")
                        .successHandler(successHandler)
                        .failureUrl("/login?error=true")
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll())
                .csrf(csrf -> csrf.disable())
                .build();
        return build;
    }


    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
