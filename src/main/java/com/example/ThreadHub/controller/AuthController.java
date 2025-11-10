package com.example.ThreadHub.controller;

import com.example.ThreadHub.dto.RegisterRequestDTO;
import com.example.ThreadHub.entity.Account;
import com.example.ThreadHub.entity.enums.AccountRole;
import com.example.ThreadHub.entity.enums.AccountStatus;
import com.example.ThreadHub.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AccountService accountService;

    @Autowired
    public AuthController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequestDTO registerRequestDTO) {
        if (!accountService.isUsernameAvailable(registerRequestDTO.getUsername())) {
            return ResponseEntity.badRequest().body("Username is taken");
        }

        if (!accountService.isEmailAvailable(registerRequestDTO.getEmail())) {
            return ResponseEntity.badRequest().body("Email is taken");
        }

        Account account = new Account();
        account.setUsername(registerRequestDTO.getUsername());
        account.setPassword(registerRequestDTO.getPassword());
        account.setEmail(registerRequestDTO.getEmail());
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
}
