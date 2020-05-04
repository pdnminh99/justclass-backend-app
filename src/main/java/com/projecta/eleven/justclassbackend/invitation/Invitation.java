package com.projecta.eleven.justclassbackend.invitation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.projecta.eleven.justclassbackend.classroom.MemberRoles;
import org.springframework.lang.Nullable;


@JsonIgnoreProperties({
        "invitationId",
        "invokeTime",
        "classroomId",
        "classroomReference",
        "invitorLocalId",
        "invitorReference"
})
public class Invitation {

    private String invitationId;

    @Nullable
    @JsonProperty("localId")
    private String localId;

    @Nullable
    @JsonProperty("email")
    private String email;

    @Nullable
    @JsonProperty("role")
    private MemberRoles role;

    @JsonIgnore
    private String classroomId;

    @JsonIgnore
    private DocumentReference classroomReference;

    @JsonIgnore
    private String invitorLocalId;

    @JsonIgnore
    private DocumentReference invitorReference;

    @JsonIgnore
    private Timestamp invokeTime;

    public Invitation(@Nullable String localId,
                      @Nullable String email,
                      @Nullable MemberRoles role) {
        this.localId = localId;
        this.email = email;
        this.role = role;
    }

    @Nullable
    public String getLocalId() {
        return localId;
    }

    public void setLocalId(@Nullable String localId) {
        this.localId = localId;
    }

    @Nullable
    public String getEmail() {
        return email;
    }

    public void setEmail(@Nullable String email) {
        this.email = email;
    }

    @Nullable
    public MemberRoles getRole() {
        return role;
    }

    public void setRole(@Nullable MemberRoles role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "Invitation{" +
                "localId='" + localId + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                '}';
    }

    public String getInvitationId() {
        return invitationId;
    }

    public void setInvitationId(String invitationId) {
        this.invitationId = invitationId;
    }

    public Timestamp getInvokeTime() {
        return invokeTime;
    }

    public void setInvokeTime(Timestamp invokeTime) {
        this.invokeTime = invokeTime;
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

    public String getInvitorLocalId() {
        return invitorLocalId;
    }

    public void setInvitorLocalId(String invitorLocalId) {
        this.invitorLocalId = invitorLocalId;
    }

    public DocumentReference getInvitorReference() {
        return invitorReference;
    }

    public void setInvitorReference(DocumentReference invitorReference) {
        this.invitorReference = invitorReference;
    }
}
