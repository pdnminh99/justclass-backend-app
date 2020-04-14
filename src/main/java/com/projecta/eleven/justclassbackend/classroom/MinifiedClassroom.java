package com.projecta.eleven.justclassbackend.classroom;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentSnapshot;
import com.projecta.eleven.justclassbackend.utils.MapSerializable;

import java.util.HashMap;
import java.util.Objects;

@JsonIgnoreProperties(value = {"role", "lastAccessTimestamp"})
public class MinifiedClassroom implements MapSerializable {

    @JsonProperty("classroomId")
    private String classroomId;

    @JsonIgnore
    private CollaboratorRoles role;

    @JsonProperty("title")
    private String title;

    @JsonProperty("subject")
    private String subject;

    @JsonProperty("theme")
    private Integer theme;

    @JsonIgnore
    private Timestamp lastAccessTimestamp;

    public MinifiedClassroom(String classroomId,
                             String title,
                             String subject,
                             Integer theme,
                             CollaboratorRoles role,
                             Timestamp lastAccessTimestamp) {
        this.classroomId = classroomId;
        this.title = title;
        this.subject = subject;
        this.theme = theme;
        this.role = role;
        this.lastAccessTimestamp = lastAccessTimestamp;
    }

    public MinifiedClassroom(DocumentSnapshot snapshot) {
        this.classroomId = snapshot.getId();
        this.title = snapshot.getString("title");
        this.subject = snapshot.getString("subject");
        this.theme = Objects.requireNonNull(snapshot.getLong("theme")).intValue();
        this.role = CollaboratorRoles.fromText(snapshot.getString("role"));
        this.lastAccessTimestamp = snapshot.getTimestamp("lastAccessTimestamp");
    }

    public String getClassroomId() {
        return classroomId;
    }

    public void setClassroomId(String classroomId) {
        this.classroomId = classroomId;
    }

    public CollaboratorRoles getRole() {
        return role;
    }

    public void setRole(CollaboratorRoles role) {
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

    @Override
    public HashMap<String, Object> toMap() {
        var map = new HashMap<String, Object>();

        ifFieldNotNullThenPutToMap("classroomId", getClassroomId(), map);
        ifFieldNotNullThenPutToMap("title", getTitle(), map);
        ifFieldNotNullThenPutToMap("subject", getSubject(), map);
        ifFieldNotNullThenPutToMap("theme", getTheme(), map);
        ifFieldNotNullThenPutToMap("lastAccessTimestamp", getLastAccessTimestamp(), map);
        ifFieldNotNullThenPutToMap("role", getRole(), map);
        return map;
    }

    public Timestamp getLastAccessTimestamp() {
        return lastAccessTimestamp;
    }

    public void setLastAccessTimestamp(Timestamp lastAccessTimestamp) {
        this.lastAccessTimestamp = lastAccessTimestamp;
    }
}
