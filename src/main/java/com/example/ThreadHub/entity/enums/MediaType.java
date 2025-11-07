package com.example.ThreadHub.entity.enums;

public enum MediaType {
    IMAGE("Image"), VIDEO("Video");

    private final String value;

    MediaType(String value) {this.value = value;}

    public String getValue() {return value;}
}
