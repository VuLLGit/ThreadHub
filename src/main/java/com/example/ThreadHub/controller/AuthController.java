package com.example.ThreadHub.controller;

import com.example.ThreadHub.dto.request.*;
import com.example.ThreadHub.entity.Account;
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

    private final AccountService accountService;

    @Autowired
    public AuthController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest registerRequest) {
        accountService.register(registerRequest);
        return ResponseEntity.ok("please check your email to verify your account");
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {

        accountService.verifyEmail(token);
        return ResponseEntity.ok("Email verified successfully!");
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<String> resendVerificationEmail(@RequestBody TokenRequest request) {

        accountService.resendVerificationEmail(request.getToken());
        return ResponseEntity.ok("please check your email to verify your account");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        String token = accountService.login(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        accountService.forgotPassword(request.getEmail());
        return ResponseEntity.ok("new password has been sent to your email address");
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest,
                                                 Authentication authentication) {
        // validate authentication
        if (authentication == null) {
            throw new UnauthorizedException("Authentication is null");
        }

        Account account = (Account) authentication.getPrincipal();
        accountService.changePassword(account, changePasswordRequest.getNewPassword(), changePasswordRequest.getRepeatNewPassword());
        return ResponseEntity.ok("password changed successfully");
    }
}
