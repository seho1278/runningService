package com.example.runningservice.security;

import com.example.runningservice.service.LogoutService;
import com.example.runningservice.service.OAuth2Service;
import com.example.runningservice.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final OAuth2Service oAuth2Service;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, LogoutService logoutService) throws Exception {
        http.authorizeHttpRequests(
                request -> request.requestMatchers(
                        "/",
                        "/login/**",
                        "/user/signup/**",
                        "/api.mailgun.net/v3/**",
                        "/token/refresh/**",
                        "/h2-console/**",
                        "/logout",
                        "/css/**",
                        "/images/**",
                        "/js/**",
                        "/region",
                        "/posts/**",
                        "/comments/**")
                    .permitAll().requestMatchers(
                        HttpMethod.GET, "/crew")
                    .permitAll().requestMatchers(
                        HttpMethod.GET, "**/regular/**")
                    .permitAll().requestMatchers(
                        "/posts/new",
                        "/comments/save",
                        "/crew/search/**",
                        "/user/**",
                        "crew/**")
                    .hasAnyAuthority("ROLE_USER")
                    .anyRequest().authenticated())
            .csrf(AbstractHttpConfigurer::disable)
            .headers((headerConfig) -> headerConfig.frameOptions(FrameOptionsConfig::disable))
            .sessionManagement(
                session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            .formLogin(AbstractHttpConfigurer::disable)
            .logout( // 로그아웃 성공 시 / 주소로 이동
                (logoutConfig) -> logoutConfig
                    .logoutUrl("/logout")
                    .addLogoutHandler(logoutService))
            // OAuth2 로그인 기능에 대한 여러 설정
            .oauth2Login(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil);
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return customUserDetailsService;
    }

    @Bean
    public AuthenticationManager authenticationManagerBean(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(
            AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(customUserDetailsService)
            .passwordEncoder(passwordEncoder);
        return authenticationManagerBuilder.build();
    }
}
