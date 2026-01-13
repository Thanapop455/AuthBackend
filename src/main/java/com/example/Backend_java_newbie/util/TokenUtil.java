//package com.example.Backend_java_newbie.util;
//
//import org.springframework.stereotype.Component;
//
//import java.nio.charset.StandardCharsets;
//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;
//import java.security.SecureRandom;
//import java.util.Base64;
//
//@Component
//public class TokenUtil {
//    private static final SecureRandom random = new SecureRandom();
//
//    public static String generateRawToken() {
//        byte[] bytes = new byte[32];
//        random.nextBytes(bytes);
//        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
//    }
//
//    public static String sha256Hex(String raw) {
//        try {
//            MessageDigest md = MessageDigest.getInstance("SHA-256");
//            byte[] out = md.digest(raw.getBytes(StandardCharsets.UTF_8));
//            StringBuilder sb = new StringBuilder();
//            for (byte b : out) sb.append(String.format("%02x", b));
//            return sb.toString();
//        } catch (NoSuchAlgorithmException e) {
//            throw new IllegalStateException(e);
//        }
//    }
//}
