package com.projecta.eleven.justclassbackend.note;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NoteService {

    private final NoteRepository repository;

    @Autowired
    public NoteService(NoteRepository repository) {
        this.repository = repository;
    }
}
