package com.awad.tmdb.service.user;


import com.awad.tmdb.exception.BusinessException;
import com.awad.tmdb.exception.type.UserException;
import com.awad.tmdb.model.UserPrincipal;
import com.awad.tmdb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        if(username.contains("@")) {
            return loadUserByEmail(username);
        }
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> BusinessException.from(UserException.USER_NOT_FOUND));
        return UserPrincipal.createFrom(user);
    }

    public UserPrincipal loadUserById(UUID id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> BusinessException.from(UserException.USER_NOT_FOUND));
        return UserPrincipal.createFrom(user);
    }

    public UserPrincipal loadUserByEmail(String userEmail) {
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> BusinessException.from(UserException.USER_NOT_FOUND));
        return UserPrincipal.createFrom(user);
    }
}
