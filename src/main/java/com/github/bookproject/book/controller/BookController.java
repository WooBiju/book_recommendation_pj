package com.github.bookproject.book.controller;

import com.github.bookproject.auth.entity.GenreType;
import com.github.bookproject.book.dto.BookDetailsResponseDTO;
import com.github.bookproject.book.dto.BookResponseDTO;
import com.github.bookproject.book.service.BookService;
import com.github.bookproject.global.config.auth.custom.CustomUserDetails;
import com.github.bookproject.global.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class BookController {
    private final BookService bookService;

    @Operation(summary = "전체 도서 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<BookResponseDTO>>> getAllBooks(
            @PageableDefault(size = 10,sort = "id" , direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.success(bookService.getAllBooks(pageable)));
    }

    @Operation(summary = "도서 상세 조회")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookDetailsResponseDTO>> getBookDetails(@PathVariable Long id,
                                                                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getId();
        BookDetailsResponseDTO dto = bookService.getBookDetail(id,userId);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @Operation(summary = "도서 검색")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<BookResponseDTO>>> searchBooks(
            @RequestParam(required = false)GenreType genre,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10,sort = "id" , direction = Sort.Direction.DESC) Pageable pageable
            ) {
        return ResponseEntity.ok(ApiResponse.success(bookService.searchBooks(genre,keyword,pageable)));
    }

}
