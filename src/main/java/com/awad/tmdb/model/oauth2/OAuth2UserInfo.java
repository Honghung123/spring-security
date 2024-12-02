package com.awad.tmdb.model.oauth2;

import java.util.Map;
import java.util.UUID;

public abstract class OAuth2UserInfo {
    private Map<String, Object> attributes;

    public OAuth2UserInfo(Map<String, Object> attributes){
        this.attributes = attributes;
    }

    public Map<String, Object> getAttributes(){
        return this.attributes;
    }

    public abstract UUID getId();
    public abstract String getName();
    public abstract String getEmail();
    public abstract String getAvatar();
}
