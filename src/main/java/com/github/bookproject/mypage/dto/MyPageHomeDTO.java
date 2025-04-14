package com.github.bookproject.mypage.dto;

import com.github.bookproject.auth.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MyPageHomeDTO {
    private String email;
    private String username;

    private int favoriteCount;
    private int reviewCount;
    private int readingRecordCount;

    public static MyPageHomeDTO from (User user,int favoriteCount, int reviewCount, int readingRecordCount) {
        return new MyPageHomeDTO(
                user.getEmail(),
                user.getUsername(),
                favoriteCount,
                reviewCount,
                readingRecordCount
        );
    }




}
