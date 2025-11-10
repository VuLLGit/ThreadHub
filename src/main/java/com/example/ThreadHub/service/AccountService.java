package com.example.ThreadHub.service;

import com.example.ThreadHub.entity.Account;

public interface AccountService {

    boolean isUsernameAvailable(String username);

    boolean isEmailAvailable(String email);

    void register(Account account);

    Account findByEmailVerificationToken(String token);

    void save(Account account);
}
