package com.projecta.eleven.justclassbackend.note;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Repository
public class NoteRepository {

    private final Firestore firestore;

    private final CollectionReference notesCollection;

    private final CollectionReference commentsCollection;

    private final DocumentReference systemsCollection;

    private WriteBatch writeBatch;

    private Note note;

    private List<Comment> comments;

    @Autowired
    NoteRepository(Firestore firestore,
                   @Qualifier("notesCollection") CollectionReference notesCollection,
                   @Qualifier("commentsCollection") CollectionReference commentsCollection,
                   @Qualifier("systemsCollection") DocumentReference systemsCollection) {
        this.firestore = firestore;
        this.notesCollection = notesCollection;
        this.commentsCollection = commentsCollection;
        this.systemsCollection = systemsCollection;
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
        map.remove("noteId");
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

    public void delete(Note note) {
        if (note == null || note.getId() == null || note.getId().trim().length() == 0) {
            return;
        }
        Map<String, Object> updateMap = Maps.newHashMap();
        updateMap.put("deletedAt", Timestamp.now());
        updateMap.put("attachmentReferences", null);
        notesCollection.document(note.getId()).update(updateMap);
    }

    public void deleteByClassroom(String classroomId) throws ExecutionException, InterruptedException {
        if (!isBatchActive()) {
            resetBatch();
        }
        notesCollection.whereEqualTo("classroomId", classroomId)
                .get()
                .get()
                .getDocuments()
                .forEach(note -> writeBatch.delete(note.getReference()));
        commentsCollection.whereEqualTo("classroomId", classroomId)
                .get()
                .get()
                .getDocuments()
                .forEach(comment -> writeBatch.delete(comment.getReference()));
    }

    public void update(Note note) {
        Map<String, Object> map = note.toMap();
        map.remove("noteId");
        map.remove("attachments");
        map.remove("author");
        if (note.getAttachmentReferences() != null && note.getAttachmentReferences().size() == 0) {
            map.put("attachmentReferences", null);
        }
        notesCollection.document(note.getId())
                .update(map);
    }

    public void flush() {
        writeBatch = null;
        note = null;
        comments = null;
    }

    public void createComment(Comment comment) {
        comment.setCommentId(commentsCollection.document().getId());

        commentsCollection.document(comment.getCommentId())
                .set(comment);
    }

    public List<Comment> getComments(String noteId) throws ExecutionException, InterruptedException {
        comments = commentsCollection.whereEqualTo("noteId", noteId)
                .get()
                .get()
                .getDocuments()
                .stream()
                .map(m -> m.toObject(Comment.class))
                .collect(Collectors.toList());
        return comments;
    }

    public Comment getComment(String commentId) throws ExecutionException, InterruptedException {
        Comment comment = commentsCollection.document(commentId)
                .get()
                .get()
                .toObject(Comment.class);
        if (comment == null) {
            return null;
        }
        if (comments != null) {
            comments.add(comment);
        } else comments = Lists.newArrayList(comment);
        return comment;
    }

    public void deleteComment(String commentId) throws ExecutionException, InterruptedException {
        Comment comment = comments.stream()
                .filter(m -> m.getCommentId().equals(commentId))
                .findFirst()
                .orElse(getComment(commentId));
        Note note = get(comment.getNoteId());
        assert note != null;
        assert note.getCommentsCount() != null;

        int commentsCount = note.getCommentsCount();
        commentsCount -= 1;

        Map<String, Object> updateMap = Maps.newHashMap();
        updateMap.put("commentsCount", commentsCount);

        notesCollection.document(note.getId())
                .update(updateMap);
        commentsCollection.document(commentId)
                .delete();
    }

    public void removeDeletedNotesBefore(Timestamp oneDayBefore) throws ExecutionException, InterruptedException {
        if (!isBatchActive()) {
            resetBatch();
        }
        List<QueryDocumentSnapshot> a = notesCollection
                .whereLessThanOrEqualTo("deletedAt", oneDayBefore)
                .get()
                .get()
                .getDocuments();
        List<String> markedNIDs = a.stream().map(DocumentSnapshot::getId).collect(Collectors.toList());

        if (!markedNIDs.isEmpty()) {
            boolean isDone = false;
            List<String> nextNotesToRemove = Lists.newArrayList();

            while (!isDone) {
                nextNotesToRemove.clear();
                int count = 0;
                while (count < Math.min(markedNIDs.size(), 10)) {
                    String item = markedNIDs.get(0);
                    nextNotesToRemove.add(item);
                    markedNIDs.remove(0);
                    count++;
                }
                commentsCollection.whereIn("noteId", nextNotesToRemove).get().get()
                        .getDocuments()
                        .stream()
                        .map(DocumentSnapshot::getReference)
                        .forEach(d -> writeBatch.delete(d));
                if (markedNIDs.size() == 0) {
                    isDone = true;
                }
            }
        }
        a.stream().map(DocumentSnapshot::getReference).forEach(doc -> writeBatch.delete(doc));
        Map<String, Object> updateMap = Maps.newHashMap();
        updateMap.put("notesRefreshAt", Timestamp.now());
        writeBatch.set(systemsCollection, updateMap);
        commit();
    }
}
