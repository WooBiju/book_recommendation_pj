package com.github.bookproject.mypage.controller;

import com.github.bookproject.global.config.auth.custom.CustomUserDetails;
import com.github.bookproject.global.dto.ApiResponse;
import com.github.bookproject.mypage.dto.*;
import com.github.bookproject.mypage.service.MyPageService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/myPage")
public class MyPageController {

    private final MyPageService myPageService;

    @Operation(summary = "마이페이지 홈 - 사용자 정보 + 활동 요약")
    @GetMapping
    public ResponseEntity<ApiResponse<MyPageHomeDTO>> getMyPageHome(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(myPageService.getMyPageHome(userDetails.getUser().getId())));
    }

    @Operation(summary = "내 정보 수정")
    @PatchMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> updateMyProfile(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                              @RequestPart("dto") UpdateUserDTO dto,
                                                               @RequestParam(value = "profileImage", required = false) MultipartFile image) {
        myPageService.updateMyProfile(userDetails.getUser().getId(),dto,image);
        return ResponseEntity.ok(ApiResponse.success("✅회원 정보가 수정되었습니다."));
    }

    @Operation(summary = "비밀번호 수정")
    @PatchMapping("/profile/password")
    public ResponseEntity<ApiResponse<String>> changeMyPassword(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                @RequestBody PasswordChangeDTO dto) {
        myPageService.changeMyPassword(userDetails.getUser().getId(),dto);
        return ResponseEntity.ok(ApiResponse.success("비밀번호가 변경되었습니다."));
    }

    @Operation(summary = "선호 장르 조회")
    @GetMapping("/preferences")
    public ResponseEntity<ApiResponse<List<String>>> getMyPreferences(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return myPageService.getMyPreferences(userDetails.getUser().getId());
    }

    @Operation(summary = "선호 장르 수정")
    @PatchMapping("/preferences")
    public ResponseEntity<ApiResponse<String>> updateMyPreferences(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                   @RequestBody List<String> preferences) {
        myPageService.updateMyPreferences(userDetails.getUser().getId(),preferences);
        return ResponseEntity.ok(ApiResponse.success("✅선호 장르가 수정되었습니다."));
    }

    @Operation(summary = "찜한 도서 목록")
    @GetMapping("/favorites")
    public ResponseEntity<ApiResponse<List<BookSummaryDTO.BookSimpleDTO>>> getMyFavorites(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return myPageService.getMyFavorites(userDetails.getUser().getId());
    }

    @Operation(summary = "내가 쓴 리뷰 조회")
    @GetMapping("/reviews")
    public ResponseEntity<ApiResponse<List<ReviewSummaryDTO.ReviewInfoDTO>>> getMyReviews(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return myPageService.getMyReviews(userDetails.getUser().getId());
    }

    @Operation(summary = "내 독서기록 전체 조회")
    @GetMapping("/records")
    public ResponseEntity<ApiResponse<List<ReadingRecordSummaryDTO.ReadingRecordSimpleDTO>>> getMyReadingRecords(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return myPageService.getMyReadingRecords(userDetails.getUser().getId());
    }

    @Operation(summary = "내 독서 기록 상세 조회")
    @GetMapping("/records/{recordId}")
    public ResponseEntity<ApiResponse<ReadingRecordDetailDTO>> getMyReadingRecordDetail(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                                        @PathVariable Long recordId) {
        return myPageService.getMyReadingRecordDetail(userDetails.getUser().getId(),recordId);
    }
}
