package com.github.bookproject.auth.controller;

import com.github.bookproject.auth.dto.JoinRequestDTO;
import com.github.bookproject.auth.dto.UserResponseDTO;
import com.github.bookproject.auth.entity.User;
import com.github.bookproject.auth.service.AuthService;
import com.github.bookproject.auth.service.UserService;
import com.github.bookproject.global.config.auth.custom.CustomUserDetails;
import com.github.bookproject.global.dto.ApiResponse;
import com.github.bookproject.global.exception.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    @Operation(summary = "이메일 중복 검증")
    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse<String>> checkEmail(@RequestParam String email) {
        if (userService.checkEmail(email)) {
            return ResponseEntity.ok(ApiResponse.fail(ErrorCode.EMAIL_ALREADY_EXISTS,ErrorCode.EMAIL_ALREADY_EXISTS.getMessage()));
        }
        return ResponseEntity.ok(ApiResponse.success("✅사용가능한 이메일 입니다."));
    }


    @Operation(summary = "회원가입")
    @PostMapping(value = "/join", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> createUser(
            @RequestPart("user") JoinRequestDTO joinRequestDTO,
            @RequestPart(value = "image", required = false) MultipartFile profileImage) {

            userService.createUser(joinRequestDTO, profileImage);
            return ResponseEntity.ok(ApiResponse.success("✅회원가입이 완료되었습니다."));

        }


    @Operation(summary = "OAuth2 로그인 구현 (카카오)")
    @GetMapping("/login/kakao")
    public ResponseEntity<ApiResponse<UserResponseDTO.JoinResultDTO>> kakaoLogin(@RequestParam("code") String accessCode,
                                                                                HttpServletResponse httpServletResponse) {

        log.info("Received Kakao Authorization Code: {}", accessCode);
        // OAuth 로그인 후 , user 객체 반환
        User user = authService.oauthLogin(accessCode,httpServletResponse);

        UserResponseDTO.JoinResultDTO joinResultDTO = UserResponseDTO.JoinResultDTO.from(user);

        return ResponseEntity.ok(ApiResponse.success(joinResultDTO));
    }

    @Operation(summary = "유저 탈퇴 (사용자 본인)")
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<String>> deleteUser(@AuthenticationPrincipal CustomUserDetails user) {
        log.info("🔐 사용자 정보: {}", user);
        Long userId = user.getUser().getId();
        userService.deleteUser(userId);
        return ResponseEntity.ok(ApiResponse.success("✅탈퇴처리 되었습니다."));
    }
}

