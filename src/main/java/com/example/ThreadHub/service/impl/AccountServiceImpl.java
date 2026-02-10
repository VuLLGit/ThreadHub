package com.example.ThreadHub.service.impl;

import com.example.ThreadHub.dto.request.RegisterRequest;
import com.example.ThreadHub.entity.Account;
import com.example.ThreadHub.entity.enums.AccountRole;
import com.example.ThreadHub.entity.enums.AccountStatus;
import com.example.ThreadHub.exception.ConflictException;
import com.example.ThreadHub.exception.NotFoundException;
import com.example.ThreadHub.repository.AccountRepository;
import com.example.ThreadHub.service.AccountService;
import com.example.ThreadHub.util.JwtUtil;
import com.example.ThreadHub.util.PasswordHasher;
import com.example.ThreadHub.util.RandomPassword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AccountServiceImpl implements AccountService {
    private final JavaMailSender javaMailSender;
    private final AccountRepository accountRepository;

    @Autowired
    public AccountServiceImpl(JavaMailSender javaMailSender, AccountRepository accountRepository) {
        this.javaMailSender = javaMailSender;
        this.accountRepository = accountRepository;
    }

    private boolean isUsernameAvailable(String username) {
        return accountRepository.findAll().stream()
                .noneMatch(account -> account.getUsername().equals(username));
    }

    private boolean isEmailAvailable(String email) {
        return accountRepository.findAll().stream()
                .noneMatch(account -> account.getEmail().equals(email));
    }

    @Override
    public void register(RegisterRequest registerRequest) {
        if (!isUsernameAvailable(registerRequest.getUsername())) {
            throw new ConflictException("Username is taken");
        }
        if (!isEmailAvailable(registerRequest.getEmail())) {
            throw new ConflictException("Email is taken");
        }
        if (!registerRequest.getPassword().equals(registerRequest.getRepeatPassword())) {
            throw new ConflictException("Password and repeat password must be the same");
        }

        Account account = new Account();
        account.setUsername(registerRequest.getUsername());
        account.setPassword(PasswordHasher.hash(registerRequest.getPassword()));
        account.setEmail(registerRequest.getEmail());
        account.setAccountRole(AccountRole.USER);
        account.setAccountStatus(AccountStatus.ACTIVE);
        account.setEmailVerified(false);

        // Generate token
        String token = UUID.randomUUID().toString();
        account.setEmailVerificationToken(token);
        account.setEmailVerificationTokenSentAt(LocalDateTime.now());

        accountRepository.save(account);

        // Prepare verification link
        String verifyLink = "http://localhost:8080/api/auth/verify?token=" + token;

        // Send email verification
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(account.getEmail());
        message.setSubject("Verify your email");
        message.setText("Click the link to verify your account: " + verifyLink);
        javaMailSender.send(message);
    }

    @Override
    public void resendVerificationEmail(String token) {
        Account account = accountRepository.findByEmailVerificationToken(token);
        if (account == null) {
            throw new NotFoundException("");
        }

        // Generate token
        String newToken = UUID.randomUUID().toString();
        account.setEmailVerificationToken(newToken);
        account.setEmailVerificationTokenSentAt(LocalDateTime.now());

        accountRepository.save(account);

        // Prepare verification link
        String verifyLink = "http://localhost:8080/api/auth/verify?token=" + newToken;

        // Send email verification
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(account.getEmail());
        message.setSubject("Verify your email");
        message.setText("Click the link to verify your account: " + verifyLink);
        javaMailSender.send(message);
    }

    @Override
    public void verifyEmail(String token) {
        Account account = accountRepository.findByEmailVerificationToken(token);
        if (account == null) {
            throw new NotFoundException("Invalid token");
        }

        LocalDateTime sentDate = account.getEmailVerificationTokenSentAt();
        if (sentDate == null || sentDate.isBefore(LocalDateTime.now().minusHours(24))) {
            throw new ConflictException("Verification link expired. Please request a new verification email.");
        }

        account.setEmailVerified(true);
        account.setEmailVerificationToken(null); // clear token
        accountRepository.save(account);
    }

    @Override
    public void changePassword(Account account, String newPassword, String repeatNewPassword) {
        // validate old password
        if (!PasswordHasher.hash(newPassword).equals(account.getPassword())) {
            throw new ConflictException("Old password is incorrect");
        }

        // validate repeat password
        if (!newPassword.equals(repeatNewPassword)) {
            throw new ConflictException("New password and repeat new password must be the same");
        }

        account.setPassword(PasswordHasher.hash(newPassword));
        accountRepository.save(account);
    }

    @Override
    public String login(String username, String password) {
        Account account = accountRepository.findByUsername(username);
        if (account == null || !PasswordHasher.hash(password).equals(account.getPassword())) {
            throw new NotFoundException("Invalid username or password");
        }

        return JwtUtil.generateToken(account);
    }

    @Override
    public Account findByUsername(String username) {
        return accountRepository.findByUsername(username);
    }

    @Override
    public void forgotPassword(String email) {
        Account account = accountRepository.findByEmail(email);
        if (account == null)
            throw new NotFoundException("Email not found");

        String newPassword = RandomPassword.generate();
        account.setPassword(PasswordHasher.hash(newPassword));
        accountRepository.save(account);

        // Send email verification
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Reset your password");
        message.setText("Your password has been change to: " + newPassword + "\n please use this password to login and change it in your profile");
        javaMailSender.send(message);
    }
}
