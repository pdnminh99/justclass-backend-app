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

    @Bean("usersCollection")
    @DependsOn("firestore")
    @Scope("singleton")
    public CollectionReference getUsersCollection() throws DatabaseFailedToInitializeException {
        return Optional.ofNullable(firestore)
                .map(db -> db.collection(isDeploymentEnvironment ? "users" : "users_dev"))
                .orElseThrow(DatabaseFailedToInitializeException::new);
    }

    @Bean("friendsCollection")
    @DependsOn("firestore")
    @Scope("singleton")
    public CollectionReference getFriendsCollection() throws DatabaseFailedToInitializeException {
        return Optional.ofNullable(firestore)
                .map(db -> db.collection(isDeploymentEnvironment ? "friends" : "friends_dev"))
                .orElseThrow(DatabaseFailedToInitializeException::new);

    }

    @Bean("membersCollection")
    @DependsOn("firestore")
    @Scope("singleton")
    public CollectionReference getMembersCollection() throws DatabaseFailedToInitializeException {
        return Optional.ofNullable(firestore)
                .map(db -> db.collection(isDeploymentEnvironment ? "members" : "members_dev"))
                .orElseThrow(DatabaseFailedToInitializeException::new);
    }

    @Bean("classroomsCollection")
    @DependsOn("firestore")
    @Scope("singleton")
    public CollectionReference getClassroomsCollection() throws DatabaseFailedToInitializeException {
        return Optional.ofNullable(firestore)
                .map(db -> db.collection(isDeploymentEnvironment ? "classrooms" : "classrooms_dev"))
                .orElseThrow(DatabaseFailedToInitializeException::new);
    }

    @Bean("notificationsCollection")
    @DependsOn("firestore")
    @Scope("singleton")
    public CollectionReference getNotificationsCollection() throws DatabaseFailedToInitializeException {
        return Optional.ofNullable(firestore)
                .map(db -> db.collection(isDeploymentEnvironment ? "notifications" : "notifications_dev"))
                .orElseThrow(DatabaseFailedToInitializeException::new);
    }

    @Bean("invitationsCollection")
    @DependsOn("firestore")
    @Scope("singleton")
    public CollectionReference getInvitationsCollection() throws DatabaseFailedToInitializeException {
        return Optional.ofNullable(firestore)
                .map(db -> db.collection(isDeploymentEnvironment ? "invitations" : "invitations_dev"))
                .orElseThrow(DatabaseFailedToInitializeException::new);
    }

    @Bean("notesCollection")
    @DependsOn("firestore")
    @Scope("singleton")
    public CollectionReference getNotesCollection() throws DatabaseFailedToInitializeException {
        return Optional.ofNullable(firestore)
                .map(db -> db.collection(isDeploymentEnvironment ? "notes" : "notes_dev"))
                .orElseThrow(DatabaseFailedToInitializeException::new);
    }

    @Bean("commentsCollection")
    @DependsOn("firestore")
    @Scope("singleton")
    public CollectionReference getCommentsCollection() throws DatabaseFailedToInitializeException {
        return Optional.ofNullable(firestore)
                .map(db -> db.collection(isDeploymentEnvironment ? "comments" : "comments_dev"))
                .orElseThrow(DatabaseFailedToInitializeException::new);
    }
}
