package com.projecta.eleven.justclassbackend.Repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.projecta.eleven.justclassbackend.Models.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class FirestoreRepository {

    private final Firestore firestore;

    @Autowired
    public FirestoreRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    public List<User> getUsers() throws ExecutionException, InterruptedException {
        ArrayList<User> users = new ArrayList<>();
        ApiFuture<QuerySnapshot> query = firestore.collection("user").get();
        QuerySnapshot querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
        String userId, name, address;
        Long age;
        User currentUser;
        for (QueryDocumentSnapshot document : documents) {
            userId = document.getId();
            name = document.getString("name");
            address = document.getString("address");
            age = document.getLong("age");
            currentUser = new User(userId, name, age, address);
            users.add(currentUser);
        }
        return users;
    }
}
