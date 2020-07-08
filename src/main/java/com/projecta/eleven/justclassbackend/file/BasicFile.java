package com.projecta.eleven.justclassbackend.file;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentSnapshot;
import com.projecta.eleven.justclassbackend.utils.MapSerializable;

import java.util.HashMap;

public class BasicFile implements MapSerializable {

    private String fileId;

    private String name;

    private String type;

    private Long size;

    private Timestamp createdAt;

    private String ownerId;

    private String classroomId;

    public BasicFile(String fileId, String name, String type, Long size, String ownerId, String classroomId, Timestamp createdAt) {
        this.fileId = fileId;
        this.name = name;
        this.type = type;
        this.size = size;
        this.ownerId = ownerId;
        this.classroomId = classroomId;
        this.createdAt = createdAt;
    }

    public BasicFile(DocumentSnapshot m) {
        this.fileId = m.getId();
        this.name = m.getString("name");
        this.type = m.getString("type");
        this.size = m.getLong("size");
        this.ownerId = m.getString("ownerId");
        this.classroomId = m.getString("classroomId");
        this.createdAt = m.getTimestamp("createdAt");
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public HashMap<String, Object> toMap(boolean isTimestampInMilliseconds) {
        var map = new HashMap<String, Object>();

        ifFieldNotNullThenPutToMap("fileId", getFileId(), map);
        ifFieldNotNullThenPutToMap("name", getName(), map);
        ifFieldNotNullThenPutToMap("type", getType(), map);
        ifFieldNotNullThenPutToMap("size", getSize(), map);
        ifFieldNotNullThenPutToMap("classroomId", getClassroomId(), map);
        if (getCreatedAt() != null) {
            ifFieldNotNullThenPutToMap("createdAt", isTimestampInMilliseconds ?
                    getCreatedAt().toDate().getTime() :
                    getCreatedAt(), map);
        }
        ifFieldNotNullThenPutToMap("ownerId", getOwnerId(), map);
        return map;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getClassroomId() {
        return classroomId;
    }

    public void setClassroomId(String classroomId) {
        this.classroomId = classroomId;
    }
}
