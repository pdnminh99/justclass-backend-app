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

    private final CollectionReference usersCollection;

    private final CollectionReference friendsCollection;

    @Autowired
    public UserRepository(@Qualifier("usersCollection") CollectionReference usersCollection,
                          @Qualifier("friendsCollection") CollectionReference friendsCollection) {
        this.usersCollection = usersCollection;
        this.friendsCollection = friendsCollection;
    }

    @Override
    public Optional<User> createUser(UserRequestBody user) {
        DocumentReference documentReference = usersCollection
                .document(user.getLocalId());
        HashMap<String, Object> userMap = user.toMap();
        userMap.remove("localId");
        Timestamp now = Timestamp.now();

        userMap.put("assignTimestamp", now);
        documentReference.set(userMap);
        return Optional.of(user.toUser(now, true));
    }

    @Override
    public void edit(String localId, HashMap<String, Object> changesMap) {
        DocumentReference documentReference = usersCollection
                .document(localId);
        documentReference.update(changesMap);
    }

    @Override
    public DocumentReference getUserReference(String localId) {
        if (Objects.isNull(localId)) {
            return null;
        }
        return usersCollection.document(localId);
    }

    @Override
    public Stream<QueryDocumentSnapshot> getUsersByEmail(List<String> emails) throws ExecutionException, InterruptedException {
        return usersCollection.whereIn("email", emails)
                .get()
                .get()
                .getDocuments()
                .stream();
    }

    @Override
    public void createFriend(FriendReference friend) {
        friendsCollection.add(friend);
    }

    @Override
    public Optional<User> getUser(String localId) throws ExecutionException, InterruptedException {
        DocumentSnapshot document = usersCollection
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
        var collection = friendsCollection
                .whereEqualTo(fieldToCompare, hostId)
                .orderBy("datetime", Query.Direction.ASCENDING);
        return Objects.isNull(lastTimeRequest) ?
                collection.get() :
                collection.whereGreaterThanOrEqualTo("datetime", lastTimeRequest).get();
    }

}
