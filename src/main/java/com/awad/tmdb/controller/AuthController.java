package com.awad.tmdb.controller;


import com.awad.tmdb.dto.request.user.UserLoginRequest;
import com.awad.tmdb.dto.response.AppResponseEntity;
import com.awad.tmdb.exception.BusinessException;
import com.awad.tmdb.exception.type.AuthException;
import com.awad.tmdb.model.AuthenticatedToken;
import com.awad.tmdb.service.auth.AuthService;
import com.awad.tmdb.utils.CookieUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    // Test login/register tại localhost:8080/auth/login trên Server
    @GetMapping("/login")
    public String loginPage() throws Exception {
        try {
            String loginFile = "templates/index.html";
            ClassPathResource resource = new ClassPathResource(loginFile);
            Path path = resource.getFile().toPath();
            // Đọc toàn bộ nội dung file thành chuỗi
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new Exception("Failed to get login page!");
    }

    @PostMapping("/login")
    public AppResponseEntity<?> handleLoginRequest(
            UserLoginRequest loginRequest,
            HttpServletResponse response) throws IOException {
        AuthenticatedToken authToken = authService.handleLoginRequest(loginRequest);
        String domain = "localhost";
        int accessTokenCookieMaxAge = 3600;
        Cookie accessTokenCookie = CookieUtils.newCookieWithHttpOnly(OAuth2ParameterNames.ACCESS_TOKEN,
                authToken.getAccessToken(), domain, accessTokenCookieMaxAge);
        int refreshTokenCookieMaxAge = 30 * 24 * 60 * 60;
        Cookie refreshTokenCookie = CookieUtils.newCookie(OAuth2ParameterNames.REFRESH_TOKEN,
                authToken.getRefreshToken(), domain, refreshTokenCookieMaxAge);
        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
        return AppResponseEntity.from(HttpStatus.OK, "Login successfully");
    }

    @PostMapping("/refresh-token")
    public AppResponseEntity<?> handleGeneratingNewTokenRequest(HttpServletRequest request,
                                                                HttpServletResponse response) {
        String oldRefreshToken = CookieUtils.getCookie(request, OAuth2ParameterNames.REFRESH_TOKEN)
                .map(Cookie::getValue).orElseThrow(() -> BusinessException.from(AuthException.TOKEN_NOT_FOUND));
        AuthenticatedToken authToken = authService.handleGeneratingNewTokenRequest(oldRefreshToken);
        String domain = "";
        int accessTokenCookieMaxAge = 180;
        Cookie accessTokenCookie = CookieUtils.newCookieWithHttpOnly(OAuth2ParameterNames.ACCESS_TOKEN,
                authToken.getAccessToken(), domain, accessTokenCookieMaxAge);
        int refreshTokenCookieMaxAge = 30 * 24 * 60 * 60;
        Cookie refreshTokenCookie = CookieUtils.newCookie(OAuth2ParameterNames.REFRESH_TOKEN,
                authToken.getRefreshToken(), domain, refreshTokenCookieMaxAge);
        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
        return AppResponseEntity.from(HttpStatus.OK, "Created new tokens successfully");
    }

}
