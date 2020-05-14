package com.projecta.eleven.justclassbackend.notification;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.projecta.eleven.justclassbackend.classroom.MinifiedClassroom;
import com.projecta.eleven.justclassbackend.user.MinifiedUser;

import java.util.HashMap;

public class ClassroomDeletedNotification extends Notification {

    private DocumentReference classroomReference;

    private MinifiedClassroom classroom;

    public ClassroomDeletedNotification(
            String notificationId,
            Timestamp invokeTime,
            MinifiedClassroom classroom,
            DocumentReference classroomReference,
            MinifiedUser invoker,
            DocumentReference invokerReference,
            String ownerId,
            DocumentReference ownerReference) {
        super(notificationId, invokeTime, invoker, invokerReference, ownerId, ownerReference, NotificationType.CLASSROOM_DELETED);
        this.classroom = classroom;
        this.classroomReference = classroomReference;
    }

    public ClassroomDeletedNotification(DocumentSnapshot snapshot) {
        super(snapshot);

        HashMap<String, Object> classroom = (HashMap<String, Object>) snapshot.getData().get("classroom");
        if (classroom != null) {
            String classroomId = (String) classroom.get("classroomId");
            String subject = (String) classroom.get("subject");
            String title = (String) classroom.get("title");
            this.classroom = new MinifiedClassroom(classroomId, title, subject, null, null, null, null);
        }
    }

    public DocumentReference getClassroomReference() {
        return classroomReference;
    }

    public void setClassroomReference(DocumentReference classroomReference) {
        this.classroomReference = classroomReference;
    }

    public MinifiedClassroom getClassroom() {
        return classroom;
    }

    public void setClassroom(MinifiedClassroom classroom) {
        this.classroom = classroom;
    }
}
