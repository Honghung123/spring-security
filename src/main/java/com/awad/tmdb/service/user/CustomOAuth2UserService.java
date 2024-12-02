package com.awad.tmdb.service.user;


import com.awad.tmdb.entity.Role;
import com.awad.tmdb.entity.User;
import com.awad.tmdb.exception.BusinessException;
import com.awad.tmdb.exception.type.RoleException;
import com.awad.tmdb.model.oauth2.OAuth2UserInfo;
import com.awad.tmdb.model.UserPrincipal;
import com.awad.tmdb.model.oauth2.OAuth2UserInfoFactory;
import com.awad.tmdb.repository.RoleRepository;
import com.awad.tmdb.repository.UserRepository;
import com.awad.tmdb.utils.EmailUtils;
import com.awad.tmdb.utils.PasswordUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registerId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.createOAuth2UserInfo(registerId, oAuth2User.getAttributes());
        String userEmail = oAuth2UserInfo.getEmail();
        var userOptional = userRepository.findByEmail(userEmail);
        User user = userOptional.isPresent() ?
                updateUserInfoIfSo(oAuth2UserInfo, userOptional.get(), registerId) :
                registerForNewUser(oAuth2UserInfo);
        return UserPrincipal.createFrom(user);
    }

    @Transactional
    User registerForNewUser(OAuth2UserInfo oAuth2UserInfo) {
        String username = EmailUtils.extractUsernameFromEmail(oAuth2UserInfo.getEmail());
        String hashedGeneratedPassword = PasswordUtils.encode(PasswordUtils.generateRandomPassword());
        Role roleUser = roleRepository.findByName("USER")
                .orElseThrow(() -> BusinessException.from(RoleException.ROLE_NOT_FOUND));
        var newUser = User.builder()
                .name(oAuth2UserInfo.getName())
                .username(username)
                .email(oAuth2UserInfo.getEmail())
                .password(hashedGeneratedPassword)
                .roles(Set.of(roleUser))
                .status(true)
                .build();
        return userRepository.save(newUser);
    }

    @Transactional
    User updateUserInfoIfSo(OAuth2UserInfo oAuth2UserInfo, User user, String registerId) {
        // Update avatar url
        return user;
    }
}
