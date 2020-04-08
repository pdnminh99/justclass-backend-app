package com.projecta.eleven.justclassbackend.user_test;

import com.projecta.eleven.justclassbackend.junit_config.CustomReplaceUnderscore;
import com.projecta.eleven.justclassbackend.user.IMinifiedUserOperations;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayNameGeneration(CustomReplaceUnderscore.class)
@DisplayName("Unit Tests for IMinifiedUserOperations interface.")
public class IMinifiedUserOperationsTest {
    private final IMinifiedUserOperations service;

    @Autowired
    public IMinifiedUserOperationsTest(IMinifiedUserOperations service) {
        this.service = service;
    }
}
