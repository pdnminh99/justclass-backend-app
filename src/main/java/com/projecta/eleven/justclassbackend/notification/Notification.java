package com.projecta.eleven.justclassbackend.notification;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.common.collect.Maps;
import com.projecta.eleven.justclassbackend.user.MinifiedUser;
import com.projecta.eleven.justclassbackend.utils.MapSerializable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Notification implements MapSerializable {
    private String notificationId;

    private Timestamp invokeTime;

    private String invokerId;

    private MinifiedUser invoker;

    private DocumentReference invokerReference;

    private String ownerId;

    private DocumentReference ownerReference;

    private NotificationType notificationType;

    private Timestamp deletedAt;

    private Timestamp seenAt;

    public Notification(
            String notificationId,
            Timestamp invokeTime,
            String invokerId,
            MinifiedUser invoker,
            DocumentReference invokerReference,
            String ownerId,
            DocumentReference ownerReference,
            NotificationType notificationType,
            Timestamp deletedAt,
            Timestamp seenAt) {
        this.notificationId = notificationId;
        this.invokeTime = invokeTime;
        this.invokerId = invokerId;
        this.invoker = invoker;
        this.invokerReference = invokerReference;
        this.ownerId = ownerId;
        this.ownerReference = ownerReference;
        this.notificationType = notificationType;
        this.deletedAt = deletedAt;
        this.seenAt = seenAt;
    }

    public Notification(DocumentSnapshot snapshot) {
        this.notificationId = snapshot.getId();
        this.invokeTime = snapshot.getTimestamp("invokeTime");
        this.invokerId = snapshot.getString("invokerId");
        this.deletedAt = snapshot.getTimestamp("deletedAt");
        this.seenAt = snapshot.getTimestamp("seenAt");
        this.invokerReference = snapshot.get("invokerReference", DocumentReference.class);
        this.ownerId = snapshot.getString("ownerId");
        this.ownerReference = snapshot.get("ownerReference", DocumentReference.class);
        this.notificationType = NotificationType.fromText(snapshot.getString("notificationType"));
    }

    public Timestamp getInvokeTime() {
        return invokeTime;
    }

    public void setInvokeTime(Timestamp invokeTime) {
        this.invokeTime = invokeTime;
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

    public MinifiedUser getInvoker() {
        return invoker;
    }

    public void setInvoker(MinifiedUser invoker) {
        this.invoker = invoker;
    }

    public String getInvokerId() {
        return invokerId;
    }

    public void setInvokerId(String invokerId) {
        this.invokerId = invokerId;
    }

    public Timestamp getSeenAt() {
        return seenAt;
    }

    public void setSeenAt(Timestamp seenAt) {
        this.seenAt = seenAt;
    }

    @Override
    public HashMap<String, Object> toMap(boolean isTimestampInMilliseconds) {
        var map = new HashMap<String, Object>();

        ifFieldNotNullThenPutToMap("notificationId", getNotificationId(), map);
        ifFieldNotNullThenPutToMap("invokeTime", getInvokeTime() != null && isTimestampInMilliseconds ?
                getInvokeTime().toDate().getTime() :
                getInvokeTime(), map);
        ifFieldNotNullThenPutToMap("invokerId", getInvokerId(), map);
        if (getInvoker() != null) {
            ifFieldNotNullThenPutToMap("invoker", getInvoker().toMap(), map);
        }
        ifFieldNotNullThenPutToMap("invokerReference", getInvokerReference(), map);
        ifFieldNotNullThenPutToMap("ownerId", getOwnerId(), map);
        ifFieldNotNullThenPutToMap("ownerReference", getOwnerReference(), map);
        if (getNotificationType() != null) {
            ifFieldNotNullThenPutToMap("notificationType", getNotificationType().toString(), map);
        }
        map.put("deletedAt", getDeletedAt() != null && isTimestampInMilliseconds ?
                getDeletedAt().toDate().getTime() :
                getDeletedAt());
        map.put("seenAt", getSeenAt() != null && isTimestampInMilliseconds ?
                getSeenAt().toDate().getTime() :
                getSeenAt());

        return map;
    }

    public Map<String, String> toMessage() {
        Map<String, String> message = Maps.newHashMap();

        message.put("notificationId", getNotificationId());
        if (getInvoker() != null) {
            message.put("invokerName", Objects.requireNonNullElse(getInvoker().getDisplayName(), "[unknown]"));
        }
        if (getNotificationType() != null) {
            message.put("type", getNotificationType().toString());
        }
        if (getInvokeTime() != null) {
            message.put("invokeTime", String.valueOf(getInvokeTime().toDate().getTime()));
        }
        return message;
    }

    public String getMessageTitle() {
        return "...";
    }

    public String getMessageBody() {
        return "...";
    }

    public Timestamp getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Timestamp deleted) {
        deletedAt = deleted;
    }
}
