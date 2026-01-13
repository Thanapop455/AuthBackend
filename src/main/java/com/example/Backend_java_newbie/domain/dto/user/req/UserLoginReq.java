package com.example.Backend_java_newbie.domain.dto.user.req;

import lombok.Data;

@Data
public class UserLoginReq {

    private String email;
    private String password;

}
