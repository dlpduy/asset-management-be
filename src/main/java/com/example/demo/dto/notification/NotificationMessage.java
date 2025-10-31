package com.example.demo.dto.notification;

import java.time.Instant;

import com.example.demo.enums.NotificationType;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class NotificationMessage {
    Long id;
    Long userId;
    Long assetId;
    String title;
    String message;
    String linkUrl;
    NotificationType type;
    boolean read;
    Instant createdAt;
}
