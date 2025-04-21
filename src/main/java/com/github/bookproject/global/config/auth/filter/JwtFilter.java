package com.github.bookproject.global.config.auth.filter;

import com.github.bookproject.global.config.auth.JwtTokenProvider;
import com.github.bookproject.global.config.auth.redis.RedisService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
// JWT 통해 사용자 인증 정보를 처리
public class JwtFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String uri = request.getRequestURI();
        log.info("📌 요청 URI: {}", uri);

        if (uri.startsWith("/login/kakao")) {
            filterChain.doFilter(request, response);
            return;
        }

        // request header 에서 JWT 토큰 추출
        String token = resolveToken(request);


        // 토큰이 유효하면 Authentication 객체를 생성하고 SecurityContext 에 저장
        if (token != null &&jwtTokenProvider.validateToken(token)) {

            // 블랙리스트 확인
            if (redisService.isBlacklisted(token)) {
                log.warn("블랙리스트 토큰입니다. 접근 차단");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("{\"message\": \"해당 토큰은 로그아웃 처리된 토큰입니다.\"}");
                return;  // 필터 중단
            }

            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            log.info("🔐 JwtFilter 인증된 사용자: {}", authentication.getName());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    // Authorization 헤더에서 Bearer 토큰 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
