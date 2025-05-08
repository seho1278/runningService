//package com.example.runningservice.config;
//
//import com.example.runningservice.enums.Role;
//import com.example.runningservice.service.OAuth2Service;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.Customizer;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//@RequiredArgsConstructor
//@EnableWebSecurity
//public class SecurityConfig {
//
//    private final OAuth2Service oAuth2Service;
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http.csrf(AbstractHttpConfigurer::disable)
//            .headers((headerConfig) -> headerConfig.frameOptions(FrameOptionsConfig::disable))
//            .authorizeHttpRequests(
//                (authorizeRequest) -> authorizeRequest.requestMatchers("/posts/new",
//                        "/comments/save").hasRole(Role.ROLE_USER.name())
//                    .requestMatchers("/", "/css/**", "images/**", "/js/**", "/login/*", "/logout/*",
//                        "/posts/**", "/comments/**").permitAll().anyRequest().authenticated())
//                .logout( // 로그아웃 성공 시 / 주소로 이동
//                    (logoutConfig) -> logoutConfig.logoutSuccessUrl("/"))
//                // OAuth2 로그인 기능에 대한 여러 설정
//                .oauth2Login(Customizer.withDefaults());
//        return http.build();
//    }
//}
