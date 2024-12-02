package com.awad.tmdb.component.auth;

import com.awad.tmdb.model.UserPrincipal;
import com.awad.tmdb.service.user.CustomUserDetailsService;
import com.awad.tmdb.utils.CookieUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilterPerRequest extends OncePerRequestFilter {
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        System.out.println("Incoming request: " + request.getRequestURI() + " - " + request.getMethod());
        final String accessToken = CookieUtils.getCookie(request, OAuth2ParameterNames.ACCESS_TOKEN)
                .map(Cookie::getValue).orElse(null); // Get token from cookie
//        final String accessToken = request.getHeader("Authorization"); // Get token from header
        if (StringUtils.hasText(accessToken)) {
            try {
                if (jwtService.isASpecificToken(accessToken, OAuth2ParameterNames.ACCESS_TOKEN)) {
                    try {
                        String userEmail = jwtService.getSubject(accessToken);
                        if (SecurityContextHolder.getContext().getAuthentication() == null) {
                            log.info("Current user session: " + userEmail);
                            UserPrincipal user = this.userDetailsService.loadUserByEmail(userEmail);
                            var authToken = new UsernamePasswordAuthenticationToken(
                                    user, null, user.getAuthorities());
                            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContext context = SecurityContextHolder.createEmptyContext();
                            context.setAuthentication(authToken);
                            SecurityContextHolder.setContext(context);
                            log.info("Saved authentication to the security context");
                        } else {
                            log.error("Failed to set user authentication in the security context!");
                        }
                    } catch (Exception e) {
                        log.error("Failed to set user authentication in the security context!");
                    }
                } else {
                    log.error("Invalid access token: " + accessToken);
                }
            } catch (Exception e) {
                log.error("Failed to check token type!");
            }
        } else {
            log.info("Not found access token in the cookie of the request");
        }
        filterChain.doFilter(request, response);
    }
}
