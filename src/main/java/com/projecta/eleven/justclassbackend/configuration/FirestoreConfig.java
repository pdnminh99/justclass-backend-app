package com.projecta.eleven.justclassbackend.configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Optional;

@Configuration
public class FirestoreConfig {

    @Bean("firestore")
    @Primary
    public Firestore getFirestore() {
        var projectId = Optional.ofNullable(System.getenv("GOOGLE_CLOUD_PROJECT"));
        return projectId.map(this::initializeFirebaseUsingDefaultCredential)
                .orElseGet(this::initializeFirebaseUsingLocalCredential);
    }

    private Firestore initializeFirebaseUsingDefaultCredential(String projectId) {
        return FirestoreOptions.getDefaultInstance().toBuilder().setProjectId(projectId).build().getService();
    }

    private Firestore initializeFirebaseUsingLocalCredential() {
        String pathToCredential = Paths.get(".").toAbsolutePath().normalize().toString() + "//key.json";

        try {
            System.err.println(
                    "Local development environment detected. Attempting to initialize Firestore using local credentials.");
            InputStream credentialFile = new FileInputStream(pathToCredential);
            GoogleCredentials credentials = GoogleCredentials.fromStream(credentialFile);
            return FirestoreOptions.newBuilder().setCredentials(credentials).build().getService();
        } catch (IOException exception) {
            System.err.println("Cannot initialize Firebase using local credentials.");
            return null;
        }
    }

}
