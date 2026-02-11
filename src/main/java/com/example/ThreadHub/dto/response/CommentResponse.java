package com.example.ThreadHub.dto.response;

import com.example.ThreadHub.entity.Account;
import com.example.ThreadHub.entity.enums.CommentStatus;

public class CommentResponse {
    private Long id;
    private String content;
    private CommentStatus commentStatus;
    private String username;

    //getter setter

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public CommentStatus getCommentStatus() {
        return commentStatus;
    }

    public void setCommentStatus(CommentStatus commentStatus) {
        this.commentStatus = commentStatus;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
