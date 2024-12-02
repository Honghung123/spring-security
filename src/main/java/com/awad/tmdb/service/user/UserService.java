package com.awad.tmdb.service.user;


import java.util.List;
import java.util.UUID;

import com.awad.tmdb.dto.request.user.UserRegisterRequest;
import com.awad.tmdb.entity.User;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    // Query
    List<User> getUserList();

    User getUserById(UUID id);

    User getUserByUsername(String username);

    User getUserByEmail(String email);
    // Commands
    User insertUser(UserRegisterRequest request);
}