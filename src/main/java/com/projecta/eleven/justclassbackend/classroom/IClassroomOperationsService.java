package com.projecta.eleven.justclassbackend.classroom;

import com.google.cloud.Timestamp;
import com.projecta.eleven.justclassbackend.user.InvalidUserInformationException;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

public interface IClassroomOperationsService {
    Stream<Classroom> get(String hostId, Boolean joinedClassesOnly, Timestamp lastRequest) throws InvalidUserInformationException;

    Optional<Classroom> create(ClassroomRequestBody classroom, String localId) throws InvalidUserInformationException, InvalidClassroomInformationException, ExecutionException, InterruptedException;

    Optional<Classroom> update(ClassroomRequestBody classroom, String localId);

    Optional<Boolean> delete(String localId, String classroomId);
}
