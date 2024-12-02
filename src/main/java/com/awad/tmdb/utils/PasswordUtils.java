package com.awad.tmdb.utils;


import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public final class PasswordUtils {
    private static PasswordEncoder passwordEncoder = null;
    public PasswordUtils(PasswordEncoder passwordEncoder) {
        PasswordUtils.passwordEncoder = passwordEncoder;
    }

    public static String encode(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public static boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public static String generateRandomPassword() {
        return generateRandomPassword(10);
    }

    public static String generateRandomPassword(int passwordLength) {
        return RandomStringUtils.secure().next(passwordLength);
    }
}
