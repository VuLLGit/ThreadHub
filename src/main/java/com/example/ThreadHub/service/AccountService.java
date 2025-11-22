package com.example.ThreadHub.service;

import com.example.ThreadHub.dto.request.RegisterRequest;
import com.example.ThreadHub.entity.Account;

public interface AccountService {

    void register(RegisterRequest registerRequest);

    void resendVerificationEmail(String token);
    
    void verifyEmail(String token);

    void changePassword(Account account, String newPassword, String repeatNewPassword);

    String login(String username, String password);

    Account findByUsername(String username);

    void forgotPassword(String email);
}
