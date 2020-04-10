package com.projecta.eleven.justclassbackend.configuration;


import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;

import java.util.Optional;

@Configuration
public class CollectionsConfig {

    private final Firestore firestore;

    @Autowired
    public CollectionsConfig(Firestore firestore) {
        this.firestore = firestore;
    }

    @Bean("userCollection")
    @DependsOn("firestore")
    @Scope("singleton")
    public CollectionReference getUserCollection() throws DatabaseFailedToInitializeException {
        return Optional.ofNullable(firestore)
                .map(db -> db.collection("user"))
                .orElseThrow(DatabaseFailedToInitializeException::new);
    }

}