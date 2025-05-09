package com.github.bookproject.auth.entity;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String password;

    private String email;

    private String phone;

    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    private Role role;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "user_genre", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private List<GenreType> preferredGenres = new ArrayList<>();


    public static User create(String username, String password, String email, String phone,
                              String profileImageUrl,List<GenreType> preferredGenres, Role role) {
        return User.builder()
                .username(username)
                .password(password)
                .email(email)
                .phone(phone)
                .profileImageUrl(profileImageUrl)
                .preferredGenres(preferredGenres)
                .role(role)
                .build();
    }

    public void updateProfile(String username, String phoneNumber) {
        if (StringUtils.hasText(username)) this.username = username;
        if (StringUtils.hasText(phoneNumber)) this.phone = phoneNumber;
    }

    public void updateProfileImage(String profileImageUrl) {
        if (profileImageUrl != null) this.profileImageUrl = profileImageUrl;
    }

    public void changePassword(String newPassword) {
        this.password = newPassword;
    }

    public void updatePreferredGenres(List<GenreType> preferredGenres) {
        this.preferredGenres.clear();
        this.preferredGenres.addAll(preferredGenres);
    }

}
