package com.baha.oop.config;

import com.baha.oop.security.CustomAccessDeniedHandler;
import com.baha.oop.security.JwtAuthenticationEntryPoint;
import com.baha.oop.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// This class configures the security settings for our application.
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                          CustomAccessDeniedHandler customAccessDeniedHandler) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
    }

    // This method defines which paths are public and which ones need authentication or special roles.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF protection because we are using stateless JWT tokens.
                .csrf(csrf -> csrf.disable())
                // Use stateless sessions because we authenticate every request with a token.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Set custom handlers for authentication errors and access denied errors.
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler))
                // Define access rules for different URLs.
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints that anyone can access without logging in.
                        .requestMatchers(HttpMethod.POST, "/api/auth/**").permitAll()
                        .requestMatchers("/login", "/logout").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        
                        // Thymeleaf UI page permissions.
                        .requestMatchers(HttpMethod.GET, "/books").authenticated()
                        .requestMatchers("/books/**").hasRole("LIBRARIAN")
                        .requestMatchers("/members/**").hasRole("LIBRARIAN")
                        .requestMatchers("/borrowings/new", "/borrowings/borrow", "/borrowings/return").hasAnyRole("LIBRARIAN", "MEMBER")
                        .requestMatchers("/borrowings/my-history").hasRole("MEMBER")
                        .requestMatchers("/borrowings/**").hasRole("LIBRARIAN")
                        .requestMatchers("/purchases/buy/**", "/purchases/buy").hasAnyRole("LIBRARIAN", "MEMBER")
                        .requestMatchers("/purchases/**").hasRole("LIBRARIAN")
                        .requestMatchers("/").authenticated()
                        
                        // REST API permissions.
                        .requestMatchers("/api/books/search").hasAnyRole("LIBRARIAN", "MEMBER")
                        .requestMatchers("/api/books/**").hasRole("LIBRARIAN")
                        .requestMatchers("/api/members/**").hasRole("LIBRARIAN")
                        .requestMatchers("/api/borrowings/my-history").hasRole("MEMBER")
                        .requestMatchers("/api/borrowings/**").hasRole("LIBRARIAN")
                        
                        // Any other request must be authenticated.
                        .anyRequest().authenticated())
                // Add our custom JWT filter before the default username/password filter.
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Use BCrypt algorithm to hash and verify passwords.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Enable Thymeleaf Spring Security tags like sec:authorize in our HTML files.
    @Bean
    public org.thymeleaf.extras.springsecurity6.dialect.SpringSecurityDialect springSecurityDialect() {
        return new org.thymeleaf.extras.springsecurity6.dialect.SpringSecurityDialect();
    }

    // Expose AuthenticationManager to handle login authentication.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
