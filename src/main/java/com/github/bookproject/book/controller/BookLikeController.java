package com.github.bookproject.book.controller;

import com.github.bookproject.book.favorite.service.FavoriteService;
import com.github.bookproject.global.config.auth.custom.CustomUserDetails;
import com.github.bookproject.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class BookLikeController {

    private final FavoriteService favoriteService;

    @PostMapping("/{bookId}/like")
    public ResponseEntity<ApiResponse<String>> addLike(
            @PathVariable Long bookId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().getId();
        favoriteService.addFavorite(bookId,userId);
        return ResponseEntity.ok(ApiResponse.success("✅찜 추가되었습니다."));
    }

    @DeleteMapping("/{bookId}/like")
    public ResponseEntity<ApiResponse<String>> cancelLike(
            @PathVariable Long bookId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().getId();
        favoriteService.removeFavorite(bookId,userId);
        return ResponseEntity.ok(ApiResponse.success("✅찜 취소되었습니다."));
    }

}
