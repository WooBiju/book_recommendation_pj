package com.github.bookproject.recommend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class KeywordRecommendRequestDTO {
    private List<ReviewForKeywordDTO> reviews;
    private List<BookForKeywordDTO> books;
}
