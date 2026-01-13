package com.example.Backend_java_newbie.domain.interfaces.repository;

import com.example.Backend_java_newbie.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepo extends CrudRepository<User, String> {

    Optional<User> findByEmail(String email);

    Optional<User> findById(String id);

    Optional<User> findByVerifyTokenHash(String verifyTokenHash);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

}
