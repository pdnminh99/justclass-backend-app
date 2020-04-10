package com.projecta.eleven.justclassbackend.user_test;

import com.google.cloud.firestore.CollectionReference;
import com.projecta.eleven.justclassbackend.junit_config.CustomReplaceUnderscore;
import com.projecta.eleven.justclassbackend.junit_config.TestCollectionsConfig;
import com.projecta.eleven.justclassbackend.user.IMinifiedUserOperations;
import com.projecta.eleven.justclassbackend.user.MinifiedUser;
import com.projecta.eleven.justclassbackend.user.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayNameGeneration(CustomReplaceUnderscore.class)
@DisplayName("Unit Tests for IMinifiedUserOperations interface.")
@Import(TestCollectionsConfig.class)
@SpringBootTest
public class IMinifiedUserOperationsTest {

    private final CollectionReference userCollection;

    private final IMinifiedUserOperations service;
    private MinifiedUser[] sampleUsers = new User[3];
    private Collection<String> sampleLocalIds = new ArrayList<>();

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
        sampleLocalIds.add(localId);

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
        sampleLocalIds.add(localId);

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
        sampleLocalIds.add(localId);
    }

    @AfterAll
    void clearSampleUsers() {
        userCollection.document().delete();
    }

    @Test
    void Constructor_Instance_of_IMinifiedUserOperations_interface_should_not_null() {
        assertNotNull(service);
    }

    @Test
    void getUsersWithIterableOfStrings_Method_should_not_throw_any_exception() {
    }

    @Test
    void getUsersWithIterableOfStrings_Pass_param_of_null_should_return_empty_list() throws ExecutionException, InterruptedException {

    }

    @Test
    void getUsersWithIterableOfStrings_Pass_param_of_null_should_not_throw_exception() {
    }

    @Test
    void getUsersWithIterableOfStrings_Pass_param_of_empty_list_should_return_empty_list() throws ExecutionException, InterruptedException {

    }

    @Test
    void getUsersWithIterableOfStrings_Pass_param_of_one_element_not_null_but_no_match_should_return_empty_list() throws ExecutionException, InterruptedException {

    }

    @Test
    void getUsersWithIterableOfStrings_Pass_param_of_one_element_null_should_return_empty_list() throws ExecutionException, InterruptedException {

    }

    @Test
    void getUsersWithIterableOfStrings_Pass_param_of_one_element_null_should_not_throw_exception() {
    }

    @Test
    void getUsersWithIterableOfStrings_Pass_param_of_one_element_not_null_should_return_one_result() throws ExecutionException, InterruptedException {

    }

    @Test
    void getUsersWithIterableOfStrings_Pass_param_of_two_elements_not_null_and_first_one_match_should_return_one_result() {
        // TODO implement this.
    }

    @Test
    void getUsersWithIterableOfStrings_Pass_param_of_two_elements_not_null_and_later_one_match_should_return_one_result() {
        // TODO implement this.
    }

    @Test
    void getUsersWithIterableOfStrings_Pass_param_of_two_elements_not_null_and_two_matches_should_return_two_results() {
// TODO implement this.
    }

    @Test
    void getUsersWithIterableOfStrings_Pass_param_of_two_elements_first_one_null_and_latter_one_match_should_return_one_result() {
// TODO implement this.
    }

    @Test
    void getUsersWithIterableOfStrings_Pass_param_of_two_elements_first_one_null_and_latter_one_is_empty_should_return_one_result() {
// TODO implement this.
    }

    @Test
    void getUsersWithIterableOfStrings_Pass_param_of_two_elements_first_one_null_and_latter_one_is_not_match_should_return_empty() {
// TODO implement this.
    }

    @Test
    void getUsersWithIterableOfStrings_Pass_param_of_two_nulls_should_return_empty_result() {
// TODO implement this.
    }

    @Test
    void getUsersWithIterableOfStrings_Pass_param_of_two_elements_first_one_matches_and_latter_one_null_should_return_one_result() {
// TODO implement this.
    }

    @Test
    void getUsersWithIterableOfStrings_Pass_param_of_two_elements_first_one_empty_and_later_one_null_should_return_empty_result() {
// TODO implement this.
    }

    @Test
    void getUsersWithIterableOfStrings_Pass_param_of_two_elements_first_one_not_matches_and_later_one_null_should_return_empty_result() {
// TODO implement this.
    }

    @Test
    void getUsersWithIterableOfStrings_Pass_param_of_two_elements_both_match_should_return_two_results() {
// TODO implement this.
    }

    @Test
    void getUsersWithIterableOfStrings_Pass_param_of_two_elements_both_not_match_should_return_empty() {
// TODO implement this.
    }
}
