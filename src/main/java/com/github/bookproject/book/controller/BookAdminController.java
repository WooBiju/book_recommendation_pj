package com.github.bookproject.book.controller;

import com.github.bookproject.book.dto.BookRequestDTO;
import com.github.bookproject.book.dto.BookUpdateDTO;
import com.github.bookproject.book.service.BookService;
import com.github.bookproject.global.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/books")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class BookAdminController {
    private final BookService bookService;

    @Operation(summary = "도서 등록")
    @PostMapping
    public ResponseEntity<ApiResponse<String>> registerBook (@RequestBody BookRequestDTO dto) {
        bookService.registerBook(dto);
        return ResponseEntity.ok(ApiResponse.success("✅도서가 등록되었습니다."));
    }

    @Operation(summary = "도서 수정")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> updateBook (@PathVariable Long id, @RequestBody BookUpdateDTO dto) {
        bookService.updateBook(id,dto);
        return ResponseEntity.ok(ApiResponse.success("✅도서가 수정되었습니다."));
    }

    @Operation(summary = "도서 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteBook (@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok(ApiResponse.success("✅도서가 삭제되었습니다."));
    }

}
