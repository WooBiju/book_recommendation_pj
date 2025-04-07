package com.github.bookproject.recommend.service;

import com.github.bookproject.auth.entity.User;
import com.github.bookproject.auth.repository.UserRepository;
import com.github.bookproject.book.dto.BookResponseDTO;
import com.github.bookproject.book.entity.Book;
import com.github.bookproject.book.favorite.repository.FavoriteRepository;
import com.github.bookproject.book.repository.BookRepository;
import com.github.bookproject.global.exception.AppException;
import com.github.bookproject.global.exception.ErrorCode;
import com.github.bookproject.recommend.dto.RecommendRequestDTO;
import com.github.bookproject.recommend.dto.RecommendResultDTO;
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

    public List<BookResponseDTO> getRecommendedBooks(Long userId) {
        // 1. 사용자 선호 장르 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // 2. 선호 장르 추출
        List<String> preferredGenres = user.getPreferredGenres().stream()
                .map(Enum::name)
                .collect(Collectors.toList());

        // 3. 찜 도서 ID 조회
        List<Long> favoriteBookIds = favoriteRepository.findByUserId(userId).stream()
                .map(fav ->fav.getBook().getId())
                .collect(Collectors.toList());

        // 4. db 에 있는 전체 도서 목록 dto 에 리스트로 담아서 저장
        List<Book> allBooks = bookRepository.findAll();
        List<RecommendRequestDTO.BookInfoDTO> bookInfos = allBooks.stream()
                .map(book -> new RecommendRequestDTO.BookInfoDTO(book.getId(),book.getGenre().name()))
                .collect(Collectors.toList());

        // 5. 파이썬 서버에 요청
        RecommendRequestDTO requestDTO = new RecommendRequestDTO(preferredGenres,favoriteBookIds,bookInfos);
        String pythonUrl = "http://127.0.0.1:5000/recommend";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<RecommendRequestDTO> entity = new HttpEntity<>(requestDTO, headers);

        ResponseEntity<RecommendResultDTO> response = restTemplate
                .postForEntity(pythonUrl, entity, RecommendResultDTO.class);

        List<Long> recommendBookIds = response.getBody().getBookIds();

        // 6. 도서 ID 들로 book 조회
        List<Book> books = bookRepository.findAllById(recommendBookIds);

        // 7. dto 로 변환
        return books.stream()
                .map(BookResponseDTO::from)
                .collect(Collectors.toList());
    }
}
