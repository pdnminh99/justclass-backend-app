package com.projecta.eleven.justclassbackend.invitation;

public enum InvitationStatus {
    PENDING("PENDING"),
    ACCEPTED("ACCEPTED"),
    DENIED("DENIED");

    private final String status;

    InvitationStatus(String status) {
        this.status = status;
    }

    public static InvitationStatus fromText(String status) {
        switch (status) {
            case "PENDING":
                return InvitationStatus.PENDING;
            case "ACCEPTED":
                return InvitationStatus.ACCEPTED;
            case "DENIED":
                return InvitationStatus.DENIED;
            default:
                return null;
        }
    }
}
