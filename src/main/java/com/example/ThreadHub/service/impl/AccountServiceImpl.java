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

    /* ===================== CONSTANT MESSAGES ===================== */
    private static final String MSG_USERNAME_TAKEN = "Username is taken";
    private static final String MSG_EMAIL_TAKEN = "Email is taken";
    private static final String MSG_INVALID_TOKEN = "Invalid token";
    private static final String MSG_VERIFICATION_LINK_EXPIRED = "Verification link expired. Please request a new verification email.";
    private static final String MSG_OLD_PASSWORD_INCORRECT = "Old password is incorrect";
    private static final String MSG_NEW_PASSWORD_MISMATCH = "New password and repeat new password must be the same";
    private static final String MSG_INVALID_LOGIN = "Invalid username or password";
    private static final String MSG_EMAIL_NOT_FOUND = "Email not found";
    private static final String MAIL_SUBJECT_VERIFY = "Verify your email";
    private static final String MAIL_SUBJECT_RESET_PASSWORD = "Reset your password";
    private static final String MAIL_VERIFY_TEXT_PREFIX = "Click the link to verify your account: ";
    private static final String MAIL_RESET_PASSWORD_TEXT = "Your password has been change to: %s\nplease use this password to login and change it in your profile";
    private static final String VERIFY_URL_PREFIX = "http://localhost:8080/api/auth/verify?token=";
    /* ============================================================= */

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
            throw new ConflictException(MSG_USERNAME_TAKEN);
        }
        if (!isEmailAvailable(registerRequest.getEmail())) {
            throw new ConflictException(MSG_EMAIL_TAKEN);
        }

        Account account = new Account();
        account.setUsername(registerRequest.getUsername());
        account.setPassword(PasswordHasher.hash(registerRequest.getPassword()));
        account.setEmail(registerRequest.getEmail());
        account.setAccountRole(AccountRole.USER);
        account.setAccountStatus(AccountStatus.ACTIVE);
        account.setEmailVerified(false);

        String token = UUID.randomUUID().toString();
        account.setEmailVerificationToken(token);
        account.setEmailVerificationTokenSentAt(LocalDateTime.now());

        accountRepository.save(account);

        String verifyLink = VERIFY_URL_PREFIX + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(account.getEmail());
        message.setSubject(MAIL_SUBJECT_VERIFY);
        message.setText(MAIL_VERIFY_TEXT_PREFIX + verifyLink);
        javaMailSender.send(message);
    }

    @Override
    public void resendVerificationEmail(String token) {
        Account account = accountRepository.findByEmailVerificationToken(token);
        if (account == null) {
            throw new NotFoundException(MSG_INVALID_TOKEN);
        }

        String newToken = UUID.randomUUID().toString();
        account.setEmailVerificationToken(newToken);
        account.setEmailVerificationTokenSentAt(LocalDateTime.now());

        accountRepository.save(account);

        String verifyLink = VERIFY_URL_PREFIX + newToken;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(account.getEmail());
        message.setSubject(MAIL_SUBJECT_VERIFY);
        message.setText(MAIL_VERIFY_TEXT_PREFIX + verifyLink);
        javaMailSender.send(message);
    }

    @Override
    public void verifyEmail(String token) {
        Account account = accountRepository.findByEmailVerificationToken(token);
        if (account == null) {
            throw new NotFoundException(MSG_INVALID_TOKEN);
        }

        LocalDateTime sentDate = account.getEmailVerificationTokenSentAt();
        if (sentDate == null || sentDate.isBefore(LocalDateTime.now().minusHours(24))) {
            throw new ConflictException(MSG_VERIFICATION_LINK_EXPIRED);
        }

        account.setEmailVerified(true);
        account.setEmailVerificationToken(null);
        accountRepository.save(account);
    }

    @Override
    public void changePassword(Account account, String newPassword, String repeatNewPassword) {
        if (!PasswordHasher.hash(newPassword).equals(account.getPassword())) {
            throw new ConflictException(MSG_OLD_PASSWORD_INCORRECT);
        }

        if (!newPassword.equals(repeatNewPassword)) {
            throw new ConflictException(MSG_NEW_PASSWORD_MISMATCH);
        }

        account.setPassword(PasswordHasher.hash(newPassword));
        accountRepository.save(account);
    }

    @Override
    public String login(String username, String password) {
        Account account = accountRepository.findByUsername(username);
        if (account == null || !PasswordHasher.hash(password).equals(account.getPassword())) {
            throw new NotFoundException(MSG_INVALID_LOGIN);
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
        if (account == null) {
            throw new NotFoundException(MSG_EMAIL_NOT_FOUND);
        }

        String newPassword = RandomPassword.generate();
        account.setPassword(PasswordHasher.hash(newPassword));
        accountRepository.save(account);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(MAIL_SUBJECT_RESET_PASSWORD);
        message.setText(String.format(MAIL_RESET_PASSWORD_TEXT, newPassword));
        javaMailSender.send(message);
    }
}

