package com.github.bookproject.global.config.auth;

import com.github.bookproject.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
@RequiredArgsConstructor
public class JwtConfig {

    private final UserRepository userRepository;

    @Bean
    public JwtTokenProvider jwtTokenProvider(@Value("${spring.jwt.secret}") String secret) {
        return new JwtTokenProvider(secret, userRepository);

    }

}
