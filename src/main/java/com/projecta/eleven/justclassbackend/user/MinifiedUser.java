package com.projecta.eleven.justclassbackend.user;

import java.util.Objects;

public class MinifiedUser {
    private final String uuid;

    private String name;

    private String photoURL;

    public MinifiedUser(String uuid, String name, String photoURL) {
        this.uuid = Objects.requireNonNullElseGet(uuid,
                () -> {
                    throw new NullPointerException("Initiate UUID for user " + name + " should not be NULL.");
                });
        this.name = Objects.requireNonNullElse(name, "");
        this.photoURL = Objects.requireNonNullElse(photoURL, "");
    }

    public String getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = Objects.requireNonNullElse(name, "");
    }

    public String getPhotoURL() {
        return photoURL;
    }
}
