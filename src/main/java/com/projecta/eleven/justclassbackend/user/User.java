package com.projecta.eleven.justclassbackend.user;

import java.time.LocalDateTime;

public class User extends UserResponseBody {

    private final LocalDateTime assignDatetime;

    private boolean isNewUser;

    public User(String localId,
                String firstName,
                String lastName,
                String fullName,
                String displayName,
                String photoUrl,
                String email,
                LocalDateTime assignDatetime,
                boolean isNewUser) {
        super(localId, firstName, lastName, fullName, displayName, photoUrl, email);
        this.assignDatetime = assignDatetime;
        this.isNewUser = isNewUser;
    }

    public LocalDateTime getAssignDatetime() {
        return assignDatetime;
    }

    public boolean isNewUser() {
        return isNewUser;
    }

    public void setIsNewUser(boolean state) {
        isNewUser = state;
    }
}
