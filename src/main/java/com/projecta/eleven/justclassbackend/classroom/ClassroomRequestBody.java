package com.projecta.eleven.justclassbackend.classroom;

import com.fasterxml.jackson.annotation.JsonProperty;

class ClassroomRequestBody {
    public Integer theme;
    private String description;
    private String section;
    private String subject;
    private String room;

    public ClassroomRequestBody(@JsonProperty("description") String description,
                                @JsonProperty("section") String section,
                                @JsonProperty("subject") String subject,
                                @JsonProperty("room") String room,
                                @JsonProperty("theme") Integer theme) {
        this.description = description;
        this.section = section;
        this.subject = subject;
        this.room = room;
        this.theme = theme;
    }

    @Override
    public String toString() {
        return "ClassroomRequestBody{" +
                "description='" + description + '\'' +
                ", section='" + section + '\'' +
                ", subject='" + subject + '\'' +
                ", room='" + room + '\'' +
                ", theme=" + theme +
                '}';
    }
}
