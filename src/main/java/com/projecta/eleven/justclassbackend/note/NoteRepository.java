package com.projecta.eleven.justclassbackend.note;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import com.google.common.collect.Maps;
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

    private Note note;

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

    public List<Note> get(String classroomId, int pageSize, int pageNumber, Timestamp lastRefresh, boolean excludeDeleted) throws ExecutionException, InterruptedException {
        QueryDocumentSnapshot startIndexDoc = getLastDocumentSnapshot(classroomId, pageSize, pageNumber, lastRefresh);
        Query basicQuery = notesCollection
                .whereEqualTo("classroomId", classroomId)
                .whereLessThanOrEqualTo("createdAt", lastRefresh)
                .orderBy("createdAt", Query.Direction.DESCENDING);
        if (pageSize > 0) {
            basicQuery = basicQuery
                    .limit(pageSize);
        }
        if (excludeDeleted) {
            basicQuery = basicQuery
                    .whereEqualTo("deletedAt", null);
        }
        if (startIndexDoc != null) {
            basicQuery = basicQuery
                    .startAfter(startIndexDoc);
        }
        List<QueryDocumentSnapshot> query = basicQuery.get().get().getDocuments();
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

    public Note get(String noteId) throws ExecutionException, InterruptedException {
        DocumentSnapshot snapshot = notesCollection.document(noteId)
                .get()
                .get();
        if (snapshot.exists()) {
            note = new Note(snapshot);
        }
        return note;
    }

    public void flush() {
        writeBatch = null;
        note = null;
    }

    public void delete(Note note) {
        if (note == null || note.getId() == null || note.getId().trim().length() == 0) {
            return;
        }
        Map<String, Object> updateMap = Maps.newHashMap();
        updateMap.put("deletedAt", Timestamp.now());
        updateMap.put("attachmentReferences", null);
        notesCollection.document(note.getId()).update(updateMap);
    }
}
