package com.example.runningservice.security;

import com.example.runningservice.service.LogoutService;
import com.example.runningservice.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
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
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, LogoutService logoutService)
        throws Exception {
        http.authorizeHttpRequests(
                request -> request
                    .requestMatchers(
                        "/index.html",
                        "/css/**",
                        "/images/**",
                        "/js/**"
                        ).permitAll()
                    .requestMatchers(
                        "/",
                        "/user/signup/**",
                        "/api.mailgun.net/v3/**",
                        "/login/**",
                        "/oauth/login",
                        "/oauth/token",
                        "/oauth/authorization/**",
                        "/h2-console/**",
                        "/region",
                        "/crew",
                        "/comments/**",
                        "/token/refresh/**",
                        "/posts/**")
                    .permitAll()
                    .requestMatchers(
                        HttpMethod.GET, "/crew/*")
                    .permitAll().requestMatchers(
                        HttpMethod.GET, "**/regular/**")
                    .permitAll().requestMatchers(
                        HttpMethod.GET, "/crew/*/activity")
                    .permitAll().requestMatchers(
                        "/posts/new",
                        "/comments/save",
                        "/crew/search/**",
                        "/logout",
                        "/crew/participate",
                        "/user/**",
                        "/crew/**")
                    .hasAnyAuthority("ROLE_USER")
                    .anyRequest().authenticated())
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .headers((headerConfig) -> headerConfig.frameOptions(FrameOptionsConfig::disable))
            .sessionManagement(
                session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtAuthenticationFilter(), LogoutFilter.class)
            .formLogin(AbstractHttpConfigurer::disable)
            .logout(
                (logoutConfig) -> logoutConfig
                    .logoutUrl("/logout")
                    .addLogoutHandler(logoutService)
                    .logoutSuccessHandler((request, response, authentication) -> {
                        response.setStatus(HttpServletResponse.SC_OK);
                        response.getWriter().write("{\"message\":\"Successfully logged out\"}");
                        response.setContentType("application/json");
                        response.getWriter().flush();
                    })
            )
            .oauth2Login(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(
            Arrays.asList("http://localhost:3000", "http://localhost:3001",
                "http://localhost:3002", "http://127.0.0.1:3001", "http://127.0.0.1:3000",
                "http://127.0.0.1:3002", "http://localhost:63342", "http://127.0.0.1:63342"));
        configuration.setAllowedMethods(
            Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
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

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService() {
        return new DefaultOAuth2UserService();
    }
}
