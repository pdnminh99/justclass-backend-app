package com.projecta.eleven.justclassbackend.classroom;

import com.google.cloud.firestore.DocumentReference;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

interface IClassroomRepository {

    DocumentReference createClassroom(Map<String, Object> classroom) throws ExecutionException, InterruptedException, InvalidClassroomInformationException;

    DocumentReference createCollaborator(HashMap<String, Object> collaboratorMap, String keyCombination) throws ExecutionException, InterruptedException;
}
