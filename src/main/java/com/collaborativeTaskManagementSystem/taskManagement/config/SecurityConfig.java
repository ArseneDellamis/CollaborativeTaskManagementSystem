package com.collaborativeTaskManagementSystem.taskManagement.config;


import com.collaborativeTaskManagementSystem.taskManagement.jwtpackage.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
/*
security configuration class is responsible for
all web authorization,validation and authentication mechanism
 */

    //    injecting some properties
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    //   SecurityFilterChain is core component used to define the security configuration and behavior for web requests
//    it sets up the security filters and rules that govern how requests are authenticated, authorized, and handled
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF to prevent attacks
                .authorizeHttpRequests((authorize) ->
                        authorize
                                // Allow access to authentication endpoints (register, login)
                                .requestMatchers("/api/auth/**").permitAll()

                                // Allow GET requests to /api/employees/** for all authenticated users
                                .requestMatchers(HttpMethod.GET, "/api/tasks/**").authenticated()

                                // Restrict POST, PUT, DELETE to only users with ADMIN,PROJECT_MANAGER role
                                .requestMatchers(HttpMethod.POST, "/api/tasks/**")
                                .hasAnyAuthority("ADMIN", "PROJECT_MANAGER")
                                .requestMatchers(HttpMethod.PUT, "/api/tasks/**")
                                .hasAnyAuthority("ADMIN", "PROJECT_MANAGER")
                                .requestMatchers(HttpMethod.DELETE, "/api/tasks/**")
                                .hasAnyAuthority("ADMIN", "PROJECT_MANAGER")
                                .requestMatchers(HttpMethod.POST, "/api/employees/**")
                                .hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/api/employees/**")
                                .hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/api/employees/**")
                                .hasAuthority("ADMIN")
                                // Any other request must be authenticated
                                .anyRequest().authenticated()
                )
                // Set session management policy to stateless (JWT-based authentication)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // Register authentication provider
                .authenticationProvider(authenticationProvider)
                // Add JWT authentication filter before UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://127.0.0.1:5500", "http://localhost:5500")); // Allow Live Server
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // HTTP methods
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type")); // Headers
        configuration.setAllowCredentials(true); // Allow cookies if needed
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Apply to all endpoints
        return source;
    }

}
