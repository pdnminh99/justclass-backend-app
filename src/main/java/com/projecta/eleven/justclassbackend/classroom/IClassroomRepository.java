package com.projecta.eleven.justclassbackend.classroom;

import com.google.cloud.firestore.DocumentReference;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

interface IClassroomRepository {

    DocumentReference createClassroom(Map<String, Object> classroom) throws ExecutionException, InterruptedException, InvalidClassroomInformationException;

    DocumentReference getClassroom(String classroomId);

    DocumentReference updateClassroom(Map<String, Object> classroom, String classroomId) throws ExecutionException, InterruptedException;

    DocumentReference createCollaborator(HashMap<String, Object> collaboratorMap, String keyCombination) throws ExecutionException, InterruptedException;

    DocumentReference getCollaborator(String classroomId, String localId);

//    DocumentReference updateCollaborator(String key, HashMap<String, Object> map);
}
