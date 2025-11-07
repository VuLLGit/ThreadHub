package com.example.ThreadHub.entity.enums;

public enum AccountStatus {
    ACTIVE("Active"), INACTIVE("Inactive");

    private final String value;

    AccountStatus(String value) {this.value = value;}

    public String getValue() {return value;}
}
