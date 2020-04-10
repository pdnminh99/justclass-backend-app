package com.projecta.eleven.justclassbackend.user_test;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.CollectionReference;
import com.projecta.eleven.justclassbackend.junit_config.CustomReplaceUnderscore;
import com.projecta.eleven.justclassbackend.junit_config.TestCollectionsConfig;
import com.projecta.eleven.justclassbackend.user.IUserOperations;
import com.projecta.eleven.justclassbackend.user.InvalidUserInformationException;
import com.projecta.eleven.justclassbackend.user.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayNameGeneration(CustomReplaceUnderscore.class)
@DisplayName("Unit Tests for IUserOperations interface.")
@Import(TestCollectionsConfig.class)
@SpringBootTest
public class IUserOperationsTest {

    private final IUserOperations service;

    private final CollectionReference userCollection;

    private int currentLocalId = 0;

    private final String firstNameExpected = "John";
    private final String lastNameExpected = "Wick";
    private final String displayNameExpected = "Johnny Week";
    private final String photoUrlExpected = "http:somewhere.to.cats.jpg";
    private final String emailExpected = "johnwick@yahoo.com";

    @Autowired
    public IUserOperationsTest(IUserOperations service, CollectionReference userCollection) {
        this.service = service;
        this.userCollection = userCollection;
    }

    @AfterEach
    void incrementLocalId() throws InterruptedException {
        Thread.sleep(500);
        userCollection.document(String.valueOf(currentLocalId)).delete();
        currentLocalId += 1;
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @NullSource
    void assignUser_Pass_not_null_params_should_not_throw_exception(Boolean autoUpdate) {
        var user = new User(String.valueOf(currentLocalId),
                "John",
                "Wick",
                "Johnny Week",
                "http:somewhere.to.cats.jpg",
                "johnwick@yahoo.com",
                Timestamp.now(),
                false);
        assertDoesNotThrow(() -> service.assignUser(user, autoUpdate));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @NullSource
    void assignUser_Pass_params_with_null_user_should_not_throw_exception(Boolean autoUpdate) {
        assertDoesNotThrow(() -> service.assignUser(null, autoUpdate));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @NullSource
    void assignUser_Pass_not_null_params_should_return_not_empty_and_exact_user(Boolean autoUpdate) throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var user = new User(String.valueOf(currentLocalId),
                firstNameExpected,
                lastNameExpected,
                displayNameExpected,
                photoUrlExpected,
                emailExpected,
                Timestamp.now(),
                false);
        var result = service.assignUser(user, autoUpdate);
        assertTrue(result.isPresent());
        assertEquals(firstNameExpected, result.get().getFirstName());
        assertEquals(lastNameExpected, result.get().getLastName());
        assertEquals(firstNameExpected + " " + lastNameExpected, result.get().getFullName());
        assertEquals(displayNameExpected, result.get().getDisplayName());
        assertEquals(emailExpected, result.get().getEmail());
        assertEquals(photoUrlExpected, result.get().getPhotoUrl());
        assertTrue(result.get().isNewUser());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @NullSource
    void assignUser_Pass_null_user_and_True_should_return_empty(Boolean autoUpdate) throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var result = service.assignUser(null, autoUpdate);
        assertTrue(result.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @NullSource
    void assignUser_Pass_valid_user_with_empty_localId_should_throw_InvalidUserInformationException(Boolean autoUpdateParam) {
        var user = new User("",
                firstNameExpected,
                lastNameExpected,
                displayNameExpected,
                photoUrlExpected,
                emailExpected,
                Timestamp.now(),
                false);
        assertThrows(InvalidUserInformationException.class, () -> service.assignUser(user, autoUpdateParam));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @NullSource
    void assignUser_Pass_valid_user_with_null_localId_should_throw_InvalidUserInformationException(Boolean autoUpdateParam) {
        var user = new User(null,
                firstNameExpected,
                lastNameExpected,
                displayNameExpected,
                photoUrlExpected,
                emailExpected,
                Timestamp.now(),
                false);
        assertThrows(InvalidUserInformationException.class, () -> service.assignUser(user, autoUpdateParam));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @NullSource
    void assignUser_Pass_valid_user_with_empty_first_name(Boolean autoUpdate) throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var user = new User(String.valueOf(currentLocalId),
                "",
                lastNameExpected,
                displayNameExpected,
                photoUrlExpected,
                emailExpected,
                Timestamp.now(),
                false);
        var result = service.assignUser(user, autoUpdate);
        assertTrue(result.isPresent());
        assertEquals("", result.get().getFirstName());
        assertEquals(lastNameExpected, result.get().getLastName());
        assertEquals(lastNameExpected, result.get().getFullName());
        assertEquals(displayNameExpected, result.get().getDisplayName());
        assertEquals(emailExpected, result.get().getEmail());
        assertEquals(photoUrlExpected, result.get().getPhotoUrl());
        assertNotNull(result.get().getAssignTimestamp());
        assertTrue(result.get().isNewUser());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @NullSource
    void assignUser_Pass_valid_user_with_null_first_name(Boolean autoUpdate) throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var lastNameExpected = "Wick";
        var displayNameExpected = "Johnny Week";
        var photoUrlExpected = "http:somewhere.to.cats.jpg";
        var emailExpected = "johnwick@yahoo.com";
        var user = new User(String.valueOf(currentLocalId),
                null,
                lastNameExpected,
                displayNameExpected,
                photoUrlExpected,
                emailExpected,
                Timestamp.now(),
                false);
        var result = service.assignUser(user, autoUpdate);
        assertTrue(result.isPresent());
        assertNull(result.get().getFirstName());
        assertEquals(lastNameExpected, result.get().getLastName());
        assertEquals(lastNameExpected, result.get().getFullName());
        assertEquals(displayNameExpected, result.get().getDisplayName());
        assertEquals(emailExpected, result.get().getEmail());
        assertEquals(photoUrlExpected, result.get().getPhotoUrl());
        assertNotNull(result.get().getAssignTimestamp());
        assertTrue(result.get().isNewUser());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @NullSource
    void assignUser_Pass_valid_user_with_empty_last_name(Boolean autoUpdate) throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var user = new User(String.valueOf(currentLocalId),
                firstNameExpected,
                "",
                displayNameExpected,
                photoUrlExpected,
                emailExpected,
                Timestamp.now(),
                false);
        var result = service.assignUser(user, autoUpdate);
        assertTrue(result.isPresent());
        assertEquals(firstNameExpected, result.get().getFirstName());
        assertEquals("", result.get().getLastName());
        assertEquals(firstNameExpected, result.get().getFullName());
        assertEquals(displayNameExpected, result.get().getDisplayName());
        assertEquals(emailExpected, result.get().getEmail());
        assertEquals(photoUrlExpected, result.get().getPhotoUrl());
        assertNotNull(result.get().getAssignTimestamp());
        assertTrue(result.get().isNewUser());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @NullSource
    void assignUser_Pass_valid_user_with_null_last_name(Boolean autoUpdate) throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var user = new User(String.valueOf(currentLocalId),
                firstNameExpected,
                null,
                displayNameExpected,
                photoUrlExpected,
                emailExpected,
                Timestamp.now(),
                false);
        var result = service.assignUser(user, autoUpdate);
        assertTrue(result.isPresent());
        assertEquals(firstNameExpected, result.get().getFirstName());
        assertNull(result.get().getLastName());
        assertEquals(firstNameExpected, result.get().getFullName());
        assertEquals(displayNameExpected, result.get().getDisplayName());
        assertEquals(emailExpected, result.get().getEmail());
        assertEquals(photoUrlExpected, result.get().getPhotoUrl());
        assertNotNull(result.get().getAssignTimestamp());
        assertTrue(result.get().isNewUser());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @NullSource
    void assignUser_Pass_valid_user_with_empty_display_name_should_throw_InvalidUserInformationException(Boolean autoUpdate) throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var user = new User(String.valueOf(currentLocalId),
                firstNameExpected,
                lastNameExpected,
                "",
                photoUrlExpected,
                emailExpected,
                Timestamp.now(),
                false);
        assertThrows(InvalidUserInformationException.class, () -> service.assignUser(user, autoUpdate));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @NullSource
    void assignUser_Pass_valid_user_with_null_display_name_should_throw_InvalidUserInformationException(Boolean autoUpdate) {
        var user = new User(String.valueOf(currentLocalId),
                firstNameExpected,
                lastNameExpected,
                null,
                photoUrlExpected,
                emailExpected,
                Timestamp.now(),
                false);
        assertThrows(InvalidUserInformationException.class, () -> service.assignUser(user, autoUpdate));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @NullSource
    void assignUser_Pass_valid_user_with_null_email_should_throw_InvalidUserInformationException(Boolean autoUpdate) {
        var user = new User(String.valueOf(currentLocalId),
                firstNameExpected,
                lastNameExpected,
                displayNameExpected,
                photoUrlExpected,
                null,
                Timestamp.now(),
                false);
        assertThrows(InvalidUserInformationException.class, () -> service.assignUser(user, autoUpdate));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @NullSource
    void assignUser_Pass_valid_user_with_empty_email_should_throw_InvalidUserInformationException(Boolean autoUpdate) {
        var user = new User(String.valueOf(currentLocalId),
                firstNameExpected,
                lastNameExpected,
                displayNameExpected,
                photoUrlExpected,
                "",
                Timestamp.now(),
                false);
        assertThrows(InvalidUserInformationException.class, () -> service.assignUser(user, autoUpdate));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @NullSource
    void assignUser_Pass_valid_user_with_null_photoUrl(Boolean autoUpdate) throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var user = new User(String.valueOf(currentLocalId),
                firstNameExpected,
                lastNameExpected,
                displayNameExpected,
                null,
                emailExpected,
                Timestamp.now(),
                false);
        var result = service.assignUser(user, autoUpdate);
        assertTrue(result.isPresent());
        assertEquals(firstNameExpected, result.get().getFirstName());
        assertEquals(lastNameExpected, result.get().getLastName());
        assertEquals(firstNameExpected + " " + lastNameExpected, result.get().getFullName());
        assertEquals(displayNameExpected, result.get().getDisplayName());
        assertEquals(emailExpected, result.get().getEmail());
        assertNull(result.get().getPhotoUrl());
        assertNotNull(result.get().getAssignTimestamp());
        assertTrue(result.get().isNewUser());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @NullSource
    void assignUser_Pass_valid_user_with_empty_photoUrl(Boolean autoUpdate) throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var user = new User(String.valueOf(currentLocalId),
                firstNameExpected,
                lastNameExpected,
                displayNameExpected,
                "",
                emailExpected,
                Timestamp.now(),
                false);
        var result = service.assignUser(user, autoUpdate);
        assertTrue(result.isPresent());
        assertEquals(firstNameExpected, result.get().getFirstName());
        assertEquals(lastNameExpected, result.get().getLastName());
        assertEquals(firstNameExpected + " " + lastNameExpected, result.get().getFullName());
        assertEquals(displayNameExpected, result.get().getDisplayName());
        assertEquals(emailExpected, result.get().getEmail());
        assertEquals("", result.get().getPhotoUrl());
        assertNotNull(result.get().getAssignTimestamp());
        assertTrue(result.get().isNewUser());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @NullSource
    void assignUserUPDATE_Pass_existing_localId_null_other_fields_should_return_result(Boolean autoUpdate) throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var user = new User(String.valueOf(currentLocalId),
                firstNameExpected,
                lastNameExpected,
                displayNameExpected,
                photoUrlExpected,
                emailExpected,
                Timestamp.now(),
                false);

        var created = service.assignUser(user, autoUpdate);
        assertTrue(created.isPresent());
        assertTrue(created.get().isNewUser());
        Thread.sleep(500);
        var retest = service.assignUser(
                new User(String.valueOf(currentLocalId),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        false), autoUpdate);
        assertTrue(retest.isPresent());
        assertEquals(String.valueOf(currentLocalId), retest.get().getLocalId());
        assertEquals(firstNameExpected, retest.get().getFirstName());
        assertEquals(lastNameExpected, retest.get().getLastName());
        assertEquals(firstNameExpected + " " + lastNameExpected, retest.get().getFullName());
        assertEquals(displayNameExpected, retest.get().getDisplayName());
        assertEquals(emailExpected, retest.get().getEmail());
        assertEquals(photoUrlExpected, retest.get().getPhotoUrl());
        assertNotNull(retest.get().getAssignTimestamp());
        assertFalse(retest.get().isNewUser());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @NullSource
    void assignUserUPDATE_Pass_existing_user_with_same_first_name(Boolean autoUpdate) throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var user = new User(String.valueOf(currentLocalId),
                firstNameExpected,
                lastNameExpected,
                displayNameExpected,
                photoUrlExpected,
                emailExpected,
                Timestamp.now(),
                false);

        var created = service.assignUser(user, autoUpdate);
        assertTrue(created.isPresent());
        assertTrue(created.get().isNewUser());
        Thread.sleep(500);
        var retest = service.assignUser(
                new User(String.valueOf(currentLocalId),
                        firstNameExpected,
                        null,
                        null,
                        null,
                        null,
                        null,
                        false), autoUpdate);
        assertTrue(retest.isPresent());
        assertEquals(String.valueOf(currentLocalId), retest.get().getLocalId());
        assertEquals(firstNameExpected, retest.get().getFirstName());
        assertEquals(lastNameExpected, retest.get().getLastName());
        assertEquals(firstNameExpected + " " + lastNameExpected, retest.get().getFullName());
        assertEquals(displayNameExpected, retest.get().getDisplayName());
        assertEquals(emailExpected, retest.get().getEmail());
        assertEquals(photoUrlExpected, retest.get().getPhotoUrl());
        assertNotNull(retest.get().getAssignTimestamp());
        assertFalse(retest.get().isNewUser());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @NullSource
    void assignUserUPDATE_Pass_existing_user_with_empty_first_name(Boolean autoUpdate) throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var user = new User(String.valueOf(currentLocalId),
                firstNameExpected,
                lastNameExpected,
                displayNameExpected,
                photoUrlExpected,
                emailExpected,
                Timestamp.now(),
                false);

        var created = service.assignUser(user, autoUpdate);
        assertTrue(created.isPresent());
        assertTrue(created.get().isNewUser());
        Thread.sleep(500);
        var retest = service.assignUser(
                new User(String.valueOf(currentLocalId),
                        "",
                        null,
                        null,
                        null,
                        null,
                        null,
                        false), autoUpdate);
        assertTrue(retest.isPresent());
        assertEquals(String.valueOf(currentLocalId), retest.get().getLocalId());
        assertEquals(firstNameExpected, retest.get().getFirstName());
        assertEquals(lastNameExpected, retest.get().getLastName());
        assertEquals(firstNameExpected + " " + lastNameExpected, retest.get().getFullName());
        assertEquals(displayNameExpected, retest.get().getDisplayName());
        assertEquals(emailExpected, retest.get().getEmail());
        assertEquals(photoUrlExpected, retest.get().getPhotoUrl());
        assertNotNull(retest.get().getAssignTimestamp());
        assertFalse(retest.get().isNewUser());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @NullSource
    void assignUserUPDATE_Pass_existing_user_with_same_last_name(Boolean autoUpdate) throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var user = new User(String.valueOf(currentLocalId),
                firstNameExpected,
                lastNameExpected,
                displayNameExpected,
                photoUrlExpected,
                emailExpected,
                Timestamp.now(),
                false);

        var created = service.assignUser(user, autoUpdate);
        assertTrue(created.isPresent());
        assertTrue(created.get().isNewUser());
        Thread.sleep(500);
        var retest = service.assignUser(
                new User(String.valueOf(currentLocalId),
                        null,
                        lastNameExpected,
                        null,
                        null,
                        null,
                        null,
                        false), autoUpdate);
        assertTrue(retest.isPresent());
        assertEquals(String.valueOf(currentLocalId), retest.get().getLocalId());
        assertEquals(firstNameExpected, retest.get().getFirstName());
        assertEquals(lastNameExpected, retest.get().getLastName());
        assertEquals(firstNameExpected + " " + lastNameExpected, retest.get().getFullName());
        assertEquals(displayNameExpected, retest.get().getDisplayName());
        assertEquals(emailExpected, retest.get().getEmail());
        assertEquals(photoUrlExpected, retest.get().getPhotoUrl());
        assertNotNull(retest.get().getAssignTimestamp());
        assertFalse(retest.get().isNewUser());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @NullSource
    void assignUserUPDATE_Pass_existing_user_with_empty_last_name(Boolean autoUpdate) throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var user = new User(String.valueOf(currentLocalId),
                firstNameExpected,
                lastNameExpected,
                displayNameExpected,
                photoUrlExpected,
                emailExpected,
                Timestamp.now(),
                false);

        var created = service.assignUser(user, autoUpdate);
        assertTrue(created.isPresent());
        assertTrue(created.get().isNewUser());
        Thread.sleep(500);
        var retest = service.assignUser(
                new User(String.valueOf(currentLocalId),
                        null,
                        "",
                        null,
                        null,
                        null,
                        null,
                        false), autoUpdate);
        assertTrue(retest.isPresent());
        assertEquals(String.valueOf(currentLocalId), retest.get().getLocalId());
        assertEquals(firstNameExpected, retest.get().getFirstName());
        assertEquals(lastNameExpected, retest.get().getLastName());
        assertEquals(firstNameExpected + " " + lastNameExpected, retest.get().getFullName());
        assertEquals(displayNameExpected, retest.get().getDisplayName());
        assertEquals(emailExpected, retest.get().getEmail());
        assertEquals(photoUrlExpected, retest.get().getPhotoUrl());
        assertNotNull(retest.get().getAssignTimestamp());
        assertFalse(retest.get().isNewUser());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @NullSource
    void assignUserUPDATE_Pass_existing_user_with_same_email(Boolean autoUpdate) throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var user = new User(String.valueOf(currentLocalId),
                firstNameExpected,
                lastNameExpected,
                displayNameExpected,
                photoUrlExpected,
                emailExpected,
                Timestamp.now(),
                false);

        var created = service.assignUser(user, autoUpdate);
        assertTrue(created.isPresent());
        assertTrue(created.get().isNewUser());
        Thread.sleep(500);
        var retest = service.assignUser(
                new User(String.valueOf(currentLocalId),
                        null,
                        null,
                        null,
                        null,
                        emailExpected,
                        null,
                        false), autoUpdate);
        assertTrue(retest.isPresent());
        assertEquals(String.valueOf(currentLocalId), retest.get().getLocalId());
        assertEquals(firstNameExpected, retest.get().getFirstName());
        assertEquals(lastNameExpected, retest.get().getLastName());
        assertEquals(firstNameExpected + " " + lastNameExpected, retest.get().getFullName());
        assertEquals(displayNameExpected, retest.get().getDisplayName());
        assertEquals(emailExpected, retest.get().getEmail());
        assertEquals(photoUrlExpected, retest.get().getPhotoUrl());
        assertNotNull(retest.get().getAssignTimestamp());
        assertFalse(retest.get().isNewUser());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @NullSource
    void assignUserUPDATE_Pass_existing_user_with_empty_email(Boolean autoUpdate) throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var user = new User(String.valueOf(currentLocalId),
                firstNameExpected,
                lastNameExpected,
                displayNameExpected,
                photoUrlExpected,
                emailExpected,
                Timestamp.now(),
                false);

        var created = service.assignUser(user, autoUpdate);
        assertTrue(created.isPresent());
        assertTrue(created.get().isNewUser());
        Thread.sleep(500);
        var retest = service.assignUser(
                new User(String.valueOf(currentLocalId),
                        null,
                        null,
                        null,
                        null,
                        "",
                        null,
                        false), autoUpdate);
        assertTrue(retest.isPresent());
        assertEquals(String.valueOf(currentLocalId), retest.get().getLocalId());
        assertEquals(firstNameExpected, retest.get().getFirstName());
        assertEquals(lastNameExpected, retest.get().getLastName());
        assertEquals(firstNameExpected + " " + lastNameExpected, retest.get().getFullName());
        assertEquals(displayNameExpected, retest.get().getDisplayName());
        assertEquals(emailExpected, retest.get().getEmail());
        assertEquals(photoUrlExpected, retest.get().getPhotoUrl());
        assertNotNull(retest.get().getAssignTimestamp());
        assertFalse(retest.get().isNewUser());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @NullSource
    void assignUserUPDATE_Pass_existing_user_with_same_photoUrl(Boolean autoUpdate) throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var user = new User(String.valueOf(currentLocalId),
                firstNameExpected,
                lastNameExpected,
                displayNameExpected,
                photoUrlExpected,
                emailExpected,
                Timestamp.now(),
                false);

        var created = service.assignUser(user, autoUpdate);
        assertTrue(created.isPresent());
        assertTrue(created.get().isNewUser());
        Thread.sleep(500);
        var retest = service.assignUser(
                new User(String.valueOf(currentLocalId),
                        null,
                        null,
                        null,
                        photoUrlExpected,
                        null,
                        null,
                        false), autoUpdate);
        assertTrue(retest.isPresent());
        assertEquals(String.valueOf(currentLocalId), retest.get().getLocalId());
        assertEquals(firstNameExpected, retest.get().getFirstName());
        assertEquals(lastNameExpected, retest.get().getLastName());
        assertEquals(firstNameExpected + " " + lastNameExpected, retest.get().getFullName());
        assertEquals(displayNameExpected, retest.get().getDisplayName());
        assertEquals(emailExpected, retest.get().getEmail());
        assertEquals(photoUrlExpected, retest.get().getPhotoUrl());
        assertNotNull(retest.get().getAssignTimestamp());
        assertFalse(retest.get().isNewUser());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @NullSource
    void assignUserUPDATE_Pass_existing_user_with_empty_photoUrl(Boolean autoUpdate) throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var user = new User(String.valueOf(currentLocalId),
                firstNameExpected,
                lastNameExpected,
                displayNameExpected,
                photoUrlExpected,
                emailExpected,
                Timestamp.now(),
                false);

        var created = service.assignUser(user, autoUpdate);
        assertTrue(created.isPresent());
        assertTrue(created.get().isNewUser());
        Thread.sleep(500);
        var retest = service.assignUser(
                new User(String.valueOf(currentLocalId),
                        null,
                        null,
                        null,
                        "",
                        null,
                        null,
                        false), autoUpdate);
        assertTrue(retest.isPresent());
        assertEquals(String.valueOf(currentLocalId), retest.get().getLocalId());
        assertEquals(firstNameExpected, retest.get().getFirstName());
        assertEquals(lastNameExpected, retest.get().getLastName());
        assertEquals(firstNameExpected + " " + lastNameExpected, retest.get().getFullName());
        assertEquals(displayNameExpected, retest.get().getDisplayName());
        assertEquals(emailExpected, retest.get().getEmail());
        assertEquals(photoUrlExpected, retest.get().getPhotoUrl());
        assertNotNull(retest.get().getAssignTimestamp());
        assertFalse(retest.get().isNewUser());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true})
    @NullSource
    void assignUserUPDATE_Pass_existing_user_with_different_first_name_allow_auto_update(Boolean autoUpdate) throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var user = new User(String.valueOf(currentLocalId),
                firstNameExpected,
                lastNameExpected,
                displayNameExpected,
                photoUrlExpected,
                emailExpected,
                Timestamp.now(),
                false);

        var created = service.assignUser(user, autoUpdate);
        assertTrue(created.isPresent());
        assertTrue(created.get().isNewUser());
        Thread.sleep(500);
        var retest = service.assignUser(
                new User(String.valueOf(currentLocalId),
                        "Bruce",
                        null,
                        null,
                        null,
                        null,
                        null,
                        false), autoUpdate);
        assertTrue(retest.isPresent());
        assertEquals(String.valueOf(currentLocalId), retest.get().getLocalId());
        assertEquals("Bruce", retest.get().getFirstName());
        assertEquals(lastNameExpected, retest.get().getLastName());
        assertEquals("Bruce " + lastNameExpected, retest.get().getFullName());
        assertEquals(displayNameExpected, retest.get().getDisplayName());
        assertEquals(emailExpected, retest.get().getEmail());
        assertEquals(photoUrlExpected, retest.get().getPhotoUrl());
        assertNotNull(retest.get().getAssignTimestamp());
        assertFalse(retest.get().isNewUser());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @NullSource
    void assignUserUPDATE_Pass_existing_user_with_different_first_name_not_allow_auto_update(Boolean autoUpdate) throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var user = new User(String.valueOf(currentLocalId),
                firstNameExpected,
                lastNameExpected,
                displayNameExpected,
                photoUrlExpected,
                emailExpected,
                Timestamp.now(),
                false);

        var created = service.assignUser(user, autoUpdate);
        assertTrue(created.isPresent());
        assertTrue(created.get().isNewUser());
        Thread.sleep(500);
        var retest = service.assignUser(
                new User(String.valueOf(currentLocalId),
                        "Bruce",
                        null,
                        null,
                        null,
                        null,
                        null,
                        false), false);
        assertTrue(retest.isPresent());
        assertEquals(String.valueOf(currentLocalId), retest.get().getLocalId());
        assertEquals(firstNameExpected, retest.get().getFirstName());
        assertEquals(lastNameExpected, retest.get().getLastName());
        assertEquals(firstNameExpected + " " + lastNameExpected, retest.get().getFullName());
        assertEquals(displayNameExpected, retest.get().getDisplayName());
        assertEquals(emailExpected, retest.get().getEmail());
        assertEquals(photoUrlExpected, retest.get().getPhotoUrl());
        assertNotNull(retest.get().getAssignTimestamp());
        assertFalse(retest.get().isNewUser());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true})
    @NullSource
    void assignUserUPDATE_Pass_existing_user_with_different_last_name_allow_auto_update(Boolean autoUpdate) throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var user = new User(String.valueOf(currentLocalId),
                firstNameExpected,
                lastNameExpected,
                displayNameExpected,
                photoUrlExpected,
                emailExpected,
                Timestamp.now(),
                false);

        var created = service.assignUser(user, autoUpdate);
        assertTrue(created.isPresent());
        assertTrue(created.get().isNewUser());
        Thread.sleep(500);
        var retest = service.assignUser(
                new User(String.valueOf(currentLocalId),
                        null,
                        "Wayne",
                        null,
                        null,
                        null,
                        null,
                        false), autoUpdate);
        assertTrue(retest.isPresent());
        assertEquals(String.valueOf(currentLocalId), retest.get().getLocalId());
        assertEquals(firstNameExpected, retest.get().getFirstName());
        assertEquals("Wayne", retest.get().getLastName());
        assertEquals(firstNameExpected + " Wayne", retest.get().getFullName());
        assertEquals(displayNameExpected, retest.get().getDisplayName());
        assertEquals(emailExpected, retest.get().getEmail());
        assertEquals(photoUrlExpected, retest.get().getPhotoUrl());
        assertNotNull(retest.get().getAssignTimestamp());
        assertFalse(retest.get().isNewUser());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @NullSource
    void assignUserUPDATE_Pass_existing_user_with_different_last_name_not_allow_auto_update(Boolean autoUpdate) throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var user = new User(String.valueOf(currentLocalId),
                firstNameExpected,
                lastNameExpected,
                displayNameExpected,
                photoUrlExpected,
                emailExpected,
                Timestamp.now(),
                false);

        var created = service.assignUser(user, autoUpdate);
        assertTrue(created.isPresent());
        assertTrue(created.get().isNewUser());
        Thread.sleep(500);
        var retest = service.assignUser(
                new User(String.valueOf(currentLocalId),
                        null,
                        "Wayne",
                        null,
                        null,
                        null,
                        null,
                        false), false);
        assertTrue(retest.isPresent());
        assertEquals(String.valueOf(currentLocalId), retest.get().getLocalId());
        assertEquals(firstNameExpected, retest.get().getFirstName());
        assertEquals(lastNameExpected, retest.get().getLastName());
        assertEquals(firstNameExpected + " " + lastNameExpected, retest.get().getFullName());
        assertEquals(displayNameExpected, retest.get().getDisplayName());
        assertEquals(emailExpected, retest.get().getEmail());
        assertEquals(photoUrlExpected, retest.get().getPhotoUrl());
        assertNotNull(retest.get().getAssignTimestamp());
        assertFalse(retest.get().isNewUser());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true})
    @NullSource
    void assignUserUPDATE_Pass_existing_user_with_different_email_allow_auto_update(Boolean autoUpdate) throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var user = new User(String.valueOf(currentLocalId),
                firstNameExpected,
                lastNameExpected,
                displayNameExpected,
                photoUrlExpected,
                emailExpected,
                Timestamp.now(),
                false);

        var created = service.assignUser(user, autoUpdate);
        assertTrue(created.isPresent());
        assertTrue(created.get().isNewUser());
        Thread.sleep(500);
        var retest = service.assignUser(
                new User(String.valueOf(currentLocalId),
                        null,
                        null,
                        null,
                        null,
                        "bruce_wayne@gmail.com",
                        null,
                        false), autoUpdate);
        assertTrue(retest.isPresent());
        assertEquals(String.valueOf(currentLocalId), retest.get().getLocalId());
        assertEquals(firstNameExpected, retest.get().getFirstName());
        assertEquals(lastNameExpected, retest.get().getLastName());
        assertEquals(firstNameExpected + " " + lastNameExpected, retest.get().getFullName());
        assertEquals(displayNameExpected, retest.get().getDisplayName());
        assertEquals("bruce_wayne@gmail.com", retest.get().getEmail());
        assertEquals(photoUrlExpected, retest.get().getPhotoUrl());
        assertNotNull(retest.get().getAssignTimestamp());
        assertFalse(retest.get().isNewUser());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @NullSource
    void assignUserUPDATE_Pass_existing_user_with_different_email_not_allow_auto_update(Boolean autoUpdate) throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var user = new User(String.valueOf(currentLocalId),
                firstNameExpected,
                lastNameExpected,
                displayNameExpected,
                photoUrlExpected,
                emailExpected,
                Timestamp.now(),
                false);

        var created = service.assignUser(user, autoUpdate);
        assertTrue(created.isPresent());
        assertTrue(created.get().isNewUser());
        Thread.sleep(500);
        var retest = service.assignUser(
                new User(String.valueOf(currentLocalId),
                        null,
                        null,
                        null,
                        null,
                        "bruce_wayne@gmail.com",
                        null,
                        false), false);
        assertTrue(retest.isPresent());
        assertEquals(String.valueOf(currentLocalId), retest.get().getLocalId());
        assertEquals(firstNameExpected, retest.get().getFirstName());
        assertEquals(lastNameExpected, retest.get().getLastName());
        assertEquals(firstNameExpected + " " + lastNameExpected, retest.get().getFullName());
        assertEquals(displayNameExpected, retest.get().getDisplayName());
        assertEquals(emailExpected, retest.get().getEmail());
        assertEquals(photoUrlExpected, retest.get().getPhotoUrl());
        assertNotNull(retest.get().getAssignTimestamp());
        assertFalse(retest.get().isNewUser());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true})
    @NullSource
    void assignUserUPDATE_Pass_existing_user_with_different_photoUrl_allow_auto_update(Boolean autoUpdate) throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var user = new User(String.valueOf(currentLocalId),
                firstNameExpected,
                lastNameExpected,
                displayNameExpected,
                photoUrlExpected,
                emailExpected,
                Timestamp.now(),
                false);

        var created = service.assignUser(user, autoUpdate);
        assertTrue(created.isPresent());
        assertTrue(created.get().isNewUser());
        Thread.sleep(500);
        var retest = service.assignUser(
                new User(String.valueOf(currentLocalId),
                        null,
                        null,
                        null,
                        "another:photo",
                        null,
                        null,
                        false), autoUpdate);
        assertTrue(retest.isPresent());
        assertEquals(String.valueOf(currentLocalId), retest.get().getLocalId());
        assertEquals(firstNameExpected, retest.get().getFirstName());
        assertEquals(lastNameExpected, retest.get().getLastName());
        assertEquals(firstNameExpected + " " + lastNameExpected, retest.get().getFullName());
        assertEquals(displayNameExpected, retest.get().getDisplayName());
        assertEquals(emailExpected, retest.get().getEmail());
        assertEquals("another:photo", retest.get().getPhotoUrl());
        assertNotNull(retest.get().getAssignTimestamp());
        assertFalse(retest.get().isNewUser());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @NullSource
    void assignUserUPDATE_Pass_existing_user_with_different_photoUrl_not_allow_auto_update(Boolean autoUpdate) throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var user = new User(String.valueOf(currentLocalId),
                firstNameExpected,
                lastNameExpected,
                displayNameExpected,
                photoUrlExpected,
                emailExpected,
                Timestamp.now(),
                false);

        var created = service.assignUser(user, autoUpdate);
        assertTrue(created.isPresent());
        assertTrue(created.get().isNewUser());
        Thread.sleep(500);
        var retest = service.assignUser(
                new User(String.valueOf(currentLocalId),
                        null,
                        null,
                        null,
                        "another:photo",
                        null,
                        null,
                        false), false);
        assertTrue(retest.isPresent());
        assertEquals(String.valueOf(currentLocalId), retest.get().getLocalId());
        assertEquals(firstNameExpected, retest.get().getFirstName());
        assertEquals(lastNameExpected, retest.get().getLastName());
        assertEquals(firstNameExpected + " " + lastNameExpected, retest.get().getFullName());
        assertEquals(displayNameExpected, retest.get().getDisplayName());
        assertEquals(emailExpected, retest.get().getEmail());
        assertEquals(photoUrlExpected, retest.get().getPhotoUrl());
        assertNotNull(retest.get().getAssignTimestamp());
        assertFalse(retest.get().isNewUser());
    }

    // TODO performance test.
}
