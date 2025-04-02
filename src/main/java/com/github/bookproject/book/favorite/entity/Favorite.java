package com.github.bookproject.book.favorite.entity;

import com.github.bookproject.auth.entity.User;
import com.github.bookproject.book.entity.Book;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Favorite {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Book book;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public static Favorite of(User user, Book book) {
        return Favorite.builder()
                .user(user)
                .book(book)
                .build();
    }

}
