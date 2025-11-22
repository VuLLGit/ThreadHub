package com.example.ThreadHub.dto.response;

import java.util.List;

public class PostResponse {
    private Long id;
    private String title;
    private String content;
    private String postStatus;
    private String communityName;
    private String communityImageUrl;
    private String accountUsername;
    private String accountAvatarUrl;
    private List<String> mediaUrls;

    // getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPostStatus() {
        return postStatus;
    }

    public void setPostStatus(String postStatus) {
        this.postStatus = postStatus;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public String getCommunityImageUrl() {
        return communityImageUrl;
    }

    public void setCommunityImageUrl(String communityImageUrl) {
        this.communityImageUrl = communityImageUrl;
    }

    public String getAccountUsername() {
        return accountUsername;
    }

    public void setAccountUsername(String accountUsername) {
        this.accountUsername = accountUsername;
    }

    public String getAccountAvatarUrl() {
        return accountAvatarUrl;
    }

    public void setAccountAvatarUrl(String accountAvatarUrl) {
        this.accountAvatarUrl = accountAvatarUrl;
    }

    public List<String> getMediaUrls() {
        return mediaUrls;
    }

    public void setMediaUrls(List<String> mediaUrls) {
        this.mediaUrls = mediaUrls;
    }
}
