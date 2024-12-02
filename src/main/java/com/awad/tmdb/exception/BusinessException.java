package com.awad.tmdb.exception;


import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final String code;
    private final String message;

    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public static BusinessException from(ExceptionType errorCode) {
        return new BusinessException(errorCode.getCode(), errorCode.getDefaultMessage());
    }

    public static BusinessException from(ExceptionType errorCode, String message) {
        return new BusinessException(errorCode.getCode(), message);
    }
}