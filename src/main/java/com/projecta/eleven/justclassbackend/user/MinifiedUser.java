package com.projecta.eleven.justclassbackend.user;

import java.util.HashMap;
import java.util.Objects;

public class MinifiedUser {
    private final String localId;

    private String displayName;

    private String photoUrl;

    public MinifiedUser(String localId, String displayName, String photoUrl) {
        this.localId = localId;
        this.displayName = displayName;
        this.photoUrl = photoUrl;
    }

    public String getLocalId() {
        return localId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();

        if (Objects.nonNull(getLocalId())) {
            map.put("localId", getLocalId());
        }
        if (Objects.nonNull(getDisplayName())) {
            map.put("displayName", getDisplayName());
        }
        if (Objects.nonNull(getPhotoUrl())) {
            map.put("photoUrl", getPhotoUrl());
        }
        return map;
    }

    @Override
    public String toString() {
        return "MinifiedUser{" +
                "localId='" + localId + '\'' +
                ", displayName='" + displayName + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                '}';
    }
}
