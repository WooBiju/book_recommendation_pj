package com.github.bookproject.book.repository;

import com.github.bookproject.book.entity.Book;
import com.github.bookproject.book.entity.BookStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> , BookRepositoryCustom {
    List<Book> findByStatusIn(List<BookStatus> statuses);
}
