package com.projecta.eleven.justclassbackend.classroom;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

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
}
