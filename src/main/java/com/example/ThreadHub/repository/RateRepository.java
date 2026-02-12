package com.example.ThreadHub.repository;

import com.example.ThreadHub.entity.Account;
import com.example.ThreadHub.entity.Rate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RateRepository extends JpaRepository<Rate, Long> {
    Rate findByAccount_IdAndPost_Id(Long account, Long postId);

    Rate findByAccount_IdAndComment_Id(Long accountId, Long commentId);
}
