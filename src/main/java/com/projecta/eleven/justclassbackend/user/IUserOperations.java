package com.projecta.eleven.justclassbackend.user;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.QueryDocumentSnapshot;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

public interface IUserOperations {
    Optional<User> assignUser(UserRequestBody user, Boolean autoUpdate)
            throws ExecutionException, InterruptedException, InvalidUserInformationException;

    DocumentReference getUserReference(String localId);

    Stream<DocumentReference> getUsersReferences(List<String> localIds);

    Stream<QueryDocumentSnapshot> getUsersByEmail(List<String> emails) throws ExecutionException, InterruptedException;
}
