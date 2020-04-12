package com.projecta.eleven.justclassbackend.classroom;

import com.google.cloud.Timestamp;

public class Classroom extends ClassroomRequestBody {
    private final String classroomId;

    private Timestamp createdTimestamp;

    public Classroom(String classroomId,
                     String title,
                     String description,
                     String section,
                     String subject,
                     String room,
                     Integer theme,
                     Timestamp createdTimestamp) {
        super(title, description, section, subject, room, theme);
        this.classroomId = classroomId;
        this.createdTimestamp = createdTimestamp;
    }

    public String getClassroomId() {
        return classroomId;
    }

    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }
}
