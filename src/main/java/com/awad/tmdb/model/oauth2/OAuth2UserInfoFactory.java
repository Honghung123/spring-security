package com.awad.tmdb.model.oauth2;

import com.awad.tmdb.constant.OAuth2AuthProvider;

import java.util.Map;

public final class OAuth2UserInfoFactory {
    public static OAuth2UserInfo createOAuth2UserInfo(String registerId, Map<String, Object> attributes){
        OAuth2AuthProvider provider = OAuth2AuthProvider.of(registerId);
        return switch (provider) {
            case GOOGLE -> new GoogleOAuth2UserInfo(attributes);
            default -> null;
        };
    }
}
