package com.projecta.eleven.justclassbackend.user;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Repository("firestoreRepository")
class UserRepository implements IUserRepository {

    private final CollectionReference userCollection;

    @Autowired
    public UserRepository(CollectionReference userCollection) {
        this.userCollection = userCollection;
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
        DocumentReference documentReference = userCollection
                .document(localId);
        try {
            DocumentSnapshot snapshot = documentReference
                    .get()
                    .get();
            var displayName = snapshot.getString("displayName");
            var photoUrl = snapshot.getString("photoUrl");
            var minifiedUser = new MinifiedUser(localId, displayName, photoUrl);
            return Optional.of(minifiedUser);
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
        if (document.exists()) {
            return Optional.of(new User(
                    localId,
                    document.getString("firstName"),
                    document.getString("lastName"),
                    document.getString("displayName"),
                    document.getString("photoUrl"),
                    document.getString("email"),
                    document.getTimestamp("assignTimestamp"),
                    false
            ));
        }
        return Optional.empty();
    }

    @Override
    public Stream<String> getFriends(String hostLocalId) throws ExecutionException, InterruptedException {
        DocumentSnapshot documentSnapshot = userCollection
                .document(hostLocalId)
                .get()
                .get();
        if (!documentSnapshot.exists()) {
            return Stream.empty();
        }
        var userData = documentSnapshot.getData();
        Objects.requireNonNull(userData)
                .forEach((k, v) -> System.out.println(k + " : " + v));
        return Stream.empty();
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
