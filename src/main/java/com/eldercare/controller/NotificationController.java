package com.eldercare.controller;

import com.eldercare.entity.Notification;
import com.eldercare.security.CustomUserDetails;
import com.eldercare.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<Notification>> getNotifications(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(notificationService.getUserNotifications(userDetails.getUser()));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Integer>> getUnreadCount(@AuthenticationPrincipal CustomUserDetails userDetails) {
        int count = notificationService.getUnreadCount(userDetails.getUser());
        return ResponseEntity.ok(Map.of("unreadCount", count));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        notificationService.markAsRead(id, userDetails.getUser());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(@AuthenticationPrincipal CustomUserDetails userDetails) {
        notificationService.markAllAsRead(userDetails.getUser());
        return ResponseEntity.ok().build();
    }
}
