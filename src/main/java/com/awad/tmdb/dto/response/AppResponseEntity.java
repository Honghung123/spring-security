package com.awad.tmdb.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
public class AppResponseEntity<T> extends ResponseEntity<AppResponseEntity.Payload<T>> {
    public static <T> AppResponseEntity<T> from(HttpStatus status, String message) {
        return new AppResponseEntity<>(status, message);
    }

    public static <T> AppResponseEntity<T> from(HttpStatus status, String message, T data) {
        return new AppResponseEntity<T>(status, message, data);
    }

    public static <T> AppResponseEntity<T> from(
            HttpStatus status, String message, T data, Object others) {
        return new AppResponseEntity<T>(status, message, data, others);
    }

    public AppResponseEntity(HttpStatus status, String message) {
        super(Payload.<T>builder().statusCode(status.value()).message(message).build(), status);
    }

    public AppResponseEntity(HttpStatus status, String message, T data) {
        super(
                Payload.<T>builder().statusCode(status.value()).message(message).data(data).build(),
                status);
    }

    public AppResponseEntity(HttpStatus status, String message, T data, Object others) {
        super(
                Payload.<T>builder()
                        .statusCode(status.value())
                        .message(message)
                        .data(data)
                        .others(others)
                        .build(),
                status);
    }

    @Getter
    @AllArgsConstructor
    @Builder
    static class Payload<T> {
        private int statusCode;
        private String message;
        private T data;
        private Object others;
    }
}
