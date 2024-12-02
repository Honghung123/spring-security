package com.awad.tmdb.dto.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record UserLoginRequest (
        @NotNull(message = "Email cannot be blank")
        @Email(message = "Email is not valid")
        String email,
        @NotNull(message = "Password cannot be blank")
        String password
){
}
