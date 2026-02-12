package com.example.ThreadHub.service;

import com.example.ThreadHub.dto.response.RateResponse;
import com.example.ThreadHub.entity.Account;

public interface RateService {
    RateResponse CreatePositivePostRate(Long postId, Account account);
    RateResponse CreateNegativePostRate(Long postId, Account account);
    RateResponse CreatePositiveCommentRate(Long postId, Long commentId, Account account);
    RateResponse CreateNegativeCommentRate(Long postId, Long commentId, Account account);
    void deletePostRate(Long postId, Long rateId, Account account);
    void deleteCommentRate(Long postId, Long commentId, Long rateId, Account account);
}
