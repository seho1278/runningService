package com.example.runningservice.util;

import static io.jsonwebtoken.Jwts.SIG.HS256;

import com.example.runningservice.enums.Role;
import com.example.runningservice.exception.CustomException;
import com.example.runningservice.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretStr;
    private SecretKey SECRET_KEY;
    private final Long REFRESH_TOKEN_EXPIRATION = 1000L * 60 * 60 * 24 * 7; //7일
    private final Long ACCESS_TOKEN_EXPIRATION = 1000L * 60 * 30; //30분

    @PostConstruct
    public void init() {
        byte[] decodedKey = Base64.getDecoder().decode(secretStr);
        SECRET_KEY = new SecretKeySpec(decodedKey, 0, decodedKey.length, "HmacSHA256");
    }

    public String generateToken(String email, Long userId, List<GrantedAuthority> authorities) {
        return createToken(email, userId, authorities, ACCESS_TOKEN_EXPIRATION);
    }

    public String generateRefreshToken(String email, Long userId,List<GrantedAuthority> authorities) {
        return createToken(email, userId, authorities, REFRESH_TOKEN_EXPIRATION); // 7 days
    }

    private String createToken(String username, Long userId, List<GrantedAuthority> authorities,
        long expirationTime) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles",
            authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        claims.put("userId", userId);
        return Jwts.builder().claims(claims).subject(username)
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + expirationTime))
            .signWith(SECRET_KEY, HS256).compact();
    }

    public Claims extractAllClaims(String token) {
        log.debug("extract token: {}", token);
        try {
            log.debug("token: {}", token);
            return Jwts.parser().verifyWith(SECRET_KEY).build().parseSignedClaims(token)
                .getPayload();
        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }

    public boolean isTokenExpired(String token) {
        boolean result = extractAllClaims(token).getExpiration().before(new Date());
        log.debug("result: {}", result);
        return result;
    }

    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    public Long extractUserId(String token) {
        return extractAllClaims(token).get("userId", Long.class);
    }

    public List<Role> extractRoles(String token) {
        List<String> roles = extractAllClaims(token).get("roles", List.class);
        return roles.stream().map(Role::valueOf).collect(Collectors.toList());
    }

    public boolean validateToken(String email, String token) {
        boolean equals = extractEmail(token).equals(email);
        log.debug("user email equals token owner email: {}", equals);
        return equals && !isTokenExpired(token);
    }

    public boolean validateToken(Long id, String token) {
        return extractUserId(token).equals(id) && !isTokenExpired(token);
    }

    public Authentication getAuthentication(String jwt) {
        log.info("Jwt : {}", jwt);
        String email = extractEmail(jwt);
        List<Role> roles = extractRoles(jwt);

        User userDetails = new User(email, "",
            roles.stream().map(role -> new SimpleGrantedAuthority(String.valueOf(role)))
                .collect(Collectors.toList()));

        return new UsernamePasswordAuthenticationToken(userDetails, "",
            userDetails.getAuthorities());
    }
}
