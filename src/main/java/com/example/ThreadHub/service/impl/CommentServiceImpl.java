package com.example.ThreadHub.service.impl;

import com.example.ThreadHub.dto.request.CommentRequest;
import com.example.ThreadHub.dto.response.CommentResponse;
import com.example.ThreadHub.entity.Account;
import com.example.ThreadHub.entity.Comment;
import com.example.ThreadHub.entity.Post;
import com.example.ThreadHub.entity.enums.CommentStatus;
import com.example.ThreadHub.exception.ForbiddenException;
import com.example.ThreadHub.exception.NotFoundException;
import com.example.ThreadHub.repository.CommentRepository;
import com.example.ThreadHub.repository.PostRepository;
import com.example.ThreadHub.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    private static final String MSG_POST_NOT_FOUND="post not found";
    private static final String MSG_COMMENT_NOT_FOUND="comment not found";
    private static final String MSG_FORBIDDEN="not have permission";

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    private CommentResponse mapToDto(Comment comment) {
        CommentResponse commentResponse = new CommentResponse();
        commentResponse.setId(comment.getId());
        commentResponse.setContent(comment.getContent());
        commentResponse.setCommentStatus(comment.getCommentStatus());
        commentResponse.setUsername(comment.getAccount().getUsername());
        return commentResponse;
    }

    @Override
    public List<CommentResponse> findAllCommentsByPost(Long postId) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            throw new NotFoundException(MSG_POST_NOT_FOUND);
        }
        List<CommentResponse> commentResponseList = new ArrayList<>();
        List<Comment> commentList = commentRepository.findAllByPost_Id(postId);
        System.out.println(commentList);
        for (Comment comment : commentList) {
            CommentResponse commentResponse = mapToDto(comment);
            commentResponseList.add(commentResponse);
        }
        return commentResponseList;
    }

    @Override
    public CommentResponse createComment(CommentRequest commentRequest, Long postId, Account account) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            throw new NotFoundException(MSG_POST_NOT_FOUND);
        }
        Comment comment = new Comment();
        comment.setContent(commentRequest.getContent());
        comment.setAccount(account);
        comment.setPost(post);
        comment.setCommentStatus(CommentStatus.ACTIVE);
        commentRepository.save(comment);
        return mapToDto(comment);
    }

    @Override
    public CommentResponse updateComment(CommentRequest commentRequest, Long postId, Long commentId, Account account) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            throw new NotFoundException(MSG_POST_NOT_FOUND);
        }
        Comment comment = commentRepository.findById(commentId).orElse(null);
        if (comment == null) {
            throw new NotFoundException(MSG_COMMENT_NOT_FOUND);
        }
        if (!comment.getAccount().getId().equals(account.getId())) {
            throw new ForbiddenException(MSG_FORBIDDEN);
        }
        comment.setContent(commentRequest.getContent());
        comment.setCommentStatus(commentRequest.getCommentStatus());
        commentRepository.save(comment);
        return mapToDto(comment);
    }

    @Override
    public void deleteComment(Long postId, Long commentId, Account account) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            throw new NotFoundException(MSG_POST_NOT_FOUND);
        }
        Comment comment = commentRepository.findById(commentId).orElse(null);
        if (comment == null) {
            throw new NotFoundException(MSG_COMMENT_NOT_FOUND);
        }
        if (!comment.getAccount().getId().equals(account.getId())) {
            throw new ForbiddenException(MSG_FORBIDDEN);
        }
        commentRepository.delete(comment);
    }
}
