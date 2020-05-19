package com.projecta.eleven.justclassbackend.note;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

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

    public void createNote(Note note) {
        if (!isBatchActive()) {
            resetBatch();
        }
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

    public List<Note> get(String classroomId, int pageSize, int pageNumber, Timestamp lastRefresh) throws ExecutionException, InterruptedException {
        QueryDocumentSnapshot startIndexDoc = getLastDocumentSnapshot(classroomId, pageSize, pageNumber, lastRefresh);
        List<QueryDocumentSnapshot> query = startIndexDoc == null ?
                notesCollection
                        .whereEqualTo("classroomId", classroomId)
                        .whereLessThanOrEqualTo("createdAt", lastRefresh)
                        .orderBy("createdAt", Query.Direction.DESCENDING)
                        .limit(pageSize)
                        .get()
                        .get()
                        .getDocuments() :
                notesCollection
                        .whereEqualTo("classroomId", classroomId)
                        .whereLessThanOrEqualTo("createdAt", lastRefresh)
                        .orderBy("createdAt", Query.Direction.DESCENDING)
                        .startAfter(startIndexDoc)
                        .limit(pageSize)
                        .get()
                        .get()
                        .getDocuments();
        return query.stream()
                .map(Note::new)
                .collect(Collectors.toList());
    }

    private QueryDocumentSnapshot getLastDocumentSnapshot(String classroomId, int pageSize, int pageNumber, Timestamp lastRefresh) throws ExecutionException, InterruptedException {
        if (pageNumber < 1) {
            return null;
        }
        QuerySnapshot querySnapshot = notesCollection
                .whereLessThanOrEqualTo("createdAt", lastRefresh)
                .whereEqualTo("classroomId", classroomId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(pageNumber * pageSize)
                .get()
                .get();
        return querySnapshot.getDocuments()
                .get(querySnapshot.size() - 1);
    }
}
