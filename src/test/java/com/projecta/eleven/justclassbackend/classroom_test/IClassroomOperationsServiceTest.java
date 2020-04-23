package com.projecta.eleven.justclassbackend.classroom_test;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteBatch;
import com.google.common.collect.Lists;
import com.projecta.eleven.justclassbackend.classroom.*;
import com.projecta.eleven.justclassbackend.configuration.DatabaseFailedToInitializeException;
import com.projecta.eleven.justclassbackend.junit_config.CustomReplaceUnderscore;
import com.projecta.eleven.justclassbackend.user.InvalidUserInformationException;
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
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

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
    private final IClassroomOperationsService service;
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
            IClassroomOperationsService service,
            @Qualifier("userCollection") CollectionReference userCollection,
            @Qualifier("classroomCollection") CollectionReference classroomCollection,
            @Qualifier("collaboratorCollection") CollectionReference collaboratorCollection) {
        this.firestore = firestore;
        this.batch = firestore.batch();
        this.service = service;
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
                calculusClass.getClassroomId() + userTom.getLocalId(),
                calculusClassDocumentReference,
                userTomDocumentReference,
                march25,
                march25,
                CollaboratorRoles.OWNER
        );
        createVirtualCollaborator(calculusOwner);

        calculusStudent01 = new Collaborator(
                calculusClass.getClassroomId() + userJerry.getLocalId(),
                calculusClassDocumentReference,
                userJerryDocumentReference,
                march25,
                march25,
                CollaboratorRoles.STUDENT
        );
        createVirtualCollaborator(calculusStudent01);

        calculusStudent02 = new Collaborator(
                calculusClass.getClassroomId() + userJohn.getLocalId(),
                calculusClassDocumentReference,
                userJohnDocumentReference,
                march25,
                march25,
                CollaboratorRoles.STUDENT
        );
        createVirtualCollaborator(calculusStudent02);

        // Setup Algorithm class.
        algorithmOwner = new Collaborator(
                algorithmClass.getClassroomId() + userJerry.getLocalId(),
                algorithmClassDocumentReference,
                userJerryDocumentReference,
                april7,
                april7,
                CollaboratorRoles.OWNER
        );
        createVirtualCollaborator(algorithmOwner);

        algorithmStudent = new Collaborator(
                algorithmClass.getClassroomId() + userTom.getLocalId(),
                algorithmClassDocumentReference,
                userTomDocumentReference,
                april7,
                april7,
                CollaboratorRoles.STUDENT
        );
        createVirtualCollaborator(algorithmStudent);

        algorithmTeacher = new Collaborator(
                algorithmClass.getClassroomId() + userJohn.getLocalId(),
                algorithmClassDocumentReference,
                userJohnDocumentReference,
                april7,
                april7,
                CollaboratorRoles.TEACHER
        );
        createVirtualCollaborator(algorithmTeacher);

        // Setup Cooking class.
        cookingOwner = new Collaborator(
                cookingClass.getClassroomId() + userJohn.getLocalId(),
                cookingClassDocumentReference,
                userJohnDocumentReference,
                april15,
                april15,
                CollaboratorRoles.OWNER
        );
        createVirtualCollaborator(cookingOwner);

        cookingTeacher01 = new Collaborator(
                cookingClass.getClassroomId() + userTom.getLocalId(),
                cookingClassDocumentReference,
                userTomDocumentReference,
                april15,
                april15,
                CollaboratorRoles.TEACHER
        );
        createVirtualCollaborator(cookingTeacher01);

        cookingTeacher02 = new Collaborator(
                cookingClass.getClassroomId() + userJerry.getLocalId(),
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
        batch.set(collaboratorCollection.document(collaborator.getCollaboratorId()), map);
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

    void assertEqualsCalculusClass(MinifiedClassroom classroom, CollaboratorRoles role) {
        var owner = classroom.getOwner();

        assertEquals(calculusClass.getClassroomId(), classroom.getClassroomId());
        assertEquals(calculusClass.getTitle(), classroom.getTitle());
        assertEquals(calculusClass.getSubject(), classroom.getSubject());
        assertEquals(calculusClass.getTheme(), classroom.getTheme());
        assertEquals(role, classroom.getRole());
        assertEquals(2, classroom.getStudentsCount());
        assertEquals(0, classroom.getTeachersCount());
        assertEquals(userTom.getLocalId(), owner.getLocalId());
        assertEquals(userTom.getDisplayName(), owner.getDisplayName());
        assertEquals(userTom.getPhotoUrl(), owner.getPhotoUrl());
    }

    void assertEqualsAlgorithmClass(MinifiedClassroom classroom, CollaboratorRoles role) {
        var owner = classroom.getOwner();

        assertEquals(algorithmClass.getClassroomId(), classroom.getClassroomId());
        assertEquals(algorithmClass.getTitle(), classroom.getTitle());
        assertEquals(algorithmClass.getSubject(), classroom.getSubject());
        assertEquals(algorithmClass.getTheme(), classroom.getTheme());
        assertEquals(role, classroom.getRole());
        assertEquals(1, classroom.getStudentsCount());
        assertEquals(1, classroom.getTeachersCount());
        assertEquals(userJerry.getLocalId(), owner.getLocalId());
        assertEquals(userJerry.getDisplayName(), owner.getDisplayName());
        assertEquals(userJerry.getPhotoUrl(), owner.getPhotoUrl());
    }

    void assertEqualsCookingClass(MinifiedClassroom classroom, CollaboratorRoles role) {
        var owner = classroom.getOwner();

        assertEquals(cookingClass.getClassroomId(), classroom.getClassroomId());
        assertEquals(cookingClass.getTitle(), classroom.getTitle());
        assertEquals(cookingClass.getSubject(), classroom.getSubject());
        assertEquals(cookingClass.getTheme(), classroom.getTheme());
        assertEquals(role, classroom.getRole());
        assertEquals(0, classroom.getStudentsCount());
        assertEquals(2, classroom.getTeachersCount());
        assertEquals(userJohn.getLocalId(), owner.getLocalId());
        assertEquals(userJohn.getDisplayName(), owner.getDisplayName());
        assertEquals(userJohn.getPhotoUrl(), owner.getPhotoUrl());
    }

    // Valid cases

    @ParameterizedTest
    @ValueSource(strings = {"100", "200", "300"})
    void getClassrooms_It_should_not_throw_classrooms_when_role_and_timestamp_are_nulls(String hostId) {
        assertDoesNotThrow(() -> service.getClassrooms(hostId, null, null));
    }

    @Test
    void getClassrooms_It_should_return_three_classrooms_when_user_is_Tom_with_role_and_timestamp_are_nulls()
            throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var results = service.getClassrooms("100", null, null);
        var resultsByList = results.collect(Collectors.toList());

        assertEquals(3, resultsByList.size());
        assertEqualsCalculusClass(resultsByList.get(2), CollaboratorRoles.OWNER);
        assertEqualsAlgorithmClass(resultsByList.get(1), CollaboratorRoles.STUDENT);
        assertEqualsCookingClass(resultsByList.get(0), CollaboratorRoles.TEACHER);
    }

    @Test
    void getClassrooms_It_should_return_three_classrooms_when_user_is_Tom_with_owner_role_and_timestamp_is_null()
            throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var results = service.getClassrooms("100", CollaboratorRoles.OWNER, null);
        var resultsByList = results.collect(Collectors.toList());

        assertEquals(1, resultsByList.size());
        assertEqualsCalculusClass(resultsByList.get(0), CollaboratorRoles.OWNER);
    }

    @Test
    void getClassrooms_It_should_return_three_classrooms_when_user_is_Tom_with_teacher_role_and_timestamp_is_null()
            throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var results = service.getClassrooms("100", CollaboratorRoles.TEACHER, null);
        var resultsByList = results.collect(Collectors.toList());

        assertEquals(1, resultsByList.size());
        assertEqualsCookingClass(resultsByList.get(0), CollaboratorRoles.TEACHER);
    }

    @Test
    void getClassrooms_It_should_return_three_classrooms_when_user_is_Tom_with_student_role_and_timestamp_is_null()
            throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var results = service.getClassrooms("100", CollaboratorRoles.STUDENT, null);
        var resultsByList = results.collect(Collectors.toList());

        assertEquals(1, resultsByList.size());
        assertEqualsAlgorithmClass(resultsByList.get(0), CollaboratorRoles.STUDENT);
    }

    @ParameterizedTest
    @ValueSource(longs = {1_584_839_542_000_000L, 1_585_012_342_000_000L, 1_585_138_304_000_000L})
    void getClassrooms_It_should_return_three_classrooms_when_user_is_Tom_with_null_role_and_at_around_timestamp_25th_march(Long epoch)
            throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var timestamp = Timestamp.ofTimeMicroseconds(epoch);
        var results = service.getClassrooms("100", null, timestamp);
        var resultsByList = results.collect(Collectors.toList());

        assertEquals(3, resultsByList.size());
        assertEqualsCalculusClass(resultsByList.get(2), CollaboratorRoles.OWNER);
        assertEqualsAlgorithmClass(resultsByList.get(1), CollaboratorRoles.STUDENT);
        assertEqualsCookingClass(resultsByList.get(0), CollaboratorRoles.TEACHER);
    }

    @ParameterizedTest
    @ValueSource(longs = {1_584_839_542_000_000L, 1_585_012_342_000_000L, 1_585_138_304_000_000L})
    void getClassrooms_It_should_return_one_classroom_when_user_is_Tom_with_owner_role_and_at_around_timestamp_25th_march(Long epoch)
            throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var timestamp = Timestamp.ofTimeMicroseconds(epoch);
        var results = service.getClassrooms("100", CollaboratorRoles.OWNER, timestamp);
        var resultsByList = results.collect(Collectors.toList());

        assertEquals(1, resultsByList.size());
        assertEqualsCalculusClass(resultsByList.get(0), CollaboratorRoles.OWNER);
    }

    @ParameterizedTest
    @ValueSource(longs = {1_584_839_542_000_000L, 1_585_012_342_000_000L, 1_585_138_304_000_000L})
    void getClassrooms_It_should_return_one_classrooms_when_user_is_Tom_with_student_role_and_at_around_timestamp_25th_march(Long epoch)
            throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var timestamp = Timestamp.ofTimeMicroseconds(epoch);
        var results = service.getClassrooms("100", CollaboratorRoles.STUDENT, timestamp);
        var resultsByList = results.collect(Collectors.toList());

        assertEquals(1, resultsByList.size());
        assertEqualsAlgorithmClass(resultsByList.get(0), CollaboratorRoles.STUDENT);
    }

    @ParameterizedTest
    @ValueSource(longs = {1_584_839_542_000_000L, 1_585_012_342_000_000L, 1_585_138_304_000_000L})
    void getClassrooms_It_should_return_three_classrooms_when_user_is_Tom_with_teacher_role_and_at_around_timestamp_25th_march(Long epoch)
            throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var timestamp = Timestamp.ofTimeMicroseconds(epoch);
        var results = service.getClassrooms("100", CollaboratorRoles.TEACHER, timestamp);
        var resultsByList = results.collect(Collectors.toList());

        assertEquals(1, resultsByList.size());
        assertEqualsCookingClass(resultsByList.get(0), CollaboratorRoles.TEACHER);
    }

    @ParameterizedTest
    @ValueSource(longs = {1_586_049_142_000_000L, 1_586_135_542_000_000L, 1_586_261_578_000_000L})
    void getClassrooms_It_should_return_two_classrooms_when_user_is_Tom_with_null_role_and_at_around_timestamp_7th_april(Long epoch)
            throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var timestamp = Timestamp.ofTimeMicroseconds(epoch);
        var results = service.getClassrooms("100", null, timestamp);
        var resultsByList = results.collect(Collectors.toList());

        assertEquals(2, resultsByList.size());
        assertEqualsAlgorithmClass(resultsByList.get(1), CollaboratorRoles.STUDENT);
        assertEqualsCookingClass(resultsByList.get(0), CollaboratorRoles.TEACHER);
    }

    @ParameterizedTest
    @ValueSource(longs = {1_586_049_142_000_000L, 1_586_135_542_000_000L, 1_586_261_578_000_000L})
    void getClassrooms_It_should_return_empty_when_user_is_Tom_with_owner_role_and_at_around_timestamp_7th_april(Long epoch)
            throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var timestamp = Timestamp.ofTimeMicroseconds(epoch);
        var results = service.getClassrooms("100", CollaboratorRoles.OWNER, timestamp);
        var resultsByList = results.collect(Collectors.toList());

        assertEquals(0, resultsByList.size());
    }

    @ParameterizedTest
    @ValueSource(longs = {1_586_049_142_000_000L, 1_586_135_542_000_000L, 1_586_261_578_000_000L})
    void getClassrooms_It_should_return_one_classroom_when_user_is_Tom_with_student_role_and_at_around_timestamp_7th_april(Long epoch)
            throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var timestamp = Timestamp.ofTimeMicroseconds(epoch);
        var results = service.getClassrooms("100", CollaboratorRoles.STUDENT, timestamp);
        var resultsByList = results.collect(Collectors.toList());

        assertEquals(1, resultsByList.size());
        assertEqualsAlgorithmClass(resultsByList.get(0), CollaboratorRoles.STUDENT);
    }

    @ParameterizedTest
    @ValueSource(longs = {1_586_049_142_000_000L, 1_586_135_542_000_000L, 1_586_261_578_000_000L})
    void getClassrooms_It_should_return_two_classrooms_when_user_is_Tom_with_teacher_role_and_at_around_timestamp_7th_april(Long epoch)
            throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var timestamp = Timestamp.ofTimeMicroseconds(epoch);
        var results = service.getClassrooms("100", CollaboratorRoles.TEACHER, timestamp);
        var resultsByList = results.collect(Collectors.toList());

        assertEquals(1, resultsByList.size());
        assertEqualsCookingClass(resultsByList.get(0), CollaboratorRoles.TEACHER);
    }

    @ParameterizedTest
    @ValueSource(longs = {1_586_653_942_000_000L, 1_586_826_742_000_000L, 1_586_952_778_000_000L})
    void getClassrooms_It_should_return_one_classroom_when_user_is_Tom_with_null_role_and_at_around_timestamp_15th_april(Long epoch)
            throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var timestamp = Timestamp.ofTimeMicroseconds(epoch);
        var results = service.getClassrooms("100", null, timestamp);
        var resultsByList = results.collect(Collectors.toList());

        assertEquals(1, resultsByList.size());
        assertEqualsCookingClass(resultsByList.get(0), CollaboratorRoles.TEACHER);
    }

    @ParameterizedTest
    @ValueSource(longs = {1_586_653_942_000_000L, 1_586_826_742_000_000L, 1_586_952_778_000_000L})
    void getClassrooms_It_should_return_empty_when_user_is_Tom_with_owner_role_and_at_around_timestamp_15th_april(Long epoch)
            throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var timestamp = Timestamp.ofTimeMicroseconds(epoch);
        var results = service.getClassrooms("100", CollaboratorRoles.OWNER, timestamp);
        var resultsByList = results.collect(Collectors.toList());

        assertEquals(0, resultsByList.size());
    }

    @ParameterizedTest
    @ValueSource(longs = {1_586_653_942_000_000L, 1_586_826_742_000_000L, 1_586_952_778_000_000L})
    void getClassrooms_It_should_return_one_classroom_when_user_is_Tom_with_teacher_role_and_at_around_timestamp_15th_april(Long epoch)
            throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var timestamp = Timestamp.ofTimeMicroseconds(epoch);
        var results = service.getClassrooms("100", CollaboratorRoles.TEACHER, timestamp);
        var resultsByList = results.collect(Collectors.toList());

        assertEquals(1, resultsByList.size());
        assertEqualsCookingClass(resultsByList.get(0), CollaboratorRoles.TEACHER);
    }

    @ParameterizedTest
    @ValueSource(longs = {1_586_653_942_000_000L, 1_586_826_742_000_000L, 1_586_952_778_000_000L})
    void getClassrooms_It_should_return_empty_when_user_is_Tom_with_student_role_and_at_around_timestamp_15th_april(Long epoch)
            throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var timestamp = Timestamp.ofTimeMicroseconds(epoch);
        var results = service.getClassrooms("100", CollaboratorRoles.STUDENT, timestamp);
        var resultsByList = results.collect(Collectors.toList());

        assertEquals(0, resultsByList.size());
    }

    @ParameterizedTest
    @ValueSource(longs = {1_586_999_542_000_000L, 1_587_777_142_000_000L, 1_588_209_142_000_000L})
    void getClassrooms_It_should_return_empty_when_user_is_Tom_with_null_role_and_at_after_timestamp_15th_april(Long epoch)
            throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var timestamp = Timestamp.ofTimeMicroseconds(epoch);
        var results = service.getClassrooms("100", null, timestamp);
        var resultsByList = results.collect(Collectors.toList());

        assertEquals(0, resultsByList.size());
    }

    @ParameterizedTest
    @ValueSource(longs = {1_586_999_542_000_000L, 1_587_777_142_000_000L, 1_588_209_142_000_000L})
    void getClassrooms_It_should_return_empty_when_user_is_Tom_with_owner_role_and_at_after_timestamp_15th_april(Long epoch)
            throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var timestamp = Timestamp.ofTimeMicroseconds(epoch);
        var results = service.getClassrooms("100", CollaboratorRoles.OWNER, timestamp);
        var resultsByList = results.collect(Collectors.toList());

        assertEquals(0, resultsByList.size());
    }

    @ParameterizedTest
    @ValueSource(longs = {1_586_999_542_000_000L, 1_587_777_142_000_000L, 1_588_209_142_000_000L})
    void getClassrooms_It_should_return_empty_when_user_is_Tom_with_student_role_and_at_after_timestamp_15th_april(Long epoch)
            throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var timestamp = Timestamp.ofTimeMicroseconds(epoch);
        var results = service.getClassrooms("100", CollaboratorRoles.STUDENT, timestamp);
        var resultsByList = results.collect(Collectors.toList());

        assertEquals(0, resultsByList.size());
    }

    @ParameterizedTest
    @ValueSource(longs = {1_586_999_542_000_000L, 1_587_777_142_000_000L, 1_588_209_142_000_000L})
    void getClassrooms_It_should_return_empty_when_user_is_Tom_with_teacher_role_and_at_after_timestamp_15th_april(Long epoch)
            throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var timestamp = Timestamp.ofTimeMicroseconds(epoch);
        var results = service.getClassrooms("100", CollaboratorRoles.TEACHER, timestamp);
        var resultsByList = results.collect(Collectors.toList());

        assertEquals(0, resultsByList.size());
    }

    // User Tom ended.

    @Test
    void getClassrooms_It_should_return_three_classrooms_when_user_is_Jerry_with_role_and_timestamp_are_nulls()
            throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var results = service.getClassrooms("200", null, null);
        var resultsByList = results.collect(Collectors.toList());

        assertEquals(3, resultsByList.size());
        assertEqualsCalculusClass(resultsByList.get(2), CollaboratorRoles.STUDENT);
        assertEqualsAlgorithmClass(resultsByList.get(1), CollaboratorRoles.OWNER);
        assertEqualsCookingClass(resultsByList.get(0), CollaboratorRoles.TEACHER);
    }

    @Test
    void getClassrooms_It_should_return_three_classrooms_when_user_is_Jerry_with_owner_role_and_timestamp_is_null()
            throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var results = service.getClassrooms("200", CollaboratorRoles.OWNER, null);
        var resultsByList = results.collect(Collectors.toList());

        assertEquals(1, resultsByList.size());
        assertEqualsAlgorithmClass(resultsByList.get(0), CollaboratorRoles.OWNER);
    }

    @Test
    void getClassrooms_It_should_return_three_classrooms_when_user_is_Jerry_with_student_role_and_timestamp_is_null()
            throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var results = service.getClassrooms("200", CollaboratorRoles.STUDENT, null);
        var resultsByList = results.collect(Collectors.toList());

        assertEquals(1, resultsByList.size());
        assertEqualsCalculusClass(resultsByList.get(2), CollaboratorRoles.STUDENT);
    }

    @Test
    void getClassrooms_It_should_return_three_classrooms_when_user_is_Jerry_with_teacher_role_and_timestamp_is_null()
            throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var results = service.getClassrooms("200", CollaboratorRoles.TEACHER, null);
        var resultsByList = results.collect(Collectors.toList());

        assertEquals(1, resultsByList.size());
        assertEqualsCookingClass(resultsByList.get(0), CollaboratorRoles.TEACHER);
    }

    /**
     * TODO: implement these.
     * <p>
     * Before or equal 25th March
     * <p>
     * After 25th March and before or equal 7th April
     * <p>
     * After 7th April and before or equal 15th April
     * <p>
     * After 15th April
     */

    @ParameterizedTest
    @ValueSource(longs = {1_584_839_542_000_000L, 1_585_012_342_000_000L, 1_585_138_304_000_000L})
    void getClassrooms_It_should_return_three_classrooms_when_user_is_Jerry_with_null_role_and_at_around_timestamp_25th_march(Long epoch) {

    }

    @ParameterizedTest
    @ValueSource(longs = {1_584_839_542_000_000L, 1_585_012_342_000_000L, 1_585_138_304_000_000L})
    void getClassrooms_It_should_return_three_classrooms_when_user_is_Jerry_with_owner_role_and_at_around_timestamp_25th_march(Long epoch) {

    }

    @ParameterizedTest
    @ValueSource(longs = {1_584_839_542_000_000L, 1_585_012_342_000_000L, 1_585_138_304_000_000L})
    void getClassrooms_It_should_return_three_classrooms_when_user_is_Jerry_with_teacher_role_and_at_around_timestamp_25th_march(Long epoch) {

    }

    @ParameterizedTest
    @ValueSource(longs = {1_584_839_542_000_000L, 1_585_012_342_000_000L, 1_585_138_304_000_000L})
    void getClassrooms_It_should_return_three_classrooms_when_user_is_Jerry_with_student_role_and_at_around_timestamp_25th_march(Long epoch) {

    }

    @ParameterizedTest
    @ValueSource(longs = {})
    void getClassrooms_It_should_return_three_classrooms_when_user_is_Jerry_with_null_role_and_at_around_timestamp_7th_april(Long epoch) {

    }

    @ParameterizedTest
    @ValueSource(longs = {})
    void getClassrooms_It_should_return_three_classrooms_when_user_is_Jerry_with_owner_role_and_at_around_timestamp_7th_april(Long epoch) {

    }

    @ParameterizedTest
    @ValueSource(longs = {})
    void getClassrooms_It_should_return_three_classrooms_when_user_is_Jerry_with_teacher_role_and_at_around_timestamp_7th_april(Long epoch) {

    }

    @ParameterizedTest
    @ValueSource(longs = {})
    void getClassrooms_It_should_return_three_classrooms_when_user_is_Jerry_with_student_role_and_at_around_timestamp_7th_april(Long epoch) {

    }

    @ParameterizedTest
    @ValueSource(longs = {})
    void getClassrooms_It_should_return_three_classrooms_when_user_is_Jerry_with_null_role_and_at_around_timestamp_15th_april(Long epoch) {

    }

    @ParameterizedTest
    @ValueSource(longs = {})
    void getClassrooms_It_should_return_three_classrooms_when_user_is_Jerry_with_owner_role_and_at_around_timestamp_15th_april(Long epoch) {

    }

    @ParameterizedTest
    @ValueSource(longs = {})
    void getClassrooms_It_should_return_three_classrooms_when_user_is_Jerry_with_teacher_role_and_at_around_timestamp_15th_april(Long epoch) {

    }

    @ParameterizedTest
    @ValueSource(longs = {})
    void getClassrooms_It_should_return_three_classrooms_when_user_is_Jerry_with_student_role_and_at_around_timestamp_15th_april(Long epoch) {

    }

    @ParameterizedTest
    @ValueSource(longs = {})
    void getClassrooms_It_should_return_three_classrooms_when_user_is_Jerry_with_null_role_and_at_after_timestamp_15th_april(Long epoch) {

    }

    @ParameterizedTest
    @ValueSource(longs = {})
    void getClassrooms_It_should_return_three_classrooms_when_user_is_Jerry_with_owner_role_and_at_after_timestamp_15th_april(Long epoch) {

    }

    @ParameterizedTest
    @ValueSource(longs = {})
    void getClassrooms_It_should_return_three_classrooms_when_user_is_Jerry_with_teacher_role_and_at_after_timestamp_15th_april(Long epoch) {

    }

    @ParameterizedTest
    @ValueSource(longs = {})
    void getClassrooms_It_should_return_three_classrooms_when_user_is_Jerry_with_student_role_and_at_after_timestamp_15th_april(Long epoch) {

    }

    @Test
    void getClassrooms_It_should_return_three_classrooms_when_user_is_John_with_role_and_timestamp_are_nulls()
            throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var results = service.getClassrooms("300", null, null);
        var resultsByList = results.collect(Collectors.toList());

        assertEquals(3, resultsByList.size());
        assertEqualsCalculusClass(resultsByList.get(2), CollaboratorRoles.STUDENT);
        assertEqualsAlgorithmClass(resultsByList.get(1), CollaboratorRoles.TEACHER);
        assertEqualsCookingClass(resultsByList.get(0), CollaboratorRoles.OWNER);
    }

    @ParameterizedTest
    @ValueSource(longs = {})
    void getClassrooms_It_should_return_three_classrooms_when_user_is_John_with_null_role_and_at_around_timestamp_25th_march(Long epoch) {

    }

    @ParameterizedTest
    @ValueSource(longs = {})
    void getClassrooms_It_should_return_three_classrooms_when_user_is_John_with_owner_role_and_at_around_timestamp_25th_march(Long epoch) {

    }

    @ParameterizedTest
    @ValueSource(longs = {})
    void getClassrooms_It_should_return_three_classrooms_when_user_is_John_with_teacher_role_and_at_around_timestamp_25th_march(Long epoch) {

    }

    @ParameterizedTest
    @ValueSource(longs = {})
    void getClassrooms_It_should_return_three_classrooms_when_user_is_John_with_student_role_and_at_around_timestamp_25th_march(Long epoch) {

    }

    @ParameterizedTest
    @ValueSource(longs = {})
    void getClassrooms_It_should_return_three_classrooms_when_user_is_John_with_null_role_and_at_around_timestamp_7th_april(Long epoch) {

    }

    @ParameterizedTest
    @ValueSource(longs = {})
    void getClassrooms_It_should_return_three_classrooms_when_user_is_John_with_owner_role_and_at_around_timestamp_7th_april(Long epoch) {

    }

    @ParameterizedTest
    @ValueSource(longs = {})
    void getClassrooms_It_should_return_three_classrooms_when_user_is_John_with_teacher_role_and_at_around_timestamp_7th_april(Long epoch) {

    }

    @ParameterizedTest
    @ValueSource(longs = {})
    void getClassrooms_It_should_return_three_classrooms_when_user_is_John_with_student_role_and_at_around_timestamp_7th_april(Long epoch) {

    }

    @ParameterizedTest
    @ValueSource(longs = {})
    void getClassrooms_It_should_return_three_classrooms_when_user_is_John_with_null_role_and_at_around_timestamp_15th_april(Long epoch) {

    }

    @ParameterizedTest
    @ValueSource(longs = {})
    void getClassrooms_It_should_return_three_classrooms_when_user_is_John_with_owner_role_and_at_around_timestamp_15th_april(Long epoch) {

    }

    @ParameterizedTest
    @ValueSource(longs = {})
    void getClassrooms_It_should_return_three_classrooms_when_user_is_John_with_teacher_role_and_at_around_timestamp_15th_april(Long epoch) {

    }

    @ParameterizedTest
    @ValueSource(longs = {})
    void getClassrooms_It_should_return_three_classrooms_when_user_is_John_with_student_role_and_at_around_timestamp_15th_april(Long epoch) {

    }


    // Invalid cases

//    @ParameterizedTest
//    @ValueSource(strings = {"400"})
//    @NullSource
//    @EmptySource
//    void getClassrooms_It_should_return_empty_when_user_null_empty_or_no_match(String hostId)
//            throws InterruptedException, ExecutionException, InvalidUserInformationException {
//        var results = service.getClassrooms(hostId, null, null)
//                .collect(Collectors.toList());
//
//        assertEquals(0, results.size());
//    }
//
//    @ParameterizedTest
//    @ValueSource(strings = {"400"})
//    @NullSource
//    @EmptySource
//    void getClassrooms_It_should_return_empty_when_user_null_empty_or_no_match_with_owner_role(String hostId)
//            throws InterruptedException, ExecutionException, InvalidUserInformationException {
//        var results = service.getClassrooms(hostId, CollaboratorRoles.OWNER, null)
//                .collect(Collectors.toList());
//
//        assertEquals(0, results.size());
//    }
//
//    @ParameterizedTest
//    @ValueSource(strings = {"400"})
//    @NullSource
//    @EmptySource
//    void getClassrooms_It_should_return_empty_when_user_null_empty_or_no_match_with_student_role(String hostId)
//            throws InterruptedException, ExecutionException, InvalidUserInformationException {
//        var results = service.getClassrooms(hostId, CollaboratorRoles.STUDENT, null)
//                .collect(Collectors.toList());
//
//        assertEquals(0, results.size());
//    }
//
//    @ParameterizedTest
//    @ValueSource(strings = {"400"})
//    @NullSource
//    @EmptySource
//    void getClassrooms_It_should_return_empty_when_user_null_empty_or_no_match_with_teacher_role(String hostId)
//            throws InterruptedException, ExecutionException, InvalidUserInformationException {
//        var results = service.getClassrooms(hostId, CollaboratorRoles.TEACHER, null)
//                .collect(Collectors.toList());
//
//        assertEquals(0, results.size());
//    }
//
//    @ParameterizedTest
//    @ValueSource(longs = {})
//    @NullSource
//    void getClassrooms_It_should_return_empty_when_user_no_match_with_owner_role_and_different_timestamp(Long epoch)
//            throws InterruptedException, ExecutionException, InvalidUserInformationException {
//        var timestamp = Timestamp.ofTimeMicroseconds(epoch);
//        var results = service.getClassrooms("400", CollaboratorRoles.OWNER, timestamp)
//                .collect(Collectors.toList());
//
//        assertEquals(0, results.size());
//    }

    @ParameterizedTest
    @ValueSource(longs = {1_584_839_542_000_000L,
            1_585_012_342_000_000L,
            1_585_138_304_000_000L,
            1_586_049_142_000_000L,
            1_586_135_542_000_000L,
            1_586_261_578_000_000L,
            1_586_653_942_000_000L,
            1_586_826_742_000_000L,
            1_586_952_778_000_000L,
            1_586_999_542_000_000L,
            1_587_777_142_000_000L,
            1_588_209_142_000_000L})
    void getClassrooms_It_should_throw_when_user_empty_with_owner_role_and_different_timestamp(Long epoch) {
        var timestamp = Timestamp.ofTimeMicroseconds(epoch);
        assertThrows(InvalidUserInformationException.class, () -> service.getClassrooms("", CollaboratorRoles.OWNER, timestamp));
    }

    @ParameterizedTest
    @ValueSource(longs = {1_584_839_542_000_000L,
            1_585_012_342_000_000L,
            1_585_138_304_000_000L,
            1_586_049_142_000_000L,
            1_586_135_542_000_000L,
            1_586_261_578_000_000L,
            1_586_653_942_000_000L,
            1_586_826_742_000_000L,
            1_586_952_778_000_000L,
            1_586_999_542_000_000L,
            1_587_777_142_000_000L,
            1_588_209_142_000_000L})
    void getClassrooms_It_should_throw_when_user_empty_with_student_role_and_different_timestamp(Long epoch) {
        var timestamp = Timestamp.ofTimeMicroseconds(epoch);
        assertThrows(InvalidUserInformationException.class, () -> service.getClassrooms("", CollaboratorRoles.STUDENT, timestamp));
    }

    @ParameterizedTest
    @ValueSource(longs = {1_584_839_542_000_000L,
            1_585_012_342_000_000L,
            1_585_138_304_000_000L,
            1_586_049_142_000_000L,
            1_586_135_542_000_000L,
            1_586_261_578_000_000L,
            1_586_653_942_000_000L,
            1_586_826_742_000_000L,
            1_586_952_778_000_000L,
            1_586_999_542_000_000L,
            1_587_777_142_000_000L,
            1_588_209_142_000_000L})
    void getClassrooms_It_should_throw_when_user_empty_with_teacher_role_and_different_timestamp(Long epoch) {
        var timestamp = Timestamp.ofTimeMicroseconds(epoch);
        assertThrows(InvalidUserInformationException.class, () -> service.getClassrooms("", CollaboratorRoles.TEACHER, timestamp));
    }

    @ParameterizedTest
    @ValueSource(longs = {1_584_839_542_000_000L,
            1_585_012_342_000_000L,
            1_585_138_304_000_000L,
            1_586_049_142_000_000L,
            1_586_135_542_000_000L,
            1_586_261_578_000_000L,
            1_586_653_942_000_000L,
            1_586_826_742_000_000L,
            1_586_952_778_000_000L,
            1_586_999_542_000_000L,
            1_587_777_142_000_000L,
            1_588_209_142_000_000L})
    void getClassrooms_It_should_throw_when_user_null_with_owner_role_and_different_timestamp(Long epoch) {
        assertThrows(InvalidUserInformationException.class, () -> service.getClassrooms(null, CollaboratorRoles.OWNER, Timestamp.ofTimeMicroseconds(epoch)));
    }

    @ParameterizedTest
    @ValueSource(longs = {1_584_839_542_000_000L,
            1_585_012_342_000_000L,
            1_585_138_304_000_000L,
            1_586_049_142_000_000L,
            1_586_135_542_000_000L,
            1_586_261_578_000_000L,
            1_586_653_942_000_000L,
            1_586_826_742_000_000L,
            1_586_952_778_000_000L,
            1_586_999_542_000_000L,
            1_587_777_142_000_000L,
            1_588_209_142_000_000L})
    void getClassrooms_It_should_throw_when_user_null_with_student_role_and_different_timestamp(Long epoch) {
        assertThrows(InvalidUserInformationException.class, () -> service.getClassrooms(null, CollaboratorRoles.STUDENT, Timestamp.ofTimeMicroseconds(epoch)));
    }

    @ParameterizedTest
    @ValueSource(longs = {1_584_839_542_000_000L,
            1_585_012_342_000_000L,
            1_585_138_304_000_000L,
            1_586_049_142_000_000L,
            1_586_135_542_000_000L,
            1_586_261_578_000_000L,
            1_586_653_942_000_000L,
            1_586_826_742_000_000L,
            1_586_952_778_000_000L,
            1_586_999_542_000_000L,
            1_587_777_142_000_000L,
            1_588_209_142_000_000L})
    void getClassrooms_It_should_throw_when_user_null_with_teacher_role_and_different_timestamp(Long epoch) {
        assertThrows(InvalidUserInformationException.class, () -> service.getClassrooms(null, CollaboratorRoles.TEACHER, Timestamp.ofTimeMicroseconds(epoch)));
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
