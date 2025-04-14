package com.github.bookproject.global.util;

import com.github.bookproject.global.exception.AppException;
import com.github.bookproject.global.exception.ErrorCode;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
@Getter
public class ImageFileUtils {

    @Value("${user.profile-image-dir}")
    private String profileImageDir;

    // 프로필 이미지 저장 메서드 (로컬)
    public String saveProfileImage(MultipartFile profileImage) throws IOException {
        if (profileImage == null || profileImage.isEmpty()) {
            return null;
        }

        // 확장자 예외 방지
        String originalFilename = profileImage.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new AppException(ErrorCode.INVALID_FILE_NAME);
        }

        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = UUID.randomUUID() + "_" + System.currentTimeMillis() + fileExtension; // 햔재 시간 기준으로 파일명 생성

        Path path = Paths.get(profileImageDir, fileName);  // 파일 경로

        try {
            Files.createDirectories(path.getParent());  // 부모 디렉토리 생성
            profileImage.transferTo(path.toFile());     // 실제 파일을 로컬 경로에 저장

        } catch (IOException e) {
            throw new IOException(e.getMessage());

        }

        return "/image/profiles/" + fileName;

    }
}
