package com.hms.service.utils;



import com.hms.service.constants.Constants;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PasswordGenerator {

    private PasswordGenerator() {}

    private static final SecureRandom random = new SecureRandom();

    public static String generatePassword(int length) {

        String[] characters = {
               Constants.NUMERIC_CODE,
               Constants.SPECIAL_CHARS_CODE,
                Constants.ALPHABETS_CAPS_CODE,
                Constants.ALPHABETS_SMALL_CODE
        };

        List<Character> passwordChars = new ArrayList<>();

        
        for (String set : characters) {
            int index = random.nextInt(set.length());
            passwordChars.add(set.charAt(index));
        }

        for (int i = passwordChars.size(); i < length; i++) {
            int randomSet = random.nextInt(characters.length);
            String selected = characters[randomSet];

            int index = random.nextInt(selected.length());
            passwordChars.add(selected.charAt(index));
        }

        
        Collections.shuffle(passwordChars);

        StringBuilder password = new StringBuilder();
        for (char c : passwordChars) {
            password.append(c);
        }

        return password.toString();
    }

    public static String generatePin(int length) {

        StringBuilder pin = new StringBuilder();

        for (int i = 0; i < length; i++) {
            pin.append(random.nextInt(10));
        }

        return pin.toString();
    }
}