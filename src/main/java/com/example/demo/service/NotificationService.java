package com.example.demo.service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.notification.NotificationCommand;
import com.example.demo.dto.notification.NotificationMessage;
import com.example.demo.dto.notification.NotificationRequest;
import com.example.demo.dto.notification.NotificationResponse;
import com.example.demo.entity.Asset;
import com.example.demo.entity.Notification;
import com.example.demo.entity.User;
import com.example.demo.enums.NotificationType;
import com.example.demo.exception.DataNotFound;
import com.example.demo.repository.AssetRepository;
import com.example.demo.repository.NotificationRepository;
import com.example.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final AssetRepository assetRepository;
    private final SimpMessagingTemplate messagingTemplate;

    private static final String USER_TOPIC_TEMPLATE = "/topic/notifications/%s";

    @Transactional
    public NotificationResponse dispatch(NotificationCommand command) {
        Objects.requireNonNull(command.userId(), "User id is required");
        Objects.requireNonNull(command.title(), "Notification title is required");
        Objects.requireNonNull(command.message(), "Notification message is required");

        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new DataNotFound("User not found with id " + command.userId()));

        Asset asset = null;
        if (command.assetId() != null) {
            asset = assetRepository.findById(command.assetId())
                    .orElseThrow(() -> new DataNotFound("Asset not found with id " + command.assetId()));
        }

        Notification notification = Notification.builder()
                .user(user)
                .asset(asset)
                .title(command.title())
                .message(command.message())
                .linkUrl(command.linkUrl())
                .type(command.type() != null ? command.type() : NotificationType.INFO)
                .read(false)
                .build();

        Notification saved = notificationRepository.save(notification);
        NotificationResponse response = mapToResponse(saved);
        publishToWebsocket(response);
        return response;
    }

    @Transactional
    public List<NotificationResponse> dispatchBulk(Collection<NotificationCommand> commands) {
        return commands.stream()
                .map(this::dispatch)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsForUser(Long userId) {
        return notificationRepository.findByUser_IdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findByIdAndUser_Id(notificationId, userId)
                .orElseThrow(() -> new DataNotFound("Notification not found"));
        if (!notification.isRead()) {
            notification.setRead(true);
            notificationRepository.save(notification);
        }
    }

    @Transactional(readOnly = true)
    public long countUnread(Long userId) {
        return notificationRepository.countByUser_IdAndReadIsFalse(userId);
    }

    @Transactional
    public NotificationResponse dispatch(NotificationRequest request) {
        NotificationCommand command = NotificationCommand.builder()
                .userId(request.getUserId())
                .title(request.getTitle())
                .message(request.getMessage())
                .linkUrl(request.getLinkUrl())
                .assetId(request.getAssetId())
                .type(request.getType())
                .build();
        return dispatch(command);
    }

    private void publishToWebsocket(NotificationResponse response) {
        NotificationMessage message = NotificationMessage.builder()
                .id(response.getId())
                .userId(response.getUserId())
                .assetId(response.getAssetId())
                .title(response.getTitle())
                .message(response.getMessage())
                .linkUrl(response.getLinkUrl())
                .type(response.getType())
                .read(response.isRead())
                .createdAt(response.getCreatedAt())
                .build();

        messagingTemplate.convertAndSend(userTopic(response.getUserId()), message);
    }

    private String userTopic(Long userId) {
        return String.format(USER_TOPIC_TEMPLATE, userId);
    }

    private NotificationResponse mapToResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .userId(notification.getUser().getId())
                .assetId(notification.getAsset() != null ? notification.getAsset().getId() : null)
                .title(notification.getTitle())
                .message(notification.getMessage())
                .linkUrl(notification.getLinkUrl())
                .type(notification.getType())
                .read(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
