package com.example.ThreadHub.entity.enums;

public enum CommunityStatus {
    ACTIVE("Active"), INACTIVE("Inactive");

    private final String value;

    CommunityStatus(String value) {this.value = value;}

    public String getValue() {return value;}
}
