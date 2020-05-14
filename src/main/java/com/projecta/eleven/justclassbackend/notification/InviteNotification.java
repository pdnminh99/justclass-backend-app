package com.projecta.eleven.justclassbackend.notification;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.projecta.eleven.justclassbackend.classroom.MemberRoles;
import com.projecta.eleven.justclassbackend.classroom.MinifiedClassroom;
import com.projecta.eleven.justclassbackend.invitation.InvitationStatus;

import java.util.HashMap;

public class InviteNotification extends Notification {

    private String classroomId;

    private DocumentReference classroomReference;

    private MinifiedClassroom classroom;

    private MemberRoles role;

    private String invitationId;

    private DocumentReference invitationReference;

    private Timestamp seen;

    private InvitationStatus invitationStatus;

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
            MemberRoles role,
            String invitationId,
            DocumentReference invitationReference,
            Timestamp seen,
            InvitationStatus invitationStatus
    ) {
        super(notificationId, invokeTimestamp, invokerId, invokerReference, ownerId, ownerReference, notificationType);
        this.classroomId = classroomId;
        this.role = role;
        this.classroomReference = classroomReference;
        this.invitationId = invitationId;
        this.invitationReference = invitationReference;
        this.seen = seen;
        this.invitationStatus = invitationStatus;
    }

    public InviteNotification(DocumentSnapshot snapshot) {
        super(snapshot);
        this.classroomId = snapshot.getString("classroomId");
        this.role = MemberRoles.fromText(snapshot.getString("role"));
        this.classroomReference = snapshot.get("classroomReference", DocumentReference.class);
        this.invitationId = snapshot.getString("invitationId");
        this.invitationReference = snapshot.get("invitationReference", DocumentReference.class);
        this.seen = snapshot.getTimestamp("seen");
        this.invitationStatus = InvitationStatus.fromText(snapshot.getString("invitationStatus"));
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

    public String getInvitationId() {
        return invitationId;
    }

    public void setInvitationId(String invitationId) {
        this.invitationId = invitationId;
    }

    public DocumentReference getInvitationReference() {
        return invitationReference;
    }

    public void setInvitationReference(DocumentReference invitationReference) {
        this.invitationReference = invitationReference;
    }

    public Timestamp getSeen() {
        return seen;
    }

    public void setSeen(Timestamp seen) {
        this.seen = seen;
    }

    public InvitationStatus getInvitationStatus() {
        return invitationStatus;
    }

    public void setInvitationStatus(InvitationStatus invitationStatus) {
        this.invitationStatus = invitationStatus;
    }

    public MinifiedClassroom getClassroom() {
        return classroom;
    }

    public void setClassroom(MinifiedClassroom classroom) {
        this.classroom = classroom;
    }

    @Override
    public HashMap<String, Object> toMap(boolean isTimestampInMilliseconds) {
        var map = super.toMap(isTimestampInMilliseconds);

        ifFieldNotNullThenPutToMap("classroomId", getClassroomId(), map);
        ifFieldNotNullThenPutToMap("classroomReference", getClassroomReference(), map);
        if (getRole() != null) {
            ifFieldNotNullThenPutToMap("role", getRole().toString(), map);
        }
        ifFieldNotNullThenPutToMap("invitationId", getInvitationId(), map);
        ifFieldNotNullThenPutToMap("invitationReference", getInvitationReference(), map);
        ifFieldNotNullThenPutToMap("seen",
                getSeen() != null && isTimestampInMilliseconds ?
                        getSeen().toDate().getTime() :
                        getSeen(),
                map);
        if (getInvitationStatus() != null) {
            ifFieldNotNullThenPutToMap("invitationStatus", getInvitationStatus().toString(), map);
        }

        return map;
    }

    @Override
    public String toString() {
        return "InviteNotification{" +
                "classroomId='" + classroomId + '\'' +
                ", classroomReference=" + classroomReference +
                ", role=" + role +
                ", invitationId='" + invitationId + '\'' +
                ", invitationReference=" + invitationReference +
                ", seen=" + seen +
                ", invitationStatus=" + invitationStatus +
                '}';
    }
}
