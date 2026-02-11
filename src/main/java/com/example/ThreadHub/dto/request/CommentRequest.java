package com.example.ThreadHub.dto.request;

import com.example.ThreadHub.entity.enums.CommentStatus;

public class CommentRequest {
    private String content;
    private CommentStatus commentStatus;

    //getter setter
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
}
