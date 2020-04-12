package com.projecta.eleven.justclassbackend.classroom;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.projecta.eleven.justclassbackend.utils.MapSerializable;

import java.util.HashMap;

@JsonIgnoreProperties("role")
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

    public MinifiedClassroom(String classroomId,
                             String title,
                             String subject,
                             Integer theme,
                             CollaboratorRoles role) {
        this.classroomId = classroomId;
        this.title = title;
        this.subject = subject;
        this.theme = theme;
        this.role = role;
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
        ifFieldNotNullThenPutToMap("role", getRole(), map);
        return map;
    }
}
