package com.awad.tmdb.utils;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.Base64;
import java.util.Optional;

import org.springframework.util.SerializationUtils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public final class CookieUtils {
    private static final String DEFAULT_COOKIE_PATH = "/";
    private CookieUtils() {
    }
    public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return Optional.of(cookie);
                }
            }
        }
        return Optional.empty();
    }

    public static Cookie newCookie(String name, String value) {
        return new Cookie(name, value);
    }

    public static Cookie newCookie(String name, String value, String domain, String path, int maxAge, boolean httpOnly) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        cookie.setPath(path);
        cookie.setDomain(domain);
        cookie.setHttpOnly(httpOnly);
        return cookie;
    }

    public static Cookie newCookie(String name, String value, String domain, int maxAge, boolean httpOnly) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        cookie.setPath(DEFAULT_COOKIE_PATH);
        cookie.setDomain(domain);
        cookie.setHttpOnly(httpOnly);
        return cookie;
    }

    public static Cookie newCookie(String name, String value, String domain, int maxAge) {
        return newCookie(name, value, domain, maxAge, false);
    }

    public static Cookie newCookieWithHttpOnly(String name, String value, String domain, int maxAge) {
        return newCookie(name, value, domain, maxAge, true);
    }

    public static Cookie newCookieWithHttpOnly(String name, String value, String domain, String path, int maxAge) {
        return newCookie(name, value, domain, path, maxAge, true);
    }

    public static Cookie newSecureCookie(String name, String value, String domain, String path, int maxAge, boolean httpOnly) {
        Cookie cookie = newCookie(name, value, domain, path, maxAge, httpOnly);
        cookie.setSecure(true);
        return cookie;
    }

    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    resetCookie(response, cookie);
                }
            }
        }
    }

    public static Optional<Cookie> removeCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie cookie = getCookie(request, name).orElse(null);
        if (cookie != null) {
            Cookie copiedCookie = (Cookie)cookie.clone();
            resetCookie(response, cookie);
            return Optional.of(copiedCookie);
        }
        return Optional.empty();
    }

    private static void resetCookie(HttpServletResponse response, Cookie cookie){
        cookie.setValue("");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    public static String serialize(Object object) {
        return Base64.getUrlEncoder()
                .encodeToString(SerializationUtils.serialize(object));
    }

    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        byte[] decodedValue = Base64.getUrlDecoder().decode(cookie.getValue());
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(decodedValue))) {
            return cls.cast(ois.readObject());
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing object", e);
        }
        // return cls.cast(SerializationUtils.deserialize(
        //                 Base64.getUrlDecoder().decode(cookie.getValue())));
    }
}
