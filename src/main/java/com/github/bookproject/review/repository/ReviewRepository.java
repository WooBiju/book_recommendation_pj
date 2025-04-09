package com.github.bookproject.review.repository;

import com.github.bookproject.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByBookId(Long bookId);

    boolean existsByUserIdAndBookId(Long userId, Long bookId);

    List<Review> findByUserId(Long userId);
}
