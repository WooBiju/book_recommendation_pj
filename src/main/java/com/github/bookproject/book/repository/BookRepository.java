package com.github.bookproject.book.repository;

import com.github.bookproject.book.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> , BookRepositoryCustom {
}
