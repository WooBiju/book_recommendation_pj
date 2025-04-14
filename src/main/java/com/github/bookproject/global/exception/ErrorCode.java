package com.github.bookproject.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // user
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 가입된 이메일 입니다."),
    IO_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "파일 처리 중 오류가 발생했습니다."),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED,"로그인에 실패했습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND,"해당 유저를 찾을 수 없습니다."),
    INVALID_FILE_NAME(HttpStatus.BAD_REQUEST,"유효하지 않은 파일 이름입니다."),

    // book
    BOOK_NOT_FOUND(HttpStatus.NOT_FOUND,"해당 도서를 찾을 수 없습니다."),
    ALREADY_FAVORITE_BOOK(HttpStatus.CONFLICT,"이미 찜한 도서입니다."),
    NOT_FAVORITE_BOOK(HttpStatus.NOT_FOUND,"찜한 도서가 존재하지 않습니다."),

    // review
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND,"해당 리뷰를 찾을 수 없습니다."),
    REVIEW_FORBIDDEN(HttpStatus.FORBIDDEN,"리뷰를 삭제할 권한이 없습니다."),
    DUPLICATE_REVIEW(HttpStatus.CONFLICT,"리뷰를 작성한 도서입니다."),
    INVALID_RATING(HttpStatus.FORBIDDEN,"별점은 1.0 ~ 5.0 사이여야 합니다."),
    REVIEW_CONFLICT(HttpStatus.CONFLICT,"다른 사용자에 요청으로 인해 별점 업데이트에 실패하였습니다. 다시 시도해 주세요."),

    // record
    DUPLICATE_READING_RECORD(HttpStatus.CONFLICT,"이미 독서 기록중인 도서입니다."),
    READING_RECORD_NOT_FOUND(HttpStatus.NOT_FOUND,"해당 기록을 찾을 수 없습니다."),
    READING_RECORD_FORBIDDEN(HttpStatus.FORBIDDEN,"사용자가 일치하지 않습니다."),
    INVALID_PROGRESS(HttpStatus.FORBIDDEN,"진행률은 0.0 ~ 1.0 사이여야 합니다."),
    ALREADY_COMPLETE(HttpStatus.ALREADY_REPORTED,"이미 독서 완료한 기록입니다."),
    INVALID_INITIAL_READING_STATUS(HttpStatus.FORBIDDEN,"독서 기록 생성 시 상태는 COMPLETE 일 수 없습니다."),

    // myPage
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST,"비밀번호가 일치하지 않습니다."),
    INVALID_GENRE(HttpStatus.BAD_REQUEST,"해당 장르는 유효하지 않습니다.");



    private final HttpStatus httpStatus;
    private final String message;

}
