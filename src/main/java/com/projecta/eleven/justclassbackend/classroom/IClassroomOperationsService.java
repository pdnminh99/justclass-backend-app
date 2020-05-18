package com.projecta.eleven.justclassbackend.classroom;

import com.google.cloud.Timestamp;
import com.projecta.eleven.justclassbackend.invitation.Invitation;
import com.projecta.eleven.justclassbackend.user.InvalidUserInformationException;
import com.projecta.eleven.justclassbackend.user.MinifiedUser;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

public interface IClassroomOperationsService {
    Stream<Classroom> get(String hostId, MemberRoles role, Timestamp lastRequest) throws InvalidUserInformationException, ExecutionException, InterruptedException;

    Optional<Classroom> get(String localId, String classroomId) throws InvalidUserInformationException, InvalidClassroomInformationException, ExecutionException, InterruptedException;

    Optional<Classroom> create(ClassroomRequestBody classroom, String localId) throws InvalidUserInformationException, InvalidClassroomInformationException, ExecutionException, InterruptedException;

    Optional<Classroom> update(Classroom classroom, String localId, Boolean requestNewPublicCode) throws InvalidClassroomInformationException, InvalidUserInformationException, ExecutionException, InterruptedException;

    Optional<Boolean> delete(String localId, String classroomId) throws InvalidUserInformationException, InvalidClassroomInformationException, ExecutionException, InterruptedException;

    void leave(String localId, String classroomId, String newOwnerId) throws InvalidUserInformationException, InvalidClassroomInformationException, ExecutionException, InterruptedException;

    Stream<MinifiedMember> invite(String localId, String classroomId, List<Invitation> invitations) throws ExecutionException, InterruptedException, InvalidClassroomInformationException, InvalidUserInformationException;

    Optional<Classroom> join(String localId, String publicCode) throws ExecutionException, InterruptedException, InvalidUserInformationException;

    Stream<MinifiedMember> getMembers(String invokerId, String classroomId) throws ExecutionException, InterruptedException, InvalidClassroomInformationException;

    Optional<Member> getMember(String localId, String classroomId) throws ExecutionException, InterruptedException;

    Stream<MinifiedUser> lookUp(String localId, String classroomId, String keyword, MemberRoles role) throws ExecutionException, InterruptedException, InvalidUserInformationException;

    Optional<Classroom> acceptInvitation(String localId, String notificationId) throws ExecutionException, InterruptedException, InvalidUserInformationException;

    void denyInvitation(String localId, String notificationId);

}
