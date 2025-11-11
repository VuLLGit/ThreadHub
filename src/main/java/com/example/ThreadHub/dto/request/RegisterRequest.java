package com.example.ThreadHub.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class RegisterRequest {
    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,50}$",
            message = "Password must be at least 8 and less than 50 characters, including uppercase, lowercase, numbers and no spaces"
    )
    private String password;

    @NotBlank(message = "Email is required")
    @Pattern(
            regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
            message = "Email is invalid"
    )
    private String email;

    //Getters and Setters

    public @NotBlank(message = "Username is required") String getUsername() {
        return username;
    }

    public void setUsername(@NotBlank(message = "Username is required") String username) {
        this.username = username;
    }

    public @NotBlank @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,50}$",
            message = "Password must be at least 8 and less than 50 characters, including uppercase, lowercase, numbers and no spaces"
    ) String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,50}$",
            message = "Password must be at least 8 and less than 50 characters, including uppercase, lowercase, numbers and no spaces"
    ) String password) {
        this.password = password;
    }

    public @NotBlank(message = "Email is required") @Pattern(
            regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
            message = "Email is invalid"
    ) String getEmail() {
        return email;
    }

    public void setEmail(@NotBlank(message = "Email is required") @Pattern(
            regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
            message = "Email is invalid"
    ) String email) {
        this.email = email;
    }
}
