package com.awad.tmdb.exception.type;

import com.awad.tmdb.exception.ExceptionType;

public enum UserException implements ExceptionType {
    USER_NOT_FOUND("user_not_found", "User Not Found"),
    USER_NOT_ACTIVATED("user_not_activated", "User not activated"),
    USER_DEACTIVATED("user_deactivated", "User deactivated"),
    EMAIL_ALREADY_EXISTS("email_already_exists", "Email already exists"),
    ;
    private final String code;
    private final String defaultMessage;

    UserException(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDefaultMessage() {
        return defaultMessage;
    }
}
