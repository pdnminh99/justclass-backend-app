package com.projecta.eleven.justclassbackend.user;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;

class FriendReference {
    private final String connectionId;
    private final DocumentReference hostReference;
    private final String hostId;

    private final DocumentReference guestReference;
    private final String guestId;

    private final Timestamp datetime;
    private final Timestamp lastAccess;

    public FriendReference(
            String connectionId,
            String hostId,
            DocumentReference hostReference,
            String guestId,
            DocumentReference guestReference,
            Timestamp datetime,
            Timestamp lastAccess) {
        this.hostReference = hostReference;
        this.connectionId = connectionId;
        this.hostId = hostId;
        this.guestReference = guestReference;
        this.guestId = guestId;
        this.datetime = datetime;
        this.lastAccess = lastAccess;
    }

    public FriendReference(DocumentSnapshot snapshot) {
        this.connectionId = snapshot.getId();
        this.hostId = snapshot.getString("hostId");
        this.hostReference = snapshot.get("hostReference", DocumentReference.class);
        this.guestId = snapshot.getString("guestId");
        this.guestReference = snapshot.get("guestReference", DocumentReference.class);
        this.datetime = snapshot.getTimestamp("datetime");
        this.lastAccess = snapshot.getTimestamp("lastAccess");
    }

    public String getConnectionId() {
        return connectionId;
    }

    public String getHostId() {
        return hostId;
    }

    public String getGuestId() {
        return guestId;
    }

    public Timestamp getDatetime() {
        return datetime;
    }

    public Timestamp getLastAccess() {
        return lastAccess;
    }

    @Override
    public String toString() {
        return "FriendReference{" +
                "connectionId='" + connectionId + '\'' +
                ", hostId='" + hostId + '\'' +
                ", guestId='" + guestId + '\'' +
                ", datetime=" + datetime +
                ", recentAccess=" + lastAccess +
                '}';
    }

    public DocumentReference getHostReference() {
        return hostReference;
    }

    public DocumentReference getGuestReference() {
        return guestReference;
    }
}

