package com.projecta.eleven.justclassbackend.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.cloud.Timestamp;

import java.util.HashMap;
import java.util.Objects;

class UserRequestBody extends MinifiedUser {

    private final String email;

    private final String firstName;

    private final String lastName;

    private final String fullName;

    public UserRequestBody(@JsonProperty("localId") String localId,
                           @JsonProperty("firstName") String firstName,
                           @JsonProperty("lastName") String lastName,
                           @JsonProperty("fullName") String fullName,
                           @JsonProperty("displayName") String displayName,
                           @JsonProperty("photoUrl") String photoUrl,
                           @JsonProperty("email") String email) {
        super(localId, displayName, photoUrl);
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        var firstName = getFirstName();
        var lastName = getLastName();
        if (isValidName(fullName)) {
            return fullName;
        }
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
                getFullName(),
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
        if (Objects.nonNull(getFullName())) {
            map.put("fullName", getFullName());
        }
        if (Objects.nonNull(getEmail())) {
            map.put("email", getEmail());
        }
        return map;
    }

    @Override
    public String toString() {
        return "UserResponseBody{" +
                "email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", fullName='" + fullName + '\'' +
                '}';
    }
}
