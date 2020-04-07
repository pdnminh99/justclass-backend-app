package com.projecta.eleven.justclassbackend.configuration_test;

import com.projecta.eleven.justclassbackend.configuration.FirestoreConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
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
