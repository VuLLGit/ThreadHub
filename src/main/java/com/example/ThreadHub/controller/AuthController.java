package com.example.ThreadHub.controller;

import com.example.ThreadHub.dto.request.RegisterRequest;
import com.example.ThreadHub.entity.Account;
import com.example.ThreadHub.entity.enums.AccountRole;
import com.example.ThreadHub.entity.enums.AccountStatus;
import com.example.ThreadHub.service.AccountService;
import com.example.ThreadHub.util.JwtUtil;
import com.example.ThreadHub.util.PasswordHasher;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

        Account account = new Account();
        account.setUsername(registerRequest.getUsername());
        account.setPassword(PasswordHasher.hash(registerRequest.getPassword()));
        account.setEmail(registerRequest.getEmail());
        account.setAccountRole(AccountRole.USER);
        account.setAccountStatus(AccountStatus.ACTIVE);
        account.setEmailVerified(false);

        accountService.register(account);
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
        
        account.setEmailVerified(true);
        account.setEmailVerificationToken(null); // clear token
        accountService.save(account);

        return ResponseEntity.ok("Email verified successfully!");
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<String> resendVerificationEmail(@RequestParam("token") String token) {
        Account account = accountService.findByEmailVerificationToken(token);

        if (account == null) {
            return ResponseEntity.badRequest().body("can't find account");
        }

        accountService.register(account);
        return ResponseEntity.ok("please check your email to verify your account");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam("username") String username, @RequestParam("password") String password) {
        Account account = accountService.login(username, password);
        if (account == null) return ResponseEntity.badRequest().body("Invalid username or password");

        String token = JwtUtil.generateToken(account);
        return ResponseEntity.ok(Map.of("token", token));
    }

}
