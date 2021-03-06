package com.projecta.eleven.justclassbackend.notification;

import com.google.api.core.ApiFutures;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.projecta.eleven.justclassbackend.invitation.InvitationStatus;
import com.projecta.eleven.justclassbackend.user.MinifiedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class NotificationService {
    private final NotificationRepository repository;

    private List<Notification> notifications = Lists.newArrayList();

    @Autowired
    public NotificationService(NotificationRepository repository) {
        this.repository = repository;
    }

    public void cleanOldDeletedNotifications() throws ExecutionException, InterruptedException {
        var calendar = Calendar.getInstance();
        calendar.setTime(Timestamp.now().toDate());
        calendar.add(Calendar.DATE, -1);

        var oneWeekBefore = Timestamp.of(calendar.getTime());
        System.err.println("> Clean up deleted notifications before: " + oneWeekBefore.toString());

        repository.removeDeletedNotificationsBefore(oneWeekBefore);
    }

    public boolean checkDuplicateId(String newId) {
        return this.notifications.stream()
                .anyMatch(n -> n.getNotificationId().equals(newId));
    }

    public void add(Notification notification) {
        if (notification != null) {
            String newId;
            do {
                newId = repository.generateId();
            } while (checkDuplicateId(newId));
            notification.setNotificationId(newId);

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

    public Stream<HashMap<String, Object>> get(String ownerId, int pageSize, int pageNumber, Timestamp lastRefresh, boolean excludeDeleted) throws ExecutionException, InterruptedException {
        if (ownerId == null || ownerId.trim().length() == 0) {
            throw new IllegalArgumentException("LocalId is null or empty.");
        }
        if (pageNumber < 0) {
            return Stream.empty();
        }

        // Query for invokerInfo.
        notifications = repository.get(ownerId, pageSize, pageNumber, lastRefresh, excludeDeleted);
        getInvokers();

        List<HashMap<String, Object>> maps = notifications
                .stream()
                .peek(m -> {
                    m.setInvokerReference(null);
                    m.setOwnerReference(null);
                    m.setInvokerReference(null);
                    m.setInvokerId(null);
                })
                .map(m -> m.toMap(true))
                .peek(m -> {
                    m.remove("invokerReference");
                    m.remove("ownerId");
                    m.remove("invitationId");
                    m.remove("ownerReference");
                    m.remove("classroomReference");
                    m.remove("invitationReference");
                }).collect(Collectors.toList());
        notifications.clear();
        return maps.stream();
    }

    private void getInvokers() throws ExecutionException, InterruptedException {
        Map<String, MinifiedUser> invokersMap = Maps.newHashMap();
        List<DocumentReference> invokersReferences = Lists.newArrayList();

        for (Notification notification : notifications) {
            boolean isExist = invokersReferences
                    .stream()
                    .anyMatch(m -> m.getId().equals(notification.getInvokerId()));

            if (!isExist) {
                invokersReferences.add(notification.getInvokerReference());
            }
        }
        ApiFutures.allAsList(
                invokersReferences
                        .stream()
                        .map(DocumentReference::get)
                        .collect(Collectors.toList())
        ).get().stream().map(MinifiedUser::new).forEach(m -> invokersMap.put(m.getLocalId(), m));

        for (var notification : notifications) {
            notification.setInvoker(invokersMap.get(notification.getInvokerId()));
        }
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

    public int getNotificationsCount(String localId, Timestamp lastRefresh) throws ExecutionException, InterruptedException {
        if (localId == null || localId.trim().length() == 0) {
            return 0;
        }
        lastRefresh = Objects.requireNonNullElse(lastRefresh, Timestamp.now());
        return repository.countNew(localId, lastRefresh);
    }
}
