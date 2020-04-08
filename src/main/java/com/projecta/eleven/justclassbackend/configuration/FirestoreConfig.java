package com.projecta.eleven.justclassbackend.configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Optional;

@Configuration
public class FirestoreConfig {

    private Firestore firestore;

    @Bean("firestore")
    public void getFirestore() {
        var projectId = Optional.ofNullable(System.getenv("GOOGLE_CLOUD_PROJECT"));
        projectId.ifPresentOrElse(
                this::initializeFirebaseUsingDefaultCredential,
                this::initializeFirebaseUsingLocalCredential);
    }

    private void initializeFirebaseUsingDefaultCredential(String projectId) {
        firestore = FirestoreOptions.getDefaultInstance()
                .toBuilder()
                .setProjectId(projectId)
                .build()
                .getService();
    }

    private void initializeFirebaseUsingLocalCredential() {
        String pathToCredential = Paths.get(".")
                .toAbsolutePath()
                .normalize()
                .toString() + "//credentials//JustClass-b81fef18281d.json";

        try {
            System.err.println("Local development environment detected. Attempting to initialize Firestore using local credentials.");
            InputStream credentialFile = new FileInputStream(pathToCredential);
            GoogleCredentials credentials = GoogleCredentials.fromStream(credentialFile);
            firestore = FirestoreOptions.newBuilder()
                    .setCredentials(credentials)
                    .build()
                    .getService();
        } catch (IOException exception) {
            System.err.println("Cannot initialize Firebase using local credentials.");
            firestore = null;
        }
    }

    @Bean("user")
    @DependsOn("firestore")
    @Scope("singleton")
    public CollectionReference getUserCollection() throws DatabaseFailedToInitializeException {
        return Optional.ofNullable(firestore)
                .map(db -> db.collection("user"))
                .orElseThrow(DatabaseFailedToInitializeException::new);
    }

}
