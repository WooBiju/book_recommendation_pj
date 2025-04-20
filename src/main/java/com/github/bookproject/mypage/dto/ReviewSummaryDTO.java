package com.github.bookproject.mypage.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

        // 소수점 한 자리로 반올림된 값 반환
        public double getRating() {
            return BigDecimal.valueOf(rating)
                    .setScale(1, RoundingMode.HALF_UP)
                    .doubleValue();
        }
    }
}
