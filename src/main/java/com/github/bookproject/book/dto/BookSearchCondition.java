package com.github.bookproject.book.dto;

import com.github.bookproject.auth.entity.GenreType;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookSearchCondition {
    private String keyword;         // 제목 or 작가
    private GenreType genre;        // 장르
}
