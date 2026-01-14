package com.example.Backend_java_newbie.domain.interfaces.service;

import com.example.Backend_java_newbie.domain.interfaces.repository.UserRepo;
import com.example.Backend_java_newbie.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class PasswordService {
    private final UserRepo repo;
    private final PasswordEncoder passwordEncoder;

    public void setResetToken(User user, String token, Instant expiresAt) {
        user.setSetPasswordToken(token);
        user.setSetPasswordTokenExpiresAt(expiresAt);
        repo.save(user);
    }

    public void updatePassword(User user, String rawPassword) {
        user.setPassword(passwordEncoder.encode(rawPassword));
        repo.save(user);
    }

    public void clearResetToken(User user) {
        user.setSetPasswordToken(null);
        user.setSetPasswordTokenExpiresAt(null);
        repo.save(user);
    }
}
