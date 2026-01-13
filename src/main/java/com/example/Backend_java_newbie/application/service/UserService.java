package com.example.Backend_java_newbie.application.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.Backend_java_newbie.domain.dto.user.req.UserLoginReq;
import com.example.Backend_java_newbie.domain.dto.user.req.UserRegisterReq;
import com.example.Backend_java_newbie.domain.dto.user.res.TokenRes;
import com.example.Backend_java_newbie.domain.dto.user.res.UserRegisterRes;
import com.example.Backend_java_newbie.domain.interfaces.service.IUserService;
import com.example.Backend_java_newbie.domain.interfaces.service.TokenService;
import com.example.Backend_java_newbie.entity.User;
import com.example.Backend_java_newbie.exception.BaseException;
import com.example.Backend_java_newbie.exception.UserException;
import com.example.Backend_java_newbie.mapper.UserMapper;
import com.example.Backend_java_newbie.util.SecurityUtil;
//import com.example.Backend_java_newbie.util.TokenUtil;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class UserService {

    private final TokenService tokenService;

    private final IUserService userService;

    private final UserMapper userMapper;

//    private final EmailService emailService;

//    public UserService(TokenService tokenService, IUserService userService, UserMapper userMapper, EmailService emailService) {
public UserService(TokenService tokenService, IUserService userService, UserMapper userMapper) {
        this.tokenService = tokenService;
        this.userService = userService;
        this.userMapper = userMapper;
//        this.emailService = emailService;
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

    public TokenRes refreshToken() throws BaseException {
        Optional<String> currentUserId = SecurityUtil.getCurrentUserId();
        if (currentUserId.isEmpty()) throw UserException.unauthorized();

        User user = userService.findById(currentUserId.get())
                .orElseThrow(UserException::notFound);

        String token = tokenService.tokenize(user);
        DecodedJWT decoded = tokenService.verify(token);
        long expiresIn = tokenService.getExpiresInSeconds(decoded);

        return TokenRes.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(expiresIn)
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
