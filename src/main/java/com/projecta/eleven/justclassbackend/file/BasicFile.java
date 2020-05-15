package com.projecta.eleven.justclassbackend.file;

import com.google.cloud.Timestamp;
import com.projecta.eleven.justclassbackend.utils.MapSerializable;

import java.util.HashMap;

public class BasicFile implements MapSerializable {

    private String Id;

    private String name;

    private String type;

    private Timestamp createAt;

    public BasicFile(String Id, String name, String type, Timestamp createAt) {
        this.Id = Id;
        this.name = name;
        this.type = type;
        this.createAt = createAt;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
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

        ifFieldNotNullThenPutToMap("fileId", getId(), map);
        ifFieldNotNullThenPutToMap("type", getType(), map);
        if (getCreateAt() != null) {
            ifFieldNotNullThenPutToMap("createAt", isTimestampInMilliseconds ?
                    getCreateAt().toDate().getTime() :
                    getCreateAt(), map);
        }
        return map;
    }
}
