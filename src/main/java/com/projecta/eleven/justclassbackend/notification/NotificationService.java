package com.projecta.eleven.justclassbackend.notification;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.projecta.eleven.justclassbackend.invitation.InvitationStatus;
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

    private final List<Notification> notifications = new ArrayList<>();

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

    public void remove(DocumentReference owner, DocumentReference classroom, Timestamp before, InvitationStatus status) throws ExecutionException, InterruptedException {
        if (owner != null && classroom != null && before != null && status != null) {
            repository.remove(owner, classroom, before, status);
        }
    }

    public void send() {
        if (notifications.size() > 0) {
            repository.commit();
            notifications.clear();
        }
    }

    public Stream<HashMap<String, Object>> get(String ownerId, int pageSize, int pageNumber) throws ExecutionException, InterruptedException {
        if (ownerId == null || ownerId.trim().length() == 0) {
            throw new IllegalArgumentException("LocalId is null or empty.");
        }
        if (pageSize < 1 || pageNumber < 0) {
            return Stream.empty();
        }
        var notifications = repository.get(ownerId, pageSize, pageNumber);

        return notifications
                .stream()
                .map(m -> m.toMap(true))
                .peek(m -> {
                    m.remove("invokerReference");
                    m.remove("ownerId");
                    m.remove("invitationId");
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
