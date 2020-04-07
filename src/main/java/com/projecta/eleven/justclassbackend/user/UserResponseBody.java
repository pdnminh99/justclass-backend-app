package com.projecta.eleven.justclassbackend.user;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

class UserResponseBody extends MinifiedUser {

    private final String email;

    private final String firstName;

    private final String lastName;

    private final String fullName;

    public UserResponseBody(@JsonProperty("localId") String localId,
                            @JsonProperty("firstName") String firstName,
                            @JsonProperty("lastName") String lastName,
                            @JsonProperty("fullName") String fullName,
                            @JsonProperty("displayName") String displayName,
                            @JsonProperty("photoUrl") String photoUrl,
                            @JsonProperty("email") String email) {
        super(localId, displayName, photoUrl);
        this.email = Objects.requireNonNullElse(email, "");
        this.firstName = Objects.requireNonNullElse(firstName, "");
        this.lastName = Objects.requireNonNullElse(lastName, "");
        this.fullName = Objects.requireNonNullElse(fullName, "");
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
        return fullName.length() == 0 ? firstName + " " + lastName : fullName;
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
