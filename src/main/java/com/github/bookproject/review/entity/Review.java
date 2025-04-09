package com.github.bookproject.review.entity;

import com.github.bookproject.auth.entity.User;
import com.github.bookproject.book.entity.Book;
import com.github.bookproject.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Book book;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private float rating;   // 1.0 ~ 5.0 범위

    public static Review create(User user, Book book, String content, float rating) {
        Review review = new Review();
        review.user = user;
        review.book = book;
        review.content = content;
        review.rating = rating;
        return review;
    }

    public void update(String content, float rating) {
        this.content = content;
        this.rating = rating;
    }
}
