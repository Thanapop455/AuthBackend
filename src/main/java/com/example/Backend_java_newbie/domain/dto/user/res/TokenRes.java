package com.example.Backend_java_newbie.domain.dto.user.res;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenRes {
    private String accessToken;
    private String tokenType;
    private long expiresIn;
}
