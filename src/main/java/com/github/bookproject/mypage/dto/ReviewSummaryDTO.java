package com.github.bookproject.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewSummaryDTO {
    private List<ReviewInfoDTO> reviews;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewInfoDTO{
        private Long reviewId;
        private Long BookId;
        private String title;
        private String content;
        private double rating;
        private LocalDate createdAt;
    }
}
