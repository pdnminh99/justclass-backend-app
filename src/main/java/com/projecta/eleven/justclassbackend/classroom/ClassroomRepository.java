package com.projecta.eleven.justclassbackend.classroom;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
class ClassroomRepository implements IClassroomRepository {

    private final CollectionReference classroomCollection;

    @Autowired
    ClassroomRepository(@Qualifier("classroomCollection") CollectionReference classroomCollection) {
        this.classroomCollection = classroomCollection;
    }

    @Override
    public Optional<Classroom> create(ClassroomRequestBody classroom, DocumentReference ownerReference) {
        return Optional.empty();
    }
}
