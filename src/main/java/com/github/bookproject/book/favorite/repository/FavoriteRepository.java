package com.github.bookproject.book.favorite.repository;

import com.github.bookproject.auth.entity.User;
import com.github.bookproject.book.entity.Book;
import com.github.bookproject.book.favorite.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    boolean existsByUserAndBook(User user, Book book);

    Optional<Favorite> findByUserAndBook(User user, Book book);
}
