package com.github.bookproject.auth.service;

import com.github.bookproject.auth.dto.KakaoDTO;
import com.github.bookproject.auth.entity.Role;
import com.github.bookproject.auth.entity.User;
import com.github.bookproject.auth.repository.UserRepository;
import com.github.bookproject.global.config.auth.JwtTokenProvider;
import com.github.bookproject.global.config.auth.custom.CustomUserDetails;
import com.github.bookproject.global.dto.JWTToken;
import com.github.bookproject.global.util.KaKaoUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final KaKaoUtil kaKaoUtil;


    public User oauthLogin(String accessCode, HttpServletResponse httpServletResponse) {
        KakaoDTO.OAuthToken oAuthToken = kaKaoUtil.requestToken(accessCode);
        KakaoDTO kakaoDTO = kaKaoUtil.requestProfile(oAuthToken);  // 최상위 DTO 반환


        String email = kakaoDTO.getKakaoAccount().getEmail();

        String nickname;
        if (kakaoDTO.getKakaoAccount().getProfile() != null) {
            nickname = kakaoDTO.getKakaoAccount().getProfile().getNickname();
        } else if (kakaoDTO.getProperties() != null) {
            nickname = kakaoDTO.getProperties().getNickname();
        }else {
            nickname = "기본 닉네임";
        }

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> createNewUser(kakaoDTO,nickname));



        // customUserDetails 객체 생성
        CustomUserDetails customUserDetails = new CustomUserDetails(user);

        // CustomUserDetails -> Authentication 변환
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                customUserDetails,null,customUserDetails.getAuthorities()
        );

        JWTToken jwtToken = jwtTokenProvider.generateToken(authentication);

        // jwt 를 응답 헤더에 추가
        httpServletResponse.setHeader("Authorization", jwtToken.getAccessToken());
        httpServletResponse.setHeader("Refresh-Token", jwtToken.getRefreshToken());

        return user;

    }

    private User createNewUser(KakaoDTO kakaoDTO, String nickname) {

        String email = kakaoDTO.getKakaoAccount().getEmail();
        String profile = kakaoDTO.getKakaoAccount().getProfile().getProfileImageUrl();

        User newUser = User.builder()
                .username(nickname)
                .password(UUID.randomUUID().toString()) // 임시 비밀번호 생성
                .email(email)
                .profileImageUrl(profile)
                .role(Role.ROLE_USER)
                .build();

        return userRepository.save(newUser);

    }
}
