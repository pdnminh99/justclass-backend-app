package com.projecta.eleven.justclassbackend.classroom;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Objects;

@JsonIgnoreProperties(value = {
        "publicCode", "createdTimestamp", "role", "lastAccess",
        "owner", "studentsCount", "collaboratorsCount", "lastEdit"})
public class Classroom extends ClassroomRequestBody {

    @JsonIgnore
    private Timestamp createdTimestamp;

    @JsonProperty("studentsNotePermission")
    private NotePermissions studentsNotePermission;

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
                     MemberRoles role,
                     Timestamp lastAccess,
                     Timestamp lastEdit,
                     NotePermissions studentsNotePermission,
                     String publicCode) {
        super(classroomId, title, description, section, subject, room, theme, role, lastAccess, lastEdit);
        this.createdTimestamp = createdTimestamp;
        this.studentsNotePermission = studentsNotePermission;
        this.publicCode = publicCode;
    }

    public Classroom(DocumentSnapshot snapshot) {
        super(snapshot);
        this.createdTimestamp = snapshot.getTimestamp("createdTimestamp");
        this.studentsNotePermission = NotePermissions.fromText(snapshot.getString("studentsNotePermission"));
        this.publicCode = snapshot.getString("publicCode");
    }

    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public NotePermissions getStudentsNotePermission() {
        return studentsNotePermission;
    }

    public void setStudentsNotePermission(NotePermissions studentsNotePermission) {
        this.studentsNotePermission = studentsNotePermission;
    }

    public String getPublicCode() {
        return publicCode;
    }

    public void setPublicCode(String publicCode) {
        this.publicCode = publicCode;
    }

    @Override
    public HashMap<String, Object> toMap(boolean isTimestampInMilliseconds) {
        var map = super.toMap(isTimestampInMilliseconds);

        ifFieldNotNullThenPutToMap("createdTimestamp",
                isTimestampInMilliseconds && createdTimestamp != null ?
                        createdTimestamp.toDate().getTime() :
                        createdTimestamp
                , map);
        ifFieldNotNullThenPutToMap("publicCode", publicCode, map);
        if (Objects.nonNull(studentsNotePermission)) {
            ifFieldNotNullThenPutToMap("studentsNotePermission", studentsNotePermission.toString(), map);
        }
        return map;
    }

    @Override
    public String toString() {
        return "Classroom{" +
                "createdTimestamp=" + createdTimestamp +
                ", studentsNotePermission=" + studentsNotePermission +
                ", publicCode='" + publicCode + '\'' +
                '}';
    }
}
