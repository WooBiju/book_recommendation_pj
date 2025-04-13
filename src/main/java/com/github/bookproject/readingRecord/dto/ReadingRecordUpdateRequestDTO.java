package com.github.bookproject.readingRecord.dto;

import com.github.bookproject.readingRecord.entity.ReadingStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReadingRecordUpdateRequestDTO {

    private String memo;

    @NotNull(message = "독서 상태는 필수입니다.")
    private ReadingStatus status;

}
