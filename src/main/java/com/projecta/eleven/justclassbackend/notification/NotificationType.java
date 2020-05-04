package com.projecta.eleven.justclassbackend.notification;

public enum NotificationType {
    INVITATION("INVITATION"),
    ROLE_CHANGE("ROLE_CHANGE");

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
            default:
                return null;
        }
    }

    @Override
    public String toString() {
        return notify;
    }
}
