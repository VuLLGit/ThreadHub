package com.example.ThreadHub.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class ForgotPasswordRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Email invalid")
    private String email;

    public @NotBlank(message = "Email is required") @Email(message = "Email invalid") String getEmail() {
        return email;
    }

    public void setEmail(@NotBlank(message = "Email is required") @Email(message = "Email invalid") String email) {
        this.email = email;
    }
}
