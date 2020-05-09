package com.projecta.eleven.justclassbackend.classroom;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Arrays;

public enum NotePermissions {
    VIEW_COMMENT_POST("VCP") {
        public boolean allowView() {
            return true;
        }

        public boolean allowComment() {
            return true;
        }

        public boolean allowPost() {
            return true;
        }
    },
    VIEW_COMMENT("VC") {
        public boolean allowView() {
            return true;
        }

        public boolean allowComment() {
            return true;
        }

        public boolean allowPost() {
            return false;
        }
    },
    VIEW("V") {
        public boolean allowView() {
            return true;
        }

        public boolean allowComment() {
            return true;
        }

        public boolean allowPost() {
            return false;
        }
    };

    @JsonIgnore
    private final String rule;

    @JsonIgnore
    NotePermissions(String rule) {
        this.rule = rule;
    }

    @JsonCreator
    public static NotePermissions fromText(String text) {
        if (text == null) {
            return null;
        }
        final String lowerCaseText = text.toLowerCase();

        return Arrays.stream(NotePermissions.values())
                .filter(v -> {
                    String lowerCaseValue = v.toString().toLowerCase();
                    String lowerCaseName = v.name().toLowerCase();

                    return lowerCaseText.equals(lowerCaseValue) || lowerCaseText.equals(lowerCaseName);
                })
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public String toString() {
        return rule;
    }
}