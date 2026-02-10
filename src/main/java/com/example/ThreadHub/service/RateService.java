package com.example.ThreadHub.service;

import com.example.ThreadHub.dto.response.RateResponse;
import com.example.ThreadHub.entity.Account;

public interface RateService {
    RateResponse CreatePositiveRate(Long postId, Account account);
    RateResponse CreateNegativeRate(Long postId, Account account);
    void deleteRate(Long postId, Account account);
}
