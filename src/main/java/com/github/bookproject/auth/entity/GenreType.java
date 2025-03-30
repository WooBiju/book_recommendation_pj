package com.github.bookproject.auth.entity;

import lombok.Getter;

@Getter
public enum GenreType {
    NOVEL("소설"),
    POETRY("시"),
    SCIENCE("과학"),
    SOCIETY("사회"),
    SELF_DEVELOPMENT("자기계발"),
    ECONOMY("경제"),
    HISTORY("역사"),
    IT("IT"),
    ESSAY("에세이"),
    TRAVEL("여행");

    private final String description;

    GenreType(String description) {
        this.description = description;
    }

}
