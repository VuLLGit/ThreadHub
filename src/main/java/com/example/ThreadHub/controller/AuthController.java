package com.example.ThreadHub.controller;

import com.example.ThreadHub.dto.request.ChangePasswordRequest;
import com.example.ThreadHub.dto.request.RegisterRequest;
import com.example.ThreadHub.entity.Account;
import com.example.ThreadHub.service.AccountService;
import com.example.ThreadHub.util.JwtUtil;
import com.example.ThreadHub.util.PasswordHasher;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AccountService accountService;

    @Autowired
    public AuthController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest registerRequest, BindingResult bindingResult) {

        // validate pattern
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            return ResponseEntity.badRequest().body(errorMessage);
        }

        // validate duplication
        if (!accountService.isUsernameAvailable(registerRequest.getUsername())) {
            return ResponseEntity.badRequest().body("Username is taken");
        }
        if (!accountService.isEmailAvailable(registerRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Email is taken");
        }

        // validate repeat password
        if (!registerRequest.getPassword().equals(registerRequest.getRepeatPassword())) {
            return ResponseEntity.badRequest().body("Password and repeat password must be the same");
        }

        accountService.register(registerRequest);
        return ResponseEntity.ok("please check your email to verify your account");
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        Account account = accountService.findByEmailVerificationToken(token);

        if (account == null) {
            return ResponseEntity.badRequest().body("Invalid token");
        }

        LocalDateTime sentDate = account.getEmailVerificationTokenSentAt();
        LocalDateTime now = LocalDateTime.now();

        if (sentDate == null || sentDate.isBefore(now.minusHours(24))) {
            return ResponseEntity.badRequest().body("Verification link expired. Please request a new verification email.");
        }

        accountService.verifyEmail(account);

        return ResponseEntity.ok("Email verified successfully!");
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<String> resendVerificationEmail(@RequestParam("token") String token) {
        Account account = accountService.findByEmailVerificationToken(token);

        if (account == null) {
            return ResponseEntity.badRequest().body("can't find account");
        }

        accountService.resendVerificationEmail(account);
        return ResponseEntity.ok("please check your email to verify your account");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam("username") String username, @RequestParam("password") String password) {
        Account account = accountService.login(username, password);
        System.out.println(PasswordHasher.hash(password));
        if (account == null) return ResponseEntity.badRequest().body("Invalid username or password");

        String token = JwtUtil.generateToken(account);
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam("email") String email) {
        Account account = accountService.findByEmail(email);
        if (account == null) return ResponseEntity.badRequest().body("can't find account");

        accountService.forgotPassword(email);
        return ResponseEntity.ok("new password has been sent to your email address");
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest,
                                                 BindingResult bindingResult,
                                                 Authentication authentication) {
        // validate authentication
        if (authentication == null) {
            return ResponseEntity.badRequest().body("unauthorized");
        }

        // validate account exist
        Account account = (Account) authentication.getPrincipal();

        // validate old password
        if (!PasswordHasher.hash(changePasswordRequest.getOldPassword()).equals(account.getPassword())) {
            return ResponseEntity.badRequest().body("old password is incorrect");
        }

        // validate new password
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            return ResponseEntity.badRequest().body(errorMessage);
        }

        // validate repeat password
        if (!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getRepeatNewPassword())) {
            return ResponseEntity.badRequest().body("new password and repeat new password must be the same");
        }

        accountService.changePassword(account, changePasswordRequest.getNewPassword());
        return ResponseEntity.ok("password changed successfully");
    }
}
