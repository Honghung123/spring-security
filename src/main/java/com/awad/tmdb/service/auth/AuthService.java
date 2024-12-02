package com.awad.tmdb.service.auth;

import com.awad.tmdb.dto.request.user.UserLoginRequest;
import com.awad.tmdb.model.AuthenticatedToken;

public interface AuthService {
    AuthenticatedToken handleLoginRequest(UserLoginRequest request) ;

    AuthenticatedToken handleGeneratingNewTokenRequest(String refreshToken);
}
