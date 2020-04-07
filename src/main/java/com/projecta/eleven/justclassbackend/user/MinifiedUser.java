package com.projecta.eleven.justclassbackend.user;

import java.util.Objects;

public class MinifiedUser {
    private final String localId;

    private String displayName;

    private String photoUrl;

    public MinifiedUser(String localId, String displayName, String photoUrl) {
        this.localId = Objects.requireNonNullElseGet(localId,
                () -> {
                    throw new NullPointerException("Initiate UUID for user " + displayName + " should not be NULL.");
                });
        this.displayName = Objects.requireNonNullElse(displayName, "");
        this.photoUrl = Objects.requireNonNullElse(photoUrl, "");
    }

    public String getLocalId() {
        return localId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = Objects.requireNonNullElse(displayName, "");
    }

    public String getPhotoUrl() {
        return photoUrl;
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
