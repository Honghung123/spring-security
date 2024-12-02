package com.awad.tmdb.constant;

public enum OAuth2AuthProvider {
    LOCAL("local"),
    FACEBOOK("facebook"),
    GOOGLE("google"),
    GITHUB("github");

    private final String name;
    OAuth2AuthProvider(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public static OAuth2AuthProvider of(String name) {
        for (OAuth2AuthProvider provider : values()) {
            if (provider.getName().equals(name)) {
                return provider;
            }
        }
        return null;
    }
}