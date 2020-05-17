package com.projecta.eleven.justclassbackend.note;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteBatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Map;

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

    public String getNextId() {
        return notesCollection.document().getId();
    }

    public boolean isBatchActive() {
        return writeBatch != null;
    }

    public void resetBatch() {
        writeBatch = firestore.batch();
    }

    public void createNote(BasicNote note) {
        if (!isBatchActive()) {
            resetBatch();
        }
//        note.setAuthorReference(null);
//        note.setClassroomReference(null);

        String nextId = note.getId();

        if (nextId == null) {
            nextId = getNextId();
            note.setId(nextId);
        }
        Map<String, Object> map = note.toMap();
        map.remove("Id");
        map.remove("attachments");
        map.remove("author");

        writeBatch.set(notesCollection.document(nextId), map);
        note.setId(nextId);
    }

    public void commit() {
        if (isBatchActive()) {
            writeBatch.commit();
            writeBatch = null;
        }
    }
}
