package com.projecta.eleven.justclassbackend.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Objects;

@Configuration
public class FirestoreConfig {

    @Bean
    public Firestore getFirestore() throws IOException {
        FirestoreOptions firestoreOptions;
        String projectId = System.getenv("GOOGLE_CLOUD_PROJECT");
        if (Objects.nonNull(projectId)) {
            firestoreOptions =
                    FirestoreOptions.getDefaultInstance().toBuilder()
                            .setProjectId(projectId)
                            .build();
        } else {
            String pathToCredential = Paths.get(".").toAbsolutePath().normalize().toString() + "//credentials//JustClass-b81fef18281d.json";
            InputStream credentialFile = new FileInputStream(pathToCredential);
            GoogleCredentials credentials = GoogleCredentials.fromStream(credentialFile);
            firestoreOptions = FirestoreOptions.newBuilder().setCredentials(credentials).build();
        }
        return firestoreOptions.getService();
    }

}
