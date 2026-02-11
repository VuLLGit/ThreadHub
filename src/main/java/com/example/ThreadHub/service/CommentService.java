package com.example.ThreadHub.service;

import com.example.ThreadHub.dto.request.CommentRequest;
import com.example.ThreadHub.dto.response.CommentResponse;
import com.example.ThreadHub.entity.Account;
import com.example.ThreadHub.entity.Comment;

import java.util.List;

public interface CommentService {
    List<CommentResponse> findAllCommentsByPost(Long postId);
    CommentResponse createComment(CommentRequest commentRequest, Long postId, Account account);
    CommentResponse updateComment(CommentRequest commentRequest, Long postId, Long commentId, Account account);
    void deleteComment(Long postId, Long commentId, Account account);
}
