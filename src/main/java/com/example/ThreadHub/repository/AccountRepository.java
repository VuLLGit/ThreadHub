package com.example.ThreadHub.repository;

import com.example.ThreadHub.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByEmailVerificationToken(String token);
    Account findByUsername(String username);
}
