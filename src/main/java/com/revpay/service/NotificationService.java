package com.revpay.service;

import com.revpay.model.Notification;
import com.revpay.repository.NotificationRepository;
import com.revpay.util.LoggerUtil;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class NotificationService {
    private static final Logger logger = LoggerUtil.getLogger(NotificationService.class);
    private final NotificationRepository notificationRepository = new NotificationRepository();

    public void sendNotification(int userId, String message, String type) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRead(false);

        notificationRepository.createNotification(notification);
    }

    public List<Notification> getUserNotifications(int userId) {
        return notificationRepository.getNotificationsByUserId(userId);
    }

    public void markAllRead(int userId) {
        notificationRepository.markAllAsRead(userId);
    }
}
