package com.example.ThreadHub.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class ChangePasswordRequest {
    private String oldPassword;

    @NotBlank
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,50}$",
            message = "Password must be at least 8 and less than 50 characters, including uppercase, lowercase, numbers and no spaces"
    )
    private String newPassword;

    private String repeatNewPassword;

    //Getter and Setter
    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public @NotBlank @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,50}$",
            message = "Password must be at least 8 and less than 50 characters, including uppercase, lowercase, numbers and no spaces"
    ) String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(@NotBlank @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,50}$",
            message = "Password must be at least 8 and less than 50 characters, including uppercase, lowercase, numbers and no spaces"
    ) String newPassword) {
        this.newPassword = newPassword;
    }

    public String getRepeatNewPassword() {
        return repeatNewPassword;
    }

    public void setRepeatNewPassword(String repeatNewPassword) {
        this.repeatNewPassword = repeatNewPassword;
    }
}
