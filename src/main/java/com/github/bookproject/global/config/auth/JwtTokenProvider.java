package com.github.bookproject.global.config.auth;

import com.github.bookproject.auth.entity.User;
import com.github.bookproject.auth.repository.UserRepository;
import com.github.bookproject.global.config.auth.custom.CustomUserDetails;
import com.github.bookproject.global.exception.AppException;
import com.github.bookproject.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import com.github.bookproject.global.dto.JWTToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.stream.Collectors;


@RequiredArgsConstructor
public class JwtTokenProvider {

    private final UserRepository userRepository;
    private final   SecretKey secretkey;


    // 토큰의 서명을 생성하기 위한 비밀키 설정
    public JwtTokenProvider(String secret, UserRepository userRepository) {
        this.userRepository = userRepository;
        System.out.println(">>> SECRET KEY FROM YML: " + secret);
        this.secretkey = Keys.secretKeyFor(SignatureAlgorithm.HS256); // 자동으로 256비트 이상의 키를 생성해줌
    }

    // user 정보를 가지고 accessToken, RefreshToken 생성하는 메서드
    public JWTToken generateToken(Authentication authentication) {
        // 권한 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();

        // AccessToken 생성
        Date expiration = new Date(now + 1000 * 60 * 60 * 24);
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim("authorities", authorities)
                .setExpiration(expiration)
                .signWith(secretkey, SignatureAlgorithm.HS256)
                .compact();

        // RefreshToken 생성
        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + 1000 * 60 * 60 * 24))
                .signWith(secretkey, SignatureAlgorithm.HS256)
                .compact();

        return JWTToken.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

    }

    // jwt 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
    public Authentication getAuthentication(String accessToken) {
        // jwt 토큰 복호화
        Claims claims = parseClaims(accessToken);

        if (claims.get("authorities") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        // email 기반으로 db 에서 유저 조회
        String email = claims.getSubject();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        CustomUserDetails userDetails = new CustomUserDetails(user);


        // userDetails 객체를 만들어서 Authentication 반환
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());

    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        {
            try {
                Jwts.parserBuilder()
                        .setSigningKey(secretkey)  // 서명 검증
                        .build()
                        .parseClaimsJws(token);
                return true;
            }catch (Exception e) {
                return false;
            }
        }
    }

    // 토큰에서 Claims 파싱
    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretkey)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        }catch (Exception e) {
            return null;
        }
    }

    public long getRemainingExpiration(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretkey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getExpiration().getTime() - System.currentTimeMillis();
    }
}
