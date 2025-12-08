package com.example.gateway_service.filter;

import com.example.gateway_service.util.JWTUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class WebSocketJwtFilter extends AbstractGatewayFilterFactory<WebSocketJwtFilter.Config> {

    private final JWTUtil jwtUtil;

    public WebSocketJwtFilter(JWTUtil jwtUtil) {
        super(Config.class);
        this.jwtUtil = jwtUtil;
    }

    public static class Config {}

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // 1. 쿼리 파라미터에서 토큰 추출
            String token = request.getQueryParams().getFirst("token");

            if (token == null || token.isEmpty()) {
                return onError(exchange, "No token in query param", HttpStatus.UNAUTHORIZED);
            }

            try {
                Claims claims = jwtUtil.parseClaims(token);
                String userId = claims.get("userId", String.class);
                String role = claims.get("role", String.class);

                ServerHttpRequest modifiedRequest = request.mutate()
                                                           .header("X-User-Id", userId)
                                                           .header("X-Role", role)
                                                           .build();

                return chain.filter(exchange.mutate().request(modifiedRequest).build());

            } catch (ExpiredJwtException e) {
                return onError(exchange, "Token expired", HttpStatus.UNAUTHORIZED);
            } catch (Exception e) {
                return onError(exchange, "Invalid token", HttpStatus.UNAUTHORIZED);
            }
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        log.error("WebSocket JWT Error: {}", err);
        return response.setComplete();
    }
}