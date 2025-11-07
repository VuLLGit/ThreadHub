package com.example.ThreadHub.entity.enums;

public enum CommunityRole {
    Moderator("Moderator"), Member("Member");

    private final String value;

    CommunityRole(String value) {this.value = value;}

    public String getValue() {return value;}
}
