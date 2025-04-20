package com.github.bookproject.book.dto;

import com.github.bookproject.auth.entity.GenreType;
import com.github.bookproject.book.entity.Book;
import com.github.bookproject.book.entity.BookStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookResponseDTO {
    private Long id;
    private String title;
    private String author;
    private String imageUrl;
    private GenreType genre;
    private double rating;
    private BookStatus status;

    public static BookResponseDTO from(Book book) {
        return BookResponseDTO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .imageUrl(book.getImageUrl())
                .genre(book.getGenre())
                .rating(book.getRating())
                .status(book.getStatus())
                .build();
    }

    public double getRating() {
        return BigDecimal.valueOf(rating)
                .setScale(1, RoundingMode.HALF_UP)
                .doubleValue();
    }


}
