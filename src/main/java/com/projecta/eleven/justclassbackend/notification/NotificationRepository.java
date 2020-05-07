package com.projecta.eleven.justclassbackend.notification;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteBatch;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ExecutionException;

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

    public <T extends Notification> T get(String notificationId) throws ExecutionException, InterruptedException {
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
}





