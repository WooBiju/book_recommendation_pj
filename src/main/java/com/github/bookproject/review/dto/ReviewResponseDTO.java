package com.github.bookproject.review.dto;

import com.github.bookproject.review.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDTO {

    private String reviewer;
    private String content;
    private float rating;
    private LocalDateTime createdAt;

    public static ReviewResponseDTO from(Review review) {
        return ReviewResponseDTO.builder()
                .reviewer(review.getUser().getUsername())
                .content(review.getContent())
                .rating(review.getRating())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
