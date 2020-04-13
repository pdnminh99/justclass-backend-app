package com.projecta.eleven.justclassbackend.classroom;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.projecta.eleven.justclassbackend.utils.MapSerializable;

import java.util.HashMap;

public class Collaborator implements MapSerializable {
    private final String collaboratorId;

    private DocumentReference classroomReference;

    private DocumentReference userReference;

    private CollaboratorRoles role;

    private Timestamp createdTimestamp;

    private Timestamp lastAccessTimestamp;

    public Collaborator(String collaboratorId,
                        DocumentReference classroomReference,
                        DocumentReference userReference,
                        Timestamp createdTimestamp,
                        Timestamp lastAccessTimestamp,
                        CollaboratorRoles role) {
        this.collaboratorId = collaboratorId;
        this.classroomReference = classroomReference;
        this.userReference = userReference;
        this.createdTimestamp = createdTimestamp;
        this.lastAccessTimestamp = lastAccessTimestamp;
        this.role = role;
    }

    public Collaborator(DocumentSnapshot snapshot) {
        this.collaboratorId = snapshot.getId();
        this.classroomReference = snapshot.get("classroomReference", DocumentReference.class);
        this.userReference = snapshot.get("userReference", DocumentReference.class);
        this.createdTimestamp = snapshot.getTimestamp("createTimestamp");
        this.lastAccessTimestamp = snapshot.getTimestamp("lastAccessTimestamp");
        this.role = CollaboratorRoles.fromText(snapshot.getString("role"));
    }

    public String getCollaboratorId() {
        return collaboratorId;
    }

    public CollaboratorRoles getRole() {
        return role;
    }

    public void setRole(CollaboratorRoles role) {
        this.role = role;
    }

    public DocumentReference getUserReference() {
        return userReference;
    }

    public void setUserReference(DocumentReference userReference) {
        this.userReference = userReference;
    }

    public String getUserId() {
        return userReference.getId();
    }

    public DocumentReference getClassroomReference() {
        return classroomReference;
    }

    public void setClassroomReference(DocumentReference classroomReference) {
        this.classroomReference = classroomReference;
    }

    public String getClassroomId() {
        return classroomReference.getId();
    }

    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public HashMap<String, Object> toMap() {
        var map = new HashMap<String, Object>();
        ifFieldNotNullThenPutToMap("collaboratorId", getCollaboratorId(), map);
        ifFieldNotNullThenPutToMap("classroomId", getClassroomId(), map);
        ifFieldNotNullThenPutToMap("classroomReference", getClassroomReference(), map);
        ifFieldNotNullThenPutToMap("userId", getUserId(), map);
        ifFieldNotNullThenPutToMap("userReference", getUserReference(), map);
        ifFieldNotNullThenPutToMap("createTimestamp", getCreatedTimestamp(), map);
        ifFieldNotNullThenPutToMap("lastAccessTimestamp", getLastAccessTimestamp(), map);
        ifFieldNotNullThenPutToMap("role", getRole().toString(), map);
        return map;
    }

    public Timestamp getLastAccessTimestamp() {
        return lastAccessTimestamp;
    }

    public void setLastAccessTimestamp(Timestamp lastAccessTimestamp) {
        this.lastAccessTimestamp = lastAccessTimestamp;
    }
}
