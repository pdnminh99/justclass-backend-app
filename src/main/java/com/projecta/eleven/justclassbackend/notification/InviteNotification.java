package com.projecta.eleven.justclassbackend.notification;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.projecta.eleven.justclassbackend.classroom.MemberRoles;
import com.projecta.eleven.justclassbackend.classroom.MinifiedClassroom;
import com.projecta.eleven.justclassbackend.invitation.InvitationStatus;
import com.projecta.eleven.justclassbackend.user.MinifiedUser;

import java.util.HashMap;
import java.util.Map;

public class InviteNotification extends Notification {

    private DocumentReference classroomReference;

    private MinifiedClassroom classroom;

    private MemberRoles role;

    private String invitationId;

    private DocumentReference invitationReference;

    private InvitationStatus invitationStatus;

    public InviteNotification(
            String notificationId,
            Timestamp invokeTimestamp,
            String invokerId,
            MinifiedUser invoker,
            DocumentReference invokerReference,
            String ownerId,
            DocumentReference ownerReference,
            MinifiedClassroom classroom,
            DocumentReference classroomReference,
            NotificationType notificationType,
            Timestamp deletedAt,
            MemberRoles role,
            String invitationId,
            DocumentReference invitationReference,
            Timestamp seenAt,
            InvitationStatus invitationStatus
    ) {
        super(notificationId, invokeTimestamp, invokerId, invoker, invokerReference, ownerId, ownerReference, notificationType, deletedAt, seenAt);
        this.role = role;
        this.classroom = classroom;
        this.classroomReference = classroomReference;
        this.invitationId = invitationId;
        this.invitationReference = invitationReference;
        this.invitationStatus = invitationStatus;
    }

    public InviteNotification(DocumentSnapshot snapshot) {
        super(snapshot);

        HashMap<String, Object> classroom = (HashMap<String, Object>) snapshot.getData().get("classroom");

        if (classroom != null) {
            String classroomId = (String) classroom.get("classroomId");
            String subject = (String) classroom.get("subject");
            String title = (String) classroom.get("title");
            this.classroom = new MinifiedClassroom(classroomId, title, subject, null, null, null, null);
        }

        this.role = MemberRoles.fromText(snapshot.getString("role"));
        this.classroomReference = snapshot.get("classroomReference", DocumentReference.class);
        this.invitationId = snapshot.getString("invitationId");
        this.invitationReference = snapshot.get("invitationReference", DocumentReference.class);
        this.invitationStatus = InvitationStatus.fromText(snapshot.getString("invitationStatus"));
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

        if (getClassroom() != null) {
            MinifiedClassroom currentClassroom = getClassroom();
            var classroomInfoMap = new HashMap<String, Object>();

            ifFieldNotNullThenPutToMap("classroomId", currentClassroom.getClassroomId(), classroomInfoMap);
            ifFieldNotNullThenPutToMap("subject", currentClassroom.getSubject(), classroomInfoMap);
            ifFieldNotNullThenPutToMap("title", currentClassroom.getTitle(), classroomInfoMap);

            map.put("classroom", classroomInfoMap);
        }
        ifFieldNotNullThenPutToMap("classroomReference", getClassroomReference(), map);
        if (getRole() != null) {
            ifFieldNotNullThenPutToMap("role", getRole().toString(), map);
        }
        ifFieldNotNullThenPutToMap("invitationId", getInvitationId(), map);
        ifFieldNotNullThenPutToMap("invitationReference", getInvitationReference(), map);
        if (getInvitationStatus() != null) {
            ifFieldNotNullThenPutToMap("invitationStatus", getInvitationStatus().toString(), map);
        }

        return map;
    }

    @Override
    public Map<String, String> toMessage() {
        Map<String, String> message = super.toMessage();
        MinifiedClassroom classroom = getClassroom();

        message.put("classroomId", classroom.getClassroomId());
        message.put("subject", classroom.getSubject());
        message.put("title", classroom.getTitle());
        message.put("role", getRole().toString());
        return message;
    }

    @Override
    public String getMessageTitle() {
        return getClassroom() != null ?
                getClassroom().getTitle() :
                "[No title]";
    }

    @Override
    public String getMessageBody() {
        String invokerName = getInvoker().getDisplayName();
        String classroomTitle = getClassroom().getTitle();
        MemberRoles role = getRole();
        String message;

        switch (getNotificationType()) {
            case INVITATION:
                message = invokerName + " invites you to join [" + classroomTitle + "]";
                if (role == null) {
                    message += ".";
                }
                return role == MemberRoles.COLLABORATOR ?
                        message + " as collaborator." :
                        message + " as student.";
            case ROLE_CHANGE:
                switch (role) {
                    case OWNER:
                        return "You were promoted as owner of classroom [" + classroomTitle + "]";
                    case COLLABORATOR:
                        return "You were promoted as collaborator of classroom [" + classroomTitle + "]";
                    case STUDENT:
                    default:
                        return "You were promoted as student of classroom [" + classroomTitle + "]";
                }
            default:
                return "...";
        }
    }
}
