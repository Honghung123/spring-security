package com.awad.tmdb.component.auth.oauth2;


import com.awad.tmdb.constant.AppPropertiesConfig;
import com.awad.tmdb.utils.CookieUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * This class is used to get and save the redirect_uri to the session cookie when clients login via social account
 *
 */

@Component
@RequiredArgsConstructor
public class HttpCookieOAuth2AuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {
    public static final String OAUTH2_AUTHORIZATION_REQUEST_COOKIE = "oauth2_auth_request";
    public static final int COOKIE_EXPIRE_SECONDS = 180;
    public final AppPropertiesConfig authConfigProperties;

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        return CookieUtils.getCookie(request, OAUTH2_AUTHORIZATION_REQUEST_COOKIE)
                .map(cookie -> CookieUtils.deserialize(cookie, OAuth2AuthorizationRequest.class))
                .orElse(null);
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request,
                                         HttpServletResponse response) {
        if (authorizationRequest == null) {
            this.removeAuthorizationRequestCookies(request, response);
            return;
        }
        String domain = "localhost";
        Cookie cookie = CookieUtils.newCookie(OAUTH2_AUTHORIZATION_REQUEST_COOKIE,
                CookieUtils.serialize(authorizationRequest), domain, COOKIE_EXPIRE_SECONDS);
        response.addCookie(cookie);
        String redirectUri = request.getParameter(OAuth2ParameterNames.REDIRECT_URI);
        if (redirectUri != null && StringUtils.hasText(redirectUri)) {
            response.addCookie(CookieUtils.newCookie(OAuth2ParameterNames.REDIRECT_URI, redirectUri, domain, COOKIE_EXPIRE_SECONDS));
        }
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request,
                                                                 HttpServletResponse response) {
        var authorizationRequest = this.loadAuthorizationRequest(request);
        CookieUtils.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE);
        return authorizationRequest;
    }

    public void removeAuthorizationRequestCookies(HttpServletRequest request, HttpServletResponse response) {
        CookieUtils.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE);
        CookieUtils.deleteCookie(request, response, OAuth2ParameterNames.REDIRECT_URI);
    }
}

