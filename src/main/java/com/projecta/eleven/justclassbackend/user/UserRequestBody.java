package com.projecta.eleven.justclassbackend.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Objects;

class UserRequestBody extends MinifiedUser {

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;

    public UserRequestBody(String localId,
                           String firstName,
                           String lastName,
                           String displayName,
                           String photoUrl,
                           String email) {
        super(localId, displayName, photoUrl, email);
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public UserRequestBody(DocumentSnapshot snapshot) {
        super(snapshot);
        this.firstName = snapshot.getString("firstName");
        this.lastName = snapshot.getString("lastName");
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        if (isValidName(firstName) && isValidName(lastName)) {
            return firstName + " " + lastName;
        }
        if (isValidName(firstName)) {
            return firstName;
        }
        if (isValidName(lastName)) {
            return lastName;
        }
        return null;
    }

    private boolean isValidName(String name) {
        return Objects.nonNull(name) && name.trim().length() != 0;
    }

    public User toUser(Timestamp assignTimestamp, boolean isNewUser) {
        return new User(
                getLocalId(),
                getFirstName(),
                getLastName(),
                getDisplayName(),
                getPhotoUrl(),
                getEmail(),
                assignTimestamp,
                isNewUser
        );
    }

    @Override
    public HashMap<String, Object> toMap() {
        HashMap<String, Object> map = super.toMap();

        if (Objects.nonNull(getFirstName())) {
            map.put("firstName", getFirstName());
        }
        if (Objects.nonNull(getLastName())) {
            map.put("lastName", getLastName());
        }
        return map;
    }
}
