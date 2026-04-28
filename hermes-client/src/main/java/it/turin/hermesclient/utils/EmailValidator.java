package it.turin.hermesclient.utils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class EmailValidator {

    private static final Pattern EMAIL_REGEX = Pattern.compile("^[a-z0-9._%+\\-]+@[a-z0-9.\\-]+\\.[a-z]{2,}$");

    public static boolean isValid(String email) {
        return EMAIL_REGEX.matcher(email).matches();
    }

    public static boolean allValid(List<String> emails) {
        throw new UnsupportedOperationException("TODO");
    }

    public static List<String> findInvalid(List<String> emails) {
        throw new UnsupportedOperationException("TODO");
    }
}

