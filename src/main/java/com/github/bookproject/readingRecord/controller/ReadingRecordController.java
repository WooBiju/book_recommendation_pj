package com.github.bookproject.readingRecord.controller;

import com.github.bookproject.global.config.auth.custom.CustomUserDetails;
import com.github.bookproject.global.dto.ApiResponse;
import com.github.bookproject.readingRecord.dto.ProgressUpdateRequestDTO;
import com.github.bookproject.readingRecord.dto.ReadingRecordRequestDTO;
import com.github.bookproject.readingRecord.dto.ReadingRecordUpdateRequestDTO;
import com.github.bookproject.readingRecord.service.ReadingRecordService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/records")
public class ReadingRecordController {

    private final ReadingRecordService readingRecordService;

    // 독서 기록
    @Operation(summary = "독서 기록 진행")
    @PostMapping
    public ResponseEntity<ApiResponse<String>> createRecord(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                            @RequestBody ReadingRecordRequestDTO dto) {
        readingRecordService.creatRecord(userDetails.getUser().getId(),dto);
        return ResponseEntity.ok(ApiResponse.success("✅독서 기록이 생성되었습니다."));
    }

    // 독서 기록 수정
    @Operation(summary = "독서 기록 내용 수정")
    @PatchMapping("/{recordId}")
    public ResponseEntity<ApiResponse<String>> updateRecord(@PathVariable Long recordId,
                                                            @RequestBody ReadingRecordUpdateRequestDTO dto,
                                                            @AuthenticationPrincipal CustomUserDetails userDetails) {
        readingRecordService.updateRecord(userDetails.getUser().getId(),dto,recordId);
        return ResponseEntity.ok(ApiResponse.success("✅독서 기록 내용이 수정되었습니다."));
    }

    @Operation(summary = "독서 진행률 수정")
    @PatchMapping("/{recordId}/progress")
    public ResponseEntity<ApiResponse<String>> updateRecordProgress(@PathVariable Long recordId,
                                                                    @RequestBody ProgressUpdateRequestDTO dto,
                                                                    @AuthenticationPrincipal CustomUserDetails userDetails) {
        readingRecordService.updateProgress(recordId,dto.getProgress(),userDetails.getUser().getId());
        return ResponseEntity.ok(ApiResponse.success("✅독서 진행률 수정되었습니다."));
    }

    @Operation(summary = "독서 완료 처리")
    @PatchMapping("/{recordId}/complete")
    public ResponseEntity<ApiResponse<String>> completeReading(@PathVariable Long recordId,
                                                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        readingRecordService.completeReading(userDetails.getUser().getId(),recordId);
        return ResponseEntity.ok(ApiResponse.success("✅독서가 완료되었습니다."));
    }

    // 독서 기록 삭제
    @Operation(summary = "독서 기록 삭제")
    @DeleteMapping("/{recordId}")
    public ResponseEntity<ApiResponse<String>> deleteRecord(@PathVariable Long recordId,
                                                            @AuthenticationPrincipal CustomUserDetails userDetails) {
        readingRecordService.deleteRecord(recordId,userDetails.getUser().getId());
        return ResponseEntity.ok(ApiResponse.success("✅독서 기록이 삭제되었습니다."));
    }


}
