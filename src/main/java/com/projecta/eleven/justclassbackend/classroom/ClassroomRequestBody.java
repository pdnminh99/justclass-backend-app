package com.projecta.eleven.justclassbackend.classroom;

import com.fasterxml.jackson.annotation.JsonProperty;

class ClassroomRequestBody {
    private String title;
    private String description;
    private String section;
    private String subject;
    private String room;
    private Integer theme;

    public ClassroomRequestBody(
            @JsonProperty("title") String title,
            @JsonProperty("description") String description,
            @JsonProperty("section") String section,
            @JsonProperty("subject") String subject,
            @JsonProperty("room") String room,
            @JsonProperty("theme") Integer theme) {
        this.title = title;
        this.description = description;
        this.section = section;
        this.subject = subject;
        this.room = room;
        this.theme = theme;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public Integer getTheme() {
        return theme;
    }

    public void setTheme(Integer theme) {
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
