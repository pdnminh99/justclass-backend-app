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
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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

    @Override
    public List<MinifiedUser> getUsers(Iterable<String> localIds) {
        return StreamSupport
                .stream(localIds.spliterator(), false)
                .map(this::getMinifiedUser)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<MinifiedUser> getMinifiedUser(String localId) {
        try {
            DocumentSnapshot snapshot = userCollection
                    .document(localId)
                    .get()
                    .get();
            return Optional.of(new MinifiedUser(snapshot));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public boolean isUserExist(String localId) throws ExecutionException, InterruptedException {
        DocumentReference documentReference = userCollection
                .document(localId);
        return documentReference.get().get().exists();
    }

    @Override
    public void edit(String localId, HashMap<String, Object> changesMap) {
        DocumentReference documentReference = userCollection
                .document(localId);
        documentReference.update(changesMap);
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
    public List<FriendReference> getRelationshipReference(String hostLocalId, Integer count)
            throws ExecutionException, InterruptedException {
        var futureQueryByHostIdSnapshot = queryFriendDocuments(hostLocalId, "hostId", count);
        var futureQueryByGuestIdSnapshot = queryFriendDocuments(hostLocalId, "guestId", count);
        var results = ApiFutures
                .allAsList(Lists.newArrayList(futureQueryByGuestIdSnapshot, futureQueryByHostIdSnapshot))
                .get()
                .stream()
                .map(QuerySnapshot::getDocuments)
                .flatMap(Collection::stream)
                .map(FriendReference::new)
                .collect(Collectors.toList());
        // TODO sort results
        System.out.println(results);
        return null;
//        return results.stream().limit(count).collect(Collectors.toList());
    }

    private ApiFuture<QuerySnapshot> queryFriendDocuments(String hostId, String fieldToCompare, Integer count) {
        Query query = friendCollection
                .orderBy("lastAccess", Query.Direction.ASCENDING)
                .whereEqualTo(fieldToCompare, hostId);
        return Objects.isNull(count) ?
                query.get() :
                query.limit(count).get();
    }

//    public List<MinifiedUser> getUsers() throws ExecutionException, InterruptedException {
//        ArrayList<MinifiedUser> minifiedUsers = new ArrayList<>();
//        ApiFuture<QuerySnapshot> query = firestore.collection("user").get();
//        QuerySnapshot querySnapshot = query.get();
//        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
//        String userId, name, address;
//        Long age;
//        MinifiedUser currentMinifiedUser;
//        for (QueryDocumentSnapshot document : documents) {
//            userId = document.getId();
//            name = document.getString("name");
//            address = document.getString("address");
//            age = document.getLong("age");
////            currentMinifiedUser = new MinifiedUser(userId, name, age, address);
////            minifiedUsers.add(currentMinifiedUser);
//        }
//        return minifiedUsers;
//    }
}
