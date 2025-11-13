package com.example.ThreadHub.entity.enums;

public enum CommentStatus {
    ACTIVE("Active"), INACTIVE("Inactive"), REMOVED_BY_MODERATOR("Removed by moderator");

    private final String value;

    CommentStatus(String value) {this.value = value;}

    public String getValue() {return value;}
}
