package com.projecta.eleven.justclassbackend.classroom;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Arrays;
import java.util.Objects;

public enum CollaboratorRoles {
    OWNER("OWNER"),
    TEACHER("TEACHER"),
    STUDENT("STUDENT");

    @JsonIgnore
    private final String role;

    @JsonIgnore
    CollaboratorRoles(String role) {
        this.role = role;
    }

    @JsonCreator
    public static CollaboratorRoles fromText(String text) {
        if (Objects.isNull(text)) {
            return null;
        }
        return CollaboratorRoles.parseRole(text);
    }

    private static CollaboratorRoles parseRole(String text) {
        final String lowerCaseText = text.toLowerCase();

        return Arrays.stream(CollaboratorRoles.values())
                .filter(v -> isRoleMatchesText(v, lowerCaseText))
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
    }

    private static boolean isRoleMatchesText(CollaboratorRoles v, String lowerCaseText) {
        String lowerCaseValue = v.toString().toLowerCase();
        String lowerCaseName = v.name().toLowerCase();

        return lowerCaseText.equals(lowerCaseValue) || lowerCaseText.equals(lowerCaseName);
    }

    @Override
    public String toString() {
        return role;
    }
}
