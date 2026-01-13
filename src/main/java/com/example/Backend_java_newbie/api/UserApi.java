package com.example.Backend_java_newbie.api;

import com.example.Backend_java_newbie.application.service.UserService;
import com.example.Backend_java_newbie.domain.dto.user.req.UserLoginReq;
import com.example.Backend_java_newbie.domain.dto.user.req.UserRegisterReq;
import com.example.Backend_java_newbie.domain.dto.user.res.TokenRes;
import com.example.Backend_java_newbie.domain.dto.user.res.UserRegisterRes;
import com.example.Backend_java_newbie.exception.BaseException;
import com.example.Backend_java_newbie.exception.UserException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
public class UserApi {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginReq request,
                                   HttpServletResponse response) throws BaseException {

        TokenRes tokenRes = userService.login(request);

        ResponseCookie cookie = ResponseCookie.from("accessToken", tokenRes.getAccessToken())
                .httpOnly(true)
                .secure(false) // true เมื่อขึ้น https
                .path("/")
                .sameSite("Lax")
                .maxAge(tokenRes.getExpiresIn())
                .build();

        response.addHeader("Set-Cookie", cookie.toString());

        return ResponseEntity.ok().body("login success");
    }


    @PostMapping("/register")
    public ResponseEntity<UserRegisterRes> register(@RequestBody UserRegisterReq request) throws BaseException {
        UserRegisterRes response = userService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<TokenRes> refreshToken() throws BaseException {
        return ResponseEntity.ok(userService.refreshToken());
    }

//    @GetMapping("/auth/verify")
//    public ResponseEntity<?> verify(@RequestParam String token) throws BaseException {
//        userService.verifyEmail(token);
//        return ResponseEntity.ok().body("verified");
//    }
}
