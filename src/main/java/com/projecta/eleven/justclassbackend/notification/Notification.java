package com.projecta.eleven.justclassbackend.notification;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;

public class Notification {
    private String notificationId;

    private Timestamp invokeTimestamp;

    private String invokerId;

    private DocumentReference invokerReference;

    private String ownerId;

    private DocumentReference ownerReference;

    private Timestamp seen;

    public Notification(
            String notificationId,
            Timestamp invokeTimestamp,
            String invokerId,
            DocumentReference invokerReference,
            String ownerId,
            DocumentReference ownerReference,
            Timestamp seen) {
        this.notificationId = notificationId;
        this.invokeTimestamp = invokeTimestamp;
        this.invokerId = invokerId;
        this.invokerReference = invokerReference;
        this.ownerId = ownerId;
        this.ownerReference = ownerReference;
        this.seen = seen;
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

    public Timestamp getSeen() {
        return seen;
    }

    public void setSeen(Timestamp seen) {
        this.seen = seen;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }
}
