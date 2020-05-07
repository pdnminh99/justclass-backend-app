package com.projecta.eleven.justclassbackend.classroom;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.projecta.eleven.justclassbackend.utils.MapSerializable;

import java.util.HashMap;

public class Member implements MapSerializable {
    private String memberId;

    private DocumentReference classroomReference;

    private DocumentReference userReference;

    private MemberRoles role;

    private Timestamp createdTimestamp;

    private Timestamp lastAccess;

    public Member(String memberId,
                  DocumentReference classroomReference,
                  DocumentReference userReference,
                  Timestamp createdTimestamp,
                  Timestamp lastAccess,
                  MemberRoles role) {
        this.memberId = memberId;
        this.classroomReference = classroomReference;
        this.userReference = userReference;
        this.createdTimestamp = createdTimestamp;
        this.lastAccess = lastAccess;
        this.role = role;
    }

    public Member(DocumentSnapshot snapshot) {
        this.memberId = snapshot.getId();
        this.classroomReference = snapshot.get("classroomReference", DocumentReference.class);
        this.userReference = snapshot.get("userReference", DocumentReference.class);
        this.createdTimestamp = snapshot.getTimestamp("createTimestamp");
        this.lastAccess = snapshot.getTimestamp("lastAccess");
        this.role = MemberRoles.fromText(snapshot.getString("role"));
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String newId) {
        this.memberId = newId;
    }

    public MemberRoles getRole() {
        return role;
    }

    public void setRole(MemberRoles role) {
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

    public HashMap<String, Object> toMap(boolean isTimestampInMilliseconds) {
        var map = new HashMap<String, Object>();
        ifFieldNotNullThenPutToMap("memberId", getMemberId(), map);
        ifFieldNotNullThenPutToMap("classroomId", getClassroomId(), map);
        ifFieldNotNullThenPutToMap("classroomReference", getClassroomReference(), map);
        ifFieldNotNullThenPutToMap("userId", getUserId(), map);
        ifFieldNotNullThenPutToMap("userReference", getUserReference(), map);
        ifFieldNotNullThenPutToMap("createTimestamp",
                isTimestampInMilliseconds && getCreatedTimestamp() != null ?
                        getCreatedTimestamp().toDate().getTime() :
                        getCreatedTimestamp()
                , map);
        ifFieldNotNullThenPutToMap("lastAccess",
                isTimestampInMilliseconds && getLastAccess() != null ?
                        getLastAccess().toDate().getTime() :
                        getLastAccess()
                , map);
        ifFieldNotNullThenPutToMap("role", getRole().toString(), map);
        return map;
    }

    public Timestamp getLastAccess() {
        return lastAccess;
    }

    public void setLastAccess(Timestamp lastAccess) {
        this.lastAccess = lastAccess;
    }
}
