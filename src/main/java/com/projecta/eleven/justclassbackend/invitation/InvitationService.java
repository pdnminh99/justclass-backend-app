package com.projecta.eleven.justclassbackend.invitation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvitationService {
    private final InvitationRepository repository;
    private List<Invitation> invitations;

    @Autowired
    public InvitationService(InvitationRepository repository) {
        this.repository = repository;
    }


}
