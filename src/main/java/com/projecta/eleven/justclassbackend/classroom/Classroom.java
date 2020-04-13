package com.projecta.eleven.justclassbackend.classroom;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.cloud.Timestamp;

import java.util.HashMap;

@JsonIgnoreProperties(value = {"publicCode"})
public class Classroom extends ClassroomRequestBody {
    private Timestamp createdTimestamp;

    //    @JsonIgnore
    @JsonProperty("notePermissions")
    private NotePermissions notePermissions;
    @JsonIgnore
    private String publicCode;

    public Classroom(String classroomId,
                     String title,
                     String description,
                     String section,
                     String subject,
                     String room,
                     Integer theme,
                     Timestamp createdTimestamp,
                     CollaboratorRoles role,
                     NotePermissions notePermissions,
                     String publicCode) {
        super(classroomId, title, description, section, subject, room, theme, role);
        this.createdTimestamp = createdTimestamp;
        this.notePermissions = notePermissions;
        this.publicCode = publicCode;
    }

    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public NotePermissions getNotePermissions() {
        return notePermissions;
    }

    public void setNotePermissions(NotePermissions notePermissions) {
        this.notePermissions = notePermissions;
    }

    public String getPublicCode() {
        return publicCode;
    }

    public void setPublicCode(String publicCode) {
        this.publicCode = publicCode;
    }

    @Override
    public HashMap<String, Object> toMap() {
        var map = super.toMap();

        ifFieldNotNullThenPutToMap("createdTimestamp", createdTimestamp, map);
        ifFieldNotNullThenPutToMap("publicCode", publicCode, map);
        ifFieldNotNullThenPutToMap("studentsNotePermission", notePermissions, map);
        return map;
    }

    @Override
    public String toString() {
        return "Classroom{" +
                "createdTimestamp=" + createdTimestamp +
                ", studentsNotePermission=" + notePermissions +
                ", publicCode='" + publicCode + '\'' +
                '}';
    }
}
