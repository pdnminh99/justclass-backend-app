package com.projecta.eleven.justclassbackend.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

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

    public Stream<HashMap<String, Object>> get(String ownerId, Integer count) throws ExecutionException, InterruptedException {
        if (ownerId == null || ownerId.trim().length() == 0) {
            throw new IllegalArgumentException("LocalId is null or empty.");
        }
        if (count == null) {
            count = 50;
        }
        if (count < 1) {
            return Stream.empty();
        }
        // TODO transform field `invoker` and `classroom` to actual objects.
        return repository.get(ownerId, count)
                .stream()
                .map(m -> m.toMap(true))
                .peek(m -> {
                    m.remove("invokerReference");
                    m.remove("ownerReference");
                    m.remove("classroomReference");
                    m.remove("invitationReference");
                });
    }

    public <T extends Notification> T find(String notificationId) throws ExecutionException, InterruptedException {
        if (notificationId == null || notificationId.trim().length() == 0) {
            return null;
        }
        return repository.find(notificationId);
    }

    public void update(Notification notification) {
        repository.update(notification);
        repository.commit();
    }
}
