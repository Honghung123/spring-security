package com.awad.tmdb.service.user;


import com.awad.tmdb.dto.request.user.UserRegisterRequest;
import com.awad.tmdb.entity.Role;
import com.awad.tmdb.entity.User;
import com.awad.tmdb.exception.BusinessException;
import com.awad.tmdb.exception.type.UserException;
import com.awad.tmdb.repository.RoleRepository;
import com.awad.tmdb.repository.UserRepository;
import com.awad.tmdb.utils.PasswordUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public List<User> getUserList() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(UUID id) {
        return userRepository
                .findById(id)
                .orElseThrow(() -> BusinessException.from(UserException.USER_NOT_FOUND));
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository
                .findByUsername(username)
                .orElseThrow(() -> BusinessException.from(UserException.USER_NOT_FOUND));
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository
                .findByEmail(email)
                .orElseThrow(() -> BusinessException.from(UserException.USER_NOT_FOUND));
    }

    @Transactional
    public User insertUser(UserRegisterRequest request) {
        if(userRepository.findByEmail(request.email()).isPresent()) {
            throw BusinessException.from(UserException.EMAIL_ALREADY_EXISTS, "Email already exists");
        }
        User user = User.builder()
                .email(request.email())
                .name(request.name())
                .password(PasswordUtils.encode(request.password()))
                .build();
        Role roleUser = roleRepository.findByNameIgnoreCase("USER").get();
        user.setRoles(Set.of(roleUser));
        return userRepository.save(user);
    }

}

