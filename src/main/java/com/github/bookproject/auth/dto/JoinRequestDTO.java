package com.github.bookproject.auth.dto;

import com.github.bookproject.auth.entity.GenreType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class JoinRequestDTO {
    private String username;
    private String email;
    private String password;
    private String phone;
    private List<GenreType> preferredGenres;

}
