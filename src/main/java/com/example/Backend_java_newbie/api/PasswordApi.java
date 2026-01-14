package com.example.Backend_java_newbie.api;
import com.example.Backend_java_newbie.application.service.UserService;
import com.example.Backend_java_newbie.domain.dto.user.req.ResetPasswordReq;
import com.example.Backend_java_newbie.domain.dto.user.req.SetPasswordReq;
import com.example.Backend_java_newbie.domain.dto.user.res.ResetPasswordRes;
import com.example.Backend_java_newbie.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/password")
@RequiredArgsConstructor
public class PasswordApi {

    private final UserService userService;

    @PostMapping("/reset")
    public ResponseEntity<ResetPasswordRes> resetPassword(@RequestBody ResetPasswordReq req) throws BaseException {
        return ResponseEntity.ok(userService.resetPassword(req));
    }

    @PostMapping("/set")
    public ResponseEntity<?> setPassword(@RequestBody SetPasswordReq req) throws BaseException {
        userService.setPassword(req);
        return ResponseEntity.ok("set password success");
    }

}
