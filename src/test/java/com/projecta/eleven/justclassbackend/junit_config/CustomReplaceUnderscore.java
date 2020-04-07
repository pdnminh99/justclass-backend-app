package com.projecta.eleven.justclassbackend.junit_config;

import org.junit.jupiter.api.DisplayNameGenerator;

import java.lang.reflect.Method;

public class CustomReplaceUnderscore extends DisplayNameGenerator.ReplaceUnderscores {
    @Override
    public String generateDisplayNameForMethod(Class<?> testClass, Method testMethod) {
        String[] testMethodNames = testMethod.getName().split("_");
        int testMethodNamesLength = testMethodNames.length;
        if (testMethodNamesLength > 1) {
            String methodName = testMethodNames[0] + "(): ";
            for (int index = 1; index < testMethodNamesLength; index++) {
                methodName += index == testMethodNamesLength - 1 ?
                        testMethodNames[index] + "." :
                        testMethodNames[index] + " ";
            }
            return methodName;
        }
        if (testMethodNamesLength == 1) {
            return testMethodNames[0];
        }
        return "";
    }
}
