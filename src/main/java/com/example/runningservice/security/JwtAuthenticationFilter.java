package com.example.runningservice.security;

import com.example.runningservice.exception.CustomException;
import com.example.runningservice.exception.ErrorCode;
import com.example.runningservice.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final String ACCESS_TOKEN_HEADER = "Authorization";
    private final String TOKEN_PREFIX = "Bearer ";

    //본인의 토큰이 맞고 유효기간이 지나지 않았을 때
    // 토큰이 없거나 유효하지 않은 경우 필터 체인으로 넘김
    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain chain
    ) throws ServletException, IOException {
        try {
            String accessJwt = resolveToken(request, ACCESS_TOKEN_HEADER);
            log.info("request path: {}", request.getRequestURI());
            log.info("accessJwt: {}", accessJwt);
            if (accessJwt != null) {
                if (!jwtUtil.isTokenExpired(accessJwt)) {
                    Authentication authentication = jwtUtil.getAuthentication(accessJwt);
                    log.info("Filtering request token Authentication: {}", authentication);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.info(String.format("[%s] -> %s ",
                        jwtUtil.extractEmail(accessJwt), request.getRequestURI())
                    );
                    // LoginUserResolver에서 request를 통해 가져오기 위해 토큰에서 id를 가져와 저장한다.
                    request.setAttribute("loginId", jwtUtil.extractUserId(accessJwt));
                } else {
                    throw new CustomException(ErrorCode.TOKEN_EXPIRED);
                }
            }
            log.info("Filtering request token: {}", accessJwt);
            log.info("authentication: {}", SecurityContextHolder.getContext().getAuthentication());
            chain.doFilter(request, response);
        } catch (CustomException e) {
            log.error("JWT authentication failed: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // 401 Unauthorized
            response.setContentType("application/json; charset=UTF-8");  // 인코딩 설정
            response.getWriter().write(
                "{\"errorCode\": \"" + e.getErrorCode().name() + "\", \"message\": \""
                    + e.getMessage() + "\"}");
            response.getWriter().flush();
        }
    }

    private String resolveToken(HttpServletRequest request, String header) {
        String token = request.getHeader(header);

        if (!ObjectUtils.isEmpty(token) && token.startsWith(TOKEN_PREFIX)) {
            return token.substring(TOKEN_PREFIX.length());
        }
        return null;
    }
}


