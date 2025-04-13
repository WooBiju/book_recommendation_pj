package com.github.bookproject.readingRecord.entity;

import com.github.bookproject.auth.entity.User;
import com.github.bookproject.book.entity.Book;
import com.github.bookproject.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReadingRecord extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Book book;

    @Column(length = 10000)
    private String memo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReadingStatus status;

    private LocalDate startDate;
    private LocalDate endDate;

    private Float progress;    // 0.0 ~ 1.0 진행률

    public static ReadingRecord create(User user, Book book, String memo, ReadingStatus status, LocalDate startDate, LocalDate endDate, Float progress) {
        ReadingRecord readingRecord = new ReadingRecord();
        readingRecord.user = user;
        readingRecord.book = book;
        readingRecord.memo = memo;
        readingRecord.status = status;
        readingRecord.startDate = startDate;
        readingRecord.endDate = endDate;
        readingRecord.progress = progress;
        return readingRecord;
    }

    public void update(String memo, ReadingStatus status) {
        this.memo = memo;
        this.status = status;
        if (status == ReadingStatus.COMPLETED) {
            this.endDate = LocalDate.now();
        }
    }

    public void updateProgress(Float progress) {
        this.progress = progress;
        if (progress >= 1.0f) {
            this.status = ReadingStatus.COMPLETED;
            this.endDate = LocalDate.now();
        }
    }

    public void completeReading() {
        this.progress = 1.0f;
        this.status = ReadingStatus.COMPLETED;
        this.endDate = LocalDate.now();
    }


}
