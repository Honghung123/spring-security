package com.awad.tmdb.component.auth.usernamepassword;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.stereotype.Component;

/**
 * This class is used to get and save the redirect_uri to the session cookie when clients login via username password
 *
 */

@Component
public class CustomRequestCache extends HttpSessionRequestCache {

    @Override
    public void saveRequest(HttpServletRequest request, HttpServletResponse response) {
        super.saveRequest(request, response);
        String redirectUri = request.getParameter("redirect_uri");
        if (redirectUri != null) {
            request.getSession().setAttribute("redirect_uri", redirectUri);
        }
    }
}