package com.projecta.eleven.justclassbackend.user_test;

import com.projecta.eleven.justclassbackend.junit_config.CustomReplaceUnderscore;
import com.projecta.eleven.justclassbackend.user.IUserOperations;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayNameGeneration(CustomReplaceUnderscore.class)
@DisplayName("Unit Tests for IUserOperations interface.")
public class IUserOperationsTest {

    private final IUserOperations service;

    @Autowired
    public IUserOperationsTest(IUserOperations service) {
        this.service = service;
    }
}
