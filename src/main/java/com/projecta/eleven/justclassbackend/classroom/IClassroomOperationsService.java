package com.projecta.eleven.justclassbackend.classroom;

import com.google.cloud.Timestamp;
import com.projecta.eleven.justclassbackend.invitation.Invitation;
import com.projecta.eleven.justclassbackend.user.InvalidUserInformationException;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

public interface IClassroomOperationsService {
    Stream<MinifiedClassroom> get(String hostId, MemberRoles role, Timestamp lastRequest) throws InvalidUserInformationException, ExecutionException, InterruptedException;

    Optional<Classroom> get(String localId, String classroomId) throws InvalidUserInformationException, InvalidClassroomInformationException, ExecutionException, InterruptedException;

    Optional<Classroom> create(ClassroomRequestBody classroom, String localId) throws InvalidUserInformationException, InvalidClassroomInformationException, ExecutionException, InterruptedException;

    Optional<Classroom> update(Classroom classroom, String localId, Boolean requestNewPublicCode) throws InvalidClassroomInformationException, InvalidUserInformationException, ExecutionException, InterruptedException;

    Optional<Boolean> delete(String localId, String classroomId) throws InvalidUserInformationException, InvalidClassroomInformationException, ExecutionException, InterruptedException;

    Stream<MinifiedMember> invite(String localId, String classroomId, Stream<Invitation> invitations);

    Optional<Classroom> join(String localId, String publicCode) throws ExecutionException, InterruptedException, InvalidUserInformationException;
}
