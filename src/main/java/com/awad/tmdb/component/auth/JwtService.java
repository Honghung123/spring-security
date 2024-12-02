package com.awad.tmdb.component.auth;


import com.awad.tmdb.model.UserPrincipal;

import java.util.Date;

public interface JwtService {
    String getSubject(String token) throws Exception;
    Date getExpiration(String token) throws Exception;
    boolean isTokenExpired(String token) throws Exception;
    String generateRefreshToken(UserPrincipal user) throws Exception;
    String generateAccessToken(UserPrincipal user) throws Exception;
    <T> T getClaimsProperty(String property, String token) throws Exception;
    boolean validateToken(String token, UserPrincipal user) throws Exception;

    boolean isAToken(String token);
    boolean isASpecificToken(String token, String type);
}