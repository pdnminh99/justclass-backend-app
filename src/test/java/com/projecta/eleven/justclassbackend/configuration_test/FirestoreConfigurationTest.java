package com.projecta.eleven.justclassbackend.configuration_test;

import com.projecta.eleven.justclassbackend.configuration.FirestoreConfig;
import com.projecta.eleven.justclassbackend.junit_config.CustomReplaceUnderscore;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@DisplayName("Unit Tests for Firestore Bean.")
@DisplayNameGeneration(CustomReplaceUnderscore.class)
public class FirestoreConfigurationTest {

    private final FirestoreConfig config;

    @Autowired
    public FirestoreConfigurationTest(FirestoreConfig config) {
        this.config = config;
    }

    @Test
    void Constructor_FirestoreConfigInstanceShouldNotBeNull() {
        assertNotNull(config, "Firestore initiated with a null value.");
    }

}
