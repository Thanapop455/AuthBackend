package com.example.Backend_java_newbie.api;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.Backend_java_newbie.application.service.UserService;
import com.example.Backend_java_newbie.domain.dto.user.req.UserLoginReq;
import com.example.Backend_java_newbie.domain.dto.user.req.UserRegisterReq;
import com.example.Backend_java_newbie.domain.dto.user.res.SelfRes;
import com.example.Backend_java_newbie.domain.dto.user.res.TokenRes;
import com.example.Backend_java_newbie.domain.dto.user.res.UserRegisterRes;
import com.example.Backend_java_newbie.domain.interfaces.service.TokenService;
import com.example.Backend_java_newbie.application.service.TokenBlacklistService;
import com.example.Backend_java_newbie.exception.BaseException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;


@RestController
@RequiredArgsConstructor
public class UserApi {

    private final UserService userService;
    private final TokenService tokenService;
    private final TokenBlacklistService blacklistService;

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

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {

        String token = extractTokenFromCookie(request); // ใช้ helper แบบเดียวกับ TokenFilter
        if (!ObjectUtils.isEmpty(token)) {
            DecodedJWT decoded = tokenService.verify(token);
            if (decoded != null) {
                String jti = decoded.getId();
                Instant exp = decoded.getExpiresAt().toInstant();
                if (!ObjectUtils.isEmpty(jti)) {
                    blacklistService.blacklist(jti, exp);
                }
            }
        }

        ResponseCookie cookie = ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .secure(false) // dev http อาจต้องทำเป็น config
                .path("/")
                .sameSite("Lax")
                .maxAge(0)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
        return ResponseEntity.ok("logout success");
    }

    private String extractTokenFromCookie(HttpServletRequest request) {
        var cookies = request.getCookies();
        if (cookies == null) return null;

        for (var c : cookies) {
            if ("accessToken".equals(c.getName())) {
                String value = c.getValue();
                return ObjectUtils.isEmpty(value) ? null : value.trim();
            }
        }
        return null;
    }
    @GetMapping("/self")
    public ResponseEntity<SelfRes> self() throws BaseException {
        return ResponseEntity.ok(userService.self());
    }

//    @PostMapping("/refresh-token")
//    public ResponseEntity<TokenRes> refreshToken() throws BaseException {
//        return ResponseEntity.ok(userService.refreshToken());
//    }

//    @GetMapping("/auth/verify")
//    public ResponseEntity<?> verify(@RequestParam String token) throws BaseException {
//        userService.verifyEmail(token);
//        return ResponseEntity.ok().body("verified");
//    }
}
