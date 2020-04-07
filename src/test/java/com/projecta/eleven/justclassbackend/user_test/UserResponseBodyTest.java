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
public class UserResponseBodyTest {

    protected final String sampleLocalId = UUID.randomUUID().toString();
    protected final String sampleFirstName = "John";
    protected final String sampleLastName = "Wick";
    protected final String sampleFullName = "John Wick";
    protected final String sampleDisplayName = "John Wick";
    protected final String samplePhotoUrl = "http://path.to.his.favourite.dog.jpg";
    protected final String sampleEmail = "i_love_pets@yahoo.com";
    protected final Timestamp sampleAssignDatetime = Timestamp.now();
    protected final boolean sampleIsNewUser = false;

    private void assertInitiateUserDoesNotThrowAnyException(
            String localId,
            String firstName,
            String lastName,
            String fullName,
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
                fullName,
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
                sampleFullName,
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
                sampleFullName,
                sampleDisplayName,
                samplePhotoUrl,
                sampleEmail,
                sampleAssignDatetime,
                sampleIsNewUser);
        assertNotNull(user);
    }

    @Test
    void Constructor_Create_instance_with_Null_LocalId_shall_throw_NullPointerException() {
        assertInitiateUserDoesNotThrowAnyException(
                null,
                sampleFirstName,
                sampleLastName,
                sampleFullName,
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
                sampleFullName,
                sampleDisplayName,
                samplePhotoUrl,
                sampleEmail,
                sampleAssignDatetime,
                sampleIsNewUser);
    }

    @Test
    void Constructor_Create_instance_with_Null_first_name_is_considered_as_empty_string() {
        User user = new User(sampleLocalId,
                null,
                sampleLastName,
                sampleFullName,
                sampleDisplayName,
                samplePhotoUrl,
                sampleEmail,
                sampleAssignDatetime,
                sampleIsNewUser);
        assertEquals("", user.getFirstName());
    }

    @Test
    void Constructor_Create_instance_with_empty_string_first_name_is_acceptable() {
        assertInitiateUserDoesNotThrowAnyException(
                sampleLocalId,
                "",
                sampleLastName,
                sampleFullName,
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
                sampleFullName,
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
                sampleFullName,
                sampleDisplayName,
                samplePhotoUrl,
                sampleEmail,
                sampleAssignDatetime,
                sampleIsNewUser);
    }

    @Test
    void Constructor_Create_instance_with_Null_last_name_is_considered_as_empty_string() {
        User user = new User(
                sampleLocalId,
                sampleFirstName,
                null,
                sampleFullName,
                sampleDisplayName,
                samplePhotoUrl,
                sampleEmail,
                sampleAssignDatetime,
                sampleIsNewUser);
        assertEquals("", user.getLastName());
    }

    @Test
    void Constructor_Create_instance_with_empty_string_last_name_is_acceptable() {
        assertInitiateUserDoesNotThrowAnyException(
                sampleLocalId,
                sampleFirstName,
                "",
                sampleFullName,
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
                sampleFullName,
                sampleDisplayName,
                samplePhotoUrl,
                sampleEmail,
                sampleAssignDatetime,
                sampleIsNewUser);
        assertEquals("", user.getLastName());
    }

    @Test
    void Constructor_Create_instance_with_Null_full_name_is_acceptable() {
        assertInitiateUserDoesNotThrowAnyException(
                sampleLocalId,
                sampleFirstName,
                sampleLastName,
                null,
                sampleDisplayName,
                samplePhotoUrl,
                sampleEmail,
                sampleAssignDatetime,
                sampleIsNewUser);
    }

    @Test
    void Constructor_Create_instance_with_Null_full_name_is_considered_as_first_and_last_name_concatenated_together() {
        User user = new User(sampleLocalId,
                sampleFirstName,
                sampleLastName,
                null,
                sampleDisplayName,
                samplePhotoUrl,
                sampleEmail,
                sampleAssignDatetime,
                sampleIsNewUser);
        String expectedFullName = user.getFirstName() + " " + user.getLastName();
        assertEquals(expectedFullName, user.getFullName());
    }

    @Test
    void Constructor_Create_instance_with_empty_string_full_name_is_acceptable() {
        assertInitiateUserDoesNotThrowAnyException(
                sampleLocalId,
                sampleFirstName,
                sampleLastName,
                "",
                sampleDisplayName,
                samplePhotoUrl,
                sampleEmail,
                sampleAssignDatetime,
                sampleIsNewUser);
    }

    @Test
    void Constructor_Create_instance_with_empty_string_full_name_is_considered_as_first_and_last_name_concatenated_together() {
        User user = new User(sampleLocalId,
                sampleFirstName,
                sampleLastName,
                "",
                sampleDisplayName,
                samplePhotoUrl,
                sampleEmail,
                sampleAssignDatetime,
                sampleIsNewUser);
        String expectedFullName = user.getFirstName() + " " + user.getLastName();
        assertEquals(expectedFullName, user.getFullName());
    }

    @Test
    void Constructor_Full_name_shall_take_precedence_over_first_and_last_names_concatenated_together() {
        User user = new User(sampleLocalId,
                "John",
                "Wick",
                "Mr.Bean",
                sampleDisplayName,
                samplePhotoUrl,
                sampleEmail,
                sampleAssignDatetime,
                sampleIsNewUser);
        assertEquals("Mr.Bean", user.getFullName());
    }

    @Test
    void Constructor_Create_instance_with_null_display_name_is_acceptable() {
        assertInitiateUserDoesNotThrowAnyException(
                sampleLocalId,
                sampleFirstName,
                sampleLastName,
                sampleFullName,
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
                sampleFullName,
                "",
                samplePhotoUrl,
                sampleEmail,
                sampleAssignDatetime,
                sampleIsNewUser
        );
    }

    @Test
    void Constructor_Create_instance_with_null_display_name_shall_return_empty_string() {
        User user = new User(sampleLocalId,
                sampleFirstName,
                sampleLastName,
                sampleFullName,
                null,
                samplePhotoUrl,
                sampleEmail,
                sampleAssignDatetime,
                sampleIsNewUser);
        assertEquals("", user.getDisplayName());
    }

    @Test
    void Constructor_Create_instance_with_empty_string_display_name_shall_return_empty_string() {
        User user = new User(sampleLocalId,
                sampleFirstName,
                sampleLastName,
                sampleFullName,
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
                sampleFullName,
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
                sampleFullName,
                sampleDisplayName,
                "",
                sampleEmail,
                sampleAssignDatetime,
                sampleIsNewUser
        );
    }

    @Test
    void Constructor_Create_instance_with_Null_photoUrl_shall_return_empty_string() {
        User user = new User(sampleLocalId,
                sampleFirstName,
                sampleLastName,
                sampleFullName,
                sampleDisplayName,
                null,
                sampleEmail,
                sampleAssignDatetime,
                sampleIsNewUser);
        assertEquals("", user.getPhotoUrl());
    }

    @Test
    void Constructor_Create_instance_with_empty_string_photoUrl_shall_return_empty_string() {
        User user = new User(sampleLocalId,
                sampleFirstName,
                sampleLastName,
                sampleFullName,
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
                sampleFullName,
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
                sampleFullName,
                sampleDisplayName,
                samplePhotoUrl,
                "",
                sampleAssignDatetime,
                sampleIsNewUser
        );
    }

    @Test
    void Constructor_Create_instance_with_Null_email_shall_return_empty_string() {
        User user = new User(sampleLocalId,
                sampleFirstName,
                sampleLastName,
                sampleFullName,
                sampleDisplayName,
                samplePhotoUrl,
                null,
                sampleAssignDatetime,
                sampleIsNewUser);
        assertEquals("", user.getEmail());
    }

    @Test
    void Constructor_Create_instance_with_empty_string_email_shall_return_empty_string() {
        User user = new User(sampleLocalId,
                sampleFirstName,
                sampleLastName,
                sampleFullName,
                sampleDisplayName,
                samplePhotoUrl,
                "",
                sampleAssignDatetime,
                sampleIsNewUser);
        assertEquals("", user.getEmail());
    }

}
