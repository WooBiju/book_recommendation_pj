package com.github.bookproject.review.service;

import com.github.bookproject.auth.entity.Role;
import com.github.bookproject.auth.entity.User;
import com.github.bookproject.auth.repository.UserRepository;
import com.github.bookproject.book.entity.Book;
import com.github.bookproject.book.repository.BookRepository;
import com.github.bookproject.global.exception.AppException;
import com.github.bookproject.global.exception.ErrorCode;
import com.github.bookproject.review.dto.ReviewRequestDTO;
import com.github.bookproject.review.entity.Review;
import com.github.bookproject.review.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final ReviewRepository reviewRepository;

    public void createReview(Long id, ReviewRequestDTO dto) {
        if (dto.getRating() < 1.0f || dto.getRating() > 5.0f) {     // 별점 값 유효성 검증
            throw new AppException(ErrorCode.INVALID_RATING);
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Book book = bookRepository.findById(dto.getBookId())
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));

        // 리뷰 중복 등록 확인
        boolean exists = reviewRepository.existsByUserIdAndBookId(user.getId(),book.getId());
        if (exists) {
            throw new AppException(ErrorCode.DUPLICATE_REVIEW);
        }

        Review review = Review.create(user,book, dto.getContent(),dto.getRating());
        reviewRepository.save(review);

        updateBookAverageRating(book);      // 도서 평균 별점 변경
    }

    private void updateBookAverageRating(Book book) {
        List<Review> reviews = reviewRepository.findByBookId(book.getId());

        double avgRating = reviews.stream()
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0.0);

        book.updateRating(avgRating);

        try {
            bookRepository.save(book);  // 버전 충돌시 예외 발생
        }catch (ObjectOptimisticLockingFailureException e) {
            throw new AppException(ErrorCode.REVIEW_CONFLICT);
        }

    }

    public void updateReview(Long userId, Long reviewId, ReviewRequestDTO dto) {
        if (dto.getRating() < 1.0f || dto.getRating() > 5.0f) {     // 별점 값 유효성 검증
            throw new AppException(ErrorCode.INVALID_RATING);
        }

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_FOUND));

        if (!review.getUser().getId().equals(userId)) {
            throw new AppException(ErrorCode.REVIEW_FORBIDDEN);
        }

        review.update(dto.getContent(),dto.getRating());
        updateBookAverageRating(review.getBook());
    }

    public void deleteReview(Long userId, Role role, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_FOUND));

        // 작성자랑 관리자가 아닐 경우
        if (!review.getUser().getId().equals(userId) && role != Role.ROLE_ADMIN) {
            throw new AppException(ErrorCode.REVIEW_FORBIDDEN);
        }

        reviewRepository.deleteById(reviewId);
        updateBookAverageRating(review.getBook());
    }
}
