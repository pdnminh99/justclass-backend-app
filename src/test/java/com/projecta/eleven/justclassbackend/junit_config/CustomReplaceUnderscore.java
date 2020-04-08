package com.projecta.eleven.justclassbackend.junit_config;

import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;

import java.lang.reflect.Method;

public class CustomReplaceUnderscore extends ReplaceUnderscores {
    @Override
    public String generateDisplayNameForMethod(Class<?> testClass, Method testMethod) {
        String[] testMethodNames = testMethod.getName().split("_");
        int testMethodNamesLength = testMethodNames.length;
        if (testMethodNamesLength > 1) {
            StringBuilder methodName = new StringBuilder(testMethodNames[0] + "(): ");
            for (int index = 1; index < testMethodNamesLength; index++) {
                methodName.append(index == testMethodNamesLength - 1 ?
                        testMethodNames[index] + "." :
                        testMethodNames[index] + " ");
            }
            return methodName.toString();
        }
        if (testMethodNamesLength == 1) {
            return testMethodNames[0];
        }
        return "";
    }
}
