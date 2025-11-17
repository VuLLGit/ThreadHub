package com.example.ThreadHub.dto.request;

import jakarta.validation.constraints.NotBlank;

public class TokenRequest {
    @NotBlank
    private String token;

    public @NotBlank String getToken() {
        return token;
    }

    public void setToken(@NotBlank String token) {
        this.token = token;
    }
}
