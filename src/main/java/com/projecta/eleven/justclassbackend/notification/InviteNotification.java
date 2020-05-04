package com.projecta.eleven.justclassbackend.notification;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.projecta.eleven.justclassbackend.classroom.MemberRoles;

import java.util.HashMap;

public class InviteNotification extends Notification {

    private String classroomId;

    private DocumentReference classroomReference;

    private MemberRoles role;

    public InviteNotification(
            String notificationId,
            Timestamp invokeTimestamp,
            String invokerId,
            DocumentReference invokerReference,
            String ownerId,
            DocumentReference ownerReference,
            String classroomId,
            DocumentReference classroomReference,
            NotificationType notificationType,
            MemberRoles role) {
        super(notificationId, invokeTimestamp, invokerId, invokerReference, ownerId, ownerReference, notificationType);
        this.classroomId = classroomId;
        this.role = role;
        this.classroomReference = classroomReference;
    }

    public String getClassroomId() {
        return classroomId;
    }

    public void setClassroomId(String classroomId) {
        this.classroomId = classroomId;
    }

    public DocumentReference getClassroomReference() {
        return classroomReference;
    }

    public void setClassroomReference(DocumentReference classroomReference) {
        this.classroomReference = classroomReference;
    }

    public MemberRoles getRole() {
        return role;
    }

    public void setRole(MemberRoles role) {
        this.role = role;
    }

    @Override
    public HashMap<String, Object> toMap(boolean isTimestampInMilliseconds) {
        var map = super.toMap(isTimestampInMilliseconds);

        ifFieldNotNullThenPutToMap("classroomId", getClassroomId(), map);
        ifFieldNotNullThenPutToMap("classroomReference", getClassroomReference(), map);
        if (getRole() != null) {
            ifFieldNotNullThenPutToMap("role", getRole().toString(), map);
        }

        return map;
    }

}
