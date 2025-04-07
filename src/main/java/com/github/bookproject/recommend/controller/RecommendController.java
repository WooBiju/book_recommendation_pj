package com.github.bookproject.recommend.controller;

import com.github.bookproject.book.dto.BookResponseDTO;
import com.github.bookproject.global.config.auth.custom.CustomUserDetails;
import com.github.bookproject.global.dto.ApiResponse;
import com.github.bookproject.recommend.service.RecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommend")
public class RecommendController {

    private final RecommendService recommendService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<BookResponseDTO>>> recommendBooks(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().getId();
        System.out.println("[추천 API] userDetails: " + userDetails);
        List<BookResponseDTO> recommendedBooks = recommendService.getRecommendedBooks(userId);
        return ResponseEntity.ok(ApiResponse.success(recommendedBooks));
    }
}
