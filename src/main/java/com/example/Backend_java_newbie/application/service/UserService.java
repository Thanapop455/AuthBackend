package com.example.Backend_java_newbie.application.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.Backend_java_newbie.domain.dto.user.req.UserLoginReq;
import com.example.Backend_java_newbie.domain.dto.user.req.UserRegisterReq;
import com.example.Backend_java_newbie.domain.dto.user.res.TokenRes;
import com.example.Backend_java_newbie.domain.dto.user.res.UserRegisterRes;
import com.example.Backend_java_newbie.domain.interfaces.service.IUserService;
import com.example.Backend_java_newbie.domain.interfaces.service.PasswordService;
import com.example.Backend_java_newbie.domain.interfaces.service.TokenService;
import com.example.Backend_java_newbie.entity.User;
import com.example.Backend_java_newbie.exception.BaseException;
import com.example.Backend_java_newbie.exception.UserException;
import com.example.Backend_java_newbie.mapper.UserMapper;
import com.example.Backend_java_newbie.util.SecurityUtil;
import com.example.Backend_java_newbie.domain.dto.user.req.ResetPasswordReq;
import com.example.Backend_java_newbie.domain.dto.user.req.SetPasswordReq;
import com.example.Backend_java_newbie.domain.dto.user.res.ResetPasswordRes;
import com.example.Backend_java_newbie.domain.dto.user.res.SelfRes;
//import com.example.Backend_java_newbie.util.TokenUtil;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final TokenService tokenService;

    private final IUserService userService;

    private final UserMapper userMapper;

    private final PasswordService passwordService;

//    private final EmailService emailService;

//    public UserService(TokenService tokenService, IUserService userService, UserMapper userMapper, EmailService emailService) {
public UserService(TokenService tokenService, IUserService userService, UserMapper userMapper, PasswordService passwordService) {
        this.tokenService = tokenService;
        this.userService = userService;
        this.userMapper = userMapper;
//        this.emailService = emailService;
        this.passwordService = passwordService;
}

    public TokenRes login(UserLoginReq request) throws BaseException {
        Optional<User> opt = userService.findByEmail(request.getEmail());
        if (opt.isEmpty()) {
            throw UserException.loginEmailNotFound();
        }
        User user = opt.get();

        if (!userService.matchPassword(request.getPassword(), user.getPassword())) {
            throw UserException.loginFailPasswordIncorrect();
        }

//        if (!user.isVerified()) {
//            throw new UserException("not.verified");
//        }

        String token = tokenService.tokenize(user);
        DecodedJWT decoded = tokenService.verify(token);
        long expiresIn = tokenService.getExpiresInSeconds(decoded);

        return TokenRes.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(expiresIn)
                .build();
    }


    public UserRegisterRes register(UserRegisterReq request) throws BaseException {
        User user = userService.create(request.getEmail(), request.getPassword(), request.getFirstname(), request.getLastname(),request.getPhone());

//        String raw = TokenUtil.generateRawToken();
//        String hash = TokenUtil.sha256Hex(raw);

//        user.setVerified(false);
//        user.setVerifyTokenHash(hash);
//        user.setVerifyTokenExpiresAt(Instant.now().plusSeconds(15 * 60)); // 15 นาที นะจ๊ะ
        userService.save(user);

//        emailService.sendVerifyEmail(user.getEmail(), user.getFirstname(), raw);

        return userMapper.toRegisterResponse(user);
    }

//    public TokenRes refreshToken() throws BaseException {
//        Optional<String> currentUserId = SecurityUtil.getCurrentUserId();
//        if (currentUserId.isEmpty()) throw UserException.unauthorized();
//
//        User user = userService.findById(currentUserId.get())
//                .orElseThrow(UserException::notFound);
//
//        String token = tokenService.tokenize(user);
//        DecodedJWT decoded = tokenService.verify(token);
//        long expiresIn = tokenService.getExpiresInSeconds(decoded);
//
//        return TokenRes.builder()
//                .accessToken(token)
//                .tokenType("Bearer")
//                .expiresIn(expiresIn)
//                .build();
//    }

    public ResetPasswordRes resetPassword(ResetPasswordReq req) throws BaseException {
        if (req == null || req.getEmail() == null) throw UserException.emailNull();

        User user = userService.findByEmail(req.getEmail())
                .orElseThrow(UserException::notFound);

        // สร้าง token + expire 15 นาที
        String token = UUID.randomUUID().toString();
        Instant exp = Instant.now().plusSeconds(15 * 60);

        passwordService.setResetToken(user, token, exp);

        long expiresIn = exp.getEpochSecond() - Instant.now().getEpochSecond();
        return ResetPasswordRes.builder()
                .email(user.getEmail())
                .setPasswordToken(token)      // ตอนนี้คืน token เพื่อทดสอบก่อน
                .expiresInSeconds(Math.max(expiresIn, 0))
                .build();
    }

    public void setPassword(SetPasswordReq req) throws BaseException {
        if (req == null)
            throw UserException.requestNull();
        if (req.getEmail() == null)
            throw UserException.createEmailNull();
        if (req.getPassword() == null)
            throw UserException.createPasswordNull();
        if (req.getSetPasswordToken() == null) {
            throw UserException.setPasswordTokenNull();
        }
        User user = userService.findByEmail(req.getEmail())
                .orElseThrow(UserException::notFound);
        // ตรวจ token
        if (user.getSetPasswordToken() == null || !user.getSetPasswordToken().equals(req.getSetPasswordToken())) {
            throw UserException.setPasswordTokenInvalid();
        }
        // ตรวจหมดอายุ
        if (user.getSetPasswordTokenExpiresAt() == null || user.getSetPasswordTokenExpiresAt().isBefore(Instant.now())) {
            throw UserException.setPasswordTokenExpired();
        }
        // ตั้งรหัสผ่านใหม่ + ล้าง token
        passwordService.updatePassword(user, req.getPassword());
        passwordService.clearResetToken(user);
    }
// ...

    public SelfRes self() throws BaseException {
        String userId = SecurityUtil.getCurrentUserId()
                .orElseThrow(UserException::unauthorized);

        User user = userService.findById(userId)
                .orElseThrow(UserException::notFound);

        return SelfRes.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .phone(user.getPhone())
                .verified(user.isVerified())
                .build();
    }



//    public void verifyEmail(String rawToken) throws BaseException {
//        String hash = TokenUtil.sha256Hex(rawToken);
//
//        User user = userService.findByVerifyTokenHash(hash)
//                .orElseThrow(() -> new UserException("verify.token.invalid"));
//
//        if (user.isVerified()) return;
//
//        if (user.getVerifyTokenExpiresAt() == null ||
//                user.getVerifyTokenExpiresAt().isBefore(Instant.now())) {
//            throw new UserException("verify.token.expired");
//        }
//
//        user.setVerified(true);
//        user.setVerifyTokenHash(null);
//        user.setVerifyTokenExpiresAt(null);
//
//        userService.save(user);
//    }
}
