package com.projecta.eleven.justclassbackend.user;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

interface IUserRepository {
    Optional<User> createUser(UserRequestBody user) throws ExecutionException, InterruptedException;

//    List<MinifiedUser> getUsers(Iterable<String> localIds) throws ExecutionException, InterruptedException;

    Optional<User> getUser(String localId) throws ExecutionException, InterruptedException;

    default Optional<User> getUser(MinifiedUser sampleUser) throws ExecutionException, InterruptedException {
        return getUser(sampleUser.getLocalId());
    }

    Stream<FriendReference> getRelationshipReferences(String hostLocalId, Timestamp lastTimeRequest)
            throws ExecutionException, InterruptedException;

//    Optional<MinifiedUser> getMinifiedUser(String localId);

//    boolean isUserExist(String hostLocalId) throws ExecutionException, InterruptedException;

    void edit(String localId, HashMap<String, Object> changesMap);

    DocumentReference getUserReference(String localId);

    Stream<QueryDocumentSnapshot> getUsersByEmail(List<String> emails) throws ExecutionException, InterruptedException;

    void createFriend(FriendReference friend);
}
