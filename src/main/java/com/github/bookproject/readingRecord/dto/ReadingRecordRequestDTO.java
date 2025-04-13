package com.github.bookproject.readingRecord.dto;

import com.github.bookproject.readingRecord.entity.ReadingStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReadingRecordRequestDTO {

    @NotNull(message = "도서 ID 는 필수입니다.")
    private Long bookId;

    private String memo;

    @NotNull(message = "독서 상태는 필수입니다.")
    private ReadingStatus status;

}
