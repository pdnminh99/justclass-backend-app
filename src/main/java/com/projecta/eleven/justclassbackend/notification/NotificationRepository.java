package com.projecta.eleven.justclassbackend.notification;

import com.google.cloud.firestore.CollectionReference;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
class NotificationRepository {

    private final CollectionReference notificationsCollection;

    private final FirebaseMessaging fcmDelivery;

    @Autowired
    NotificationRepository(
            @Qualifier("notificationsCollection") CollectionReference notificationsCollection,
            FirebaseMessaging fcmDelivery) {
        this.fcmDelivery = fcmDelivery;
        this.notificationsCollection = notificationsCollection;
    }
}
