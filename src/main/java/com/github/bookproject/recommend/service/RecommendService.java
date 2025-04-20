package com.github.bookproject.recommend.service;

import com.github.bookproject.auth.entity.User;
import com.github.bookproject.auth.repository.UserRepository;
import com.github.bookproject.book.dto.BookResponseDTO;
import com.github.bookproject.book.entity.Book;
import com.github.bookproject.book.entity.BookStatus;
import com.github.bookproject.book.favorite.repository.FavoriteRepository;
import com.github.bookproject.book.repository.BookRepository;
import com.github.bookproject.global.exception.AppException;
import com.github.bookproject.global.exception.ErrorCode;
import com.github.bookproject.readingRecord.repository.ReadingRecordRepository;
import com.github.bookproject.recommend.dto.*;
import com.github.bookproject.review.entity.Review;
import com.github.bookproject.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendService {

    private final RestTemplate restTemplate;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final FavoriteRepository favoriteRepository;
    private final ReviewRepository reviewRepository;
    private final ReadingRecordRepository readingRecordRepository;

    public List<BookResponseDTO> getRecommendedBooks(Long userId) {
        // 1. 사용자 정보, 선호 장르 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        List<String> preferredGenres = user.getPreferredGenres().stream()
                .map(Enum::name)
                .collect(Collectors.toList());

        // 2. 찜 도서 ID 리스트
        List<Integer> favoriteBooks = favoriteRepository.findByUserId(userId).stream()
                .map(fav -> fav.getBook().getId().intValue())
                .collect(Collectors.toList());

        // 3. 별점 남긴 도서 리스트
        List<RecommendRequestDTO.RatedBookDTO> ratedBooks = reviewRepository.findByUserId(userId).stream()
                .map(review -> new RecommendRequestDTO.RatedBookDTO(
                     review.getBook().getId(),
                        review.getRating()
                ))
                .collect(Collectors.toList());

        // 4. 독서 기록중인 도서 리스트
        List<RecommendRequestDTO.ReadingHistoryDTO> records = readingRecordRepository.findByUserId(userId).stream()
                .map(record -> new RecommendRequestDTO.ReadingHistoryDTO(
                        record.getBook().getId(),
                        record.getBook().getGenre().name(),
                        record.getProgress(),
                        record.getStatus().name()
                ))
                .collect(Collectors.toList());


        // 5. 전체 도서 목록
        List<Book> availableBooks = bookRepository.findByStatusIn(List.of(BookStatus.AVAILABLE,BookStatus.COMING_SOON));
        List<RecommendRequestDTO.BookInfoDTO> bookInfos = availableBooks.stream()
                .map(book -> new RecommendRequestDTO.BookInfoDTO(
                        book.getId(),
                        book.getGenre().name()
                        ))
                .collect(Collectors.toList());

        // 6. 파이썬 서버에 요청
        RecommendRequestDTO requestDTO = new RecommendRequestDTO(
                preferredGenres,
                favoriteBooks,
                ratedBooks,
                bookInfos,
                records);
        String pythonUrl = "http://127.0.0.1:5000/recommend";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<RecommendRequestDTO> entity = new HttpEntity<>(requestDTO, headers);

        ResponseEntity<RecommendResultDTO> response = restTemplate
                .postForEntity(pythonUrl, entity, RecommendResultDTO.class);

        List<Long> recommendBookIds = response.getBody().getBookIds();

        // 7. 추천 도서 조회
        List<Book> books = bookRepository.findAllById(recommendBookIds);

        // 8. dto 로 변환
        return books.stream()
                .map(BookResponseDTO::from)
                .collect(Collectors.toList());
    }

    public List<BookResponseDTO> recommendByReviewKeywords(Long userId) {
        // 1. 해당 유저의 모든 리뷰 가져옴
        List<Review> reviews = reviewRepository.findByUserId(userId);

        List<ReviewForKeywordDTO> reviewDtos = reviews.stream()
                .map(review -> new ReviewForKeywordDTO(
                        review.getId(),
                        review.getBook().getId(),
                        review.getContent()
                ))
                .collect(Collectors.toList());

        // 2. 전체 도서 description 포함해서 가져옴
        List<Book> books = bookRepository.findByStatusIn(List.of(BookStatus.AVAILABLE,BookStatus.COMING_SOON));
        List<BookForKeywordDTO> bookDtos = books.stream()
                .map(book -> new BookForKeywordDTO(
                        book.getId(),
                        book.getDescription()
                ))
                .collect(Collectors.toList());

        // 3. 전체 dto 생성
        KeywordRecommendRequestDTO requestDTO = new KeywordRecommendRequestDTO(reviewDtos, bookDtos);

        // 3. 파이썬 서버로 전송
        String pythonUrl = "http://127.0.0.1:5000/recommend/keywords";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<KeywordRecommendRequestDTO> request = new HttpEntity<>(requestDTO, headers);

        ResponseEntity<RecommendResultDTO> response = restTemplate.exchange(
                pythonUrl,
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<RecommendResultDTO>() {}
        );

        // 4. 추천 도서 ID 조회
        List<Long> recommendBookIds = response.getBody().getBookIds();

        // 5. 도서 dto 변환
        List<Book> recommendedBooks = bookRepository.findAllById(recommendBookIds);
        return recommendedBooks.stream()
                .map(BookResponseDTO::from)
                .collect(Collectors.toList());
    }
}
