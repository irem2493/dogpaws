package com.dogpaws.backend.controller.rim;

public enum NotificationType {
    CHAT("채팅"),
    MATCHING("매칭"),
    ADMIN("관리자");

    private final String description;

    NotificationType(String description) {
        this.description = description;
    }
}