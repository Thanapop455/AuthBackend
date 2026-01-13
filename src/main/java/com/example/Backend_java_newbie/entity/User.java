package com.example.Backend_java_newbie.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "users")
public class User extends BaseEntity{

    @Column(nullable = false, unique = true, length = 60)
    private String email;

    @Column(nullable = false, length = 60)
    private String password;

    @Column(nullable = false, length = 60)
    private String firstname;

    @Column(nullable = false, length = 60)
    private String lastname;

    @Column(nullable = false, unique = true, length = 20)
    private String phone;

    @Column(nullable = false)
    private boolean verified = false;

    @Column(length = 64)
    private String verifyTokenHash;

    private Instant verifyTokenExpiresAt;
}
