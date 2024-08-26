package com.example.runningservice.service;

import com.example.runningservice.exception.CustomException;
import com.example.runningservice.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogoutService implements LogoutHandler {

    private final TokenBlackList tokenBlackList;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) {

        if (authentication == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        String refreshToken = authHeader.substring("Bearer ".length());

        if (tokenBlackList.isListed(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        tokenBlackList.add(refreshToken);
        SecurityContextHolder.clearContext();
    }
}
