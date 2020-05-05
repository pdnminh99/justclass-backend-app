package com.projecta.eleven.justclassbackend.notification;

public enum NotificationType {
    INVITATION("INVITATION"),
    ROLE_CHANGE("ROLE_CHANGE"),
    CLASSROOM_DELETED("CLASSROOM_DELETED"),
    CLASSROOM_INFO_CHANGED("CLASSROOM_INFO_CHANGED");

    private final String notify;

    NotificationType(String notify) {
        this.notify = notify;
    }

    public static NotificationType fromText(String text) {
        switch (text) {
            case "INVITATION":
                return NotificationType.INVITATION;
            case "PROMOTION":
                return NotificationType.ROLE_CHANGE;
            case "CLASSROOM_DELETED":
                return NotificationType.CLASSROOM_DELETED;
            case "CLASSROOM_INFO_CHANGED":
                return NotificationType.CLASSROOM_INFO_CHANGED;
            default:
                return null;
        }
    }

    @Override
    public String toString() {
        return notify;
    }
}
