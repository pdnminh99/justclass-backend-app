package com.projecta.eleven.justclassbackend.user;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Repository("firestoreRepository")
public class UserRepository implements IUserRepository {

    private final Firestore firestore;

    @Autowired
    public UserRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    @Override
    public Optional<User> createUser(UserResponseBody user) throws ExecutionException, InterruptedException {
        var createdUserMap = firestore.collection("user")
                .add(user)
                .get()
                .get()
                .get();
        var localId = createdUserMap.getId();
        var firstName = createdUserMap.getString("firstName");
        var lastName = createdUserMap.getString("lastName");
        var fullName = createdUserMap.getString("fullName");
        var displayName = createdUserMap.getString("displayName");
        var photoUrl = createdUserMap.getString("photoUrl");
        var email = createdUserMap.getString("email");
        var assignDatetime = createdUserMap.getTimestamp("assignDatetime");
        var userInstance = new User(
                localId,
                firstName,
                lastName,
                fullName,
                displayName,
                photoUrl,
                email,
                assignDatetime,
                true);
        return Optional.of(userInstance);
    }

    @Override
    public Iterable<MinifiedUser> getUsers(Iterable<String> localIds) {
        return null;
    }

    @Override
    public Optional<User> getUser(String localId) throws ExecutionException, InterruptedException {
        DocumentSnapshot document = firestore.collection("user")
                .document(localId)
                .get()
                .get();
        if (document.exists()) {
            String firstName = document.getString("firstName");
            String lastName = document.getString("lastName");
            String fullName = document.getString("fullName");
            String displayName = document.getString("displayName");
            String photoUrl = document.getString("photoUrl");
            String email = document.getString("email");
            Timestamp assignDatetime = document.getTimestamp("assignDatetime");
            return Optional.of(new User(
                    localId,
                    firstName,
                    lastName,
                    fullName,
                    displayName,
                    photoUrl,
                    email,
                    assignDatetime,
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
