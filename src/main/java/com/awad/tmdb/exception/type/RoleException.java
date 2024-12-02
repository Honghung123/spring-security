package com.awad.tmdb.exception.type;

import com.awad.tmdb.exception.ExceptionType;

public enum RoleException implements ExceptionType {
    ROLE_NOT_FOUND("role_not_found", "Role not found"),
    ROLE_NOT_ACTIVATED("role_not_activated", "Role not activated"),
    ROLE_DEACTIVATED("role_deactivated", "Role deactivated");

    private final String code;
    private final String defaultMessage;

    RoleException(String code, String defaultMessage) {
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
