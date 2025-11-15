package com.example.ThreadHub.service.impl;

import com.example.ThreadHub.dto.request.RegisterRequest;
import com.example.ThreadHub.entity.Account;
import com.example.ThreadHub.entity.enums.AccountRole;
import com.example.ThreadHub.entity.enums.AccountStatus;
import com.example.ThreadHub.repository.AccountRepository;
import com.example.ThreadHub.service.AccountService;
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

    @Override
    public boolean isUsernameAvailable(String username) {
        return accountRepository.findAll().stream()
                .noneMatch(account -> account.getUsername().equals(username));
    }

    @Override
    public boolean isEmailAvailable(String email) {
        return accountRepository.findAll().stream()
                .noneMatch(account -> account.getEmail().equals(email));
    }

    @Override
    public void register(RegisterRequest registerRequest) {
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
    public Account findByEmailVerificationToken(String token) {
        return accountRepository.findByEmailVerificationToken(token);
    }

    @Override
    public void resendVerificationEmail(Account account) {
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
    public void verifyEmail(Account account) {
        account.setEmailVerified(true);
        account.setEmailVerificationToken(null); // clear token
        accountRepository.save(account);
    }

    @Override
    public void changePassword(Account account, String newPassword) {
        account.setPassword(PasswordHasher.hash(newPassword));
        accountRepository.save(account);
    }

    @Override
    public Account login(String username, String password) {
        Account account = accountRepository.findByUsername(username);
        if (account == null || !PasswordHasher.hash(password).equals(account.getPassword())) {
            return null;
        }
        return account;
    }

    @Override
    public Account findByUsername(String username) {
        return accountRepository.findByUsername(username);
    }

    @Override
    public void forgotPassword(String email) {
        Account account = accountRepository.findByEmail(email);

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

    @Override
    public Account findByEmail(String email) {
        return accountRepository.findByEmail(email);
    }
}
