package com.projecta.eleven.justclassbackend.invitation;

import com.google.cloud.firestore.CollectionReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class InvitationRepository {
    private final CollectionReference invitationsCollection;

    @Autowired
    public InvitationRepository(
            @Qualifier("invitationsCollection") CollectionReference invitationsCollection) {
        this.invitationsCollection = invitationsCollection;
    }
}
