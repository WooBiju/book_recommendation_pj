package com.github.bookproject.auth.dto;

import com.github.bookproject.auth.entity.User;
import lombok.*;

@Getter
public class UserResponseDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JoinResultDTO{
        private Long userId;
        private String email;
        private String nickname;

        public static JoinResultDTO from(User user) {
            return new JoinResultDTO(
                    user.getId(),
                    user.getEmail(),
                    user.getUsername()
            );
        }
    }
}
