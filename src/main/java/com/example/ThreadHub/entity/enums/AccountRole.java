package com.example.ThreadHub.entity.enums;

public enum AccountRole {
    ADMIN("Admin"), USER("User");

    private final String value;

    AccountRole(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
