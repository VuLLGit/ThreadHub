package com.example.ThreadHub.service.impl;

import com.example.ThreadHub.entity.Account;
import com.example.ThreadHub.repository.AccountRepository;
import com.example.ThreadHub.service.AccountService;
import com.example.ThreadHub.util.PasswordHasher;
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
    public void register(Account account) {
        // Save account first
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
    public void save(Account account) {
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
}
