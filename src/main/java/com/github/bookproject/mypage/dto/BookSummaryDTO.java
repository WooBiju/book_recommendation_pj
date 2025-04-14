package com.github.bookproject.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookSummaryDTO {
    private List<BookSimpleDTO> books;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookSimpleDTO{
        private Long id;
        private String title;
        private String author;
        private String genre;
    }
}
