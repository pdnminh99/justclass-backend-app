package com.projecta.eleven.justclassbackend.user_test;

import com.google.cloud.Timestamp;
import com.projecta.eleven.justclassbackend.junit_config.CustomReplaceUnderscore;
import com.projecta.eleven.justclassbackend.user.MinifiedUser;
import com.projecta.eleven.justclassbackend.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayNameGeneration(CustomReplaceUnderscore.class)
@DisplayName("Unit Tests for UserResponseBody")
public class UserRequestBodyTest {

    protected final String sampleLocalId = UUID.randomUUID().toString();
    protected final String sampleFirstName = "John";
    protected final String sampleLastName = "Wick";
    protected final String sampleDisplayName = "John Wick";
    protected final String samplePhotoUrl = "http://path.to.his.favourite.dog.jpg";
    protected final String sampleEmail = "i_love_pets@yahoo.com";
    protected final Timestamp sampleAssignDatetime = Timestamp.now();
    protected final boolean sampleIsNewUser = false;

    private void assertInitiateUserDoesNotThrowAnyException(
            String localId,
            String firstName,
            String lastName,
            String displayName,
            String photoUrl,
            String email,
            Timestamp assignDatetime,
            boolean isNewUser
    ) {
        assertDoesNotThrow(() -> new User(
                localId,
                firstName,
                lastName,
                displayName,
                photoUrl,
                email,
                assignDatetime,
                isNewUser));
    }

    /**
     * Inheritance test methods
     */
    @Test
    void Super_It_should_extends_MinifiedUser() {
        MinifiedUser userResponseBody = new User(
                sampleLocalId,
                sampleFirstName,
                sampleLastName,
                sampleDisplayName,
                samplePhotoUrl,
                sampleEmail,
                sampleAssignDatetime,
                sampleIsNewUser);
        assertNotNull(userResponseBody);
    }

    /**
     * Constructor test methods
     */
    @Test
    void Constructor_Create_instance_with_no_Null() {
        User user = new User(
                sampleLocalId,
                sampleFirstName,
                sampleLastName,
                sampleDisplayName,
                samplePhotoUrl,
                sampleEmail,
                sampleAssignDatetime,
                sampleIsNewUser);
        assertNotNull(user);
    }

    @Test
    void Constructor_Create_instance_with_null_localId_shall_not_throw_exception() {
        assertInitiateUserDoesNotThrowAnyException(
                null,
                sampleFirstName,
                sampleLastName,
                sampleDisplayName,
                samplePhotoUrl,
                sampleEmail,
                sampleAssignDatetime,
                sampleIsNewUser
        );
    }

    @Test
    void Constructor_Create_instance_with_Null_first_name_is_acceptable() {
        assertInitiateUserDoesNotThrowAnyException(
                sampleLocalId,
                null,
                sampleLastName,
                sampleDisplayName,
                samplePhotoUrl,
                sampleEmail,
                sampleAssignDatetime,
                sampleIsNewUser);
    }

    @Test
    void Constructor_Create_instance_with_null_first_name_shall_return_null_getter() {
        User user = new User(sampleLocalId,
                null,
                sampleLastName,
                sampleDisplayName,
                samplePhotoUrl,
                sampleEmail,
                sampleAssignDatetime,
                sampleIsNewUser);
        assertNull(user.getFirstName());
    }

    @Test
    void Constructor_Create_instance_with_empty_string_first_name_is_acceptable() {
        assertInitiateUserDoesNotThrowAnyException(
                sampleLocalId,
                "",
                sampleLastName,
                sampleDisplayName,
                samplePhotoUrl,
                sampleEmail,
                sampleAssignDatetime,
                sampleIsNewUser);
    }

    @Test
    void Constructor_Create_instance_with_empty_string_first_name_shall_return_empty_string() {
        User user = new User(sampleLocalId,
                "",
                sampleLastName,
                sampleDisplayName,
                samplePhotoUrl,
                sampleEmail,
                sampleAssignDatetime,
                sampleIsNewUser);
        assertEquals("", user.getFirstName());
    }

    @Test
    void Constructor_Create_instance_with_Null_last_name_is_acceptable() {
        assertInitiateUserDoesNotThrowAnyException(
                sampleLocalId,
                sampleFirstName,
                null,
                sampleDisplayName,
                samplePhotoUrl,
                sampleEmail,
                sampleAssignDatetime,
                sampleIsNewUser);
    }

    @Test
    void Constructor_Create_instance_with_Null_last_name_shall_returns_null_getter() {
        User user = new User(
                sampleLocalId,
                sampleFirstName,
                null,
                sampleDisplayName,
                samplePhotoUrl,
                sampleEmail,
                sampleAssignDatetime,
                sampleIsNewUser);
        assertNull(user.getLastName());
    }

    @Test
    void Constructor_Create_instance_with_empty_string_last_name_is_acceptable() {
        assertInitiateUserDoesNotThrowAnyException(
                sampleLocalId,
                sampleFirstName,
                "",
                sampleDisplayName,
                samplePhotoUrl,
                sampleEmail,
                sampleAssignDatetime,
                sampleIsNewUser);
    }

    @Test
    void Constructor_Create_instance_with_empty_string_last_name_shall_return_empty_string() {
        User user = new User(sampleLocalId,
                sampleFirstName,
                "",
                sampleDisplayName,
                samplePhotoUrl,
                sampleEmail,
                sampleAssignDatetime,
                sampleIsNewUser);
        assertEquals("", user.getLastName());
    }

    @Test
    void Constructor_Create_instance_with_null_display_name_is_acceptable() {
        assertInitiateUserDoesNotThrowAnyException(
                sampleLocalId,
                sampleFirstName,
                sampleLastName,
                null,
                samplePhotoUrl,
                sampleEmail,
                sampleAssignDatetime,
                sampleIsNewUser
        );
    }

    @Test
    void Constructor_Create_instance_with_empty_string_display_name_is_acceptable() {
        assertInitiateUserDoesNotThrowAnyException(
                sampleLocalId,
                sampleFirstName,
                sampleLastName,
                "",
                samplePhotoUrl,
                sampleEmail,
                sampleAssignDatetime,
                sampleIsNewUser
        );
    }

    @Test
    void Constructor_Create_instance_with_null_display_name_shall_return_null_getter() {
        User user = new User(sampleLocalId,
                sampleFirstName,
                sampleLastName,
                null,
                samplePhotoUrl,
                sampleEmail,
                sampleAssignDatetime,
                sampleIsNewUser);
        assertNull(user.getDisplayName());
    }

    @Test
    void Constructor_Create_instance_with_empty_string_display_name_shall_return_empty_string() {
        User user = new User(sampleLocalId,
                sampleFirstName,
                sampleLastName,
                "",
                samplePhotoUrl,
                sampleEmail,
                sampleAssignDatetime,
                sampleIsNewUser);
        assertEquals("", user.getDisplayName());
    }

    @Test
    void Constructor_Create_instance_with_Null_photoUrl_is_acceptable() {
        assertInitiateUserDoesNotThrowAnyException(
                sampleLocalId,
                sampleFirstName,
                sampleLastName,
                sampleDisplayName,
                null,
                sampleEmail,
                sampleAssignDatetime,
                sampleIsNewUser
        );
    }


    @Test
    void Constructor_Create_instance_with_empty_string_photoUrl_is_acceptable() {
        assertInitiateUserDoesNotThrowAnyException(
                sampleLocalId,
                sampleFirstName,
                sampleLastName,
                sampleDisplayName,
                "",
                sampleEmail,
                sampleAssignDatetime,
                sampleIsNewUser
        );
    }

    @Test
    void Constructor_Create_instance_with_null_photoUrl_shall_return_null_getter() {
        User user = new User(sampleLocalId,
                sampleFirstName,
                sampleLastName,
                sampleDisplayName,
                null,
                sampleEmail,
                sampleAssignDatetime,
                sampleIsNewUser);
        assertNull(user.getPhotoUrl());
    }

    @Test
    void Constructor_Create_instance_with_empty_string_photoUrl_shall_return_empty_string() {
        User user = new User(sampleLocalId,
                sampleFirstName,
                sampleLastName,
                sampleDisplayName,
                "",
                sampleEmail,
                sampleAssignDatetime,
                sampleIsNewUser);
        assertEquals("", user.getPhotoUrl());
    }

    @Test
    void Constructor_Create_instance_with_Null_email_is_acceptable() {
        assertInitiateUserDoesNotThrowAnyException(
                sampleLocalId,
                sampleFirstName,
                sampleLastName,
                sampleDisplayName,
                samplePhotoUrl,
                null,
                sampleAssignDatetime,
                sampleIsNewUser
        );
    }

    @Test
    void Constructor_Create_instance_with_empty_string_email_is_acceptable() {
        assertInitiateUserDoesNotThrowAnyException(
                sampleLocalId,
                sampleFirstName,
                sampleLastName,
                sampleDisplayName,
                samplePhotoUrl,
                "",
                sampleAssignDatetime,
                sampleIsNewUser
        );
    }

    @Test
    void Constructor_Create_instance_with_null_email_shall_return_null_getter() {
        User user = new User(sampleLocalId,
                sampleFirstName,
                sampleLastName,
                sampleDisplayName,
                samplePhotoUrl,
                null,
                sampleAssignDatetime,
                sampleIsNewUser);
        assertNull(user.getEmail());
    }

    @Test
    void Constructor_Create_instance_with_empty_string_email_shall_return_empty_string() {
        User user = new User(sampleLocalId,
                sampleFirstName,
                sampleLastName,
                sampleDisplayName,
                samplePhotoUrl,
                "",
                sampleAssignDatetime,
                sampleIsNewUser);
        assertEquals("", user.getEmail());
    }

}
