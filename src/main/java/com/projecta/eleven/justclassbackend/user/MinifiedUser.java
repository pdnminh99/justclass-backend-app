package com.projecta.eleven.justclassbackend.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.cloud.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Objects;

public class MinifiedUser {

    @JsonProperty("localId")
    private final String localId;

    @JsonProperty("displayName")
    private String displayName;

    @JsonProperty("photoUrl")
    private String photoUrl;

    @JsonProperty("email")
    private String email;

    public MinifiedUser(String localId, String displayName, String photoUrl, String email) {
        this.localId = localId;
        this.displayName = displayName;
        this.photoUrl = photoUrl;
        this.email = email;
    }

    public MinifiedUser(DocumentSnapshot snapshot) {
        this.localId = snapshot.getId();
        this.displayName = snapshot.getString("displayName");
        this.photoUrl = snapshot.getString("photoUrl");
        this.email = snapshot.getString("email");
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

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public HashMap<String, Object> toMap() {
        var map = new HashMap<String, Object>();

        if (Objects.nonNull(getLocalId())) {
            map.put("localId", getLocalId());
        }
        if (Objects.nonNull(getDisplayName())) {
            map.put("displayName", getDisplayName());
        }
        if (Objects.nonNull(getPhotoUrl())) {
            map.put("photoUrl", getPhotoUrl());
        }
        if (Objects.nonNull(getEmail())) {
            map.put("email", getEmail());
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
