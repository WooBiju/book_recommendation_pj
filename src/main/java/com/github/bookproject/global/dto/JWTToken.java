package com.github.bookproject.global.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JWTToken {

    private String accessToken;
    private String refreshToken;
}
