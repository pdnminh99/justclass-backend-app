package com.projecta.eleven.justclassbackend.user;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Repository
public class UserRepository {

    private final Firestore firestore;

    @Autowired
    public UserRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    public List<MinifiedUser> getUsers() throws ExecutionException, InterruptedException {
        ArrayList<MinifiedUser> minifiedUsers = new ArrayList<>();
        ApiFuture<QuerySnapshot> query = firestore.collection("user").get();
        QuerySnapshot querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
        String userId, name, address;
        Long age;
        MinifiedUser currentMinifiedUser;
        for (QueryDocumentSnapshot document : documents) {
            userId = document.getId();
            name = document.getString("name");
            address = document.getString("address");
            age = document.getLong("age");
//            currentMinifiedUser = new MinifiedUser(userId, name, age, address);
//            minifiedUsers.add(currentMinifiedUser);
        }
        return minifiedUsers;
    }
}
