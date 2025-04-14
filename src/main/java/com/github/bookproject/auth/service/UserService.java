package com.github.bookproject.auth.service;

import com.github.bookproject.auth.dto.JoinRequestDTO;
import com.github.bookproject.auth.entity.Role;
import com.github.bookproject.auth.entity.User;
import com.github.bookproject.auth.repository.UserRepository;
import com.github.bookproject.global.exception.AppException;
import com.github.bookproject.global.exception.ErrorCode;
import com.github.bookproject.global.util.ImageFileUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ImageFileUtils imageFileUtils;

    public boolean checkEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public void createUser(JoinRequestDTO dto, MultipartFile profileImage){

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        String profileImageUrl = null;
        try{
            profileImageUrl = imageFileUtils.saveProfileImage(profileImage);
        } catch (IOException e) {
            throw new AppException(ErrorCode.IO_ERROR);
        }

        User user = User.create(
                dto.getUsername(),
                encodePassword(dto.getPassword()),
                dto.getEmail(),
                dto.getPhone(),
                profileImageUrl,
                dto.getPreferredGenres(),
                Role.ROLE_USER
        );

        userRepository.save(user);

    }

    // 비밀번호 암호화
    private String encodePassword(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        userRepository.delete(user);
    }
}
