package com.projecta.eleven.justclassbackend.user;

import com.google.cloud.Timestamp;

import java.util.HashMap;
import java.util.Objects;

public class User extends UserRequestBody {

    private boolean isNewUser;

    private Timestamp assignTimestamp;

    public User(String localId,
                String firstName,
                String lastName,
                String fullName,
                String displayName,
                String photoUrl,
                String email,
                Timestamp assignTimestamp,
                boolean isNewUser) {
        super(localId, firstName, lastName, fullName, displayName, photoUrl, email);
        this.isNewUser = isNewUser;
        this.assignTimestamp = assignTimestamp;
    }

    public boolean isNewUser() {
        return isNewUser;
    }

    public void setIsNewUser(boolean state) {
        isNewUser = state;
    }

    public Timestamp getAssignTimestamp() {
        return assignTimestamp;
    }

    @Override
    public HashMap<String, Object> toMap() {
        HashMap<String, Object> map = super.toMap();
        if (Objects.nonNull(getAssignTimestamp())) {
            map.put("assignTimestamp", getAssignTimestamp());
        }
        map.put("isNewUser", isNewUser());
        return map;
    }
}
