package com.example.ThreadHub.service.impl;

import com.example.ThreadHub.dto.response.RateResponse;
import com.example.ThreadHub.entity.Account;
import com.example.ThreadHub.entity.Post;
import com.example.ThreadHub.entity.Rate;
import com.example.ThreadHub.exception.ConflictException;
import com.example.ThreadHub.exception.ForbiddenException;
import com.example.ThreadHub.exception.NotFoundException;
import com.example.ThreadHub.repository.PostRepository;
import com.example.ThreadHub.repository.RateRepository;
import com.example.ThreadHub.service.RateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RateServiceImpl implements RateService {
    private static final String MSG_POST_NOT_FOUND = "post not found";
    private static final String MSG_RATE_NOT_FOUND = "rate not found";
    private static final String MSG_FORBIDDEN = "account not found";
    private static final String MSH_CONFLICT = "already exists";

    private final RateRepository rateRepository;
    private final PostRepository postRepository;

    @Autowired
    public RateServiceImpl(RateRepository rateRepository, PostRepository postRepository) {
        this.rateRepository = rateRepository;
        this.postRepository = postRepository;
    }

    private RateResponse mapToDto(Rate rate) {
        RateResponse rateResponse = new RateResponse();
        rateResponse.setId(rate.getId());
        rateResponse.setPositive(rate.getPositive());
        rateResponse.setAccountId(rate.getAccount().getId());
        if(rate.getPost() != null){
            rateResponse.setPostId(rate.getPost().getId());
        }else {
            rateResponse.setCommentId(rate.getComment().getId());
        }
        return rateResponse;
    }

    @Override
    public RateResponse CreatePositiveRate(Long postId, Account account) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            throw new NotFoundException(MSG_POST_NOT_FOUND);
        }
        if (rateRepository.findByAccountAndPostId(account, postId) != null) {
            throw new ConflictException(MSH_CONFLICT);
        }

        Rate rate = new Rate();
        rate.setAccount(account);
        rate.setPost(post);
        rate.setPositive(true);
        rateRepository.save(rate);
        return mapToDto(rate);
    }

    @Override
    public RateResponse CreateNegativeRate(Long postId, Account account) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            throw new NotFoundException(MSG_POST_NOT_FOUND);
        }
        if (rateRepository.findByAccountAndPostId(account, postId) != null) {
            throw new ConflictException(MSH_CONFLICT);
        }

        Rate rate = new Rate();
        rate.setAccount(account);
        rate.setPost(post);
        rate.setPositive(false);
        rateRepository.save(rate);
        return mapToDto(rate);
    }

    @Override
    public void deleteRate(Long postId, Account account) {
        Rate rate = rateRepository.findByAccountAndPostId(account, postId);
        if (rate == null) {
            throw new NotFoundException(MSG_RATE_NOT_FOUND);
        }
        if (!rate.getAccount().getId().equals(account.getId())) {
            throw new ForbiddenException(MSG_FORBIDDEN);
        }

        rateRepository.deleteById(rate.getId());
    }
}
