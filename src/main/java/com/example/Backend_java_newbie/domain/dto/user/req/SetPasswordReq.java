package com.example.Backend_java_newbie.domain.dto.user.req;

import lombok.Data;

@Data
public class SetPasswordReq {

    private String email;
    private String setPasswordToken;
    private String password;

}
