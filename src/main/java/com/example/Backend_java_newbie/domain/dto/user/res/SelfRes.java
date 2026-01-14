package com.example.Backend_java_newbie.domain.dto.user.res;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SelfRes {

    private String id;
    private String email;
    private String firstname;
    private String lastname;
    private String phone;
    private boolean verified;

}
