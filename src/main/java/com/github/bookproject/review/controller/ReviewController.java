package com.github.bookproject.review.controller;

import com.github.bookproject.global.config.auth.custom.CustomUserDetails;
import com.github.bookproject.global.dto.ApiResponse;
import com.github.bookproject.review.dto.ReviewRequestDTO;
import com.github.bookproject.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "리뷰 등록")
    @PostMapping
    public ResponseEntity<ApiResponse<String>> createReview (@RequestBody ReviewRequestDTO dto,
                                                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        reviewService.createReview(userDetails.getUser().getId(),dto);
        return ResponseEntity.ok(ApiResponse.success("✅리뷰가 등록되었습니다."));
    }


    @Operation(summary = "리뷰 수정")
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> updateReview(@PathVariable Long id,
                                                            @RequestBody ReviewRequestDTO dto,
                                                            @AuthenticationPrincipal CustomUserDetails userDetails) {
        reviewService.updateReview(userDetails.getUser().getId(),id,dto);
        return ResponseEntity.ok(ApiResponse.success("✅리뷰가 수정되었습니다."));
    }


    @Operation(summary = "리뷰 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteReview(@PathVariable Long id,
                                                            @AuthenticationPrincipal CustomUserDetails userDetails) {
        reviewService.deleteReview(userDetails.getUser().getId(),userDetails.getUser().getRole(),id);
        return ResponseEntity.ok(ApiResponse.success("✅리뷰가 삭제되었습니다."));
    }

}
