package com.example.ThreadHub.controller;

import com.example.ThreadHub.dto.request.CommentRequest;
import com.example.ThreadHub.entity.Account;
import com.example.ThreadHub.entity.Comment;
import com.example.ThreadHub.exception.UnauthorizedException;
import com.example.ThreadHub.repository.CommentRepository;
import com.example.ThreadHub.service.CommentService;
import org.eclipse.angus.mail.iap.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts/{postId}")
public class CommentController {
    private static final String MSG_AUTH_NULL = "Authentication object is null";
    private static final String MSG_COMMENT_DELETED = "comment deleted";

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/comments")
    public ResponseEntity<?> getAllCommentsByPost(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.findAllCommentsByPost(postId));
    }

    @PostMapping("/comments/create")
    public ResponseEntity<?> createComment(@RequestBody CommentRequest commentRequest,
                                           @PathVariable Long postId,
                                           Authentication authentication) {
        if (authentication == null) {
            throw new UnauthorizedException(MSG_AUTH_NULL);
        }
        Account account = (Account) authentication.getPrincipal();

        return ResponseEntity.ok(commentService.createComment(commentRequest, postId, account));
    }

    @PatchMapping("/comments/{commentId}/update")
    public ResponseEntity<?> updateComment(@RequestBody CommentRequest commentRequest,
                                           @PathVariable Long postId,
                                           @PathVariable Long commentId,
                                           Authentication authentication) {
        if (authentication == null) {
            throw new UnauthorizedException(MSG_AUTH_NULL);
        }
        Account account = (Account) authentication.getPrincipal();

        return ResponseEntity.ok(commentService.updateComment(commentRequest, postId, commentId, account));
    }

    @DeleteMapping("/comments/{commentId}/delete")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId,
                                           @PathVariable Long postId,
                                           Authentication authentication) {
        if (authentication == null) {
            throw new UnauthorizedException(MSG_AUTH_NULL);
        }
        Account account = (Account) authentication.getPrincipal();

        commentService.deleteComment(postId, commentId, account);
        return ResponseEntity.ok(MSG_COMMENT_DELETED);
    }
}
