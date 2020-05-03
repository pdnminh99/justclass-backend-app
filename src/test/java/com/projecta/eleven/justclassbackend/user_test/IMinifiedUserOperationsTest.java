package com.projecta.eleven.justclassbackend.user_test;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.common.collect.Lists;
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

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayNameGeneration(CustomReplaceUnderscore.class)
@DisplayName("Unit Tests for IMinifiedUserOperations interface.")
@SpringBootTest(properties = "spring.main.allow-bean-definition-overriding=true")
public class IMinifiedUserOperationsTest {

    private final CollectionReference usersCollection;
    private final CollectionReference friendsCollection;

    private final IMinifiedUserOperations service;
    private Collection<String> sampleLocalIds = new ArrayList<>();
    private Collection<String> sampleRelationshipIds = new ArrayList<>();

    @Autowired
    IMinifiedUserOperationsTest(IMinifiedUserOperations service,
                                @Qualifier("usersCollection") CollectionReference usersCollection,
                                @Qualifier("friendsCollection") CollectionReference friendsCollection) {
        this.service = service;
        this.usersCollection = usersCollection;
        this.friendsCollection = friendsCollection;
    }

    @BeforeAll
    void createSampleUsers() throws ExecutionException, InterruptedException {
        sampleLocalIds.add("51b5b274-8142-46d2-bccc-e2e894061e7f");
        sampleLocalIds.add("8667dadc-aecf-4678-bf14-b1a6611aa0c4");
        sampleLocalIds.add("982da0a1-673e-4c7d-8fb8-ff3e51f74408");
        sampleRelationshipIds.add("1");
        sampleRelationshipIds.add("2");
        /*
         * John <-------> Bruce <-------> Alfred
         */
        ApiFutures.allAsList(createSampleUsersStream().collect(Collectors.toList()))
                .get();
        Thread.sleep(1000);
        ApiFutures.allAsList(createSampleRelationshipsStream().collect(Collectors.toList()))
                .get();
    }

    private Stream<ApiFuture<WriteResult>> createSampleUsersStream() {
        Collection<Map<String, Object>> sampleMaps = new ArrayList<>();

        var user = new User(
                "51b5b274-8142-46d2-bccc-e2e894061e7f",
                "John",
                "Wick",
                "Johnny Wick",
                "http://path.to.favourite.dog.jpg",
                "john_wick@private.com",
                null,
                false);
        sampleMaps.add(user.toMap());

        user = new User(
                "8667dadc-aecf-4678-bf14-b1a6611aa0c4",
                "Bruce",
                "Wayne",
                "Bruce Wayne",
                "http://path.to.batcave.pdf",
                "the_batman@wayne.com",
                null,
                false);
        sampleMaps.add(user.toMap());

        user = new User("982da0a1-673e-4c7d-8fb8-ff3e51f74408",
                "Alfred",
                "Pennyworth",
                "Alfred Pennyworth",
                "http://path.to.batcave.pdf",
                "i_am_not_batman@alfred.com",
                null,
                false);
        sampleMaps.add(user.toMap());

        return sampleMaps.stream()
                .map(this::transformUserMapToFirestoreDocument);
    }

    private Stream<ApiFuture<WriteResult>> createSampleRelationshipsStream() throws ExecutionException, InterruptedException {
        Collection<Map<String, Object>> sampleMaps = new ArrayList<>();

        List<DocumentSnapshot> snapshots = ApiFutures.allAsList(
                Lists.newArrayList(
                        usersCollection.document("8667dadc-aecf-4678-bf14-b1a6611aa0c4").get(),
                        usersCollection.document("982da0a1-673e-4c7d-8fb8-ff3e51f74408").get(),
                        usersCollection.document("51b5b274-8142-46d2-bccc-e2e894061e7f").get()))
                .get();

        var bruceReference = snapshots.get(0).getReference();
        var alfredReference = snapshots.get(1).getReference();
        var johnReference = snapshots.get(2).getReference();

        var firstRelationship = new HashMap<String, Object>();
        firstRelationship.put("relationshipId", "1");
        firstRelationship.put("hostId", "8667dadc-aecf-4678-bf14-b1a6611aa0c4");
        firstRelationship.put("hostReference", bruceReference);
        firstRelationship.put("guestId", "982da0a1-673e-4c7d-8fb8-ff3e51f74408");
        firstRelationship.put("guestReference", alfredReference);
        firstRelationship.put("datetime", Timestamp.parseTimestamp("2020-04-05T07:20:50.52Z"));
        firstRelationship.put("lastAccess", null);

        sampleMaps.add(firstRelationship);

        var secondRelationship = new HashMap<String, Object>();
        secondRelationship.put("relationshipId", "2");
        secondRelationship.put("hostId", "8667dadc-aecf-4678-bf14-b1a6611aa0c4");
        secondRelationship.put("hostReference", bruceReference);
        secondRelationship.put("guestId", "51b5b274-8142-46d2-bccc-e2e894061e7f");
        secondRelationship.put("guestReference", johnReference);
        secondRelationship.put("datetime", Timestamp.parseTimestamp("2020-04-10T07:20:50.52Z"));
        secondRelationship.put("lastAccess", null);

        sampleMaps.add(secondRelationship);

        return sampleMaps.stream()
                .map(this::transformRelationshipMapToFirestoreDocument);
    }

    private ApiFuture<WriteResult> transformRelationshipMapToFirestoreDocument(Map<String, Object> map) {
        var relationshipId = (String) map.get("relationshipId");
        map.remove("relationshipId");
        return friendsCollection.document(relationshipId)
                .set(map);
    }

    private ApiFuture<WriteResult> transformUserMapToFirestoreDocument(Map<String, Object> map) {
        var localId = (String) map.get("localId");
        map.remove("localId");
        return usersCollection.document(localId)
                .set(map);
    }

    @AfterAll
    void clearSampleUsers() throws ExecutionException, InterruptedException {
        var merge = Stream.concat(
                sampleLocalIds.stream()
                        .map(this::transformUserDeleteQuery),
                sampleRelationshipIds.stream()
                        .map(this::transformRelationshipDeleteQuery)
        );

        ApiFutures.allAsList(merge.collect(Collectors.toList()))
                .get();
    }

    private ApiFuture<WriteResult> transformUserDeleteQuery(String id) {
        return usersCollection.document(id)
                .delete();
    }

    private ApiFuture<WriteResult> transformRelationshipDeleteQuery(String id) {
        return friendsCollection.document(id)
                .delete();
    }

    private void assertEqualBruce(MinifiedUser user) {
        assertEquals("8667dadc-aecf-4678-bf14-b1a6611aa0c4", user.getLocalId());
        assertEquals("Bruce Wayne", user.getDisplayName());
        assertEquals("http://path.to.batcave.pdf", user.getPhotoUrl());
    }

    private void assertEqualJohn(MinifiedUser user) {
        assertEquals("51b5b274-8142-46d2-bccc-e2e894061e7f", user.getLocalId());
        assertEquals("Johnny Wick", user.getDisplayName());
        assertEquals("http://path.to.favourite.dog.jpg", user.getPhotoUrl());
    }

    private void assertEqualAlfred(MinifiedUser user) {
        assertEquals("982da0a1-673e-4c7d-8fb8-ff3e51f74408", user.getLocalId());
        assertEquals("Alfred Pennyworth", user.getDisplayName());
        assertEquals("http://path.to.batcave.pdf", user.getPhotoUrl());
    }

    @Test
    void getFriendsOfUser_Pass_localId_of_Bruce_and_null_timestamp_shall_returns_two_results() throws ExecutionException, InterruptedException {
        var result = service.getFriendsOfUser("8667dadc-aecf-4678-bf14-b1a6611aa0c4", null).collect(Collectors.toList());

        assertEquals(2, result.size());
        assertEqualJohn(result.get(1));
        assertEqualAlfred(result.get(0));
    }

    @Test
    void getFriendsOfUser_Pass_localId_of_Bruce_and_null_timestamp_shall_not_throw_exception() {
        assertDoesNotThrow(() -> service.getFriendsOfUser("8667dadc-aecf-4678-bf14-b1a6611aa0c4", null));
    }

    @Test
    void getFriendsOfUser_Pass_localId_of_Bruce_and_before_timestamp_shall_returns_two_results() throws ExecutionException, InterruptedException {
        var result = service.getFriendsOfUser("8667dadc-aecf-4678-bf14-b1a6611aa0c4", Timestamp.parseTimestamp("2020-04-03T07:20:50.52Z")).collect(Collectors.toList());

        assertEquals(2, result.size());
        assertEqualJohn(result.get(1));
        assertEqualAlfred(result.get(0));
    }

    @Test
    void getFriendsOfUser_Pass_localId_of_Bruce_and_before_timestamp_shall_not_throw_exception() {
        assertDoesNotThrow(() -> service.getFriendsOfUser("8667dadc-aecf-4678-bf14-b1a6611aa0c4", Timestamp.parseTimestamp("2020-04-03T07:20:50.52Z")));
    }

    @Test
    void getFriendsOfUser_Pass_localId_of_Bruce_and_first_timestamp_shall_returns_two_results() throws ExecutionException, InterruptedException {
        var result = service.getFriendsOfUser("8667dadc-aecf-4678-bf14-b1a6611aa0c4", Timestamp.parseTimestamp("2020-04-05T07:20:50.52Z")).collect(Collectors.toList());

        assertEquals(2, result.size());
        assertEqualJohn(result.get(1));
        assertEqualAlfred(result.get(0));
    }

    @Test
    void getFriendsOfUser_Pass_localId_of_Bruce_and_first_timestamp_shall_not_throw_exception() {
        assertDoesNotThrow(() -> service.getFriendsOfUser("8667dadc-aecf-4678-bf14-b1a6611aa0c4", Timestamp.parseTimestamp("2020-04-05T07:20:50.52Z")));
    }

    @Test
    void getFriendsOfUser_Pass_localId_of_Bruce_and_middle_timestamp_shall_returns_one_results() throws ExecutionException, InterruptedException {
        var result = service.getFriendsOfUser("8667dadc-aecf-4678-bf14-b1a6611aa0c4", Timestamp.parseTimestamp("2020-04-07T07:20:50.52Z")).collect(Collectors.toList());

        assertEquals(1, result.size());
        assertEqualJohn(result.get(0));
    }

    @Test
    void getFriendsOfUser_Pass_localId_of_Bruce_and_middle_timestamp_shall_not_throw_exception() {
        assertDoesNotThrow(() -> service.getFriendsOfUser("8667dadc-aecf-4678-bf14-b1a6611aa0c4", Timestamp.parseTimestamp("2020-04-07T07:20:50.52Z")));
    }

    @Test
    void getFriendsOfUser_Pass_localId_of_Bruce_and_last_timestamp_shall_returns_one_results() throws ExecutionException, InterruptedException {
        var result = service.getFriendsOfUser("8667dadc-aecf-4678-bf14-b1a6611aa0c4", Timestamp.parseTimestamp("2020-04-10T07:20:50.52Z")).collect(Collectors.toList());

        assertEquals(1, result.size());
        assertEqualJohn(result.get(0));
    }

    @Test
    void getFriendsOfUser_Pass_localId_of_Bruce_and_last_timestamp_shall_not_throw_exception() {
        assertDoesNotThrow(() -> service.getFriendsOfUser("8667dadc-aecf-4678-bf14-b1a6611aa0c4", Timestamp.parseTimestamp("2020-04-10T07:20:50.52Z")));
    }

    @Test
    void getFriendsOfUser_Pass_localId_of_Bruce_and_after_timestamp_shall_returns_empty() throws ExecutionException, InterruptedException {
        var result = service.getFriendsOfUser("8667dadc-aecf-4678-bf14-b1a6611aa0c4", Timestamp.parseTimestamp("2020-04-11T07:20:50.52Z")).collect(Collectors.toList());

        assertEquals(0, result.size());
    }

    @Test
    void getFriendsOfUser_Pass_localId_of_Bruce_and_after_timestamp_shall_not_throw_exception() {
        assertDoesNotThrow(() -> service.getFriendsOfUser("8667dadc-aecf-4678-bf14-b1a6611aa0c4", Timestamp.parseTimestamp("2020-04-11T07:20:50.52Z")));
    }

    @Test
    void getFriendsOfUser_Pass_localId_of_John_and_null_timestamp_shall_returns_one_result() throws ExecutionException, InterruptedException {
        var result = service.getFriendsOfUser("51b5b274-8142-46d2-bccc-e2e894061e7f", null).collect(Collectors.toList());

        assertEquals(1, result.size());
        assertEqualBruce(result.get(0));
    }

    @Test
    void getFriendsOfUser_Pass_localId_of_John_and_null_timestamp_shall_not_throw_exception() {
        assertDoesNotThrow(() -> service.getFriendsOfUser("51b5b274-8142-46d2-bccc-e2e894061e7f", null));
    }

    @Test
    void getFriendsOfUser_Pass_localId_of_John_and_before_timestamp_shall_returns_one_result() throws ExecutionException, InterruptedException {
        var result = service.getFriendsOfUser("51b5b274-8142-46d2-bccc-e2e894061e7f", Timestamp.parseTimestamp("2020-04-09T07:20:50.52Z")).collect(Collectors.toList());

        assertEquals(1, result.size());
        assertEqualBruce(result.get(0));
    }

    @Test
    void getFriendsOfUser_Pass_localId_of_John_and_before_timestamp_shall_not_throw_exception() {
        assertDoesNotThrow(() -> service.getFriendsOfUser("51b5b274-8142-46d2-bccc-e2e894061e7f", Timestamp.parseTimestamp("2020-04-09T07:20:50.52Z")));
    }

    @Test
    void getFriendsOfUser_Pass_localId_of_John_and_middle_timestamp_shall_returns_one_result() throws ExecutionException, InterruptedException {
        var result = service.getFriendsOfUser("51b5b274-8142-46d2-bccc-e2e894061e7f", Timestamp.parseTimestamp("2020-04-10T07:20:50.52Z")).collect(Collectors.toList());

        assertEquals(1, result.size());
        assertEqualBruce(result.get(0));
    }

    @Test
    void getFriendsOfUser_Pass_localId_of_John_and_middle_timestamp_shall_not_throw_exception() {
        assertDoesNotThrow(() -> service.getFriendsOfUser("51b5b274-8142-46d2-bccc-e2e894061e7f", Timestamp.parseTimestamp("2020-04-10T07:20:50.52Z")));
    }

    @Test
    void getFriendsOfUser_Pass_localId_of_John_and_after_timestamp_shall_returns_empty() throws ExecutionException, InterruptedException {
        var result = service.getFriendsOfUser("51b5b274-8142-46d2-bccc-e2e894061e7f", Timestamp.parseTimestamp("2020-04-11T07:20:50.52Z")).collect(Collectors.toList());

        assertEquals(0, result.size());
    }

    @Test
    void getFriendsOfUser_Pass_localId_of_John_and_after_timestamp_shall_not_throw_exception() {
        assertDoesNotThrow(() -> service.getFriendsOfUser("51b5b274-8142-46d2-bccc-e2e894061e7f", Timestamp.parseTimestamp("2020-04-11T07:20:50.52Z")));
    }

    @Test
    void getFriendsOfUser_Pass_null_localId_and_null_timestamp_shall_returns_empty() throws ExecutionException, InterruptedException {
        var result = service.getFriendsOfUser(null, null).collect(Collectors.toList());

        assertEquals(0, result.size());
    }

    @Test
    void getFriendsOfUser_Pass_null_localId_and_null_timestamp_shall_not_throw_exception() {
        assertDoesNotThrow(() -> service.getFriendsOfUser(null, null));
    }

    @Test
    void getFriendsOfUser_Pass_null_localId_and_before_timestamp_shall_returns_empty() throws ExecutionException, InterruptedException {
        var result = service.getFriendsOfUser(null, Timestamp.parseTimestamp("2020-04-09T07:20:50.52Z")).collect(Collectors.toList());

        assertEquals(0, result.size());
    }

    @Test
    void getFriendsOfUser_Pass_null_localId_and_before_timestamp_shall_not_throw_exception() {
        assertDoesNotThrow(() -> service.getFriendsOfUser(null, Timestamp.parseTimestamp("2020-04-09T07:20:50.52Z")));
    }

    @Test
    void getFriendsOfUser_Pass_null_localId_and_middle_timestamp_shall_returns_empty() throws ExecutionException, InterruptedException {
        var result = service.getFriendsOfUser(null, Timestamp.parseTimestamp("2020-04-10T07:20:50.52Z")).collect(Collectors.toList());

        assertEquals(0, result.size());
    }

    @Test
    void getFriendsOfUser_Pass_null_localId_and_middle_timestamp_shall_not_throw_exception() {
        assertDoesNotThrow(() -> service.getFriendsOfUser(null, Timestamp.parseTimestamp("2020-04-10T07:20:50.52Z")));
    }

    @Test
    void getFriendsOfUser_Pass_null_localId_and_after_timestamp_shall_returns_empty() throws ExecutionException, InterruptedException {
        var result = service.getFriendsOfUser(null, Timestamp.parseTimestamp("2020-04-11T07:20:50.52Z")).collect(Collectors.toList());

        assertEquals(0, result.size());
    }

    @Test
    void getFriendsOfUser_Pass_null_localId_and_after_timestamp_shall_not_throw_exception() {
        assertDoesNotThrow(() -> service.getFriendsOfUser(null, Timestamp.parseTimestamp("2020-04-11T07:20:50.52Z")));
    }

    @TestConfiguration
    static class TestCollectionsConfig {

        private final Firestore firestore;

        @Autowired
        TestCollectionsConfig(Firestore firestore) {
            this.firestore = firestore;
        }

        @Bean("usersCollection")
        @DependsOn("firestore")
        public CollectionReference getUsersCollection() throws DatabaseFailedToInitializeException {
            return Optional.ofNullable(firestore)
                    .map(db -> db.collection("users_test"))
                    .orElseThrow(DatabaseFailedToInitializeException::new);
        }

        @Bean("friendsCollection")
        @DependsOn("firestore")
        public CollectionReference getFriendsCollection() throws DatabaseFailedToInitializeException {
            return Optional.ofNullable(firestore)
                    .map(db -> db.collection("friends_test"))
                    .orElseThrow(DatabaseFailedToInitializeException::new);
        }

    }

}
