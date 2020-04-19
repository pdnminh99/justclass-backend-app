package com.projecta.eleven.justclassbackend.classroom_test;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteBatch;
import com.google.common.collect.Lists;
import com.projecta.eleven.justclassbackend.classroom.Classroom;
import com.projecta.eleven.justclassbackend.classroom.Collaborator;
import com.projecta.eleven.justclassbackend.classroom.CollaboratorRoles;
import com.projecta.eleven.justclassbackend.configuration.DatabaseFailedToInitializeException;
import com.projecta.eleven.justclassbackend.junit_config.CustomReplaceUnderscore;
import com.projecta.eleven.justclassbackend.user.User;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayNameGeneration(CustomReplaceUnderscore.class)
@DisplayName("Unit Tests for IClassroomOperationsServiceTest interface.")
@SpringBootTest(properties = "spring.main.allow-bean-definition-overriding=true")
public class IClassroomOperationsServiceTest {

    private final CollectionReference userCollection;

    /*
     * TEST PLAN
     *
     * I/ Setup
     *
     * 1) Calculus:
     *  - Students: Jerry & John
     *  - Owner: Tom
     * 2) Algorithm:
     *  - Student: Tom
     *  - Owner: Jerry
     *  - Teacher: John
     * 3) Cooking:
     *  - Owner: John
     *  - Teacher: Tom & Jerry
     *
     * II/ getClassrooms( ... )
     *
     * 1) String: Valid
     *
     *
     */
    private final CollectionReference classroomCollection;
    private final CollectionReference collaboratorCollection;
    private final Firestore firestore;
    private final Timestamp documentsCreatedTimestamp = Timestamp.now();
    private final Timestamp march25 = Timestamp.ofTimeMicroseconds(1_585_138_304_000_000L);
    private final Timestamp april7 = Timestamp.ofTimeMicroseconds(1_586_261_578_000_000L);
    private final Timestamp april15 = Timestamp.ofTimeMicroseconds(1_586_952_778_000_000L);
    WriteBatch batch;
    private User userTom;
    private User userJerry;
    private User userJohn;
    private Classroom calculusClass;
    private Collaborator calculusOwner;
    private Collaborator calculusStudent01;
    private Collaborator calculusStudent02;
    private Classroom algorithmClass;
    private Collaborator algorithmOwner;
    private Collaborator algorithmStudent;
    private Collaborator algorithmTeacher;
    private Classroom cookingClass;
    private Collaborator cookingOwner;
    private Collaborator cookingTeacher01;
    private Collaborator cookingTeacher02;

    @Autowired
    public IClassroomOperationsServiceTest(
            Firestore firestore,
            @Qualifier("userCollection") CollectionReference userCollection,
            @Qualifier("classroomCollection") CollectionReference classroomCollection,
            @Qualifier("collaboratorCollection") CollectionReference collaboratorCollection) {
        this.firestore = firestore;
        this.batch = firestore.batch();
        this.userCollection = userCollection;
        this.classroomCollection = classroomCollection;
        this.collaboratorCollection = collaboratorCollection;
    }

    @BeforeAll
    void initializeTestsData() throws ExecutionException, InterruptedException {
        initializeUsersDocuments();
        initializeClassroomsDocuments();
        initializeCollaboratorsDocuments();
        batch.commit().get();
        batch = firestore.batch();
    }

    private void initializeUsersDocuments() {
        // Create user Tom.
        userTom = new User("100",
                "Tom",
                "Riddle",
                "Tom Marvelous Riddle",
                "http:path/to/photo",
                "voldermort@email.com",
                documentsCreatedTimestamp,
                true);
        createVirtualUser(userTom);

        // Create user Jerry.
        userJerry = new User("200",
                "Jerry",
                "Oswald",
                "Jerry TheOswald",
                "http:path/to/photo",
                "jerry_os@email.com",
                documentsCreatedTimestamp,
                true);
        createVirtualUser(userJerry);

        // Create user John.
        userJohn = new User("300",
                "John",
                "Wick",
                "Johnny Wicky",
                "http:path/to/photo",
                "john_wick@email.com",
                documentsCreatedTimestamp, true);
        createVirtualUser(userJohn);
    }

    private void createVirtualUser(User userToCreate) {
        var map = userToCreate.toMap();
        map.remove("isNewUser");
        batch.set(userCollection.document(userToCreate.getLocalId()), map);
    }

    private void initializeClassroomsDocuments() {
        calculusClass = new Classroom(
                "100",
                "Calculus 01",
                "DESC Calculus 01",
                "SECT Calculus 01",
                "CS50",
                "101",
                1,
                march25,
                null,
                null,
                null,
                "101"
        );
        createVirtualClassroom(calculusClass);

        algorithmClass = new Classroom(
                "200",
                "Algorithm 01",
                "DESC Algorithm 01",
                "SECT Algorithm 01",
                "CS50",
                "101",
                1,
                april7,
                null,
                null,
                null,
                "303"
        );
        createVirtualClassroom(algorithmClass);

        cookingClass = new Classroom(
                "300",
                "Cooking 01",
                "DESC Cooking 01",
                "SECT Cooking 01",
                "CS50",
                "101",
                1,
                april15,
                null,
                null,
                null,
                "404"
        );
        createVirtualClassroom(cookingClass);
    }

    private void createVirtualClassroom(Classroom classroomToCreate) {
        var map = classroomToCreate.toMap();
        map.remove("classroomId");
        map.remove("role");
        map.remove("lastAccessTimestamp");
        map.remove("studentsNotePermission");
        batch.set(classroomCollection.document(classroomToCreate.getClassroomId()), map);
    }

    private void initializeCollaboratorsDocuments() {
        var userTomDocumentReference = userCollection.document(userTom.getLocalId());
        var userJerryDocumentReference = userCollection.document(userJerry.getLocalId());
        var userJohnDocumentReference = userCollection.document(userJohn.getLocalId());

        var calculusClassDocumentReference = classroomCollection.document(calculusClass.getClassroomId());
        var algorithmClassDocumentReference = classroomCollection.document(algorithmClass.getClassroomId());
        var cookingClassDocumentReference = classroomCollection.document(cookingClass.getClassroomId());

        // Setup Calculus class.
        calculusOwner = new Collaborator(
                "100",
                calculusClassDocumentReference,
                userTomDocumentReference,
                march25,
                march25,
                CollaboratorRoles.OWNER
        );
        createVirtualCollaborator(calculusOwner);

        calculusStudent01 = new Collaborator(
                "101",
                calculusClassDocumentReference,
                userJerryDocumentReference,
                march25,
                march25,
                CollaboratorRoles.STUDENT
        );
        createVirtualCollaborator(calculusStudent01);

        calculusStudent02 = new Collaborator(
                "102",
                calculusClassDocumentReference,
                userJohnDocumentReference,
                march25,
                march25,
                CollaboratorRoles.STUDENT
        );
        createVirtualCollaborator(calculusStudent02);

        // Setup Algorithm class.
        algorithmOwner = new Collaborator(
                "200",
                algorithmClassDocumentReference,
                userJerryDocumentReference,
                april7,
                april7,
                CollaboratorRoles.OWNER
        );
        createVirtualCollaborator(algorithmOwner);

        algorithmStudent = new Collaborator(
                "201",
                algorithmClassDocumentReference,
                userTomDocumentReference,
                april7,
                april7,
                CollaboratorRoles.STUDENT
        );
        createVirtualCollaborator(algorithmStudent);

        algorithmTeacher = new Collaborator(
                "202",
                algorithmClassDocumentReference,
                userJohnDocumentReference,
                april7,
                april7,
                CollaboratorRoles.TEACHER
        );
        createVirtualCollaborator(algorithmTeacher);

        // Setup Cooking class.
        cookingOwner = new Collaborator(
                "300",
                cookingClassDocumentReference,
                userJohnDocumentReference,
                april15,
                april15,
                CollaboratorRoles.OWNER
        );
        createVirtualCollaborator(cookingOwner);

        cookingTeacher01 = new Collaborator(
                "301",
                cookingClassDocumentReference,
                userTomDocumentReference,
                april15,
                april15,
                CollaboratorRoles.TEACHER
        );
        createVirtualCollaborator(cookingTeacher01);

        cookingTeacher02 = new Collaborator(
                "302",
                cookingClassDocumentReference,
                userJerryDocumentReference,
                april15,
                april15,
                CollaboratorRoles.TEACHER
        );
        createVirtualCollaborator(cookingTeacher02);
    }

    private void createVirtualCollaborator(Collaborator collaborator) {
        var map = collaborator.toMap();
        batch.set(collaboratorCollection.document(collaborator.getClassroomId()), map);
    }

    @AfterAll
    void cleanupData() {
        var userTomDocumentReference = userCollection.document(userTom.getLocalId());
        var userJerryDocumentReference = userCollection.document(userJerry.getLocalId());
        var userJohnDocumentReference = userCollection.document(userJohn.getLocalId());

        var calculusClassDocumentReference = classroomCollection.document(calculusClass.getClassroomId());
        var algorithmClassDocumentReference = classroomCollection.document(algorithmClass.getClassroomId());
        var cookingClassDocumentReference = classroomCollection.document(cookingClass.getClassroomId());

        var calculusOwnerDocumentReference = collaboratorCollection.document(calculusOwner.getCollaboratorId());
        var calculusStudent01DocumentReference = collaboratorCollection.document(calculusStudent01.getCollaboratorId());
        var calculusStudent02DocumentReference = collaboratorCollection.document(calculusStudent01.getCollaboratorId());

        var algorithmOwnerDocumentReference = collaboratorCollection.document(algorithmOwner.getCollaboratorId());
        var algorithmStudentDocumentReference = collaboratorCollection.document(algorithmStudent.getCollaboratorId());
        var algorithmTeacherDocumentReference = collaboratorCollection.document(algorithmTeacher.getCollaboratorId());

        var cookingOwnerDocumentReference = collaboratorCollection.document(cookingOwner.getCollaboratorId());
        var cookingTeacher01DocumentReference = collaboratorCollection.document(cookingTeacher01.getCollaboratorId());
        var cookingTeacher02DocumentReference = collaboratorCollection.document(cookingTeacher02.getCollaboratorId());

        List<DocumentReference> references = Lists.newArrayList(
                userTomDocumentReference,
                userJerryDocumentReference,
                userJohnDocumentReference,

                calculusClassDocumentReference,
                algorithmClassDocumentReference,
                cookingClassDocumentReference,

                calculusOwnerDocumentReference,
                calculusStudent01DocumentReference,
                calculusStudent02DocumentReference,

                algorithmOwnerDocumentReference,
                algorithmStudentDocumentReference,
                algorithmTeacherDocumentReference,

                cookingOwnerDocumentReference,
                cookingTeacher01DocumentReference,
                cookingTeacher02DocumentReference
        );

        for (var ref : references) {
            batch.delete(ref);
        }
        batch.commit();
    }

    @ParameterizedTest
    @ValueSource(strings = {""})
    void getClassrooms_It_should_return_three_classrooms_when_role_and_timestamp_are_nulls() {

    }

    @TestConfiguration
    static class TestCollectionsConfig {

        private final Firestore firestore;

        @Autowired
        TestCollectionsConfig(Firestore firestore) {
            this.firestore = firestore;
        }

        @Bean("userCollection")
        @DependsOn("firestore")
        public CollectionReference getUserCollection() throws DatabaseFailedToInitializeException {
            return Optional.ofNullable(firestore)
                    .map(db -> db.collection("users_test"))
                    .orElseThrow(DatabaseFailedToInitializeException::new);
        }

        @Bean("classroomCollection")
        @DependsOn("firestore")
        public CollectionReference getClassroomCollection() throws DatabaseFailedToInitializeException {
            return Optional.ofNullable(firestore)
                    .map(db -> db.collection("classrooms_test"))
                    .orElseThrow(DatabaseFailedToInitializeException::new);
        }

        @Bean("collaboratorCollection")
        @DependsOn("firestore")
        public CollectionReference getCollaboratorCollection() throws DatabaseFailedToInitializeException {
            return Optional.ofNullable(firestore)
                    .map(db -> db.collection("collaborators_test"))
                    .orElseThrow(DatabaseFailedToInitializeException::new);
        }

    }
}
