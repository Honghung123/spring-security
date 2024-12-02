package com.awad.tmdb.component.auth.usernamepassword;

import com.awad.tmdb.component.auth.JwtService;
import com.awad.tmdb.constant.AppPropertiesConfig;
import com.awad.tmdb.model.UserPrincipal;
import com.awad.tmdb.utils.CookieUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class UsernamePasswordAuhenticationSuccessHandler extends AbstractAuthenticationTargetUrlRequestHandler
        implements AuthenticationSuccessHandler {
    private final JwtService jwtService;
    private final AppPropertiesConfig properties;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                        Authentication authentication) throws IOException, ServletException {
        this.onAuthenticationSuccess(request, response, authentication);
    }

    @Override
    @SneakyThrows
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String domain = "localhost";
        final UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String accessToken = jwtService.generateAccessToken(userPrincipal);
        int accessTokenCookieMaxAge = 180;
        Cookie accessTokenCookie = CookieUtils.newCookieWithHttpOnly(OAuth2ParameterNames.ACCESS_TOKEN,
                accessToken, domain, accessTokenCookieMaxAge);
        String refreshToken = jwtService.generateRefreshToken(userPrincipal);
        int refreshTokenCookieMaxAge = 30 * 24 * 60 * 60;
        Cookie refreshTokenCookie = CookieUtils.newCookie(OAuth2ParameterNames.REFRESH_TOKEN,
                refreshToken, domain, refreshTokenCookieMaxAge);
        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
        String redirectUrl = this.determineTargetUrl(request, response, authentication);
        this.clearAuthenticationAttributes(request);
        this.getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }

    protected final void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {
        String redirectUri = request.getParameter(OAuth2ParameterNames.REDIRECT_URI);
        if (redirectUri == null || !this.isRedirectUriTheSameAllowedOrigins(redirectUri)) {
            log.error("Unknown redirect URI: " + redirectUri);
            redirectUri = this.getDefaultTargetUrl();
        }
        return UriComponentsBuilder.fromUriString(redirectUri)
                .build().toUriString();
    }

    private boolean isRedirectUriTheSameAllowedOrigins(String uri) {
        URI clientRedirectUri = URI.create(uri);
        var redirectUris = List.of("http://localhost:3000", "http://localhost:3001");
        return redirectUris
                .stream()
                .anyMatch(authorizedRedirectUri -> {
                    URI authorizedURI = URI.create(authorizedRedirectUri);
                    return authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                            && authorizedURI.getPort() == clientRedirectUri.getPort();
                });
    }
}
