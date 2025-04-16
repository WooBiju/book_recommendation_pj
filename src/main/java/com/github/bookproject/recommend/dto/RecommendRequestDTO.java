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
    private List<ReadingHistoryDTO> readingHistory;

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

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReadingHistoryDTO {
        private Long id;
        private String genre;
        private float progress;
        private String status;
    }

}
