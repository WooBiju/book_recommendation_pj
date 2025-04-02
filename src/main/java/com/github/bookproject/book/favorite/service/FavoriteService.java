package com.github.bookproject.book.favorite.service;

import com.github.bookproject.auth.entity.User;
import com.github.bookproject.auth.repository.UserRepository;
import com.github.bookproject.book.entity.Book;
import com.github.bookproject.book.favorite.entity.Favorite;
import com.github.bookproject.book.favorite.repository.FavoriteRepository;
import com.github.bookproject.book.repository.BookRepository;
import com.github.bookproject.global.exception.AppException;
import com.github.bookproject.global.exception.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public void addFavorite(Long bookId, Long userId ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));

        if (favoriteRepository.existsByUserAndBook(user,book)) {
            throw new AppException(ErrorCode.ALREADY_FAVORITE_BOOK);
        }

        Favorite favorite = Favorite.of(user, book);
        favoriteRepository.save(favorite);
    }

    @Transactional
    public void removeFavorite(Long bookId, Long userId ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));

        Favorite favorite = favoriteRepository.findByUserAndBook(user,book)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FAVORITE_BOOK));

        favoriteRepository.delete(favorite);
    }
}
