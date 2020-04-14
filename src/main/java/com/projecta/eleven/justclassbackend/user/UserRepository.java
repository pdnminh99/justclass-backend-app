package com.projecta.eleven.justclassbackend.user;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

@Repository("firestoreRepository")
class UserRepository implements IUserRepository {

    private final CollectionReference userCollection;

    private final CollectionReference friendCollection;

    @Autowired
    public UserRepository(@Qualifier("userCollection") CollectionReference userCollection,
                          @Qualifier("friendCollection") CollectionReference friendCollection) {
        this.userCollection = userCollection;
        this.friendCollection = friendCollection;
    }

    @Override
    public Optional<User> createUser(UserRequestBody user) {
        DocumentReference documentReference = userCollection
                .document(user.getLocalId());
        HashMap<String, Object> userMap = user.toMap();
        userMap.remove("localId");
        Timestamp now = Timestamp.now();

        userMap.put("assignTimestamp", now);
        documentReference.set(userMap);
        return Optional.of(user.toUser(now, true));
    }

//    @Override
//    public List<MinifiedUser> getUsers(Iterable<String> localIds) throws ExecutionException, InterruptedException {
//        return ApiFutures.allAsList(StreamSupport
//                .stream(localIds.spliterator(), false)
//                .map(s -> userCollection.document(s).get())
//                .collect(Collectors.toList()))
//                .get()
//                .stream()
//                .map(MinifiedUser::new)
//                .collect(Collectors.toList());
//    }

//    @Override
//    public Optional<MinifiedUser> getMinifiedUser(String localId) {
//        try {
//            DocumentSnapshot snapshot = userCollection
//                    .document(localId)
//                    .get()
//                    .get();
//            return Optional.of(new MinifiedUser(snapshot));
//        } catch (InterruptedException | ExecutionException e) {
//            e.printStackTrace();
//            return Optional.empty();
//        }
//    }
//
//    @Override
//    public boolean isUserExist(String localId) throws ExecutionException, InterruptedException {
//        DocumentReference documentReference = userCollection
//                .document(localId);
//        return documentReference.get().get().exists();
//    }

    @Override
    public void edit(String localId, HashMap<String, Object> changesMap) {
        DocumentReference documentReference = userCollection
                .document(localId);
        documentReference.update(changesMap);
    }

    @Override
    public DocumentReference getUserReference(String localId) {
        if (Objects.isNull(localId)) {
            return null;
        }
        return userCollection.document(localId);
    }

    @Override
    public Optional<User> getUser(String localId) throws ExecutionException, InterruptedException {
        DocumentSnapshot document = userCollection
                .document(localId)
                .get()
                .get();
        return document.exists() ?
                Optional.of(new User(document, false)) :
                Optional.empty();
    }

    @Override
    public Stream<FriendReference> getRelationshipReferences(String hostLocalId, Timestamp lastTimeRequest)
            throws ExecutionException, InterruptedException {
        var futureQueryByHostIdSnapshot = queryFriendsDocuments(
                hostLocalId, "hostId", lastTimeRequest);
        var futureQueryByGuestIdSnapshot = queryFriendsDocuments(
                hostLocalId, "guestId", lastTimeRequest);
        return ApiFutures
                .allAsList(Lists.newArrayList(futureQueryByGuestIdSnapshot, futureQueryByHostIdSnapshot))
                .get()
                .stream()
                .map(QuerySnapshot::getDocuments)
                .flatMap(Collection::stream)
                .map(FriendReference::new)
                .sorted(Comparator.comparing(FriendReference::getDatetime));
    }

    private ApiFuture<QuerySnapshot> queryFriendsDocuments(String hostId, String fieldToCompare, Timestamp lastTimeRequest) {
        var collection = friendCollection
                .whereEqualTo(fieldToCompare, hostId)
                .orderBy("datetime", Query.Direction.ASCENDING);
        return Objects.isNull(lastTimeRequest) ?
                collection.get() :
                collection.whereGreaterThanOrEqualTo("datetime", lastTimeRequest).get();
    }

}
