package com.projecta.eleven.justclassbackend.notification;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;

public class InviteNotification extends Notification {

    private String classroomId;

    private DocumentReference classroomReference;

    public InviteNotification(
            String notificationId,
            Timestamp invokeTimestamp,
            String invokerId,
            DocumentReference invokerReference,
            String ownerId,
            DocumentReference ownerReference,
            Timestamp seen,
            String classroomId,
            DocumentReference classroomReference) {
        super(notificationId, invokeTimestamp, invokerId, invokerReference, ownerId, ownerReference, seen);
        this.classroomId = classroomId;
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
}
