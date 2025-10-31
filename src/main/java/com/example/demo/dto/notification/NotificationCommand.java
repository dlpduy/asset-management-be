package com.example.demo.dto.notification;

import com.example.demo.enums.NotificationType;

import lombok.Builder;

@Builder
public record NotificationCommand(
        Long userId,
        String title,
        String message,
        String linkUrl,
        Long assetId,
        NotificationType type) {

    public NotificationType type() {
        return type != null ? type : NotificationType.INFO;
    }
}
