package com.projecta.eleven.justclassbackend.classroom;

import com.google.cloud.Timestamp;

import java.util.HashMap;

public class Classroom extends ClassroomRequestBody {
    private Timestamp createdTimestamp;

    public Classroom(String classroomId,
                     String title,
                     String description,
                     String section,
                     String subject,
                     String room,
                     Integer theme,
                     Timestamp createdTimestamp,
                     CollaboratorRoles role) {
        super(classroomId, title, description, section, subject, room, theme, role);
        this.createdTimestamp = createdTimestamp;
    }

    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    @Override
    public HashMap<String, Object> toMap() {
        var map = super.toMap();

        ifFieldNotNullThenPutToMap("createdTimestamp", createdTimestamp, map);
        return map;
    }
}
