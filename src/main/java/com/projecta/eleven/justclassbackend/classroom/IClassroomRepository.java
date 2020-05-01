package com.projecta.eleven.justclassbackend.classroom;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

interface IClassroomRepository {

    DocumentReference createClassroom(Classroom classroom) throws ExecutionException, InterruptedException, InvalidClassroomInformationException;

    DocumentReference getClassroom(String classroomId);

    Optional<DocumentReference> getClassroomByPublicCode(String publicCode) throws ExecutionException, InterruptedException;

    DocumentReference updateClassroom(Map<String, Object> classroom, String classroomId) throws ExecutionException, InterruptedException;

    DocumentReference createMember(Member member) throws ExecutionException, InterruptedException;

    DocumentReference getMember(String classroomId, String localId);

    ApiFuture<QuerySnapshot> getMembers(String classroomId, MemberRoles role);

    Stream<DocumentReference> getMembersByClassroom(String classroomId) throws ExecutionException, InterruptedException;

    Stream<QueryDocumentSnapshot> getMembersByUser(String hostId, MemberRoles role) throws ExecutionException, InterruptedException;

    boolean isPublicCodeAlreadyExist(String publicCode) throws ExecutionException, InterruptedException;
    //    DocumentReference updateCollaborator(String key, HashMap<String, Object> map);
}
