package com.projecta.eleven.justclassbackend.notification;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import com.google.common.collect.Lists;
import com.google.firebase.messaging.FirebaseMessaging;
import com.projecta.eleven.justclassbackend.invitation.InvitationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Repository
class NotificationRepository {

    private final Firestore firestore;

    private final CollectionReference notificationsCollection;

    private final FirebaseMessaging fcmDelivery;

    private WriteBatch writeBatch;

    @Autowired
    NotificationRepository(
            Firestore firestore,
            @Qualifier("notificationsCollection") CollectionReference notificationsCollection,
            FirebaseMessaging fcmDelivery) {
        this.fcmDelivery = fcmDelivery;
        this.firestore = firestore;
        this.notificationsCollection = notificationsCollection;
        this.writeBatch = firestore.batch();
    }

    public boolean isBatchActive() {
        return writeBatch != null;
    }

    public void resetBatch() {
        writeBatch = firestore.batch();
    }

    public void insert(Notification notification) {
        if (!isBatchActive()) {
            resetBatch();
        }
        var map = notification.toMap();
        map.remove("notificationId");
        writeBatch.create(notificationsCollection.document(), map);
    }

    public void commit() {
        if (isBatchActive()) {
            writeBatch.commit();
            writeBatch = null;
        }
    }

    public <T extends Notification> T find(String notificationId) throws ExecutionException, InterruptedException {
        DocumentSnapshot notificationSnapshot = notificationsCollection.document(notificationId)
                .get()
                .get();
        if (!notificationSnapshot.exists()) {
            return null;
        }
        String typeRepresentation = notificationSnapshot.getString("notificationType");
        assert typeRepresentation != null;
        assert typeRepresentation.length() > 0;
        NotificationType type = NotificationType.fromText(typeRepresentation);
        switch (type) {
            case INVITATION:
            case ROLE_CHANGE:
                return (T) new InviteNotification(notificationSnapshot);
            default:
                return null;
        }
    }

    public <T extends Notification> List<T> get(String ownerId, int pageSize, int pageNumber) throws ExecutionException, InterruptedException {
        if (ownerId == null || ownerId.trim().length() == 0) {
            return Lists.newArrayList();
        }
        QueryDocumentSnapshot startIndexDoc = getLastDocumentSnapshot(ownerId, pageSize, pageNumber);
        List<QueryDocumentSnapshot> query = startIndexDoc == null ?
                notificationsCollection.whereEqualTo("ownerId", ownerId)
                        .orderBy("invokeTime", Query.Direction.DESCENDING)
                        .limit(pageSize)
                        .get()
                        .get()
                        .getDocuments() :
                notificationsCollection.whereEqualTo("ownerId", ownerId)
                        .orderBy("invokeTime", Query.Direction.DESCENDING)
                        .startAfter(startIndexDoc)
                        .limit(pageSize)
                        .get()
                        .get()
                        .getDocuments();

        List<T> results = Lists.newArrayList();
        T notification;

        for (QueryDocumentSnapshot snap : query) {
            String notificationRepresentation = snap.getString("notificationType");
            assert notificationRepresentation != null;
            NotificationType notificationType = NotificationType.fromText(notificationRepresentation);

            switch (notificationType) {
                case INVITATION:
                case ROLE_CHANGE:
                    notification = (T) new InviteNotification(snap);
                    results.add(notification);
                    break;
                case CLASSROOM_DELETED:
                    notification = (T) new ClassroomDeletedNotification(snap);
                    results.add(notification);
                    break;
                default:
                    break;
            }
        }
        return results.stream()
                .sorted(Comparator.comparing(Notification::getInvokeTime))
                .collect(Collectors.toList());
    }

    private QueryDocumentSnapshot getLastDocumentSnapshot(String ownerId, int pageSize, int pageNumber) throws ExecutionException, InterruptedException {
        if (pageNumber < 1) {
            return null;
        }
        QuerySnapshot querySnapshot = notificationsCollection
                .whereEqualTo("ownerId", ownerId)
                .orderBy("invokeTime", Query.Direction.DESCENDING)
                .limit(pageNumber * pageSize)
                .get()
                .get();
        return querySnapshot.getDocuments()
                .get(querySnapshot.size() - 1);
    }

    public void update(Notification notification) {
        if (notification == null || notification.getNotificationId() == null || notification.getNotificationId().trim().length() == 0) {
            return;
        }
        if (!isBatchActive()) {
            resetBatch();
        }
        var map = notification.toMap();
        map.remove("notificationId");
        String notificationId = notification.getNotificationId();

        writeBatch.update(notificationsCollection.document(notificationId), map);
    }

    public void remove(DocumentReference owner, DocumentReference classroom, Timestamp before, InvitationStatus status) throws ExecutionException, InterruptedException {
        notificationsCollection.whereEqualTo("classroomReference", classroom)
                .whereEqualTo("ownerReference", owner)
                .whereEqualTo("invitationStatus", status.toString())
                .whereLessThan("invokeTime", before).get().get()
                .getDocuments()
                .stream()
                .map(DocumentSnapshot::getReference)
                .forEach(ref -> writeBatch.delete(ref));
    }
}





