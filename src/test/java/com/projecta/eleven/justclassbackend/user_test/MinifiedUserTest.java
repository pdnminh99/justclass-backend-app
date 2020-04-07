package com.projecta.eleven.justclassbackend.user_test;

import com.projecta.eleven.justclassbackend.junit_config.CustomReplaceUnderscore;
import com.projecta.eleven.justclassbackend.user.MinifiedUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayNameGeneration(CustomReplaceUnderscore.class)
@DisplayName("Unit Tests for MinifiedUser class.")
public class MinifiedUserTest {

    private final String samplePhotoURL = "http://somewhere.that.store.cat.jpg";

    private final String sampleName = "Mr.Bean";

    private final String sampleUUID = UUID.randomUUID().toString();

    @Test
    void Constructor_Create_instance_should_not_Null() {
        var minifiedUser = new MinifiedUser(sampleUUID, sampleName, samplePhotoURL);

        assertNotNull(minifiedUser, "An instance created from MinifiedUser class is null.");
    }

    @Test
    void Constructor_Create_instance_with_Null_UUID_shall_throws_invalid_userId_exception() {
        assertThrows(NullPointerException.class,
                () -> new MinifiedUser(null, sampleName, samplePhotoURL),
                "Create minified user with null uuid does not throw NullPointerException.");
    }

    @Test
    void Constructor_Create_instance_with_empty_UUID_string_is_acceptable() {
        assertDoesNotThrow(() -> new MinifiedUser("", sampleName, samplePhotoURL));
    }

    @Test
    void Constructor_Create_instance_with_empty_UUID_string_shall_returns_empty_UUID_in_getter_method() {
        var minifiedUser = new MinifiedUser("", sampleName, samplePhotoURL);

        assertEquals("", minifiedUser.getUUID());
    }

    @Test
    void Constructor_Create_instance_with_Null_name_value_is_acceptable() {
        assertDoesNotThrow(() -> new MinifiedUser(sampleUUID, null, samplePhotoURL));
    }

    @Test
    void Constructor_Create_instance_with_Null_name_value_is_considered_as_empty_string() {
        var minifiedUser = new MinifiedUser(sampleUUID, null, samplePhotoURL);

        assertEquals("", minifiedUser.getName());
    }

    @Test
    void Constructor_Create_instance_with_empty_name_value_is_acceptable() {
        assertDoesNotThrow(() -> new MinifiedUser(sampleUUID, "", samplePhotoURL));
    }

    @Test
    void Constructor_Create_instance_with_Null_photoURL_value_is_acceptable() {
        assertDoesNotThrow(() -> new MinifiedUser(sampleUUID, sampleName, null));
    }

    @Test
    void Constructor_Create_instance_with_Null_photoURL_value_is_considered_as_empty_string() {
        var minifiedUser = new MinifiedUser(sampleUUID, sampleName, null);

        assertEquals("", minifiedUser.getPhotoURL());
    }

    @Test
    void getUUID_UUID_does_not_change_after_initiate_constructor() {
        var UUIDString = UUID.randomUUID().toString();
        var minifiedUser = new MinifiedUser(UUIDString, sampleName, samplePhotoURL);

        assertEquals(UUIDString, minifiedUser.getUUID(), String.format("UUID should be %s, but returns %s instead", UUIDString, minifiedUser.getUUID()));
    }

    @Test
    void getName_Name_does_not_change_after_initiate_constructor() {
        var minifiedUser = new MinifiedUser(sampleUUID, sampleName, samplePhotoURL);

        assertEquals(sampleName, minifiedUser.getName(), String.format("Name should be %s, but returns %s instead", sampleName, minifiedUser.getName()));
    }

    @Test
    void setName_Set_name_does_change_the_value() {
        String newName = "Mr.John";
        var minifiedUser = new MinifiedUser(sampleUUID, sampleName, samplePhotoURL);

        minifiedUser.setName(newName);
        assertEquals(newName, minifiedUser.getName(), String.format("Name should be %s, but returns %s instead", newName, minifiedUser.getName()));
    }

    @Test
    void setName_Set_Null_value_is_acceptable() {
        var minifiedUser = new MinifiedUser(sampleUUID, sampleName, samplePhotoURL);
        assertDoesNotThrow(() -> minifiedUser.setName(null));
    }

    @Test
    void setName_Set_Null_value_is_considered_as_empty_string() {
        var minifiedUser = new MinifiedUser(sampleUUID, sampleName, samplePhotoURL);
        minifiedUser.setName(null);
        assertEquals("", minifiedUser.getName());
    }

    @Test
    void setName_Set_empty_value_is_acceptable() {
        var minifiedUser = new MinifiedUser(sampleUUID, sampleName, samplePhotoURL);
        assertDoesNotThrow(() -> minifiedUser.setName(""));
    }

    @Test
    void setName_Set_empty_value_shall_returns_empty_string() {
        var minifiedUser = new MinifiedUser(sampleUUID, sampleName, samplePhotoURL);
        minifiedUser.setName("");
        assertEquals("", minifiedUser.getName());
    }

}
