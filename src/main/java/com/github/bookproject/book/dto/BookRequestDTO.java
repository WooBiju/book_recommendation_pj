package com.github.bookproject.book.dto;

import com.github.bookproject.auth.entity.GenreType;
import com.github.bookproject.book.entity.Book;
import com.github.bookproject.book.entity.BookStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Year;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookRequestDTO {
    private String title;
    private String author;
    private String publisher;
    private String isbn;
    private String description;
    private String imageUrl;
    private GenreType genre;
    private int pageCount;
    private Year publishedYear;
    private BookStatus status;

    public Book toBook() {
        return Book.builder()
                .title(title)
                .author(author)
                .publisher(publisher)
                .isbn(isbn)
                .description(description)
                .imageUrl(imageUrl)
                .genre(genre)
                .pageCount(pageCount)
                .publishedYear(publishedYear)
                .status(status)
                .build();
    }


}
