package com.projecta.eleven.justclassbackend.classroom;

import com.google.cloud.firestore.DocumentReference;

public class Collaborator {
    private final String collaboratorId;

    private DocumentReference classroomReference;

    private DocumentReference userReference;

    private CollaboratorRoles role;

    public Collaborator(String collaboratorId,
                        DocumentReference classroomReference,
                        DocumentReference userReference,
                        CollaboratorRoles role) {
        this.collaboratorId = collaboratorId;
        this.classroomReference = classroomReference;
        this.userReference = userReference;
        this.role = role;
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
}
