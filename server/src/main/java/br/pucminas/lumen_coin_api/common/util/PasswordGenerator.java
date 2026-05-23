package br.pucminas.lumen_coin_api.common.util;

import java.security.SecureRandom;

public final class PasswordGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "!@#$%&*";
    private static final String ALL = UPPER + LOWER + DIGITS + SPECIAL;
    private static final int PASSWORD_LENGTH = 12;

    private PasswordGenerator() {
    }

    public static String generate() {
        char[] password = new char[PASSWORD_LENGTH];
        password[0] = UPPER.charAt(RANDOM.nextInt(UPPER.length()));
        password[1] = LOWER.charAt(RANDOM.nextInt(LOWER.length()));
        password[2] = DIGITS.charAt(RANDOM.nextInt(DIGITS.length()));
        password[3] = SPECIAL.charAt(RANDOM.nextInt(SPECIAL.length()));

        for (int i = 4; i < PASSWORD_LENGTH; i++) {
            password[i] = ALL.charAt(RANDOM.nextInt(ALL.length()));
        }

        for (int i = PASSWORD_LENGTH - 1; i > 0; i--) {
            int j = RANDOM.nextInt(i + 1);
            char temp = password[i];
            password[i] = password[j];
            password[j] = temp;
        }

        return new String(password);
    }
}
