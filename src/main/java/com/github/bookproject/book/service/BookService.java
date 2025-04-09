package com.github.bookproject.book.service;

import com.github.bookproject.auth.entity.GenreType;
import com.github.bookproject.book.dto.*;
import com.github.bookproject.book.entity.Book;
import com.github.bookproject.book.repository.BookRepository;
import com.github.bookproject.global.exception.AppException;
import com.github.bookproject.global.exception.ErrorCode;
import com.github.bookproject.review.dto.ReviewResponseDTO;
import com.github.bookproject.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final ReviewRepository reviewRepository;

    public Page<BookResponseDTO> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable)
                .map(BookResponseDTO::from);
    }

    public BookDetailsResponseDTO getBookDetail(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));

        List<ReviewResponseDTO> reviews = reviewRepository.findByBookId(id)
                .stream()
                .map(ReviewResponseDTO::from)
                .collect(Collectors.toList());

        return BookDetailsResponseDTO.from(book,reviews);
    }

    public void registerBook(BookRequestDTO dto) {
        Book book = dto.toBook();
        bookRepository.save(book);
    }

    public void updateBook(Long id, BookUpdateDTO dto) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));

        book.update(dto);
        bookRepository.save(book);
    }

    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));

        bookRepository.delete(book);
    }

    public Page<BookResponseDTO> searchBooks(GenreType genre, String keyword, Pageable pageable) {

        BookSearchCondition condition = BookSearchCondition.builder()
                .genre(genre)
                .keyword(keyword)
                .build();

        Page<Book> books = bookRepository.search(condition, pageable);
        return books.map(BookResponseDTO::from);
    }
}
