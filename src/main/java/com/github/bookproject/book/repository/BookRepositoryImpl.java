package com.github.bookproject.book.repository;

import com.github.bookproject.auth.entity.GenreType;
import com.github.bookproject.book.dto.BookSearchCondition;
import com.github.bookproject.book.entity.Book;
import com.github.bookproject.book.entity.QBook;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BookRepositoryImpl implements BookRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Book> search(BookSearchCondition condition, Pageable pageable) {
        QBook book = QBook.book;

        List<Book> result = queryFactory.selectFrom(book)
                .where(
                        keywordContains(condition.getKeyword()),
                        genreEq(condition.getGenre())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        long count = Optional.ofNullable(
                queryFactory
                        .select(Wildcard.count)     // SELECT COUNT(*)
                        .from(book)
                        .where(
                                keywordContains(condition.getKeyword()),
                                genreEq(condition.getGenre())
                        )
                        .fetchOne()       // fetchOne() : 단일결과 반환 , null 반환 가능성 있음
        ).orElse(0L);

        return new PageImpl<>(result, pageable, count);
    }

    private BooleanExpression keywordContains(String keyword) {
        if (StringUtils.hasText(keyword)) {
            return QBook.book.title.containsIgnoreCase(keyword)
                    .or(QBook.book.author.containsIgnoreCase(keyword));
        }
        return null;
    }

    private BooleanExpression genreEq(GenreType genre) {
        return genre != null ? QBook.book.genre.eq(genre) : null;
    }
}
