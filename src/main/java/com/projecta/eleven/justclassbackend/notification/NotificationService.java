package com.projecta.eleven.justclassbackend.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class NotificationService {
    private final NotificationRepository repository;

    private List<Notification> notifications = new ArrayList<>();

    @Autowired
    public NotificationService(NotificationRepository repository) {
        this.repository = repository;
    }

    public void add(Notification notification) {
        if (notification != null) {
            notifications.add(notification);
            repository.insert(notification);
        }
    }

    public void send() {
        if (notifications.size() > 0) {
            repository.commit();
            notifications.clear();
        }
    }

    public <T extends Notification> T get(String notificationId) throws ExecutionException, InterruptedException {
        if (notificationId == null || notificationId.trim().length() == 0) {
            return null;
        }
        return repository.get(notificationId);
    }

    public void update(Notification notification) {
        repository.update(notification);
        repository.commit();
    }
}
