package com.github.bookproject.mypage.dto;

import com.github.bookproject.readingRecord.entity.ReadingStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReadingRecordDetailDTO {
    private Long readingId;
    private String bookTitle;
    private String memo;
    private ReadingStatus status;
    private float progress;
    private LocalDate startDate;
    private LocalDate endDate;
}
