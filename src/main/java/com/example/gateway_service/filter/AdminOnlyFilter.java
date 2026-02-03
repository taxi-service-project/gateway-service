package com.example.gateway_service.filter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import com.example.gateway_service.util.JWTUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AdminOnlyFilter extends AbstractGatewayFilterFactory<AdminOnlyFilter.Config> {

    private final JWTUtil jwtUtil;

    public AdminOnlyFilter(JWTUtil jwtUtil) {
        super(Config.class);
        this.jwtUtil = jwtUtil;
    }

    public static class Config {
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "No authorization header", HttpStatus.UNAUTHORIZED);
            }

            String authorizationHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return onError(exchange, "Invalid token", HttpStatus.UNAUTHORIZED);
            }

            String token = authorizationHeader.replace("Bearer ", "");

            try {
                // 2. 토큰 파싱
                Claims claims = jwtUtil.parseClaims(token);
                String role = claims.get("role", String.class);

                //  관리자 권한 체크
                if (!"ROLE_ADMIN".equals(role)) {
                    log.warn("⛔ 일반 유저가 관리자 페이지 접근 시도: {}", claims.get("userId"));
                    return onError(exchange, "Admin access denied", HttpStatus.FORBIDDEN); // 403 리턴
                }

                ServerHttpRequest modifiedRequest = request.mutate()
                                                           .header("X-User-Id", claims.get("userId", String.class))
                                                           .header("X-Role", role) // ROLE_ADMIN
                                                           .build();

                return chain.filter(exchange.mutate().request(modifiedRequest).build());

            } catch (ExpiredJwtException e) {
                return onError(exchange, "Token is expired", HttpStatus.UNAUTHORIZED);
            } catch (JwtException | IllegalArgumentException e) {
                // 서명 불일치, 구조 이상 등 모든 JWT 오류
                return onError(exchange, "Token is invalid", HttpStatus.UNAUTHORIZED);
            }
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        log.error("JWT Error: {}", err);
        return response.setComplete();
    }
}
