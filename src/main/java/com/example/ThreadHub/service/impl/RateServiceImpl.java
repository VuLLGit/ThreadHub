package com.example.ThreadHub.service.impl;

import com.example.ThreadHub.dto.response.RateResponse;
import com.example.ThreadHub.entity.Account;
import com.example.ThreadHub.entity.Comment;
import com.example.ThreadHub.entity.Post;
import com.example.ThreadHub.entity.Rate;
import com.example.ThreadHub.exception.BadRequestException;
import com.example.ThreadHub.exception.ConflictException;
import com.example.ThreadHub.exception.ForbiddenException;
import com.example.ThreadHub.exception.NotFoundException;
import com.example.ThreadHub.repository.AccountRepository;
import com.example.ThreadHub.repository.CommentRepository;
import com.example.ThreadHub.repository.PostRepository;
import com.example.ThreadHub.repository.RateRepository;
import com.example.ThreadHub.service.RateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RateServiceImpl implements RateService {
    private static final String MSG_POST_NOT_FOUND = "post not found";
    private static final String MSG_RATE_NOT_FOUND = "rate not found";
    private static final String MSG_COMMENT_NOT_FOUND = "comment not found";
    private static final String MSG_FORBIDDEN = "account not found";
    private static final String MSG_CONFLICT = "already exists";
    private static final String MSG_BAD_REQUEST = "bad request";

    private final RateRepository rateRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public RateServiceImpl(RateRepository rateRepository, PostRepository postRepository,  CommentRepository commentRepository) {
        this.rateRepository = rateRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }

    private RateResponse mapToDto(Rate rate) {
        RateResponse rateResponse = new RateResponse();
        rateResponse.setId(rate.getId());
        rateResponse.setPositive(rate.getPositive());
        rateResponse.setAccountId(rate.getAccount().getId());
        rateResponse.setPostId(rate.getPost().getId());
        rateResponse.setCommentId(rate.getComment().getId());
        return rateResponse;
    }

    @Override
    public RateResponse CreatePositivePostRate(Long postId, Account account) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            throw new NotFoundException(MSG_POST_NOT_FOUND);
        }
        if (rateRepository.findByAccount_IdAndPost_Id(account.getId(), postId) != null) {
            throw new ConflictException(MSG_CONFLICT);
        }

        Rate rate = new Rate();
        rate.setAccount(account);
        rate.setPost(post);
        rate.setPositive(true);
        rateRepository.save(rate);
        return mapToDto(rate);
    }

    @Override
    public RateResponse CreateNegativePostRate(Long postId, Account account) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            throw new NotFoundException(MSG_POST_NOT_FOUND);
        }
        if (rateRepository.findByAccount_IdAndPost_Id(account.getId(), postId) != null) {
            throw new ConflictException(MSG_CONFLICT);
        }

        Rate rate = new Rate();
        rate.setAccount(account);
        rate.setPost(post);
        rate.setPositive(false);
        rateRepository.save(rate);
        return mapToDto(rate);
    }

    @Override
    public RateResponse CreatePositiveCommentRate(Long postId, Long commentId, Account account) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            throw new NotFoundException(MSG_POST_NOT_FOUND);
        }
        Comment comment = commentRepository.findById(commentId).orElse(null);
        if (comment == null) {
            throw new NotFoundException(MSG_COMMENT_NOT_FOUND);
        }
        if (rateRepository.findByAccount_IdAndComment_Id(account.getId(), commentId) != null) {
            throw new ConflictException(MSG_CONFLICT);
        }

        Rate rate = new Rate();
        rate.setAccount(account);
        rate.setPost(post);
        rate.setComment(comment);
        rate.setPositive(true);
        rateRepository.save(rate);
        return mapToDto(rate);
    }

    @Override
    public RateResponse CreateNegativeCommentRate(Long postId, Long commentId, Account account) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            throw new NotFoundException(MSG_POST_NOT_FOUND);
        }
        Comment comment = commentRepository.findById(commentId).orElse(null);
        if (comment == null) {
            throw new NotFoundException(MSG_COMMENT_NOT_FOUND);
        }
        if (rateRepository.findByAccount_IdAndComment_Id(account.getId(), commentId) != null) {
            throw new ConflictException(MSG_CONFLICT);
        }

        Rate rate = new Rate();
        rate.setAccount(account);
        rate.setPost(post);
        rate.setComment(comment);
        rate.setPositive(false);
        rateRepository.save(rate);
        return mapToDto(rate);
    }

    @Override
    public void deletePostRate(Long postId, Long rateId, Account account) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            throw new NotFoundException(MSG_POST_NOT_FOUND);
        }
        Rate rate = rateRepository.findById(rateId).orElse(null);
        if (rate == null) {
            throw new NotFoundException(MSG_RATE_NOT_FOUND);
        }
        if (!rate.getAccount().getId().equals(account.getId())) {
            throw new ForbiddenException(MSG_FORBIDDEN);
        }
        if (!rate.getPost().getId().equals(postId)) {
            throw new ConflictException(MSG_BAD_REQUEST);
        }

        rateRepository.deleteById(rateId);
    }

    @Override
    public void deleteCommentRate(Long postId, Long commentId, Long rateId, Account account) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            throw new NotFoundException(MSG_POST_NOT_FOUND);
        }
        Comment comment = commentRepository.findById(commentId).orElse(null);
        if (comment == null) {
            throw new NotFoundException(MSG_COMMENT_NOT_FOUND);
        }
        Rate rate = rateRepository.findById(rateId).orElse(null);
        if (rate == null) {
            throw new NotFoundException(MSG_RATE_NOT_FOUND);
        }
        if (!rate.getAccount().getId().equals(account.getId())) {
            throw new ForbiddenException(MSG_FORBIDDEN);
        }
        if (!rate.getComment().getId().equals(comment.getId()) || !rate.getPost().getId().equals(post.getId())) {
            throw new BadRequestException(MSG_BAD_REQUEST);
        }

        rateRepository.deleteById(rateId);
    }
}
