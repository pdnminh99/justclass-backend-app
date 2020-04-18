package com.projecta.eleven.justclassbackend.classroom;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

interface IClassroomRepository {

    DocumentReference createClassroom(Map<String, Object> classroom) throws ExecutionException, InterruptedException, InvalidClassroomInformationException;

    DocumentReference getClassroom(String classroomId);

    DocumentReference updateClassroom(Map<String, Object> classroom, String classroomId) throws ExecutionException, InterruptedException;

    DocumentReference createCollaborator(HashMap<String, Object> collaboratorMap, String keyCombination) throws ExecutionException, InterruptedException;

    DocumentReference getCollaborator(String classroomId, String localId);

    ApiFuture<QuerySnapshot> getCollaborators(String classroomId, CollaboratorRoles role);

    Stream<DocumentReference> getCollaboratorsByClassroom(String classroomId) throws ExecutionException, InterruptedException;

    Stream<QueryDocumentSnapshot> getCollaboratorsByUser(String hostId, CollaboratorRoles role, Timestamp lastRequest) throws ExecutionException, InterruptedException;
//    DocumentReference updateCollaborator(String key, HashMap<String, Object> map);
}
