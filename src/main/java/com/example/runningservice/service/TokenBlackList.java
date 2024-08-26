package com.example.runningservice.service;

import com.example.runningservice.exception.CustomException;
import com.example.runningservice.exception.ErrorCode;
import com.example.runningservice.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenBlackList {
    private final JwtUtil jwtUtil;

    private final Map<String, Date> blacklist = new ConcurrentHashMap<>();

    public void add(String refreshToken) {
        log.debug("logoutService: refreshToken: {}", refreshToken);
        Date expiry = jwtUtil.extractAllClaims(refreshToken)
                .getExpiration();
        // 토큰을 블랙리스트에 추가
        blacklist.put(refreshToken, expiry);
    }

    // 토큰이 블랙리스트에 있는지 확인
    public boolean isListed(String token) {
        if (token == null) {
            throw new CustomException(ErrorCode.NO_VALID_REFRESH_TOKEN);
        }
        if (blacklist.isEmpty()) {
            return false;
        }

        return blacklist.containsKey(token) &&
                blacklist.get(token).after(new Date());
    }
    // 주기적으로 블랙리스트에서 만료된 토큰 제거
    @Scheduled(fixedRate = 60000 * 60) //1시간
    public void removeExpiredTokens() {
        blacklist.entrySet().removeIf(entry -> entry.getValue().before(new Date()));
    }
}
