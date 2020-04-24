package com.projecta.eleven.justclassbackend.classroom;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Arrays;
import java.util.Objects;

public enum MemberRoles {
    OWNER("OWNER"),
    COLLABORATOR("COLLABORATOR"),
    STUDENT("STUDENT");

    @JsonIgnore
    private final String role;

    @JsonIgnore
    MemberRoles(String role) {
        this.role = role;
    }

    @JsonCreator
    public static MemberRoles fromText(String text) {
        if (Objects.isNull(text)) {
            return null;
        }
        return MemberRoles.parseRole(text);
    }

    private static MemberRoles parseRole(String text) {
        final String lowerCaseText = text.toLowerCase();

        return Arrays.stream(MemberRoles.values())
                .filter(v -> isRoleMatchesText(v, lowerCaseText))
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
    }

    private static boolean isRoleMatchesText(MemberRoles v, String lowerCaseText) {
        String lowerCaseValue = v.toString().toLowerCase();
        String lowerCaseName = v.name().toLowerCase();

        return lowerCaseText.equals(lowerCaseValue) || lowerCaseText.equals(lowerCaseName);
    }

    @Override
    public String toString() {
        return role;
    }
}
