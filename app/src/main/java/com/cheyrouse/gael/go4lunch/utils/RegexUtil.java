package com.cheyrouse.gael.go4lunch.utils;


public class RegexUtil {

    // Pattern android to valid email
    public static boolean checkForEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
