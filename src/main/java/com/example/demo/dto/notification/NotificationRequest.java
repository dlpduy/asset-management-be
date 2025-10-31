package com.example.demo.dto.notification;

import com.example.demo.enums.NotificationType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NotificationRequest {

    @NotNull
    private Long userId;

    @NotBlank
    private String title;

    @NotBlank
    private String message;

    private String linkUrl;

    private Long assetId;

    private NotificationType type = NotificationType.INFO;
}
