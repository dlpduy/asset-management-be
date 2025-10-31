package com.example.demo.controller.notification;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ResponseObject;
import com.example.demo.dto.notification.NotificationRequest;
import com.example.demo.dto.notification.NotificationResponse;
import com.example.demo.service.NotificationService;
import com.example.demo.service.security.CurrentUserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/notifications")
@Validated
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final CurrentUserService currentUserService;

    @GetMapping
    public ResponseEntity<ResponseObject> getNotifications() {
        Long currentUserId = currentUserService.getCurrentUserId();
        List<NotificationResponse> notifications = notificationService.getNotificationsForUser(currentUserId);
        return ResponseEntity.ok(ResponseObject.builder()
                .data(notifications)
                .build());
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ResponseObject> getUnreadCount() {
        Long currentUserId = currentUserService.getCurrentUserId();
        long count = notificationService.countUnread(currentUserId);
        return ResponseEntity.ok(ResponseObject.builder()
                .data(count)
                .build());
    }

    @PostMapping("/{notificationId}/read")
    public ResponseEntity<ResponseObject> markAsRead(@PathVariable Long notificationId) {
        Long currentUserId = currentUserService.getCurrentUserId();
        notificationService.markAsRead(notificationId, currentUserId);
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Notification marked as read")
                .build());
    }

    @PostMapping
    public ResponseEntity<ResponseObject> createNotification(@Valid @RequestBody NotificationRequest request) {
        NotificationResponse notification = notificationService.dispatch(request);
        return ResponseEntity.ok(ResponseObject.builder()
                .data(notification)
                .message("Notification created")
                .build());
    }
}
