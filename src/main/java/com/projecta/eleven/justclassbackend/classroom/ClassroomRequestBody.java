package com.projecta.eleven.justclassbackend.classroom;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentSnapshot;

import java.util.HashMap;

class ClassroomRequestBody extends MinifiedClassroom {
    @JsonProperty("description")
    private String description;

    @JsonProperty("section")
    private String section;

    @JsonProperty("room")
    private String room;

    public ClassroomRequestBody(
            String classroomId,
            String title,
            String description,
            String section,
            String subject,
            String room,
            Integer theme,
            MemberRoles role,
            Timestamp lastAccess,
            Timestamp lastEdit) {
        super(classroomId, title, subject, theme, role, lastAccess, lastEdit);
        this.description = description;
        this.section = section;
        this.room = room;
    }

    public ClassroomRequestBody(DocumentSnapshot snapshot) {
        super(snapshot);
        this.description = snapshot.getString("description");
        this.section = snapshot.getString("section");
        this.room = snapshot.getString("room");
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public Classroom toClassroom(Timestamp createdTimestamp,
                                 NotePermissions studentsNotePermission,
                                 String publicCode) {
        return new Classroom(getClassroomId(),
                getTitle(),
                getDescription(),
                getSection(),
                getSubject(),
                getRoom(),
                getTheme(),
                createdTimestamp,
                getRole(),
                getLastAccess(),
                getLastEdit(),
                studentsNotePermission,
                publicCode
        );
    }

    public Classroom toClassroom(Timestamp createdTimestamp,
                                 NotePermissions studentsNotePermission) {
        return toClassroom(createdTimestamp, studentsNotePermission, null);
    }

    public Classroom toClassroom(Timestamp createdTimestamp) {
        return toClassroom(createdTimestamp, null);
    }

    public Classroom toClassroom() {
        return toClassroom(null);
    }

    @Override
    public HashMap<String, Object> toMap(boolean isTimestampInMilliseconds) {
        var map = super.toMap(isTimestampInMilliseconds);
        ifFieldNotNullThenPutToMap("description", getDescription(), map);
        ifFieldNotNullThenPutToMap("section", getSection(), map);
        ifFieldNotNullThenPutToMap("room", getRoom(), map);
        return map;
    }

}
