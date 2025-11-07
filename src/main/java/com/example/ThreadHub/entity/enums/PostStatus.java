package com.example.ThreadHub.entity.enums;

public enum PostStatus {
    ACTIVE("Active"), INACTIVE("Inactive");

    private final String value;

    PostStatus(String value) {this.value = value;}

    public String getValue() {return value;}
}
