package com.projecta.eleven.justclassbackend.file;

import com.google.cloud.Timestamp;
import com.projecta.eleven.justclassbackend.utils.MapSerializable;

import java.util.HashMap;

public class BasicFile implements MapSerializable {

    private String fileId;

    private String name;

    private String type;

    private Long size;

    private Timestamp createAt;

    public BasicFile(String fileId, String name, String type, Long size, Timestamp createAt) {
        this.fileId = fileId;
        this.name = name;
        this.type = type;
        this.size = size;
        this.createAt = createAt;
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

    public Timestamp getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Timestamp createAt) {
        this.createAt = createAt;
    }

    @Override
    public HashMap<String, Object> toMap(boolean isTimestampInMilliseconds) {
        var map = new HashMap<String, Object>();

        ifFieldNotNullThenPutToMap("fileId", getFileId(), map);
        ifFieldNotNullThenPutToMap("name", getName(), map);
        ifFieldNotNullThenPutToMap("type", getType(), map);
        ifFieldNotNullThenPutToMap("size", getSize(), map);
        if (getCreateAt() != null) {
            ifFieldNotNullThenPutToMap("createAt", isTimestampInMilliseconds ?
                    getCreateAt().toDate().getTime() :
                    getCreateAt(), map);
        }
        return map;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }
}
