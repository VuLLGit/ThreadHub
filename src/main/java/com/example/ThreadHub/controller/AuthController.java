package com.example.ThreadHub.controller;

import com.example.ThreadHub.dto.request.*;
import com.example.ThreadHub.entity.Account;
import com.example.ThreadHub.exception.ConflictException;
import com.example.ThreadHub.exception.UnauthorizedException;
import com.example.ThreadHub.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    /* ===== MESSAGE CONSTANTS ===== */
    public static final String PASSWORD_NOT_MATCH = "Password and repeat password must be the same";
    private static final String MSG_CHECK_EMAIL_VERIFY = "please check your email to verify your account";
    private static final String MSG_EMAIL_VERIFIED_SUCCESS = "Email verified successfully!";
    private static final String MSG_NEW_PASSWORD_SENT = "new password has been sent to your email address";
    private static final String MSG_PASSWORD_CHANGED_SUCCESS = "password changed successfully";
    private static final String MSG_AUTH_NULL = "Authentication is null";

    private final AccountService accountService;

    @Autowired
    public AuthController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest registerRequest) {
        if (!registerRequest.getPassword().equals(registerRequest.getRepeatPassword())) {
            throw new ConflictException(PASSWORD_NOT_MATCH);
        }
        accountService.register(registerRequest);
        return ResponseEntity.ok(MSG_CHECK_EMAIL_VERIFY);
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {

        accountService.verifyEmail(token);
        return ResponseEntity.ok(MSG_EMAIL_VERIFIED_SUCCESS);
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<String> resendVerificationEmail(@RequestBody TokenRequest request) {

        accountService.resendVerificationEmail(request.getToken());
        return ResponseEntity.ok(MSG_CHECK_EMAIL_VERIFY);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        String token = accountService.login(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        accountService.forgotPassword(request.getEmail());
        return ResponseEntity.ok(MSG_NEW_PASSWORD_SENT);
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest,
                                                 Authentication authentication) {
        if (authentication == null) {
            throw new UnauthorizedException(MSG_AUTH_NULL);
        }

        Account account = (Account) authentication.getPrincipal();
        accountService.changePassword(
                account,
                changePasswordRequest.getNewPassword(),
                changePasswordRequest.getRepeatNewPassword()
        );

        return ResponseEntity.ok(MSG_PASSWORD_CHANGED_SUCCESS);
    }
}

