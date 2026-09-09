package com.eldercare.service;

import com.eldercare.entity.Notification;
import com.eldercare.entity.User;
import com.eldercare.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public Notification createNotification(User user, String message) {
        Notification notification = Notification.builder()
                .user(user)
                .message(message)
                .isRead(false)
                .build();
        return notificationRepository.save(notification);
    }

    public List<Notification> getUserNotifications(User user) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    public int getUnreadCount(User user) {
        return notificationRepository.countByUserIdAndIsReadFalse(user.getId());
    }

    @Transactional
    public void markAsRead(Long id, User user) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        if (!notification.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead(User user) {
        List<Notification> unreadList = notificationRepository.findByUserIdAndIsReadFalse(user.getId());
        for (Notification notification : unreadList) {
            notification.setRead(true);
        }
        notificationRepository.saveAll(unreadList);
    }
}
