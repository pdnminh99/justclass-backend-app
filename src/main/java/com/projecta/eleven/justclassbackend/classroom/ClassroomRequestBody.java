package com.projecta.eleven.justclassbackend.classroom;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.cloud.Timestamp;
import com.projecta.eleven.justclassbackend.utils.MapSerializable;

import java.util.HashMap;
import java.util.Objects;

class ClassroomRequestBody implements MapSerializable {
    private String title;

    private String description;

    private String section;

    private String subject;

    private String room;

    private String stream;

    private Integer theme;

    public ClassroomRequestBody(
            @JsonProperty("title") String title,
            @JsonProperty("description") String description,
            @JsonProperty("section") String section,
            @JsonProperty("subject") String subject,
            @JsonProperty("room") String room,
            @JsonProperty("stream") String stream,
            @JsonProperty("theme") Integer theme) {
        this.title = title;
        this.description = description;
        this.section = section;
        this.subject = subject;
        this.room = room;
        setStream(stream);
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

    public Classroom toClassroom(String classroomId, Timestamp createdTimestamp) {
        return new Classroom(classroomId,
                getTitle(),
                getDescription(),
                getSection(),
                getSubject(),
                getRoom(),
                getStream(),
                getTheme(),
                createdTimestamp);
    }

    public HashMap<String, Object> toMap() {
        var map = new HashMap<String, Object>();

        ifFieldNotNullThenPutToMap("title", getTitle(), map);
        ifFieldNotNullThenPutToMap("description", getDescription(), map);
        ifFieldNotNullThenPutToMap("section", getSection(), map);
        ifFieldNotNullThenPutToMap("subject", getSubject(), map);
        ifFieldNotNullThenPutToMap("stream", getStream(), map);
        ifFieldNotNullThenPutToMap("room", getRoom(), map);
        return map;
    }

    @Override
    public String toString() {
        return "ClassroomRequestBody{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", section='" + section + '\'' +
                ", subject='" + subject + '\'' +
                ", room='" + room + '\'' +
                ", stream='" + stream + '\'' +
                ", theme=" + theme +
                '}';
    }

    public String getStream() {
        return stream;
    }

    public void setStream(String stream) {
        if (Objects.isNull(stream) || (stream != "VIEW_COMMENT_POST" && stream != "VIEW_COMMENT" && stream != "VIEW")) {
            this.stream = "VIEW_COMMENT_POST";
        } else this.stream = stream;
    }
}
