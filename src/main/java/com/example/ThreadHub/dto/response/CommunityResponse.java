package com.example.ThreadHub.dto.response;

import com.example.ThreadHub.entity.enums.CommunityStatus;

public class CommunityResponse {
    private Long id;
    private String name;
    private String imageUrl;
    private CommunityStatus communityStatus;

    // getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public CommunityStatus getCommunityStatus() {
        return communityStatus;
    }

    public void setCommunityStatus(CommunityStatus communityStatus) {
        this.communityStatus = communityStatus;
    }
}
