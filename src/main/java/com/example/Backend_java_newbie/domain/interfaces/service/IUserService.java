package com.example.Backend_java_newbie.domain.interfaces.service;

import com.example.Backend_java_newbie.domain.interfaces.repository.UserRepo;
import com.example.Backend_java_newbie.entity.User;
import com.example.Backend_java_newbie.exception.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IUserService {

    final UserRepo repo;
    final PasswordEncoder passwordEncoder;

    public Optional<User> findByEmail(String email) {
        return repo.findByEmail(email);
    }
    public Optional<User> findById(String id) {
        return repo.findById(id);
    }

    public boolean matchPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public Optional<User> findByVerifyTokenHash(String hash) {
        return repo.findByVerifyTokenHash(hash);
    }

    public User save(User user) {
        return repo.save(user);
    }

    public User create(String email, String password, String firstname, String lastname, String phone) throws UserException {
        //validate
        if (Objects.isNull(email)) {
            throw UserException.createEmailNull();
        }
        if (Objects.isNull(password)) {
            throw UserException.createPasswordNull();
        }
        if (Objects.isNull(firstname)) {
            throw UserException.createFirstNameNull();
        }
        if (Objects.isNull(lastname)) {
            throw UserException.createLastNameNull();
        }
        if (Objects.isNull(phone)) {
            throw UserException.createPhoneNull();
        }
        if (repo.existsByPhone(phone)) {
            throw UserException.createPhoneDuplicated();
        }

        if (repo.existsByEmail(email)) {
            throw UserException.createEmailDuplicated();
        }


        User entity = new User();
        entity.setEmail(email);
        entity.setPassword(passwordEncoder.encode(password));
        entity.setFirstname(firstname);
        entity.setLastname(lastname);
        entity.setPhone(phone);

        return repo.save(entity);
    }
}
