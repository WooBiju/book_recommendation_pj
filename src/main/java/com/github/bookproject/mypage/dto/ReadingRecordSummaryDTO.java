package com.github.bookproject.mypage.dto;

import com.github.bookproject.readingRecord.entity.ReadingStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReadingRecordSummaryDTO {
    private List<ReadingRecordSimpleDTO> records;



    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReadingRecordSimpleDTO {
        private Long readingId;
        private String title;
        private ReadingStatus status;
        private float progress;
    }


}
