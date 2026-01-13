package com.example.Backend_java_newbie.util;

import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class SecurityUtil {

    public static Optional<String> getCurrentUserId() {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context==null) {
            return Optional.empty();
        }
        Authentication authentication = context.getAuthentication();
        if (authentication == null){
            return Optional.empty();
        }
        Object pricipal = authentication.getPrincipal();
        if (pricipal == null) {
            return Optional.empty();
        }

        String userId = (String) pricipal;
        return Optional.of(userId);

    }

//    public static String generateToken() {
//        List<CharacterRule> rules = Arrays.asList(
//                new CharacterRule(EnglishCharacterData.LowerCase, 10),
//                new CharacterRule(EnglishCharacterData.UpperCase, 10),
//                new CharacterRule(EnglishCharacterData.Digit, 10)
//        );
//
//        PasswordGenerator generator = new PasswordGenerator();
//
//        return generator.generatePassword(30, rules);
//    }

}
