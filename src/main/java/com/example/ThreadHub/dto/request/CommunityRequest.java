package com.example.ThreadHub.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class CommunityRequest {
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Community name can only contain letters, numbers, and underscores")
    private String name;

    private String imageUrl;

    //getter setter
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
}
