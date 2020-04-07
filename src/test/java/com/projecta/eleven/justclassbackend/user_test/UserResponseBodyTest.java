package com.projecta.eleven.justclassbackend.user_test;

import com.projecta.eleven.justclassbackend.junit_config.CustomReplaceUnderscore;
import com.projecta.eleven.justclassbackend.user.MinifiedUser;
import com.projecta.eleven.justclassbackend.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

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
    protected final LocalDateTime sampleAssignDatetime = LocalDateTime.now();
    protected final boolean sampleIsNewUser = false;

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

    }

    @Test
    void Constructor_Create_instance_with_Null_first_name_is_acceptable() {

    }

    @Test
    void Constructor_Create_instance_with_Null_first_name_is_considered_as_empty_string() {

    }

    @Test
    void Constructor_Create_instance_with_empty_string_first_name_is_acceptable() {

    }

    @Test
    void Constructor_Create_instance_with_empty_string_first_name_shall_return_empty_string() {

    }

    @Test
    void Constructor_Create_instance_with_Null_last_name_is_acceptable() {

    }

    @Test
    void Constructor_Create_instance_with_Null_last_name_is_considered_as_empty_string() {

    }

    @Test
    void Constructor_Create_instance_with_empty_string_last_name_is_acceptable() {

    }

    @Test
    void Constructor_Create_instance_with_empty_string_last_name_shall_return_empty_string() {

    }

    @Test
    void Constructor_Create_instance_with_Null_full_name_is_acceptable() {

    }

    @Test
    void Constructor_Create_instance_with_Null_full_name_is_considered_as_first_and_last_name_concatenated_together() {

    }

    @Test
    void Constructor_Create_instance_with_empty_string_full_name_is_acceptable() {

    }

    @Test
    void Constructor_Create_instance_with_empty_string_full_name_is_considered_as_first_and_last_name_concatenated_together() {

    }

    @Test
    void Constructor_Full_name_shall_take_precedence_over_first_and_last_names_concatenated_together() {

    }

    @Test
    void Constructor_Create_instance_with_empty_string_display_name_is_acceptable() {

    }

    @Test
    void Constructor_Create_instance_with_empty_string_display_name_shall_return_empty_string() {

    }

    @Test
    void Constructor_Create_instance_with_empty_string_photoUrl_is_acceptable() {

    }

    @Test
    void Constructor_Create_instance_with_empty_string_photoUrl_shall_return_empty_string() {

    }

    /**
     * UserResponseBody class tests
     */

}
