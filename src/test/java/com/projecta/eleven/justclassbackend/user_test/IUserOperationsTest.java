package com.projecta.eleven.justclassbackend.user_test;

import com.google.cloud.firestore.CollectionReference;
import com.projecta.eleven.justclassbackend.junit_config.CustomReplaceUnderscore;
import com.projecta.eleven.justclassbackend.junit_config.TestCollectionsConfig;
import com.projecta.eleven.justclassbackend.user.IUserOperations;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Import;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayNameGeneration(CustomReplaceUnderscore.class)
@DisplayName("Unit Tests for IUserOperations interface.")
@Import(TestCollectionsConfig.class)
public class IUserOperationsTest {

    private final IUserOperations service;

    private final CollectionReference userCollection;

    private int currentLocalId = 0;

    @Autowired
    public IUserOperationsTest(IUserOperations service,
                               @Qualifier("userCollectionTest") CollectionReference userCollection) {
        this.service = service;
        this.userCollection = userCollection;
//        this.sampleUser = new User(UUID.randomUUID().toString(),
//                "John",
//                "Wick",
//                "Johnny Week",
//                "http:somewhere.to.cats.jpg",
//                "johnwick@yahoo.com",
//                Timestamp.now(),
//                false);
    }

    @AfterEach
    void incrementLocalId() {
        currentLocalId += 1;
    }

    @Test
    void assignUser_Pass_not_null_params_should_not_throw_exception() {

    }

    @Test
    void assignUser_Pass_params_with_null_user_should_not_throw_exception() {

    }

    @Test
    void assignUser_Pass_params_with_null_boolean_should_not_throw_exception() {

    }

    @Test
    void assignUser_Pass_null_params_should_not_throw_exception() {

    }

    @Test
    void assignUser_Pass_not_null_params_should_return_not_empty() {

    }

    @Test
    void assignUser_Pass_null_user_and_True_should_return_empty() {

    }

    @Test
    void assignUser_Pass_null_user_and_False_should_return_empty() {

    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @NullSource
    void assignUser_Pass_valid_user_and_autoUpdate_should_create_new_exact_user(Boolean autoUpdateParam) {

    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @NullSource
    void assignUser_Pass_valid_user_with_empty_localId_should_throw_InvalidUserInformationException(Boolean autoUpdateParam) {

    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @NullSource
    void assignUser_Pass_valid_user_with_null_localId_should_throw_InvalidUserInformationException(Boolean autoUpdateParam) {

    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @NullSource
    void assignUser_Pass_valid_user_with_empty_first_name(Boolean autoUpdateParam) {

    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @NullSource
    void assignUser_Pass_valid_user_with_null_first_name(Boolean autoUpdateParam) {

    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @NullSource
    void assignUser_Pass_valid_user_with_empty_last_name(Boolean autoUpdateParam) {

    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @NullSource
    void assignUser_Pass_valid_user_with_null_last_name(Boolean autoUpdateParam) {

    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @NullSource
    void assignUser_Pass_valid_user_with_empty_display_name_should_throw_InvalidUserInformationException(Boolean autoUpdateParam) {

    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @NullSource
    void assignUser_Pass_valid_user_with_null_display_name_should_throw_InvalidUserInformationException(Boolean autoUpdateParam) {

    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @NullSource
    void assignUser_Pass_valid_user_with_null_email_should_throw_InvalidUserInformationException(Boolean autoUpdateParam) {

    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @NullSource
    void assignUser_Pass_valid_user_with_empty_email_should_throw_InvalidUserInformationException(Boolean autoUpdateParam) {

    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @NullSource
    void assignUser_Pass_valid_user_with_null_photoUrl(Boolean autoUpdateParam) {

    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @NullSource
    void assignUser_Pass_valid_user_with_empty_photoUrl(Boolean autoUpdateParam) {

        // Assert newUserTrue
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @NullSource
    void assignUserUPDATE_Pass_existing_localId_null_other_fields_should_return_result(Boolean autoUpdateParam) {

        // Assert newUserFalse
    }

    // update field

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @NullSource
    void assignUserUPDATE_Pass_existing_user_with_same_first_name() {

    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @NullSource
    void assignUserUPDATE_Pass_existing_user_with_empty_first_name() {

    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @NullSource
    void assignUserUPDATE_Pass_existing_user_with_same_last_name() {

    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @NullSource
    void assignUserUPDATE_Pass_existing_user_with_empty_last_name() {

    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @NullSource
    void assignUserUPDATE_Pass_existing_user_with_same_email() {

    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @NullSource
    void assignUserUPDATE_Pass_existing_user_with_empty_email() {

    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @NullSource
    void assignUserUPDATE_Pass_existing_user_with_same_photoUrl() {

    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @NullSource
    void assignUserUPDATE_Pass_existing_user_with_empty_photoUrl() {

    }

    @ParameterizedTest
    @ValueSource(booleans = {true})
    @NullSource
    void assignUserUPDATE_Pass_existing_user_with_different_first_name_allow_auto_update() {

    }

    @Test
    void assignUserUPDATE_Pass_existing_user_with_different_first_name_not_allow_auto_update() {

    }

    @ParameterizedTest
    @ValueSource(booleans = {true})
    @NullSource
    void assignUserUPDATE_Pass_existing_user_with_different_last_name_allow_auto_update() {

    }

    @Test
    void assignUserUPDATE_Pass_existing_user_with_different_last_name_not_allow_auto_update() {

    }

    @ParameterizedTest
    @ValueSource(booleans = {true})
    @NullSource
    void assignUserUPDATE_Pass_existing_user_with_different_email_allow_auto_update() {

    }

    @Test
    void assignUserUPDATE_Pass_existing_user_with_different_email_not_allow_auto_update() {

    }

    @ParameterizedTest
    @ValueSource(booleans = {true})
    @NullSource
    void assignUserUPDATE_Pass_existing_user_with_different_photoUrl_allow_auto_update() {

    }

    @Test
    void assignUserUPDATE_Pass_existing_user_with_different_photoUrl_not_allow_auto_update() {

    }
}
