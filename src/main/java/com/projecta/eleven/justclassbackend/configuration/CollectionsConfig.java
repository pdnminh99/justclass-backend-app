package com.projecta.eleven.justclassbackend.configuration;


import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;

import java.util.Optional;

@Configuration
public class CollectionsConfig {

    private final Firestore firestore;

    private final boolean isDeploymentEnvironment = Boolean.parseBoolean(System.getenv("envi"));

    @Autowired
    public CollectionsConfig(Firestore firestore) {
        this.firestore = firestore;
    }

    @Bean("userCollection")
    @DependsOn("firestore")
    @Scope("singleton")
    public CollectionReference getUserCollection() throws DatabaseFailedToInitializeException {
        return Optional.ofNullable(firestore)
                .map(db -> db.collection(isDeploymentEnvironment ? "users" : "users_dev"))
                .orElseThrow(DatabaseFailedToInitializeException::new);
    }

    @Bean("friendCollection")
    @DependsOn("firestore")
    @Scope("singleton")
    public CollectionReference getFriendCollection() throws DatabaseFailedToInitializeException {
        return Optional.ofNullable(firestore)
                .map(db -> db.collection(isDeploymentEnvironment ? "friends" : "friends_dev"))
                .orElseThrow(DatabaseFailedToInitializeException::new);

    }

    @Bean("collaboratorCollection")
    @DependsOn("firestore")
    @Scope("singleton")
    public CollectionReference getCollaboratorCollection() throws DatabaseFailedToInitializeException {
        return Optional.ofNullable(firestore)
                .map(db -> db.collection(isDeploymentEnvironment ? "collaborators" : "collaborators_dev"))
                .orElseThrow(DatabaseFailedToInitializeException::new);
    }

    @Bean("classroomCollection")
    @DependsOn("firestore")
    @Scope("singleton")
    public CollectionReference getClassroomCollection() throws DatabaseFailedToInitializeException {
        return Optional.ofNullable(firestore)
                .map(db -> db.collection(isDeploymentEnvironment ? "classrooms" : "classrooms_dev"))
                .orElseThrow(DatabaseFailedToInitializeException::new);
    }
}
