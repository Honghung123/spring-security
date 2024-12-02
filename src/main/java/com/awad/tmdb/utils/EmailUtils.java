package com.awad.tmdb.utils;

import com.awad.tmdb.exception.BusinessException;
import com.awad.tmdb.exception.type.UserException;

public final class EmailUtils {
    public static String extractUsernameFromEmail(String email) {
        if (email != null && email.contains("@")) {
            return email.split("@")[0];
        }
        else return null;
    }

    private EmailUtils() {
    }
}
