package com.projecta.eleven.justclassbackend.classroom;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

@Repository
class ClassroomRepository implements IClassroomRepository {

    private final CollectionReference classroomCollection;

    private final CollectionReference collaboratorCollection;

    @Autowired
    ClassroomRepository(@Qualifier("classroomCollection") CollectionReference classroomCollection,
                        @Qualifier("collaboratorCollection") CollectionReference collaboratorCollection) {
        this.classroomCollection = classroomCollection;
        this.collaboratorCollection = collaboratorCollection;
    }

    @Override
    public DocumentReference getClassroom(String classroomId) {
        return classroomCollection.document(classroomId);
    }

    @Override
    public DocumentReference createClassroom(Map<String, Object> classroomMap)
            throws ExecutionException, InterruptedException, InvalidClassroomInformationException {
        if (Objects.isNull(classroomMap)) {
            throw new InvalidClassroomInformationException("Parameter `classroomMap` (type of Classroom) for `create` method is null.", new NullPointerException("Classroom instance is null."));
        }
        return classroomCollection
                .add(classroomMap)
                .get();
    }

    @Override
    public DocumentReference updateClassroom(Map<String, Object> classroom, String classroomId)
            throws ExecutionException, InterruptedException {
        var classroomReference = classroomCollection.document(classroomId);
        classroomReference.update(classroom);
        return classroomReference;
    }

    @Override
    public DocumentReference createCollaborator(HashMap<String, Object> collaboratorMap, String keyCombination)
            throws ExecutionException, InterruptedException {
        var reference = collaboratorCollection
                .document(keyCombination);
        // if the collaborator already existed, returns null.
        if (reference.get().get().exists()) {
            return null;
        }
        reference.set(collaboratorMap);
        return reference;
    }

    @Override
    public DocumentReference getCollaborator(String classroomId, String localId) {
        if (Objects.isNull(classroomId) || Objects.isNull(localId)) {
            return null;
        }
        return collaboratorCollection.document(classroomId + localId);
    }

    @Override
    public Stream<DocumentReference> getCollaboratorsByClassroom(String classroomId)
            throws ExecutionException, InterruptedException {
        return collaboratorCollection
                .whereEqualTo("classroomId", classroomId)
                .get()
                .get()
                .getDocuments()
                .stream()
                .map(DocumentSnapshot::getReference);
    }

    @Override
    public Stream<QueryDocumentSnapshot> getCollaboratorsByUser(String hostId, CollaboratorRoles role, Timestamp lastRequest) throws ExecutionException, InterruptedException {
//        System.out.println(role);
        if (Objects.isNull(hostId) || hostId.trim().length() == 0) {
            return Stream.empty();
        }
        if (Objects.isNull(role) && Objects.isNull(lastRequest)) {
            return collaboratorCollection.whereEqualTo("userId", hostId)
                    .orderBy("lastAccessTimestamp", Query.Direction.DESCENDING)
                    .get()
                    .get()
                    .getDocuments()
                    .stream();
        }
        if (Objects.isNull(role)) {
            return collaboratorCollection.whereEqualTo("userId", hostId)
                    .orderBy("lastAccessTimestamp", Query.Direction.DESCENDING)
                    .whereGreaterThanOrEqualTo("lastAccessTimestamp", lastRequest)
                    .get()
                    .get()
                    .getDocuments()
                    .stream();
        } else if (Objects.isNull(lastRequest)) {
            return collaboratorCollection.whereEqualTo("userId", hostId)
                    .whereEqualTo("role", role.toString())
                    .get()
                    .get()
                    .getDocuments()
                    .stream();
        } else return collaboratorCollection.whereEqualTo("userId", hostId)
                .orderBy("lastAccessTimestamp", Query.Direction.DESCENDING)
                .whereGreaterThanOrEqualTo("lastAccessTimestamp", lastRequest)
                .whereEqualTo("role", role.toString())
                .get()
                .get()
                .getDocuments()
                .stream();
    }

//    @Override
//    public DocumentReference updateCollaborator(String key, HashMap<String, Object> map) {
//        var ref = collaboratorCollection.document(key);
//        ref.update(map);
//        return ref;
//    }
}
