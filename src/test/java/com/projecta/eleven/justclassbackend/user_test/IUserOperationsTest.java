package com.projecta.eleven.justclassbackend.user_test;

import com.google.cloud.firestore.CollectionReference;
import com.projecta.eleven.justclassbackend.junit_config.CustomReplaceUnderscore;
import com.projecta.eleven.justclassbackend.junit_config.TestCollectionsConfig;
import com.projecta.eleven.justclassbackend.user.IUserOperations;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Import;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayNameGeneration(CustomReplaceUnderscore.class)
@DisplayName("Unit Tests for IUserOperations interface.")
@Import(TestCollectionsConfig.class)
public class IUserOperationsTest {

    private final IUserOperations service;

    @Autowired
    public IUserOperationsTest(IUserOperations service,
                               @Qualifier("userCollectionTest") CollectionReference userCollection) {
        this.service = service;
    }



}
