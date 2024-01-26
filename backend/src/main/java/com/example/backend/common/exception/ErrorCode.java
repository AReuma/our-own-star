package com.example.backend.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    NOT_FOUND_USER_ID(HttpStatus.NOT_FOUND, ""),
    USER_DUPLICATED(HttpStatus.CONFLICT, ""),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, ""),
    USER_NICKNAME_DUPLICATED(HttpStatus.CONFLICT, ""),
    NOT_FOUND_USER_NICKNAME(HttpStatus.CONFLICT, ""),
    NOT_FOUND_CATEGORY(HttpStatus.NOT_FOUND, ""),
    UNAUTHORIZED_ACCESS(HttpStatus.FORBIDDEN, ""),
    NOT_FOUND_BOARD(HttpStatus.NOT_FOUND, ""),
    EMAIL_SEND_FAIL(HttpStatus.CONFLICT, ""),
    CACHE_DATA_NOT_EXISTED(HttpStatus.NOT_FOUND, ""),
    IDOL_CATEGORY_DUPLICATED(HttpStatus.CONFLICT, ""),
    IDOL_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, ""),
    ALREADY_JOIN_USER(HttpStatus.CONFLICT, ""),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
