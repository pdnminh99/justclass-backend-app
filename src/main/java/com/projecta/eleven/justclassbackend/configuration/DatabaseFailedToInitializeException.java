package com.projecta.eleven.justclassbackend.configuration;

import java.io.IOException;

public class DatabaseFailedToInitializeException extends Exception {

    public DatabaseFailedToInitializeException() {
        super("Cannot initialize Firebase.", new IOException("Cannot find credentials."));
    }

}
