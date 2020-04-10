package com.projecta.eleven.justclassbackend.junit_config;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.projecta.eleven.justclassbackend.configuration.DatabaseFailedToInitializeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;

import java.util.Optional;

@TestConfiguration
public class TestCollectionsConfig {

    private final Firestore firestore;

    @Autowired
    TestCollectionsConfig(Firestore firestore) {
        this.firestore = firestore;
    }

    @Bean("userCollectionTest")
    @DependsOn("firestore")
    public CollectionReference getUserCollection() throws DatabaseFailedToInitializeException {
        return Optional.ofNullable(firestore)
                .map(db -> db.collection("user_test"))
                .orElseThrow(DatabaseFailedToInitializeException::new);
    }

}
