package com.example.ThreadHub.entity.enums;

public enum MemberRole {
    MODERATOR("Moderator"), MEMBER("Member");

    private final String value;

    MemberRole(String value) {this.value = value;}

    public String getValue() {return value;}
}
