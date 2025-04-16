package com.github.bookproject.global.config.auth;

import com.github.bookproject.global.config.auth.filter.JwtFilter;
import com.github.bookproject.global.config.auth.filter.LoginFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtFilter jwtFilter;
    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    // 로그인 시에 사용자의 비밀번호를 db 에서 가져온 암호화된 비밀번호와 비교할때 사용됨
    public BCryptPasswordEncoder bCryptPasswordEncoder() {  // 비밀번호 암호화
        return new BCryptPasswordEncoder();
    }

    @Bean
    // HttpSecurity 객체 사용해서 보안 관련 설정하는 메서드
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        System.out.println("✅ filterChain 실행됨");

        http
                .csrf((auth) -> auth.disable());    // csrf 비활성화
        http
                .formLogin((auth) -> auth.disable());   // 기본 로그인폼 비활성화
        http
                .httpBasic((auth) -> auth.disable());   // http 기본 인증 비활성화
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/","/join","/check-email","/login/**","/swagger-ui/**","/v3/api-docs/**").permitAll()  // 모든 사용자
                        .requestMatchers("/api/**").authenticated()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated());  // 인증된 사용자
        http
                .addFilterBefore(jwtFilter,UsernamePasswordAuthenticationFilter.class);

        LoginFilter loginFilter = new LoginFilter(authenticationManager(authenticationConfiguration), jwtTokenProvider);
        loginFilter.setFilterProcessesUrl("/login");   // 로그인 엔드포인트 지정 (동작보장)
        http.addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class);  // 로그인 커스텀 필터 추가

        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));  // jwt 토큰 사용시 세션 무상태로 변경해줘야 함
        return http.build();
    }
}
