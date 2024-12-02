package com.awad.tmdb.service.auth;

import com.awad.tmdb.component.auth.JwtService;
import com.awad.tmdb.constant.AppPropertiesConfig;
import com.awad.tmdb.dto.request.user.UserLoginRequest;
import com.awad.tmdb.entity.User;
import com.awad.tmdb.exception.BusinessException;
import com.awad.tmdb.exception.type.AuthException;
import com.awad.tmdb.exception.type.UserException;
import com.awad.tmdb.model.AuthenticatedToken;
import com.awad.tmdb.model.UserPrincipal;
import com.awad.tmdb.repository.UserRepository;
import com.awad.tmdb.utils.PasswordUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @SneakyThrows
    @Override
    public AuthenticatedToken handleLoginRequest(UserLoginRequest request) {
        // Get user by email
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> BusinessException.from(UserException.USER_NOT_FOUND));
        // Verify password
        if (!PasswordUtils.matches(user.getPassword(), request.password())) {
            throw BusinessException.from(AuthException.PASSWORD_NOT_MATCH);
        }
        UserPrincipal userPrincipal = UserPrincipal.createFrom(user);
        // Generate token
        String accessToken = jwtService.generateAccessToken(userPrincipal);
        String refreshToken = jwtService.generateRefreshToken(userPrincipal);
        return AuthenticatedToken.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @SneakyThrows
    @Override
    public AuthenticatedToken handleGeneratingNewTokenRequest(String refreshToken) {
        // Verify refreshToken
        if(!jwtService.isAToken(refreshToken)){
            throw BusinessException.from(AuthException.NOT_A_TOKEN);
        }
        if(!jwtService.isASpecificToken(refreshToken, OAuth2ParameterNames.REFRESH_TOKEN)){
            throw BusinessException.from(AuthException.INVALID_TOKEN);
        }
        String userEmail = jwtService.getSubject(refreshToken);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> BusinessException.from(UserException.USER_NOT_FOUND));
        UserPrincipal userPrincipal = UserPrincipal.createFrom(user);
        // Generate token
        String newAccessToken = jwtService.generateAccessToken(userPrincipal);
        String newRefreshToken = jwtService.generateRefreshToken(userPrincipal);
        return AuthenticatedToken.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }
}
