package com.example.ThreadHub.repository;

import com.example.ThreadHub.entity.Account;
import com.example.ThreadHub.entity.Rate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RateRepository extends JpaRepository<Rate, Long> {
    Rate findByAccountAndPostId(Account account, Long postId);
}
