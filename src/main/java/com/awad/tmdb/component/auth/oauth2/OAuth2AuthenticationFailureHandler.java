package com.awad.tmdb.component.auth.oauth2;

import com.awad.tmdb.utils.CookieUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRqRepo;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        final String ERROR_PARAM_NAME = "error";
        String targetUrl = CookieUtils
                .getCookie(request, OAuth2ParameterNames.REDIRECT_URI)
                .map(Cookie::getValue).orElse("/");
        targetUrl = UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam(ERROR_PARAM_NAME, exception.getLocalizedMessage())
                .build().toUriString();
        this.httpCookieOAuth2AuthorizationRqRepo.removeAuthorizationRequestCookies(request, response);
        this.getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
