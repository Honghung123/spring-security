package com.awad.tmdb.model.oauth2;


import java.util.Map;
import java.util.UUID;

public class GoogleOAuth2UserInfo extends OAuth2UserInfo{
    public GoogleOAuth2UserInfo(Map<String, Object> attributes){
        super(attributes);
    }

    @Override
    public UUID getId() {
        return UUID.fromString((String) this.getAttributes().get("id"));
    }

    @Override
    public String getName() {
        return (String) this.getAttributes().get("name");
    }

    @Override
    public String getEmail() {
        return (String) this.getAttributes().get("email");
    }

    @Override
    public String getAvatar() {
        return (String) this.getAttributes().get("picture");
    }
}
