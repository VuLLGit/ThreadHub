package com.example.ThreadHub.service;

import com.example.ThreadHub.dto.request.RegisterRequest;
import com.example.ThreadHub.entity.Account;

public interface AccountService {

    boolean isUsernameAvailable(String username);

    boolean isEmailAvailable(String email);

    void register(RegisterRequest registerRequest);

    Account findByEmailVerificationToken(String token);

    void resendVerificationEmail(Account account);
    
    void verifyEmail(Account account);

    void changePassword(Account account, String newPassword);

    Account login(String username, String password);

    Account findByUsername(String username);

    void forgotPassword(String email);

    Account findByEmail(String email);
}
