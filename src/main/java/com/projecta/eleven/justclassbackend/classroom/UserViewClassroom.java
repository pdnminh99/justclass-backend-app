package com.projecta.eleven.justclassbackend.classroom;

import com.google.cloud.Timestamp;

import java.util.HashMap;

class UserViewClassroom extends Classroom {
    private CollaboratorRoles role;

    public UserViewClassroom(String classroomId,
                             String title,
                             String description,
                             String section,
                             String subject,
                             String room,
                             String stream,
                             Integer theme,
                             Timestamp createdTimestamp,
                             CollaboratorRoles role) {
        super(classroomId, title, description, section, subject, room, stream, theme, createdTimestamp);
        this.role = role;
    }

    public CollaboratorRoles getRole() {
        return role;
    }

    public void setRoles(CollaboratorRoles role) {
        this.role = role;
    }

    @Override
    public HashMap<String, Object> toMap() {
        var map = super.toMap();
        ifFieldNotNullThenPutToMap("role", getRole().toString(), map);
        return map;
    }
}
