package com.projecta.eleven.justclassbackend.user;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Repository("firestoreRepository")
class UserRepository implements IUserRepository {

    private final Firestore firestore;

    private boolean isTestMode = false;

    public void enableTestMode() {
        isTestMode = true;
    }

    private String getUserCollection() {
        return isTestMode ? "user_test" : "user";
    }

    @Autowired
    public UserRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    @Override
    public Optional<User> createUser(UserRequestBody user) {
        DocumentReference documentReference = firestore.collection(getUserCollection())
                .document(user.getLocalId());
        HashMap<String, Object> userMap = user.toMap();
        Timestamp now = Timestamp.now();

        userMap.put("assignTimestamp", now);
        documentReference.set(userMap);
        return Optional.of(user.toUser(now, true));
    }

    @Override
    public Iterable<MinifiedUser> getUsers(Iterable<String> localIds) {
        return null;
    }

    @Override
    public Optional<User> getUser(String localId) throws ExecutionException, InterruptedException {
        DocumentSnapshot document = firestore.collection(getUserCollection())
                .document(localId)
                .get()
                .get();
        if (document.exists()) {
            return Optional.of(new User(
                    localId,
                    document.getString("firstName"),
                    document.getString("lastName"),
                    document.getString("fullName"),
                    document.getString("displayName"),
                    document.getString("photoUrl"),
                    document.getString("email"),
                    document.getTimestamp("assignTimestamp"),
                    false
            ));
        }
        return Optional.empty();
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
