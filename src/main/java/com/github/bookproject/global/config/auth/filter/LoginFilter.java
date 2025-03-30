package com.github.bookproject.global.config.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bookproject.auth.dto.LoginRequestDTO;
import com.github.bookproject.global.config.auth.JwtTokenProvider;
import com.github.bookproject.global.dto.ApiResponse;
import com.github.bookproject.global.dto.JWTToken;
import com.github.bookproject.global.exception.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;


@RequiredArgsConstructor
// 로그인 커스텀 필터 (아이디,비밀번호 검증 필터)
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        String email;
        String password;

        try {
            if (request.getContentType() != null && request.getContentType().contains("application/json")) {
                LoginRequestDTO loginRequest = mapper.readValue(request.getInputStream(), LoginRequestDTO.class);
                email = loginRequest.getEmail();
                password = loginRequest.getPassword();
            }else {
                // x-www-form-urlencoded 요청 처리
                email = request.getParameter("email");
                password = request.getParameter("password");
            }

            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(email, password);
            return authenticationManager.authenticate(authRequest);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 로그인 성공시 실행하는 메서드 (jwt 토큰 발급)
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {
        // JWT 생성 (accessToken & refreshToken)
        JWTToken jwtToken = jwtTokenProvider.generateToken(authentication);

        // JSON 응답 추가
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ApiResponse<JWTToken> apiResponse  = ApiResponse.success(jwtToken);

        response.getWriter().write(mapper.writeValueAsString(apiResponse));
    }

    // 로그인 실패시 실행하는 메서드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ApiResponse<String> apiResponse = ApiResponse.fail(ErrorCode.LOGIN_FAILED,ErrorCode.LOGIN_FAILED.getMessage());

        response.getWriter().write(mapper.writeValueAsString(apiResponse));
    }

}
