package com.github.bookproject.recommend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RecommendRequestDTO {
    private List<String> preferredGenres;
    private List<Integer> favoriteBooks;
    private List<RatedBookDTO> ratedBooks;
    private List<BookInfoDTO> bookInfos;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookInfoDTO {
        private Long id;
        private String genre;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RatedBookDTO {
        private Long id;
        private double rating;
    }

}
