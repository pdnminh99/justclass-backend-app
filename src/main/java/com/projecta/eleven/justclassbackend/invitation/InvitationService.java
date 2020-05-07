package com.projecta.eleven.justclassbackend.invitation;

import com.google.cloud.firestore.DocumentReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class InvitationService {

    private final InvitationRepository repository;

    private List<Invitation> invitations = new ArrayList<>();

    @Autowired
    public InvitationService(InvitationRepository repository) {
        this.repository = repository;
    }

    public void addInvitation(Invitation invitation) {
        invitations.add(invitation);
        repository.insert(invitation);
    }

    public void send() {
        invitations.clear();
        repository.send();
    }

    public DocumentReference getInvitationReference(String classroomId, String localId) {
        return repository.getInvitationReference(classroomId, localId);
    }

}
