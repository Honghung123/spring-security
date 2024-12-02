package com.awad.tmdb.model;


import com.awad.tmdb.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Builder
public class UserPrincipal implements UserDetails, OAuth2User {
    private UUID id;
    private String username;
    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private Map<String, Object> attributes;

    @Override
    public String getName() {
        return this.id.toString();
    }

    public static UserPrincipal createFrom(User user){
        List<SimpleGrantedAuthority> userAuthorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName())).toList();
        return UserPrincipal.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .authorities(userAuthorities)
                .build();
    }

}

