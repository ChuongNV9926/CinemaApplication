package com.example.CinemaApplication.Security;

import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomAuthenticationEntryPoint entryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final JwtFilter jwtFilter;
    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration){
        return authenticationConfiguration.getAuthenticationManager();
        // Cách tự động, Spring tự tìm bean thuộc tính của DaoAuthentcationProvider  - Ví dụ customUserDetailService
        // Thủ công: truyền customuserdetail và password encoder cho
     }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity){
              httpSecurity.sessionManagement(session->
                              session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)).
                      formLogin(AbstractHttpConfigurer::disable).
                      requestCache(AbstractHttpConfigurer::disable).
                      logout(AbstractHttpConfigurer::disable).
                      csrf(AbstractHttpConfigurer::disable).
                      authorizeHttpRequests(authorize->
                      authorize.dispatcherTypeMatchers(DispatcherType.ERROR).permitAll().requestMatchers("/api/auth/login",
                              "/api/auth/register",
                              "/api/auth/refresh").permitAll().
                              requestMatchers(HttpMethod.GET, "/api/genres",
                                      "/api/movies", "/api/movies/*", "/api/movies/*/showtimes",
                                      "/api/movies/*/reviews", "/api/movies/*/rating",
                                      "/api/showtimes/*", "/api/showtimes/*/booked-seats").permitAll().
                              requestMatchers("/api/auth/me").authenticated().
                              requestMatchers("/api/admin/**").hasRole("ADMIN").
                              requestMatchers("/api/customer/**").hasRole("CUSTOMER").
                              anyRequest().denyAll()).
                              exceptionHandling(error->
                                      error.authenticationEntryPoint(entryPoint).accessDeniedHandler(accessDeniedHandler)).
                              addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
              return  httpSecurity.build();
    }
}
