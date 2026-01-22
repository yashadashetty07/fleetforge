package com.fleetforge.apigateway.filters;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        if (path.startsWith("/api/auth/login") || path.startsWith("/api/auth/register")) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return this.onError(exchange, "Missing or invalid Authorization header", HttpStatus.UNAUTHORIZED);
        }
        String token = authHeader.substring(7);

        Claims claims;

        try {

            claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

        } catch (Exception e) {
            return this.onError(exchange, "Invalid or expired JWT", HttpStatus.UNAUTHORIZED);
        }

        String role=claims.get("role",String.class);

        if (path.startsWith("/api/vehicles") ||
                path.startsWith("/api/drivers") ||
                path.startsWith("/api/routes") ||
                path.startsWith("/api/trips/admin") ||   // you can adjust later
                path.startsWith("/api/locations/admin")) {

            if (!"ADMIN".equals(role)) {
                return this.onError(exchange, "Access denied: ADMIN required", HttpStatus.FORBIDDEN);
            }
        }

        if (path.startsWith("/api/trips/driver") ||
                path.startsWith("/api/locations/driver")) {

            if (!"DRIVER".equals(role)) {
                return this.onError(exchange, "Access denied: DRIVER required", HttpStatus.FORBIDDEN);
            }
        }

        ServerWebExchange modifiedExchange = exchange.mutate()
                .request(r -> r.headers(headers -> {
                    headers.remove("X-User-Name");   // 🔥 force remove
                    headers.remove("X-User-Role");
                    headers.set("X-User-Name", claims.getSubject());
                    headers.set("X-User-Role", role);
                }))
                .build();



        return chain.filter(modifiedExchange);
    }

    private Mono<Void> onError(ServerWebExchange exchange, String errorMsg, HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        byte[] bytes = errorMsg.getBytes();
        return exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse()
                        .bufferFactory()
                        .wrap(bytes)));
    }


    @Override
    public int getOrder() {
        return 0;
    }
}
