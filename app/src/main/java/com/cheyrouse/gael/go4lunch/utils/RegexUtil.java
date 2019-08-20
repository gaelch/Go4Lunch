package com.cheyrouse.gael.go4lunch.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {


    // Regex to valid email
    public static boolean isValidEmail(String email){
        Pattern pattern;
        Matcher matcher;

        final String EMAIL_PATTERN = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";

        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);

        return matcher.matches();
    }
}
