package com.github.bookproject.readingRecord.entity;

import com.github.bookproject.auth.entity.User;
import com.github.bookproject.book.entity.Book;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReadingRecord {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Book book;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReadingStatus status;

    private LocalDate startDate;
    private LocalDate endDate;

    private Float progress;    // 0.0 ~ 1.0 진행률

    public static ReadingRecord create(User user, Book book, ReadingStatus status, LocalDate startDate, LocalDate endDate, Float progress) {
        ReadingRecord readingRecord = new ReadingRecord();
        readingRecord.user = user;
        readingRecord.book = book;
        readingRecord.status = status;
        readingRecord.startDate = startDate;
        readingRecord.endDate = endDate;
        readingRecord.progress = progress;
        return readingRecord;
    }


}
