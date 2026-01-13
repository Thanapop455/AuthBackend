package com.example.Backend_java_newbie.domain.dto.user.req;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class UserRegisterReq {

    private String email;
    private String password;
    private String firstname;
    private String lastname;
    private String phone;

}
