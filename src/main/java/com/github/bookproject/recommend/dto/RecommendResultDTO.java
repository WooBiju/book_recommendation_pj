package com.github.bookproject.recommend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecommendResultDTO {
    private List<Long> bookIds;  // 파이썬 서버에서 응답한 추천 도서 ID 목록
}
