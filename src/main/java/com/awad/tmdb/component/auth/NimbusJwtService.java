package com.awad.tmdb.component.auth;

import java.text.ParseException;
import java.util.Date;

import com.awad.tmdb.constant.AppPropertiesConfig;
import com.awad.tmdb.exception.BusinessException;
import com.awad.tmdb.exception.type.AuthException;
import com.awad.tmdb.model.UserPrincipal;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.stereotype.Component;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Component
@RequiredArgsConstructor
@Slf4j
public class NimbusJwtService implements JwtService{
    private final AppPropertiesConfig authConfigProperties;

    private String generateToken(UserPrincipal user, Date expiryDate, String tokenType) throws JOSEException {
        var jwsClaims = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .jwtID(user.getId().toString())
                .expirationTime(expiryDate)
                .issueTime(new Date())
                .issuer("honghung.com")
                // .claim("scope", buildUserRoles(user.getAuthorities()))
                .claim("tokenType", tokenType)
                .build();
        return this.buildToken(jwsClaims);
    }

    private String buildToken(JWTClaimsSet jwsClaims) throws JOSEException {
        var jwsHeader = new JWSHeader(JWSAlgorithm.parse(authConfigProperties.getJwt().getMacAlgorithm()));
        var payload = new Payload(jwsClaims.toJSONObject());
        var jwsObject = new JWSObject(jwsHeader, payload);
        jwsObject.sign(new MACSigner(authConfigProperties.getJwt().getSecret().getBytes()));
        return jwsObject.serialize();
    }

    public JWTClaimsSet getJWTClaimsSet(String token) throws ParseException, JOSEException {
        var jwsVerifier = new MACVerifier(authConfigProperties.getJwt().getSecret().getBytes());
        var signedJwt = SignedJWT.parse(token);
        boolean verified = signedJwt.verify(jwsVerifier);
        if (!verified) {
            throw BusinessException.from(AuthException.INVALID_TOKEN);
        }
        return signedJwt.getJWTClaimsSet();
    }

    @Override
    public String getSubject(String token) throws Exception {
        return this.getJWTClaimsSet(token).getSubject();
    }

    @Override
    public Date getExpiration(String token) throws Exception {
        return this.getJWTClaimsSet(token).getExpirationTime();
    }

    @Override
    public boolean isTokenExpired(String token) throws Exception {
        return this.getExpiration(token).before(new Date());
    }

    @Override
    public String generateRefreshToken(UserPrincipal user) throws JOSEException {
        var expiryDate = authConfigProperties.getJwt().getRefreshTokenExpirationDate();
        return this.generateToken(user, expiryDate, OAuth2ParameterNames.REFRESH_TOKEN);
    }

    @Override
    public String generateAccessToken(UserPrincipal user) throws JOSEException {
        var accessTokenExpiryDate = authConfigProperties.getJwt().getAccessTokenExpirationDate();
        return this.generateToken(user, accessTokenExpiryDate, OAuth2ParameterNames.ACCESS_TOKEN);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getClaimsProperty(String property, String token){
        try {
            return (T)this.getJWTClaimsSet(token).getClaim(property);
        } catch (Exception e) {
            throw new RuntimeException("Failed to cast to the specific type");
        }
    }

    @Override
    public boolean validateToken(String token, UserPrincipal user){
        try {
            if (this.getSubject(token).equals(user.getEmail())) {
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("Failed to verify token. Error message: {}", e.getLocalizedMessage(), e);
            return false;
        }
    }

    @Override
    public boolean isAToken(String token){
        return true;
    }

    @Override
    public boolean isASpecificToken(String token, String tokenType){
        try {
            return this.getClaimsProperty("tokenType", token).equals(tokenType);
        } catch (Exception e) {
            log.error("Failed to check the type of the token. Error message: {}", e.getLocalizedMessage(), e);
            return false;
        }
    }
}

