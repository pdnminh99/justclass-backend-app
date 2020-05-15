package com.projecta.eleven.justclassbackend.note;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteBatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
class NoteRepository {

    private final Firestore firestore;
    private final CollectionReference notesCollection;
    private WriteBatch writeBatch;

    @Autowired
    NoteRepository(Firestore firestore, CollectionReference notesCollection) {
        this.firestore = firestore;
        this.notesCollection = notesCollection;
    }


}
