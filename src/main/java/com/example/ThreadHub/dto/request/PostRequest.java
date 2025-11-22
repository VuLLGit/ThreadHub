package com.example.ThreadHub.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PostRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @NotNull
    private Long communityId;

    // getters and setters
    public @NotBlank String getTitle() {
        return title;
    }

    public void setTitle(@NotBlank String title) {
        this.title = title;
    }

    public @NotBlank String getContent() {
        return content;
    }

    public void setContent(@NotBlank String content) {
        this.content = content;
    }

    public @NotNull Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(@NotNull Long communityId) {
        this.communityId = communityId;
    }
}
