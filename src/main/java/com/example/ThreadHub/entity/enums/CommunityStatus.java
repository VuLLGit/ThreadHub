package com.example.ThreadHub.entity.enums;

public enum CommunityStatus {
    ACTIVE("Active"), INACTIVE("Inactive"), REMOVED_BY_ADMIN("Removed by ADMIN");

    private final String value;

    CommunityStatus(String value) {this.value = value;}

    public String getValue() {return value;}
}
