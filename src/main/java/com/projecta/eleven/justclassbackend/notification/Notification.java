package com.projecta.eleven.justclassbackend.notification;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.projecta.eleven.justclassbackend.utils.MapSerializable;

import java.util.HashMap;

public class Notification implements MapSerializable {
    private String notificationId;

    private Timestamp invokeTimestamp;

    private String invokerId;

    private DocumentReference invokerReference;

    private String ownerId;

    private DocumentReference ownerReference;

    private NotificationType notificationType;

    public Notification(
            String notificationId,
            Timestamp invokeTimestamp,
            String invokerId,
            DocumentReference invokerReference,
            String ownerId,
            DocumentReference ownerReference,
            NotificationType notificationType) {
        this.notificationId = notificationId;
        this.invokeTimestamp = invokeTimestamp;
        this.invokerId = invokerId;
        this.invokerReference = invokerReference;
        this.ownerId = ownerId;
        this.ownerReference = ownerReference;
        this.notificationType = notificationType;
    }

    public Timestamp getInvokeTimestamp() {
        return invokeTimestamp;
    }

    public void setInvokeTimestamp(Timestamp invokeTimestamp) {
        this.invokeTimestamp = invokeTimestamp;
    }

    public String getInvokerId() {
        return invokerId;
    }

    public void setInvokerId(String invokerId) {
        this.invokerId = invokerId;
    }

    public DocumentReference getInvokerReference() {
        return invokerReference;
    }

    public void setInvokerReference(DocumentReference invokerReference) {
        this.invokerReference = invokerReference;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public DocumentReference getOwnerReference() {
        return ownerReference;
    }

    public void setOwnerReference(DocumentReference ownerReference) {
        this.ownerReference = ownerReference;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    @Override
    public HashMap<String, Object> toMap(boolean isTimestampInMilliseconds) {
        var map = new HashMap<String, Object>();

        ifFieldNotNullThenPutToMap("notificationId", getNotificationId(), map);
        ifFieldNotNullThenPutToMap("invokeTimestamp", getInvokeTimestamp() != null && isTimestampInMilliseconds ?
                getInvokeTimestamp().toDate().getTime() :
                getInvokeTimestamp(), map);
        ifFieldNotNullThenPutToMap("invokerId", getInvokerId(), map);
        ifFieldNotNullThenPutToMap("invokerReference", getInvokerReference(), map);
        ifFieldNotNullThenPutToMap("ownerId", getOwnerId(), map);
        ifFieldNotNullThenPutToMap("ownerReference", getOwnerReference(), map);
        if (getNotificationType() != null) {
            ifFieldNotNullThenPutToMap("notificationType", getNotificationType().toString(), map);
        }

        return map;
    }
}
