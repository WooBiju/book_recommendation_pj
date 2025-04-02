package com.github.bookproject.book.repository;

import com.github.bookproject.book.dto.BookSearchCondition;
import com.github.bookproject.book.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepositoryCustom {
    Page<Book> search(BookSearchCondition condition, Pageable pageable);
}
