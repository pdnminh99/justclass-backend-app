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
    void Constructor_Create_instance_should_not_null() {
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
    void Constructor_Create_instance_with_null_first_name_is_acceptable() {
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
    void Constructor_Create_instance_with_null_last_name_is_acceptable() {
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
    void Constructor_Create_instance_with_null_last_name_shall_returns_null_getter() {
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
    void Constructor_Create_instance_with_null_photoUrl_is_acceptable() {
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
    void Constructor_Create_instance_with_null_email_is_acceptable() {
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

    /**
     * Tests on setters
     */
    @Test
    void setEmail_Set_normal_string_should_return_normal_string_getter() {
        User user = new User(sampleLocalId,
                sampleFirstName,
                sampleLastName,
                sampleDisplayName,
                samplePhotoUrl,
                sampleEmail,
                sampleAssignDatetime,
                sampleIsNewUser);
        user.setEmail("another@me.com");
        assertEquals("another@me.com", user.getEmail());
    }

    @Test
    void setEmail_Set_null_should_return_null_getter() {
        User user = new User(sampleLocalId,
                sampleFirstName,
                sampleLastName,
                sampleDisplayName,
                samplePhotoUrl,
                sampleEmail,
                sampleAssignDatetime,
                sampleIsNewUser);
        user.setEmail(null);
        assertNull(user.getEmail());
    }

    @Test
    void setEmail_Set_empty_string_should_return_empty_string_getter() {
        User user = new User(sampleLocalId,
                sampleFirstName,
                sampleLastName,
                sampleDisplayName,
                samplePhotoUrl,
                sampleEmail,
                sampleAssignDatetime,
                sampleIsNewUser);
        user.setEmail("");
        assertEquals("", user.getEmail());
    }

    @Test
    void setFirstName_Set_normal_string_should_return_normal_string_getter() {
        User user = new User(sampleLocalId,
                sampleFirstName,
                sampleLastName,
                sampleDisplayName,
                samplePhotoUrl,
                sampleEmail,
                sampleAssignDatetime,
                sampleIsNewUser);
        user.setFirstName("another");
        assertEquals("another", user.getFirstName());
    }

    @Test
    void setFirstName_Set_null_should_return_null_getter() {
        User user = new User(sampleLocalId,
                sampleFirstName,
                sampleLastName,
                sampleDisplayName,
                samplePhotoUrl,
                sampleEmail,
                sampleAssignDatetime,
                sampleIsNewUser);
        user.setFirstName(null);
        assertNull(user.getFirstName());
    }

    @Test
    void setFirstName_Set_empty_string_should_return_empty_string_getter() {
        User user = new User(sampleLocalId,
                sampleFirstName,
                sampleLastName,
                sampleDisplayName,
                samplePhotoUrl,
                sampleEmail,
                sampleAssignDatetime,
                sampleIsNewUser);
        user.setFirstName("");
        assertEquals("", user.getFirstName());
    }

    @Test
    void setLastName_Set_normal_string_should_return_normal_string_getter() {
        User user = new User(sampleLocalId,
                sampleFirstName,
                sampleLastName,
                sampleDisplayName,
                samplePhotoUrl,
                sampleEmail,
                sampleAssignDatetime,
                sampleIsNewUser);
        user.setLastName("another");
        assertEquals("another", user.getLastName());
    }

    @Test
    void setLastName_Set_null_should_return_null_getter() {
        User user = new User(sampleLocalId,
                sampleFirstName,
                sampleLastName,
                sampleDisplayName,
                samplePhotoUrl,
                sampleEmail,
                sampleAssignDatetime,
                sampleIsNewUser);
        user.setLastName(null);
        assertNull(user.getLastName());
    }

    @Test
    void setLastName_Set_empty_string_should_return_empty_string_getter() {
        User user = new User(sampleLocalId,
                sampleFirstName,
                sampleLastName,
                sampleDisplayName,
                samplePhotoUrl,
                sampleEmail,
                sampleAssignDatetime,
                sampleIsNewUser);
        user.setLastName("");
        assertEquals("", user.getLastName());
    }

    @Test
    void getFullName_It_should_return_firstName_and_lastName_concatenated_together() {
        User user = new User(sampleLocalId,
                sampleFirstName,
                sampleLastName,
                sampleDisplayName,
                samplePhotoUrl,
                sampleEmail,
                sampleAssignDatetime,
                sampleIsNewUser);

        assertEquals(sampleFirstName + " " + sampleLastName, user.getFullName());
    }

    @Test
    void getFullName_It_should_return_firstName_when_lastName_is_null() {
        User user = new User(sampleLocalId,
                sampleFirstName,
                null,
                sampleDisplayName,
                samplePhotoUrl,
                sampleEmail,
                sampleAssignDatetime,
                sampleIsNewUser);

        assertEquals(sampleFirstName, user.getFullName());
    }

    @Test
    void getFullName_It_should_return_lastName_when_firstName_is_null() {
        User user = new User(sampleLocalId,
                null,
                sampleLastName,
                sampleDisplayName,
                samplePhotoUrl,
                sampleEmail,
                sampleAssignDatetime,
                sampleIsNewUser);

        assertEquals(sampleLastName, user.getFullName());
    }

    @Test
    void getFullName_It_should_return_null_when_firstName_and_lastName_is_null() {
        User user = new User(sampleLocalId,
                null,
                null,
                sampleDisplayName,
                samplePhotoUrl,
                sampleEmail,
                sampleAssignDatetime,
                sampleIsNewUser);

        assertNull(user.getFullName());
    }

    @Test
    void toMap_It_should_not_return_null() {
        User user = new User(sampleLocalId,
                sampleFirstName,
                sampleLastName,
                sampleDisplayName,
                samplePhotoUrl,
                sampleEmail,
                sampleAssignDatetime,
                sampleIsNewUser);
        assertNotNull(user.toMap());
    }

    @Test
    void toMap_It_should_return_same_firstName() {
        User user = new User(sampleLocalId,
                sampleFirstName,
                sampleLastName,
                sampleDisplayName,
                samplePhotoUrl,
                sampleEmail,
                sampleAssignDatetime,
                sampleIsNewUser);
        var map = user.toMap();
        assertEquals(sampleFirstName, map.get("firstName"));
    }

    @Test
    void toMap_It_should_return_same_lastName() {
        User user = new User(sampleLocalId,
                sampleFirstName,
                sampleLastName,
                sampleDisplayName,
                samplePhotoUrl,
                sampleEmail,
                sampleAssignDatetime,
                sampleIsNewUser);
        var map = user.toMap();
        assertEquals(sampleLastName, map.get("lastName"));
    }

    @Test
    void toMap_It_should_return_same_email() {
        User user = new User(sampleLocalId,
                sampleFirstName,
                sampleLastName,
                sampleDisplayName,
                samplePhotoUrl,
                sampleEmail,
                sampleAssignDatetime,
                sampleIsNewUser);
        var map = user.toMap();
        assertEquals(sampleEmail, map.get("email"));
    }

    @Test
    void toMap_It_should_return_null_first_name() {
        User user = new User(sampleLocalId,
                null,
                sampleLastName,
                sampleDisplayName,
                samplePhotoUrl,
                sampleEmail,
                sampleAssignDatetime,
                sampleIsNewUser);
        var map = user.toMap();
        assertNull(map.get("firstName"));
    }

    @Test
    void toMap_It_should_return_null_last_name() {
        User user = new User(sampleLocalId,
                sampleFirstName,
                null,
                sampleDisplayName,
                samplePhotoUrl,
                sampleEmail,
                sampleAssignDatetime,
                sampleIsNewUser);
        var map = user.toMap();
        assertNull(map.get("lastName"));
    }

    @Test
    void toMap_It_should_return_null_email() {
        User user = new User(sampleLocalId,
                sampleFirstName,
                sampleLastName,
                sampleDisplayName,
                samplePhotoUrl,
                null,
                sampleAssignDatetime,
                sampleIsNewUser);
        var map = user.toMap();
        assertNull(map.get("email"));
    }

    @Test
    void toMap_It_should_return_empty_first_name() {
        User user = new User(sampleLocalId,
                "",
                sampleLastName,
                sampleDisplayName,
                samplePhotoUrl,
                sampleEmail,
                sampleAssignDatetime,
                sampleIsNewUser);
        var map = user.toMap();
        assertEquals("", map.get("firstName"));
    }

    @Test
    void toMap_It_should_return_empty_last_name() {
        User user = new User(sampleLocalId,
                sampleFirstName,
                "",
                sampleDisplayName,
                samplePhotoUrl,
                sampleEmail,
                sampleAssignDatetime,
                sampleIsNewUser);
        var map = user.toMap();
        assertEquals("", map.get("lastName"));
    }

    @Test
    void toMap_It_should_return_empty_email() {
        User user = new User(sampleLocalId,
                sampleFirstName,
                sampleLastName,
                sampleDisplayName,
                samplePhotoUrl,
                "",
                sampleAssignDatetime,
                sampleIsNewUser);
        var map = user.toMap();
        assertEquals("", map.get("email"));
    }

    @Test
    void toUser_It_should_not_return_null() {
        User user = new User(sampleLocalId,
                sampleFirstName,
                sampleLastName,
                sampleDisplayName,
                samplePhotoUrl,
                sampleEmail,
                sampleAssignDatetime,
                sampleIsNewUser);
        assertNotNull(user.toUser(sampleAssignDatetime, sampleIsNewUser));
    }

    @Test
    void toUser_It_should_has_the_same_localId_not_null() {

    }

    @Test
    void toUser_It_should_has_the_same_localId_and_null() {

    }

    @Test
    void toUser_It_should_has_the_same_localId_and_empty() {

    }

    @Test
    void toUser_It_should_has_the_same_firstName_not_null() {

    }

    @Test
    void toUser_It_should_has_the_same_firstName_and_null() {

    }

    @Test
    void toUser_It_should_has_the_same_firstName_and_empty() {

    }

    @Test
    void toUser_It_should_has_the_same_lastName_not_null() {

    }

    @Test
    void toUser_It_should_has_the_same_lastName_and_null() {

    }

    @Test
    void toUser_It_should_has_the_same_lastName_and_empty() {

    }

    @Test
    void toUser_It_should_has_the_same_displayName_not_null() {

    }

    @Test
    void toUser_It_should_has_the_same_displayName_and_null() {

    }

    @Test
    void toUser_It_should_has_the_same_displayName_and_empty() {

    }

    @Test
    void toUser_It_should_has_the_same_photoUrl_not_null() {

    }

    @Test
    void toUser_It_should_has_the_same_photoUrl_and_null() {

    }

    @Test
    void toUser_It_should_has_the_same_photoUrl_and_empty() {

    }

    @Test
    void toUser_It_should_has_the_same_email_not_null() {

    }

    @Test
    void toUser_It_should_has_the_same_email_and_null() {

    }

    @Test
    void toUser_It_should_has_the_same_email_and_empty() {

    }

    @Test
    void toUser_It_should_update_new_assign_timestamp_not_null() {

    }

    @Test
    void toUser_It_should_update_new_assign_timestamp_and_null() {

    }

    @Test
    void toUser_It_should_update_new_is_new_user_state_and_true() {

    }

    @Test
    void toUser_It_should_update_new_is_new_user_state_and_false() {

    }

}
