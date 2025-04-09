package com.github.bookproject.book.dto;


import com.github.bookproject.auth.entity.GenreType;
import com.github.bookproject.book.entity.Book;
import com.github.bookproject.book.entity.BookStatus;
import com.github.bookproject.review.dto.ReviewResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Year;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDetailsResponseDTO {

    private Long id;
    private String title;
    private String author;
    private String publisher;
    private String isbn;
    private String description;
    private String imageUrl;
    private GenreType genre;

    private int pageCount;
    private Year publishedYear;

    private double rating;
    private BookStatus status;
    private List<ReviewResponseDTO> reviews;

    public static BookDetailsResponseDTO from(Book book,List<ReviewResponseDTO> reviews) {
        return BookDetailsResponseDTO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .publisher(book.getPublisher())
                .isbn(book.getIsbn())
                .description(book.getDescription())
                .imageUrl(book.getImageUrl())
                .genre(book.getGenre())
                .pageCount(book.getPageCount())
                .publishedYear(book.getPublishedYear())
                .rating(book.getRating())
                .status(book.getStatus())
                .reviews(reviews)
                .build();
    }
}
