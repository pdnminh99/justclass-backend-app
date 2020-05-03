package com.projecta.eleven.justclassbackend.classroom;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentSnapshot;
import com.projecta.eleven.justclassbackend.user.MinifiedUser;
import com.projecta.eleven.justclassbackend.utils.MapSerializable;

import java.util.HashMap;
import java.util.Objects;

@JsonIgnoreProperties(value = {"role", "lastAccess", "owner", "studentsCount", "collaboratorsCount", "lastEdit"})
public class MinifiedClassroom implements MapSerializable {

    @JsonProperty("classroomId")
    private String classroomId;

    @JsonIgnore
    private MemberRoles role;

    @JsonProperty("title")
    private String title;

    @JsonProperty("subject")
    private String subject;

    @JsonProperty("theme")
    private Integer theme;

    @JsonIgnore
    private MinifiedUser owner;

    @JsonIgnore
    private Integer studentsCount;

    @JsonIgnore
    private Integer collaboratorsCount;

    @JsonIgnore
    private Timestamp lastAccess;

    @JsonIgnore
    private Timestamp lastEdit;

    public MinifiedClassroom(String classroomId,
                             String title,
                             String subject,
                             Integer theme,
                             MemberRoles role,
                             Timestamp lastAccess,
                             Timestamp lastEdit) {
        this.classroomId = classroomId;
        this.title = title;
        this.subject = subject;
        this.theme = theme;
        this.role = role;
        this.lastAccess = lastAccess;
        this.lastEdit = lastEdit;
    }

    public MinifiedClassroom(DocumentSnapshot snapshot) {
        this.classroomId = snapshot.getId();
        this.title = snapshot.getString("title");
        this.subject = snapshot.getString("subject");
        this.theme = Objects.requireNonNull(snapshot.getLong("theme")).intValue();
        this.role = MemberRoles.fromText(snapshot.getString("role"));
        this.lastAccess = snapshot.getTimestamp("lastAccess");
        this.lastEdit = snapshot.getTimestamp("lastEdit");
    }

    public String getClassroomId() {
        return classroomId;
    }

    public void setClassroomId(String classroomId) {
        this.classroomId = classroomId;
    }

    public MemberRoles getRole() {
        return role;
    }

    public void setRole(MemberRoles role) {
        this.role = role;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Integer getTheme() {
        return theme;
    }

    public void setTheme(Integer theme) {
        this.theme = theme;
    }

    public Timestamp getLastAccess() {
        return lastAccess;
    }

    public void setLastAccess(Timestamp lastAccess) {
        this.lastAccess = lastAccess;
    }

    public MinifiedUser getOwner() {
        return owner;
    }

    public void setOwner(MinifiedUser owner) {
        this.owner = owner;
    }

    public Integer getStudentsCount() {
        return studentsCount;
    }

    public void setStudentsCount(Integer studentsCount) {
        this.studentsCount = studentsCount;
    }

    public Integer getCollaboratorsCount() {
        return collaboratorsCount;
    }

    public void setCollaboratorsCount(Integer collaboratorsCount) {
        this.collaboratorsCount = collaboratorsCount;
    }

    public Timestamp getLastEdit() {
        return lastEdit;
    }

    public void setLastEdit(Timestamp lastEdit) {
        this.lastEdit = lastEdit;
    }

    @Override
    public HashMap<String, Object> toMap(boolean isTimestampInMilliseconds) {
        var map = new HashMap<String, Object>();

        ifFieldNotNullThenPutToMap("classroomId", getClassroomId(), map);
        ifFieldNotNullThenPutToMap("title", getTitle(), map);
        ifFieldNotNullThenPutToMap("subject", getSubject(), map);
        ifFieldNotNullThenPutToMap("theme", getTheme(), map);

        ifFieldNotNullThenPutToMap("lastAccess",
                isTimestampInMilliseconds && getLastAccess() != null ?
                        getLastAccess().toDate().getTime() :
                        getLastAccess()
                , map);

        ifFieldNotNullThenPutToMap("lastEdit",
                isTimestampInMilliseconds && getLastEdit() != null ?
                        getLastEdit().toDate().getTime() :
                        getLastEdit()
                , map);

        ifFieldNotNullThenPutToMap("role", getRole(), map);
        ifFieldNotNullThenPutToMap("studentsCount", getStudentsCount(), map);
        ifFieldNotNullThenPutToMap("collaboratorsCount", getCollaboratorsCount(), map);
        ifFieldNotNullThenPutToMap("owner", getOwner(), map);
        return map;
    }
}