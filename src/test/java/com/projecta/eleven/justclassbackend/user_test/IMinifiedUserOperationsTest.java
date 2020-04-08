package com.projecta.eleven.justclassbackend.user_test;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.projecta.eleven.justclassbackend.configuration.DatabaseFailedToInitializeException;
import com.projecta.eleven.justclassbackend.junit_config.CustomReplaceUnderscore;
import com.projecta.eleven.justclassbackend.user.IMinifiedUserOperations;
import com.projecta.eleven.justclassbackend.user.MinifiedUser;
import com.projecta.eleven.justclassbackend.user.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayNameGeneration(CustomReplaceUnderscore.class)
@DisplayName("Unit Tests for IMinifiedUserOperations interface.")
@SpringBootTest
public class IMinifiedUserOperationsTest {

    private final CollectionReference userCollection;

    private final IMinifiedUserOperations service;
    private MinifiedUser[] sampleUsers = new User[3];
    private String[] sampleLocalIds = new String[3];

    @Autowired
    IMinifiedUserOperationsTest(IMinifiedUserOperations service,
                                @Qualifier("userCollectionTest") CollectionReference userCollection) {
        this.service = service;
        this.userCollection = userCollection;
    }

    @BeforeAll
    void createSampleUsers() throws ExecutionException, InterruptedException {
        // first
        var localId = "51b5b274-8142-46d2-bccc-e2e894061e7f";
        var user = new User(localId,
                "John",
                "Wick",
                "Johnny Wick", "http://path.to.favourite.dog.jpg",
                "john_wick@private.com",
                null,
                false);
        HashMap<String, Object> map = user.toMap();
        map.remove("localId");

        userCollection.document(localId)
                .set(map).get();
        sampleUsers[0] = user;
        sampleLocalIds[0] = localId;

        // second
        localId = "8667dadc-aecf-4678-bf14-b1a6611aa0c4";
        user = new User(localId,
                "Bruce",
                "Wayne",
                "Bruce Wayne",
                "http://path.to.batcave.pdf",
                "the_batman@wayne.com",
                null,
                false);
        map = user.toMap();
        map.remove("localId");

        userCollection.document(localId)
                .set(map).get();
        sampleUsers[1] = user;
        sampleLocalIds[1] = localId;

        // third
        localId = "982da0a1-673e-4c7d-8fb8-ff3e51f74408";
        user = new User(localId,
                "Alfred",
                "Pennyworth",
                "Alfred Pennyworth",
                "http://path.to.batcave.pdf",
                "i_am_not_batman@alfred.com",
                null,
                false);
        map = user.toMap();
        map.remove("localId");

        userCollection.document(localId)
                .set(map).get();
        sampleUsers[2] = user;
        sampleLocalIds[2] = localId;
    }

    @Test
    void Constructor_Instance_of_IMinifiedUserOperations_interface_should_not_null() {
        assertNotNull(service);
    }

    @Test
    void getUsersWithIterableOfStrings_Method_should_not_throw_any_exception() {
        assertDoesNotThrow(() -> service.getUsers(Arrays.asList(sampleLocalIds)));
    }

    @TestConfiguration
    static class FirestoreTestConfig {

        private final Firestore firestore;

        @Autowired
        FirestoreTestConfig(Firestore firestore) {
            this.firestore = firestore;
        }

        @Bean("userCollectionTest")
        @DependsOn("firestore")
        @Scope("singleton")
        public CollectionReference getUserCollection() throws DatabaseFailedToInitializeException {
            return Optional.ofNullable(firestore)
                    .map(db -> db.collection("user_test"))
                    .orElseThrow(DatabaseFailedToInitializeException::new);
        }

    }
}
