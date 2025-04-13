package com.github.bookproject.readingRecord.service;

import com.github.bookproject.auth.entity.User;
import com.github.bookproject.auth.repository.UserRepository;
import com.github.bookproject.book.entity.Book;
import com.github.bookproject.book.repository.BookRepository;
import com.github.bookproject.global.exception.AppException;
import com.github.bookproject.global.exception.ErrorCode;
import com.github.bookproject.readingRecord.dto.ReadingRecordRequestDTO;
import com.github.bookproject.readingRecord.dto.ReadingRecordUpdateRequestDTO;
import com.github.bookproject.readingRecord.entity.ReadingRecord;
import com.github.bookproject.readingRecord.entity.ReadingStatus;
import com.github.bookproject.readingRecord.repository.ReadingRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ReadingRecordService {

    private final ReadingRecordRepository readingRecordRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public void creatRecord(Long userId, ReadingRecordRequestDTO dto) {
        // 독서 기록 셍성시 COMPLETE 값이 들어오는거 방지
        if (dto.getStatus() == ReadingStatus.COMPLETED) {
            throw new AppException(ErrorCode.INVALID_INITIAL_READING_STATUS);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Book book = bookRepository.findById(dto.getBookId())
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));

        // 기록 중복 방지
        if (readingRecordRepository.findByUserIdAndBookId(userId,book.getId()).isPresent()) {
            throw new AppException(ErrorCode.DUPLICATE_READING_RECORD);
        }

        ReadingRecord record = ReadingRecord.create(
                user,
                book,
                dto.getMemo(),
                dto.getStatus(),
                LocalDate.now(),
                null,
                0.0f   // 진행도 0.0으로 초기화
        );
        readingRecordRepository.save(record);
    }

    public void updateRecord(Long userId, ReadingRecordUpdateRequestDTO dto, Long recordId) {
        ReadingRecord record = readingRecordRepository.findById(recordId)
                .orElseThrow(() -> new AppException(ErrorCode.READING_RECORD_NOT_FOUND));

        if (!record.getUser().getId().equals(userId)) {
            throw new AppException(ErrorCode.READING_RECORD_FORBIDDEN);
        }

        record.update(dto.getMemo(), dto.getStatus());  // 만약, 상태를 독서 완료라고 변경한다면 endDate 현재 날짜로 자동 설정
        readingRecordRepository.save(record);
    }

    public void updateProgress(Long recordId, float progress,Long userId) {
        ReadingRecord record = readingRecordRepository.findById(recordId)
                .orElseThrow(() -> new AppException(ErrorCode.READING_RECORD_NOT_FOUND));

        if (!record.getUser().getId().equals(userId)) {
            throw new AppException(ErrorCode.READING_RECORD_FORBIDDEN);
        }

        if (progress < 0.0f || progress > 1.0f) {
            throw new AppException(ErrorCode.INVALID_PROGRESS);
        }
        record.updateProgress(progress);    // 만약, 진행도가 1.0 이 된다면 상태를 COMPLETE 로 변경하고, endDate 자동 설정
        readingRecordRepository.save(record);
    }

    public void completeReading(Long userId, Long recordId) {
        ReadingRecord record = readingRecordRepository.findById(recordId)
                .orElseThrow(() -> new AppException(ErrorCode.READING_RECORD_NOT_FOUND));

        if (!record.getUser().getId().equals(userId)) {
            throw new AppException(ErrorCode.READING_RECORD_FORBIDDEN);
        }
        // 독서 완료 시점 중복 처리 방지
        if (record.getStatus() == ReadingStatus.COMPLETED) {
            throw new AppException(ErrorCode.ALREADY_COMPLETE);
        }

        record.completeReading();   // 독서 완료 버튼을 누르면 진행도 1.0 , 상태 COMPLETE , endDate 현재시간으로 변경
        readingRecordRepository.save(record);
    }

    public void deleteRecord(Long recordId,Long userId) {
        ReadingRecord record = readingRecordRepository.findById(recordId)
                .orElseThrow(() -> new AppException(ErrorCode.READING_RECORD_NOT_FOUND));

        if (!record.getUser().getId().equals(userId)) {
            throw new AppException(ErrorCode.READING_RECORD_FORBIDDEN);
        }

        readingRecordRepository.deleteById(recordId);
    }


}
