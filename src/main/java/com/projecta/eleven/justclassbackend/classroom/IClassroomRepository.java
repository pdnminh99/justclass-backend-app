package com.projecta.eleven.justclassbackend.classroom;

import com.google.cloud.firestore.DocumentReference;

import java.util.Optional;

interface IClassroomRepository {

    Optional<Classroom> create(ClassroomRequestBody classroom, DocumentReference ownerReference);

}
