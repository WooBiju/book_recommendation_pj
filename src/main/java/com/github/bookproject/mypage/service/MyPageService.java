package com.github.bookproject.mypage.service;

import com.github.bookproject.auth.entity.GenreType;
import com.github.bookproject.auth.entity.User;
import com.github.bookproject.auth.repository.UserRepository;
import com.github.bookproject.book.entity.Book;
import com.github.bookproject.book.favorite.repository.FavoriteRepository;
import com.github.bookproject.book.repository.BookRepository;
import com.github.bookproject.global.dto.ApiResponse;
import com.github.bookproject.global.exception.AppException;
import com.github.bookproject.global.exception.ErrorCode;
import com.github.bookproject.global.util.ImageFileUtils;
import com.github.bookproject.mypage.dto.*;
import com.github.bookproject.readingRecord.entity.ReadingRecord;
import com.github.bookproject.readingRecord.repository.ReadingRecordRepository;
import com.github.bookproject.review.entity.Review;
import com.github.bookproject.review.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final ReadingRecordRepository readingRecordRepository;
    private final FavoriteRepository favoriteRepository;
    private final ImageFileUtils imageFileUtils;
    private final BCryptPasswordEncoder passwordEncoder;


    public MyPageHomeDTO getMyPageHome(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        int reviewCount = reviewRepository.countByUserId(userId);
        int favoriteCount = favoriteRepository.countByUserId(userId);
        int recordCount = readingRecordRepository.countByUserId(userId);

        return MyPageHomeDTO.from(user, reviewCount, favoriteCount, recordCount);
    }

    public void updateMyProfile(Long userId, UpdateUserDTO dto, MultipartFile image) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        user.updateProfile(dto.getUsername(),dto.getPhoneNumber());

        if (image != null && !image.isEmpty()) {
            try {
                String newImageUrl = imageFileUtils.saveProfileImage(image);
                user.updateProfileImage(newImageUrl);
            }catch (IOException e){
                throw new AppException(ErrorCode.IO_ERROR);
            }
        }
        userRepository.save(user);
    }

    @Transactional
    public void changeMyPassword(Long userId, PasswordChangeDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // 현재 비밀번호 검증
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_PASSWORD);
        }

        user.changePassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
    }

    public ResponseEntity<ApiResponse<List<String>>> getMyPreferences(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        List<String> preferences = user.getPreferredGenres().stream()
                .map(Enum::name)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(preferences));
    }

    @Transactional
    public void updateMyPreferences(Long userId, List<String> preferences) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // 문자열 -> Enum 으로 변경
        List<GenreType> newPreferences = preferences.stream()
                .map(pref ->{
                    try {
                        return GenreType.valueOf(pref.toUpperCase());
                    }catch (IllegalArgumentException e){
                        throw new AppException(ErrorCode.INVALID_GENRE);
                    }
                })
                .collect(Collectors.toList());

        user.updatePreferredGenres(newPreferences);
        userRepository.save(user);

    }

    public ResponseEntity<ApiResponse<List<BookSummaryDTO.BookSimpleDTO>>> getMyFavorites(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        List<BookSummaryDTO.BookSimpleDTO> favorites = favoriteRepository.findByUserId(user.getId()).stream()
                .map(fav -> {
                    Book book = fav.getBook();
                    return new BookSummaryDTO.BookSimpleDTO(
                            book.getId(),
                            book.getTitle(),
                            book.getAuthor(),
                            book.getGenre().name()
                    );
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(favorites));
    }

    public ResponseEntity<ApiResponse<List<ReviewSummaryDTO.ReviewInfoDTO>>> getMyReviews(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        List<ReviewSummaryDTO.ReviewInfoDTO> reviews = reviewRepository.findByUserId(user.getId()).stream()
                .map(review -> {
                    Book book = review.getBook();
                    return new ReviewSummaryDTO.ReviewInfoDTO(
                            review.getId(),
                            book.getId(),
                            book.getTitle(),
                            review.getContent(),
                            review.getRating(),
                            review.getCreatedAt().toLocalDate()

                    );
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(reviews));
    }

    public ResponseEntity<ApiResponse<List<ReadingRecordSummaryDTO.ReadingRecordSimpleDTO>>> getMyReadingRecords(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        List<ReadingRecordSummaryDTO.ReadingRecordSimpleDTO> records = readingRecordRepository.findByUserId(user.getId()).stream()
                .map(record -> {
                    Book book = record.getBook();
                    return new ReadingRecordSummaryDTO.ReadingRecordSimpleDTO(
                            record.getId(),
                            book.getTitle(),
                            record.getStatus(),
                            record.getProgress()
                    );
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(records));
    }

    public ResponseEntity<ApiResponse<ReadingRecordDetailDTO>> getMyReadingRecordDetail(Long userId, Long recordId) {
        ReadingRecord record = readingRecordRepository.findById(recordId)
                .orElseThrow(() -> new AppException(ErrorCode.READING_RECORD_NOT_FOUND));

        if (!record.getUser().getId().equals(userId)) {
            throw new AppException(ErrorCode.READING_RECORD_FORBIDDEN);
        }

        Book book = record.getBook();

        ReadingRecordDetailDTO dto = new ReadingRecordDetailDTO(
                record.getId(),
                book.getTitle(),
                record.getMemo(),
                record.getStatus(),
                record.getProgress(),
                record.getStartDate(),
                record.getEndDate()
        );
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

}
