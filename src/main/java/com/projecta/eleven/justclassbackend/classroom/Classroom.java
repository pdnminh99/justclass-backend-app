package com.projecta.eleven.justclassbackend.classroom;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.cloud.Timestamp;

import java.util.HashMap;
import java.util.Objects;

@JsonIgnoreProperties(value = {"publicCode"})
public class Classroom extends ClassroomRequestBody {

    @JsonProperty("createdTimestamp")
    private Timestamp createdTimestamp;

    @JsonProperty("notePermission")
    private NotePermissions notePermission;

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
                     NotePermissions notePermission,
                     String publicCode) {
        super(classroomId, title, description, section, subject, room, theme, role);
        this.createdTimestamp = createdTimestamp;
        this.notePermission = notePermission;
        this.publicCode = publicCode;
    }

    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public NotePermissions getNotePermission() {
        return notePermission;
    }

    public void setNotePermission(NotePermissions notePermission) {
        this.notePermission = notePermission;
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
        if (Objects.nonNull(notePermission)) {
            ifFieldNotNullThenPutToMap("studentsNotePermission", notePermission.toString(), map);
        }
        return map;
    }

    @Override
    public String toString() {
        return "Classroom{" +
                "createdTimestamp=" + createdTimestamp +
                ", studentsNotePermission=" + notePermission +
                ", publicCode='" + publicCode + '\'' +
                '}';
    }
}
