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

    private final String sampleDisplayName = "John Wick";

    private final String sampleLocalId = UUID.randomUUID().toString();

    /**
     * Constructor testing methods
     */
    @Test
    void Constructor_Create_instance_should_not_null() {
        var minifiedUser = new MinifiedUser(sampleLocalId, sampleDisplayName, samplePhotoURL, null);

        assertNotNull(minifiedUser, "An instance created from MinifiedUser class is null.");
    }

    @Test
    void Constructor_Create_instance_with_null_LocalId_will_not_throw_exception() {
        assertDoesNotThrow(() -> new MinifiedUser(null, sampleDisplayName, samplePhotoURL, null));
    }

    @Test
    void Constructor_Create_instance_with_null_localId_will_return_null_getter_method() {
        var minifiedUser = new MinifiedUser(null, sampleDisplayName, samplePhotoURL, null);

        assertNull(minifiedUser.getLocalId());
    }

    @Test
    void Constructor_Create_instance_with_empty_localId_string_is_acceptable() {
        assertDoesNotThrow(() -> new MinifiedUser("", sampleDisplayName, samplePhotoURL, null));
    }

    @Test
    void Constructor_Create_instance_with_empty_LocalId_string_shall_returns_empty_LocalId_in_getter_method() {
        var minifiedUser = new MinifiedUser("", sampleDisplayName, samplePhotoURL, null);

        assertEquals("", minifiedUser.getLocalId());
    }

    @Test
    void Constructor_Create_instance_with_null_display_name_is_acceptable() {
        assertDoesNotThrow(() -> new MinifiedUser(sampleLocalId, null, samplePhotoURL, null));
    }

    @Test
    void Constructor_Create_instance_with_null_display_name_will_return_empty_string_getter_method() {
        var minifiedUser = new MinifiedUser(sampleLocalId, null, samplePhotoURL, null);

        assertNull(minifiedUser.getDisplayName());
    }

    @Test
    void Constructor_Create_instance_with_empty_string_display_name_value_is_acceptable() {
        assertDoesNotThrow(() -> new MinifiedUser(sampleLocalId, "", samplePhotoURL, null));
    }

    @Test
    void Constructor_Create_instance_with_empty_string_display_name_will_return_empty_string_getter_method() {
        var minifiedUser = new MinifiedUser(sampleLocalId, "", samplePhotoURL, null);

        assertEquals("", minifiedUser.getDisplayName());
    }

    @Test
    void Constructor_Create_instance_with_null_photoUrl_value_is_acceptable() {
        assertDoesNotThrow(() -> new MinifiedUser(sampleLocalId, sampleDisplayName, null, null));
    }

    @Test
    void Constructor_Create_instance_with_null_photoURL_value_will_return_null_getter_method() {
        var minifiedUser = new MinifiedUser(sampleLocalId, sampleDisplayName, null, null);

        assertNull(minifiedUser.getPhotoUrl());
    }

    @Test
    void Constructor_Create_instance_with_empty_string_photoUrl_is_acceptable() {
        assertDoesNotThrow(() -> new MinifiedUser(sampleLocalId, sampleDisplayName, "", null));
    }

    @Test
    void Constructor_Create_instance_with_empty_string_photoUrl_will_return_empty_string_getter_method() {
        var minifiedUser = new MinifiedUser(sampleLocalId, sampleDisplayName, "", null);

        assertEquals("", minifiedUser.getPhotoUrl());
    }

    /**
     * getLocalId testing methods
     */
    @Test
    void getLocalId_LocalId_does_not_change_after_initiate_constructor() {
        var minifiedUser = new MinifiedUser(sampleLocalId, sampleDisplayName, samplePhotoURL, null);

        assertEquals(sampleLocalId, minifiedUser.getLocalId());
    }

    /**
     * getName testing methods
     */
    @Test
    void getDisplayName_Display_name_does_not_change_after_initiate_constructor() {
        var minifiedUser = new MinifiedUser(sampleLocalId, sampleDisplayName, samplePhotoURL, null);

        assertEquals(sampleDisplayName, minifiedUser.getDisplayName());
    }

    /**
     * getName testing methods
     */
    @Test
    void setDisplayName_Set_display_name_does_change_the_value() {
        String newName = "Mr.John";
        var minifiedUser = new MinifiedUser(sampleLocalId, sampleDisplayName, samplePhotoURL, null);

        minifiedUser.setDisplayName(newName);
        assertEquals(newName, minifiedUser.getDisplayName());
    }

    @Test
    void setDisplayName_Set_null_value_is_acceptable() {
        var minifiedUser = new MinifiedUser(sampleLocalId, sampleDisplayName, samplePhotoURL, null);
        assertDoesNotThrow(() -> minifiedUser.setDisplayName(null));
    }

    @Test
    void setDisplayName_Set_null_value_shall_return_null_getter() {
        var minifiedUser = new MinifiedUser(sampleLocalId, sampleDisplayName, samplePhotoURL, null);
        minifiedUser.setDisplayName(null);
        assertNull(minifiedUser.getDisplayName());
    }

    @Test
    void setDisplayName_Set_empty_value_is_acceptable() {
        var minifiedUser = new MinifiedUser(sampleLocalId, sampleDisplayName, samplePhotoURL, null);
        assertDoesNotThrow(() -> minifiedUser.setDisplayName(""));
    }

    @Test
    void setDisplayName_Set_empty_value_shall_returns_empty_string() {
        var minifiedUser = new MinifiedUser(sampleLocalId, sampleDisplayName, samplePhotoURL, null);
        minifiedUser.setDisplayName("");
        assertEquals("", minifiedUser.getDisplayName());
    }

    /**
     * getPhotoUrl testing methods
     */
    @Test
    void getPhotoUrl_Get_photoUrl_does_not_change_after_initiate_constructor() {
        var minifiedUser = new MinifiedUser(sampleLocalId, sampleDisplayName, samplePhotoURL, null);

        assertEquals(samplePhotoURL, minifiedUser.getPhotoUrl());
    }

    /**
     * setPhotoUrl testing methods
     */
    @Test
    void setPhotoUrl_Set_null_photoUrl_is_acceptable() {
        var minifiedUser = new MinifiedUser(sampleLocalId, sampleDisplayName, samplePhotoURL, null);

        assertDoesNotThrow(() -> minifiedUser.setPhotoUrl(null));
    }

    @Test
    void setPhotoUrl_Set_null_photoUrl_will_return_null_getter_method() {
        var minifiedUser = new MinifiedUser(sampleLocalId, sampleDisplayName, samplePhotoURL, null);
        minifiedUser.setPhotoUrl(null);
        assertNull(minifiedUser.getPhotoUrl());
    }

    @Test
    void setPhotoUrl_Set_empty_string_photoUrl_is_acceptable() {
        var minifiedUser = new MinifiedUser(sampleLocalId, sampleDisplayName, samplePhotoURL, null);

        assertDoesNotThrow(() -> minifiedUser.setPhotoUrl(""));
    }

    @Test
    void setPhotoUrl_Set_empty_string_photoUrl_will_return_empty_string_getter_method() {
        var minifiedUser = new MinifiedUser(sampleLocalId, sampleDisplayName, samplePhotoURL, null);
        minifiedUser.setPhotoUrl("");
        assertEquals("", minifiedUser.getPhotoUrl());
    }

    /**
     * toMap testing methods
     */
    @Test
    void toMap_Method_does_not_return_null() {
        var minifiedUser = new MinifiedUser(sampleLocalId, sampleDisplayName, samplePhotoURL, null);

        assertNotNull(minifiedUser.toMap());
    }

    @Test
    void toMap_localId_value_remain_the_same() {
        var minifiedUser = new MinifiedUser(sampleLocalId, sampleDisplayName, samplePhotoURL, null);
        var map = minifiedUser.toMap();

        assertEquals(sampleLocalId, map.get("localId"));
    }

    @Test
    void toMap_localId_should_be_empty() {
        var minifiedUser = new MinifiedUser("", sampleDisplayName, samplePhotoURL, null);
        var map = minifiedUser.toMap();

        assertEquals("", map.get("localId"));
    }

    @Test
    void toMap_DisplayName_value_remain_the_same() {
        var minifiedUser = new MinifiedUser(sampleLocalId, sampleDisplayName, samplePhotoURL, null);
        var map = minifiedUser.toMap();

        assertEquals(sampleDisplayName, map.get("displayName"));
    }

    @Test
    void toMap_DisplayName_should_be_empty() {
        var minifiedUser = new MinifiedUser(sampleLocalId, "", samplePhotoURL, null);
        var map = minifiedUser.toMap();

        assertEquals("", map.get("displayName"));
    }

    @Test
    void toMap_PhotoUrl_value_remain_the_same() {
        var minifiedUser = new MinifiedUser(sampleLocalId, sampleDisplayName, samplePhotoURL, null);
        var map = minifiedUser.toMap();

        assertEquals(samplePhotoURL, map.get("photoUrl"));
    }

    @Test
    void toMap_PhotoUrl_should_be_empty() {
        var minifiedUser = new MinifiedUser(sampleLocalId, sampleDisplayName, "", null);
        var map = minifiedUser.toMap();

        assertEquals("", map.get("photoUrl"));
    }

    @Test
    void toMap_LocalId_should_be_null() {
        var minifiedUser = new MinifiedUser(null, sampleDisplayName, samplePhotoURL, null);
        var map = minifiedUser.toMap();

        assertNull(map.get("localId"));
    }

    @Test
    void toMap_DisplayName_should_be_null() {
        var minifiedUser = new MinifiedUser(sampleLocalId, null, samplePhotoURL, null);
        var map = minifiedUser.toMap();

        assertNull(map.get("displayName"));
    }

    @Test
    void toMap_PhotoUrl_should_be_null() {
        var minifiedUser = new MinifiedUser(sampleLocalId, sampleDisplayName, null, null);
        var map = minifiedUser.toMap();

        assertNull(map.get("photoUrl"));
    }
}
