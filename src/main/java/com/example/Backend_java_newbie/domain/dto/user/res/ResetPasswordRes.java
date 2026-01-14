package com.example.Backend_java_newbie.domain.dto.user.res;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResetPasswordRes {

    private String email;
    private String setPasswordToken;
    private long expiresInSeconds;

}
