package com.procurement.procurement.config;

import com.procurement.procurement.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // Disable CSRF (Not required for REST APIs)
                .csrf(csrf -> csrf.disable())

                // Disable CORS for now (can configure later)
                .cors(cors -> cors.disable())

                // Stateless session (JWT based)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Authorization rules
                .authorizeHttpRequests(auth -> auth

                        // 🔓 Public endpoints
                        .requestMatchers("/api/auth/**").permitAll()

                        // 📄 Report APIs (Admin + Manager)
                        .requestMatchers("/reports/**")
                        .hasAnyRole("ADMIN", "PROCUREMENT_MANAGER")

                        // 🏢 Vendor APIs
                        .requestMatchers("/api/vendor/**")
                        .hasAnyRole("ADMIN", "PROCUREMENT_MANAGER", "EMPLOYEE")

                        // 📦 Requisition APIs
                        .requestMatchers("/procurement/requisition/**")
                        .hasAnyRole("ADMIN", "PROCUREMENT_MANAGER", "EMPLOYEE")

                        // 🧾 Purchase Order APIs
                        .requestMatchers("/procurement/purchase-order/**")
                        .hasAnyRole("ADMIN", "PROCUREMENT_MANAGER")

                        // ✅ Approval APIs
                        .requestMatchers("/procurement/approval/**")
                        .hasAnyRole("ADMIN", "PROCUREMENT_MANAGER")

                        // 🔐 Any other request must be authenticated
                        .anyRequest().authenticated()
                );

        // Add JWT filter before UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 🔐 Password encoder (BCrypt)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 🔑 Authentication manager
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}