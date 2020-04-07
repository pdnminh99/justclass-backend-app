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

    private final String sampleLocalId = UUID.randomUUID().toString();

    /**
     * Constructor testing methods
     */
    @Test
    void Constructor_Create_instance_should_not_Null() {
        var minifiedUser = new MinifiedUser(sampleLocalId, sampleName, samplePhotoURL);

        assertNotNull(minifiedUser, "An instance created from MinifiedUser class is null.");
    }

    @Test
    void Constructor_Create_instance_with_Null_LocalId_shall_throws_invalid_userId_exception() {
        assertDoesNotThrow(() -> new MinifiedUser(null, sampleName, samplePhotoURL));
    }

    @Test
    void Constructor_Create_instance_with_empty_LocalId_string_is_acceptable() {
        assertDoesNotThrow(() -> new MinifiedUser("", sampleName, samplePhotoURL));
    }

    @Test
    void Constructor_Create_instance_with_empty_LocalId_string_shall_returns_empty_LocalId_in_getter_method() {
        var minifiedUser = new MinifiedUser("", sampleName, samplePhotoURL);

        assertEquals("", minifiedUser.getLocalId());
    }

    @Test
    void Constructor_Create_instance_with_Null_display_name_value_is_acceptable() {
        assertDoesNotThrow(() -> new MinifiedUser(sampleLocalId, null, samplePhotoURL));
    }

    @Test
    void Constructor_Create_instance_with_Null_display_name_value_is_considered_as_empty_string() {
        var minifiedUser = new MinifiedUser(sampleLocalId, null, samplePhotoURL);

        assertEquals("", minifiedUser.getDisplayName());
    }

    @Test
    void Constructor_Create_instance_with_empty_display_name_value_is_acceptable() {
        assertDoesNotThrow(() -> new MinifiedUser(sampleLocalId, "", samplePhotoURL));
    }

    @Test
    void Constructor_Create_instance_with_Null_photoURL_value_is_acceptable() {
        assertDoesNotThrow(() -> new MinifiedUser(sampleLocalId, sampleName, null));
    }

    @Test
    void Constructor_Create_instance_with_Null_photoURL_value_is_considered_as_empty_string() {
        var minifiedUser = new MinifiedUser(sampleLocalId, sampleName, null);

        assertEquals("", minifiedUser.getPhotoUrl());
    }

    /**
     * getLocalId testing methods
     */
    @Test
    void getLocalId_LocalId_does_not_change_after_initiate_constructor() {
        var minifiedUser = new MinifiedUser(sampleLocalId, sampleName, samplePhotoURL);

        assertEquals(sampleLocalId, minifiedUser.getLocalId(), String.format("LocalId should be %s, but returns %s instead", sampleLocalId, minifiedUser.getLocalId()));
    }

    /**
     * getName testing methods
     */
    @Test
    void getName_Display_name_does_not_change_after_initiate_constructor() {
        var minifiedUser = new MinifiedUser(sampleLocalId, sampleName, samplePhotoURL);

        assertEquals(sampleName, minifiedUser.getDisplayName(), String.format("Name should be %s, but returns %s instead", sampleName, minifiedUser.getDisplayName()));
    }

    @Test
    void setName_Set_display_name_does_change_the_value() {
        String newName = "Mr.John";
        var minifiedUser = new MinifiedUser(sampleLocalId, sampleName, samplePhotoURL);

        minifiedUser.setDisplayName(newName);
        assertEquals(newName, minifiedUser.getDisplayName(), String.format("Name should be %s, but returns %s instead", newName, minifiedUser.getDisplayName()));
    }

    @Test
    void setName_Set_Null_value_is_acceptable() {
        var minifiedUser = new MinifiedUser(sampleLocalId, sampleName, samplePhotoURL);
        assertDoesNotThrow(() -> minifiedUser.setDisplayName(null));
    }

    @Test
    void setName_Set_Null_value_is_considered_as_empty_string() {
        var minifiedUser = new MinifiedUser(sampleLocalId, sampleName, samplePhotoURL);
        minifiedUser.setDisplayName(null);
        assertEquals("", minifiedUser.getDisplayName());
    }

    @Test
    void setName_Set_empty_value_is_acceptable() {
        var minifiedUser = new MinifiedUser(sampleLocalId, sampleName, samplePhotoURL);
        assertDoesNotThrow(() -> minifiedUser.setDisplayName(""));
    }

    @Test
    void setName_Set_empty_value_shall_returns_empty_string() {
        var minifiedUser = new MinifiedUser(sampleLocalId, sampleName, samplePhotoURL);
        minifiedUser.setDisplayName("");
        assertEquals("", minifiedUser.getDisplayName());
    }

    /**
     * getPhotoUrl testing methods
     */
    @Test
    void getPhotoUrl_Get_photoUrl_does_not_change_after_initiate_constructor() {
        var minifiedUser = new MinifiedUser(sampleLocalId, sampleName, samplePhotoURL);

        assertEquals(samplePhotoURL, minifiedUser.getPhotoUrl());
    }
}
