package com.awad.tmdb.component.auth.oauth2;


import com.awad.tmdb.component.auth.JwtService;
import com.awad.tmdb.constant.AppPropertiesConfig;
import com.awad.tmdb.model.UserPrincipal;
import com.awad.tmdb.utils.CookieUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.List;

/**
 * This
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRqRepo;
    private final AppPropertiesConfig configProperties;
    private final JwtService jwtService;

    @Override
    @SneakyThrows
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String targetUrl = this.determineTargetUrl(request, response, authentication);
        String domain = "localhost";
        if (response.isCommitted()) {
            log.debug("Response has been committed. Unable to redirect to " + targetUrl);
            return;
        }
        this.clearAuthenticationAttributes(request);
        final UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String userAccessToken = jwtService.generateAccessToken(userPrincipal);
        int accessTokenCookieMaxAge = 180;
        Cookie accessTokenCookie = CookieUtils.newCookie(OAuth2ParameterNames.ACCESS_TOKEN, userAccessToken, domain,
                accessTokenCookieMaxAge);
        String userRefreshToken = jwtService.generateRefreshToken(userPrincipal);
        int refreshTokenCookieMaxAge = 30 * 24 * 60 * 60;
        Cookie refreshTokenCookie = CookieUtils.newCookie(OAuth2ParameterNames.REFRESH_TOKEN, userRefreshToken, domain,
                refreshTokenCookieMaxAge);
        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
        this.getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                        Authentication authentication) throws IOException, ServletException {
        super.onAuthenticationSuccess(request, response, chain, authentication);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {
        String redirectUri = CookieUtils.removeCookie(request, response, OAuth2ParameterNames.REDIRECT_URI)
                .map(Cookie::getValue).orElse("/");
        if (!this.isRedirectUriTheSameAllowedOrigins(redirectUri)) {
            throw new RuntimeException("Unknown redirect URI: " + redirectUri);
        }
        return UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("auth", true)
                .build().toUriString();
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        this.httpCookieOAuth2AuthorizationRqRepo.removeAuthorizationRequestCookies(request, response);
    }

    private boolean isRedirectUriTheSameAllowedOrigins(String uri) {
        URI clientRedirectUri = URI.create(uri);
        var redirectUris = List.of("http://localhost:3000", "http://localhost:3001", "http://localhost:8080");
        return redirectUris
                .stream()
                .anyMatch(authorizedRedirectUri -> {
                    URI authorizedURI = URI.create(authorizedRedirectUri);
                    return authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                            && authorizedURI.getPort() == clientRedirectUri.getPort();
                });
    }
}
