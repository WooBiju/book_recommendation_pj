package com.github.bookproject.recommend.service;

import com.github.bookproject.auth.entity.User;
import com.github.bookproject.auth.repository.UserRepository;
import com.github.bookproject.book.dto.BookResponseDTO;
import com.github.bookproject.book.entity.Book;
import com.github.bookproject.book.favorite.repository.FavoriteRepository;
import com.github.bookproject.book.repository.BookRepository;
import com.github.bookproject.global.exception.AppException;
import com.github.bookproject.global.exception.ErrorCode;
import com.github.bookproject.readingRecord.repository.ReadingRecordRepository;
import com.github.bookproject.recommend.dto.RecommendRequestDTO;
import com.github.bookproject.recommend.dto.RecommendResultDTO;
import com.github.bookproject.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
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
        List<Book> allBooks = bookRepository.findAll();
        List<RecommendRequestDTO.BookInfoDTO> bookInfos = allBooks.stream()
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
}
