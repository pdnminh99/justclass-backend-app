package com.projecta.eleven.justclassbackend.user_test;

import com.google.cloud.Timestamp;
import com.projecta.eleven.justclassbackend.junit_config.CustomReplaceUnderscore;
import com.projecta.eleven.justclassbackend.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayNameGeneration(CustomReplaceUnderscore.class)
@DisplayName("Unit Tests for UserResponseBody")
public class UserTest extends UserRequestBodyTest {

    @Test
    void Constructor_Create_instance_with_null_assign_datetime_is_acceptable() {
        assertDoesNotThrow(() -> new User(
                sampleLocalId,
                sampleFirstName,
                sampleLastName,
                sampleDisplayName,
                samplePhotoUrl,
                sampleEmail,
                null,
                sampleIsNewUser
        ));
    }

    @Test
    void Constructor_Create_instance_with_null_assign_datetime_shall_return_null_getter_method() {
        var user = new User(
                sampleLocalId,
                sampleFirstName,
                sampleLastName,
                sampleDisplayName,
                samplePhotoUrl,
                sampleEmail,
                null,
                sampleIsNewUser
        );
        assertNull(user.getAssignTimestamp());
    }

    @Test
    void Constructor_Create_instance_with_new_user_True() {
        var user = new User(
                sampleLocalId,
                sampleFirstName,
                sampleLastName,
                sampleDisplayName,
                samplePhotoUrl,
                sampleEmail,
                sampleAssignDatetime,
                true
        );
        assertTrue(user.isNewUser());
    }

    @Test
    void Constructor_Create_instance_with_new_user_False() {
        var user = new User(
                sampleLocalId,
                sampleFirstName,
                sampleLastName,
                sampleDisplayName,
                samplePhotoUrl,
                sampleEmail,
                sampleAssignDatetime,
                false
        );
        assertFalse(user.isNewUser());
    }

    @Test
    void setAssignTimestamp_Set_current_timestamp_should_return_valid_timestamp() {
        var user = new User(
                sampleLocalId,
                sampleFirstName,
                sampleLastName,
                sampleDisplayName,
                samplePhotoUrl,
                sampleEmail,
                sampleAssignDatetime,
                sampleIsNewUser
        );
        Timestamp now = Timestamp.now();
        user.setAssignTimestamp(now);
        assertEquals(now, user.getAssignTimestamp());
    }

    @Test
    void setAssignTimestamp_Set_null_timestamp_should_return_null_getter_method() {
        var user = new User(
                sampleLocalId,
                sampleFirstName,
                sampleLastName,
                sampleDisplayName,
                samplePhotoUrl,
                sampleEmail,
                sampleAssignDatetime,
                sampleIsNewUser
        );
        user.setAssignTimestamp(null);
        assertNull(user.getAssignTimestamp());
    }

    @Test
    void setIsNewUser_Set_false_should_return_false_getter_method() {
        var user = new User(
                sampleLocalId,
                sampleFirstName,
                sampleLastName,
                sampleDisplayName,
                samplePhotoUrl,
                sampleEmail,
                sampleAssignDatetime,
                true
        );
        user.setIsNewUser(false);
        assertFalse(user.isNewUser());
    }

    @Test
    void setIsNewUser_Set_true_should_return_true_getter_method() {
        var user = new User(
                sampleLocalId,
                sampleFirstName,
                sampleLastName,
                sampleDisplayName,
                samplePhotoUrl,
                sampleEmail,
                sampleAssignDatetime,
                false
        );
        user.setIsNewUser(true);
        assertTrue(user.isNewUser());
    }
}
