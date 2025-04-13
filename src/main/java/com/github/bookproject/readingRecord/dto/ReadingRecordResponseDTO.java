package com.github.bookproject.readingRecord.dto;

import com.github.bookproject.readingRecord.entity.ReadingRecord;
import com.github.bookproject.readingRecord.entity.ReadingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadingRecordResponseDTO {
    private Long recordId;
    private String memo;
    private ReadingStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private Float progress;

    public static ReadingRecordResponseDTO from(ReadingRecord record) {
        return ReadingRecordResponseDTO.builder()
                .recordId(record.getId())
                .memo(record.getMemo())
                .status(record.getStatus())
                .startDate(record.getStartDate())
                .endDate(record.getEndDate())
                .progress(record.getProgress())
                .build();
    }
}
