package com.projecta.eleven.justclassbackend.invitation;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteBatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class InvitationRepository {
    private final CollectionReference invitationsCollection;

    private final Firestore firestore;

    private WriteBatch writeBatch;

    @Autowired
    public InvitationRepository(
            Firestore firestore,
            @Qualifier("invitationsCollection") CollectionReference invitationsCollection) {
        this.firestore = firestore;
        this.invitationsCollection = invitationsCollection;
        this.writeBatch = firestore.batch();
    }

    public boolean isBatchActive() {
        return writeBatch != null;
    }

    public void resetWriteBatch() {
        writeBatch = firestore.batch();
    }

    public void insert(Invitation invitation) {
        if (!isBatchActive()) {
            resetWriteBatch();
        }
        String invitationId = invitation.getInvitationId();
        var map = invitation.toMap();
        map.remove("invitationId");
        writeBatch.set(invitationsCollection.document(invitationId), map);
    }

    public void send() {
        if (isBatchActive()) {
            writeBatch.commit();
            writeBatch = null;
        }
    }

    public DocumentReference getInvitationReference(String classroomId, String localId) {
        return invitationsCollection.document(classroomId + "_" + localId);
    }
}
