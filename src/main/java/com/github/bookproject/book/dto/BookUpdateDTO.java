package com.github.bookproject.book.dto;

import com.github.bookproject.book.entity.BookStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookUpdateDTO {

    private String title;
    private String description;
    private String imageUrl;
    private BookStatus status;

}
