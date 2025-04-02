package com.github.bookproject.book.entity;

import com.github.bookproject.auth.entity.GenreType;
import com.github.bookproject.book.dto.BookUpdateDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Year;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;               // 도서 제목
    private String author;              // 작가
    private String publisher;           // 출판사
    private String isbn;                // ISBN
    private String description;         // 책 소개

    private String imageUrl;            // 도서 썸네일

    @Enumerated(EnumType.STRING)
    private GenreType genre;            // 도서 장르

    private int pageCount;              // 페이지 수
    private Year publishedYear;          // 출판년도

    private double rating;              // 평균 평점

    @Enumerated(EnumType.STRING)
    private BookStatus status;          // 상태


    public void update(BookUpdateDTO dto) {
        this.title = dto.getTitle();
        this.description = dto.getDescription();
        this.imageUrl = dto.getImageUrl();
        this.status = dto.getStatus();
    }




}
