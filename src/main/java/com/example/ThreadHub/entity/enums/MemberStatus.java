package com.example.ThreadHub.entity.enums;

public enum MemberStatus {
    ACTIVE("Active"), BANNED("Banned");

    private final String value;

    MemberStatus(String value) {this.value = value;}

    public String getValue() {return value;}
}
