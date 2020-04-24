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

    private final CollectionReference usersCollection;

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
    private final CollectionReference classroomsCollection;
    private final CollectionReference membersCollection;
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
    private Member calculusOwner;
    private Member calculusStudent01;
    private Member calculusStudent02;
    private Classroom algorithmClass;
    private Member algorithmOwner;
    private Member algorithmStudent;
    private Member algorithmCollaborator;
    private Classroom cookingClass;
    private Member cookingOwner;
    private Member cookingCollaborator01;
    private Member cookingCollaborator02;

    @Autowired
    public IClassroomOperationsServiceTest(
            Firestore firestore,
            IClassroomOperationsService service,
            @Qualifier("usersCollection") CollectionReference usersCollection,
            @Qualifier("classroomsCollection") CollectionReference classroomsCollection,
            @Qualifier("membersCollection") CollectionReference membersCollection) {
        this.firestore = firestore;
        this.batch = firestore.batch();
        this.service = service;
        this.usersCollection = usersCollection;
        this.classroomsCollection = classroomsCollection;
        this.membersCollection = membersCollection;
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
        batch.set(usersCollection.document(userToCreate.getLocalId()), map);
    }

    private void initializeClassroomsDocuments() {
        var now = Timestamp.now();

        calculusClass = new Classroom(
                "100",
                "Calculus 01",
                "DESC Calculus 01",
                "SECT Calculus 01",
                "CS50",
                "101",
                1,
                now,
                null,
                null,
                march25,
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
                now,
                null,
                null,
                april7,
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
                now,
                null,
                null,
                april15,
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
        batch.set(classroomsCollection.document(classroomToCreate.getClassroomId()), map);
    }

    private void initializeCollaboratorsDocuments() {
        var now = Timestamp.now();
        var userTomDocumentReference = usersCollection.document(userTom.getLocalId());
        var userJerryDocumentReference = usersCollection.document(userJerry.getLocalId());
        var userJohnDocumentReference = usersCollection.document(userJohn.getLocalId());

        var calculusClassDocumentReference = classroomsCollection.document(calculusClass.getClassroomId());
        var algorithmClassDocumentReference = classroomsCollection.document(algorithmClass.getClassroomId());
        var cookingClassDocumentReference = classroomsCollection.document(cookingClass.getClassroomId());

        // Setup Calculus class.
        calculusOwner = new Member(
                calculusClass.getClassroomId() + userTom.getLocalId(),
                calculusClassDocumentReference,
                userTomDocumentReference,
                now,
                march25,
                MemberRoles.OWNER
        );
        createVirtualCollaborator(calculusOwner);

        calculusStudent01 = new Member(
                calculusClass.getClassroomId() + userJerry.getLocalId(),
                calculusClassDocumentReference,
                userJerryDocumentReference,
                now,
                march25,
                MemberRoles.STUDENT
        );
        createVirtualCollaborator(calculusStudent01);

        calculusStudent02 = new Member(
                calculusClass.getClassroomId() + userJohn.getLocalId(),
                calculusClassDocumentReference,
                userJohnDocumentReference,
                now,
                march25,
                MemberRoles.STUDENT
        );
        createVirtualCollaborator(calculusStudent02);

        // Setup Algorithm class.
        algorithmOwner = new Member(
                algorithmClass.getClassroomId() + userJerry.getLocalId(),
                algorithmClassDocumentReference,
                userJerryDocumentReference,
                now,
                april7,
                MemberRoles.OWNER
        );
        createVirtualCollaborator(algorithmOwner);

        algorithmStudent = new Member(
                algorithmClass.getClassroomId() + userTom.getLocalId(),
                algorithmClassDocumentReference,
                userTomDocumentReference,
                now,
                april7,
                MemberRoles.STUDENT
        );
        createVirtualCollaborator(algorithmStudent);

        algorithmCollaborator = new Member(
                algorithmClass.getClassroomId() + userJohn.getLocalId(),
                algorithmClassDocumentReference,
                userJohnDocumentReference,
                now,
                april7,
                MemberRoles.COLLABORATOR
        );
        createVirtualCollaborator(algorithmCollaborator);

        // Setup Cooking class.
        cookingOwner = new Member(
                cookingClass.getClassroomId() + userJohn.getLocalId(),
                cookingClassDocumentReference,
                userJohnDocumentReference,
                now,
                april15,
                MemberRoles.OWNER
        );
        createVirtualCollaborator(cookingOwner);

        cookingCollaborator01 = new Member(
                cookingClass.getClassroomId() + userTom.getLocalId(),
                cookingClassDocumentReference,
                userTomDocumentReference,
                now,
                april15,
                MemberRoles.COLLABORATOR
        );
        createVirtualCollaborator(cookingCollaborator01);

        cookingCollaborator02 = new Member(
                cookingClass.getClassroomId() + userJerry.getLocalId(),
                cookingClassDocumentReference,
                userJerryDocumentReference,
                now,
                april15,
                MemberRoles.COLLABORATOR
        );
        createVirtualCollaborator(cookingCollaborator02);
    }

    private void createVirtualCollaborator(Member member) {
        var map = member.toMap();
        batch.set(membersCollection.document(member.getMemberId()), map);
    }

    @AfterAll
    void cleanupData() {
        var userTomDocumentReference = usersCollection.document(userTom.getLocalId());
        var userJerryDocumentReference = usersCollection.document(userJerry.getLocalId());
        var userJohnDocumentReference = usersCollection.document(userJohn.getLocalId());

        var calculusClassDocumentReference = classroomsCollection.document(calculusClass.getClassroomId());
        var algorithmClassDocumentReference = classroomsCollection.document(algorithmClass.getClassroomId());
        var cookingClassDocumentReference = classroomsCollection.document(cookingClass.getClassroomId());

        var calculusOwnerDocumentReference = membersCollection.document(calculusOwner.getMemberId());
        var calculusStudent01DocumentReference = membersCollection.document(calculusStudent01.getMemberId());
        var calculusStudent02DocumentReference = membersCollection.document(calculusStudent01.getMemberId());

        var algorithmOwnerDocumentReference = membersCollection.document(algorithmOwner.getMemberId());
        var algorithmStudentDocumentReference = membersCollection.document(algorithmStudent.getMemberId());
        var algorithmTeacherDocumentReference = membersCollection.document(algorithmCollaborator.getMemberId());

        var cookingOwnerDocumentReference = membersCollection.document(cookingOwner.getMemberId());
        var cookingTeacher01DocumentReference = membersCollection.document(cookingCollaborator01.getMemberId());
        var cookingTeacher02DocumentReference = membersCollection.document(cookingCollaborator02.getMemberId());

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

    void assertEqualsCalculusClass(MinifiedClassroom classroom, MemberRoles role) {
        var owner = classroom.getOwner();

        assertEquals(calculusClass.getClassroomId(), classroom.getClassroomId());
        assertEquals(calculusClass.getTitle(), classroom.getTitle());
        assertEquals(calculusClass.getSubject(), classroom.getSubject());
        assertEquals(calculusClass.getTheme(), classroom.getTheme());
        assertEquals(role, classroom.getRole());
        assertEquals(2, classroom.getStudentsCount());
        assertEquals(0, classroom.getCollaboratorsCount());
        assertEquals(userTom.getLocalId(), owner.getLocalId());
        assertEquals(userTom.getDisplayName(), owner.getDisplayName());
        assertEquals(userTom.getPhotoUrl(), owner.getPhotoUrl());
    }

    void assertEqualsAlgorithmClass(MinifiedClassroom classroom, MemberRoles role) {
        var owner = classroom.getOwner();

        assertEquals(algorithmClass.getClassroomId(), classroom.getClassroomId());
        assertEquals(algorithmClass.getTitle(), classroom.getTitle());
        assertEquals(algorithmClass.getSubject(), classroom.getSubject());
        assertEquals(algorithmClass.getTheme(), classroom.getTheme());
        assertEquals(role, classroom.getRole());
        assertEquals(1, classroom.getStudentsCount());
        assertEquals(1, classroom.getCollaboratorsCount());
        assertEquals(userJerry.getLocalId(), owner.getLocalId());
        assertEquals(userJerry.getDisplayName(), owner.getDisplayName());
        assertEquals(userJerry.getPhotoUrl(), owner.getPhotoUrl());
    }

    void assertEqualsCookingClass(MinifiedClassroom classroom, MemberRoles role) {
        var owner = classroom.getOwner();

        assertEquals(cookingClass.getClassroomId(), classroom.getClassroomId());
        assertEquals(cookingClass.getTitle(), classroom.getTitle());
        assertEquals(cookingClass.getSubject(), classroom.getSubject());
        assertEquals(cookingClass.getTheme(), classroom.getTheme());
        assertEquals(role, classroom.getRole());
        assertEquals(0, classroom.getStudentsCount());
        assertEquals(2, classroom.getCollaboratorsCount());
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
        assertEqualsCalculusClass(resultsByList.get(2), MemberRoles.OWNER);
        assertEqualsAlgorithmClass(resultsByList.get(1), MemberRoles.STUDENT);
        assertEqualsCookingClass(resultsByList.get(0), MemberRoles.COLLABORATOR);
    }

    @Test
    void getClassrooms_It_should_return_three_classrooms_when_user_is_Tom_with_owner_role_and_timestamp_is_null()
            throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var results = service.getClassrooms("100", MemberRoles.OWNER, null);
        var resultsByList = results.collect(Collectors.toList());

        assertEquals(1, resultsByList.size());
        assertEqualsCalculusClass(resultsByList.get(0), MemberRoles.OWNER);
    }

    @Test
    void getClassrooms_It_should_return_three_classrooms_when_user_is_Tom_with_teacher_role_and_timestamp_is_null()
            throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var results = service.getClassrooms("100", MemberRoles.COLLABORATOR, null);
        var resultsByList = results.collect(Collectors.toList());

        assertEquals(1, resultsByList.size());
        assertEqualsCookingClass(resultsByList.get(0), MemberRoles.COLLABORATOR);
    }

    @Test
    void getClassrooms_It_should_return_three_classrooms_when_user_is_Tom_with_student_role_and_timestamp_is_null()
            throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var results = service.getClassrooms("100", MemberRoles.STUDENT, null);
        var resultsByList = results.collect(Collectors.toList());

        assertEquals(1, resultsByList.size());
        assertEqualsAlgorithmClass(resultsByList.get(0), MemberRoles.STUDENT);
    }

    @ParameterizedTest
    @ValueSource(longs = {1_584_839_542_000_000L, 1_585_012_342_000_000L, 1_585_138_304_000_000L})
    void getClassrooms_It_should_return_three_classrooms_when_user_is_Tom_with_null_role_and_at_around_timestamp_25th_march(Long epoch)
            throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var timestamp = Timestamp.ofTimeMicroseconds(epoch);
        var results = service.getClassrooms("100", null, timestamp);
        var resultsByList = results.collect(Collectors.toList());

        assertEquals(3, resultsByList.size());
        assertEqualsCalculusClass(resultsByList.get(2), MemberRoles.OWNER);
        assertEqualsAlgorithmClass(resultsByList.get(1), MemberRoles.STUDENT);
        assertEqualsCookingClass(resultsByList.get(0), MemberRoles.COLLABORATOR);
    }

    @ParameterizedTest
    @ValueSource(longs = {1_584_839_542_000_000L, 1_585_012_342_000_000L, 1_585_138_304_000_000L})
    void getClassrooms_It_should_return_one_classroom_when_user_is_Tom_with_owner_role_and_at_around_timestamp_25th_march(Long epoch)
            throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var timestamp = Timestamp.ofTimeMicroseconds(epoch);
        var results = service.getClassrooms("100", MemberRoles.OWNER, timestamp);
        var resultsByList = results.collect(Collectors.toList());

        assertEquals(1, resultsByList.size());
        assertEqualsCalculusClass(resultsByList.get(0), MemberRoles.OWNER);
    }

    @ParameterizedTest
    @ValueSource(longs = {1_584_839_542_000_000L, 1_585_012_342_000_000L, 1_585_138_304_000_000L})
    void getClassrooms_It_should_return_one_classrooms_when_user_is_Tom_with_student_role_and_at_around_timestamp_25th_march(Long epoch)
            throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var timestamp = Timestamp.ofTimeMicroseconds(epoch);
        var results = service.getClassrooms("100", MemberRoles.STUDENT, timestamp);
        var resultsByList = results.collect(Collectors.toList());

        assertEquals(1, resultsByList.size());
        assertEqualsAlgorithmClass(resultsByList.get(0), MemberRoles.STUDENT);
    }

    @ParameterizedTest
    @ValueSource(longs = {1_584_839_542_000_000L, 1_585_012_342_000_000L, 1_585_138_304_000_000L})
    void getClassrooms_It_should_return_three_classrooms_when_user_is_Tom_with_teacher_role_and_at_around_timestamp_25th_march(Long epoch)
            throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var timestamp = Timestamp.ofTimeMicroseconds(epoch);
        var results = service.getClassrooms("100", MemberRoles.COLLABORATOR, timestamp);
        var resultsByList = results.collect(Collectors.toList());

        assertEquals(1, resultsByList.size());
        assertEqualsCookingClass(resultsByList.get(0), MemberRoles.COLLABORATOR);
    }

    @ParameterizedTest
    @ValueSource(longs = {1_586_049_142_000_000L, 1_586_135_542_000_000L, 1_586_261_578_000_000L})
    void getClassrooms_It_should_return_two_classrooms_when_user_is_Tom_with_null_role_and_at_around_timestamp_7th_april(Long epoch)
            throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var timestamp = Timestamp.ofTimeMicroseconds(epoch);
        var results = service.getClassrooms("100", null, timestamp);
        var resultsByList = results.collect(Collectors.toList());

        assertEquals(2, resultsByList.size());
        assertEqualsAlgorithmClass(resultsByList.get(1), MemberRoles.STUDENT);
        assertEqualsCookingClass(resultsByList.get(0), MemberRoles.COLLABORATOR);
    }

    @ParameterizedTest
    @ValueSource(longs = {1_586_049_142_000_000L, 1_586_135_542_000_000L, 1_586_261_578_000_000L})
    void getClassrooms_It_should_return_empty_when_user_is_Tom_with_owner_role_and_at_around_timestamp_7th_april(Long epoch)
            throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var timestamp = Timestamp.ofTimeMicroseconds(epoch);
        var results = service.getClassrooms("100", MemberRoles.OWNER, timestamp);
        var resultsByList = results.collect(Collectors.toList());

        assertEquals(0, resultsByList.size());
    }

    @ParameterizedTest
    @ValueSource(longs = {1_586_049_142_000_000L, 1_586_135_542_000_000L, 1_586_261_578_000_000L})
    void getClassrooms_It_should_return_one_classroom_when_user_is_Tom_with_student_role_and_at_around_timestamp_7th_april(Long epoch)
            throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var timestamp = Timestamp.ofTimeMicroseconds(epoch);
        var results = service.getClassrooms("100", MemberRoles.STUDENT, timestamp);
        var resultsByList = results.collect(Collectors.toList());

        assertEquals(1, resultsByList.size());
        assertEqualsAlgorithmClass(resultsByList.get(0), MemberRoles.STUDENT);
    }

    @ParameterizedTest
    @ValueSource(longs = {1_586_049_142_000_000L, 1_586_135_542_000_000L, 1_586_261_578_000_000L})
    void getClassrooms_It_should_return_two_classrooms_when_user_is_Tom_with_teacher_role_and_at_around_timestamp_7th_april(Long epoch)
            throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var timestamp = Timestamp.ofTimeMicroseconds(epoch);
        var results = service.getClassrooms("100", MemberRoles.COLLABORATOR, timestamp);
        var resultsByList = results.collect(Collectors.toList());

        assertEquals(1, resultsByList.size());
        assertEqualsCookingClass(resultsByList.get(0), MemberRoles.COLLABORATOR);
    }

    @ParameterizedTest
    @ValueSource(longs = {1_586_653_942_000_000L, 1_586_826_742_000_000L, 1_586_952_778_000_000L})
    void getClassrooms_It_should_return_one_classroom_when_user_is_Tom_with_null_role_and_at_around_timestamp_15th_april(Long epoch)
            throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var timestamp = Timestamp.ofTimeMicroseconds(epoch);
        var results = service.getClassrooms("100", null, timestamp);
        var resultsByList = results.collect(Collectors.toList());

        assertEquals(1, resultsByList.size());
        assertEqualsCookingClass(resultsByList.get(0), MemberRoles.COLLABORATOR);
    }

    @ParameterizedTest
    @ValueSource(longs = {1_586_653_942_000_000L, 1_586_826_742_000_000L, 1_586_952_778_000_000L})
    void getClassrooms_It_should_return_empty_when_user_is_Tom_with_owner_role_and_at_around_timestamp_15th_april(Long epoch)
            throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var timestamp = Timestamp.ofTimeMicroseconds(epoch);
        var results = service.getClassrooms("100", MemberRoles.OWNER, timestamp);
        var resultsByList = results.collect(Collectors.toList());

        assertEquals(0, resultsByList.size());
    }

    @ParameterizedTest
    @ValueSource(longs = {1_586_653_942_000_000L, 1_586_826_742_000_000L, 1_586_952_778_000_000L})
    void getClassrooms_It_should_return_one_classroom_when_user_is_Tom_with_teacher_role_and_at_around_timestamp_15th_april(Long epoch)
            throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var timestamp = Timestamp.ofTimeMicroseconds(epoch);
        var results = service.getClassrooms("100", MemberRoles.COLLABORATOR, timestamp);
        var resultsByList = results.collect(Collectors.toList());

        assertEquals(1, resultsByList.size());
        assertEqualsCookingClass(resultsByList.get(0), MemberRoles.COLLABORATOR);
    }

    @ParameterizedTest
    @ValueSource(longs = {1_586_653_942_000_000L, 1_586_826_742_000_000L, 1_586_952_778_000_000L})
    void getClassrooms_It_should_return_empty_when_user_is_Tom_with_student_role_and_at_around_timestamp_15th_april(Long epoch)
            throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var timestamp = Timestamp.ofTimeMicroseconds(epoch);
        var results = service.getClassrooms("100", MemberRoles.STUDENT, timestamp);
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
        var results = service.getClassrooms("100", MemberRoles.OWNER, timestamp);
        var resultsByList = results.collect(Collectors.toList());

        assertEquals(0, resultsByList.size());
    }

    @ParameterizedTest
    @ValueSource(longs = {1_586_999_542_000_000L, 1_587_777_142_000_000L, 1_588_209_142_000_000L})
    void getClassrooms_It_should_return_empty_when_user_is_Tom_with_student_role_and_at_after_timestamp_15th_april(Long epoch)
            throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var timestamp = Timestamp.ofTimeMicroseconds(epoch);
        var results = service.getClassrooms("100", MemberRoles.STUDENT, timestamp);
        var resultsByList = results.collect(Collectors.toList());

        assertEquals(0, resultsByList.size());
    }

    @ParameterizedTest
    @ValueSource(longs = {1_586_999_542_000_000L, 1_587_777_142_000_000L, 1_588_209_142_000_000L})
    void getClassrooms_It_should_return_empty_when_user_is_Tom_with_teacher_role_and_at_after_timestamp_15th_april(Long epoch)
            throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var timestamp = Timestamp.ofTimeMicroseconds(epoch);
        var results = service.getClassrooms("100", MemberRoles.COLLABORATOR, timestamp);
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
        assertEqualsCalculusClass(resultsByList.get(2), MemberRoles.STUDENT);
        assertEqualsAlgorithmClass(resultsByList.get(1), MemberRoles.OWNER);
        assertEqualsCookingClass(resultsByList.get(0), MemberRoles.COLLABORATOR);
    }

    @Test
    void getClassrooms_It_should_return_three_classrooms_when_user_is_Jerry_with_owner_role_and_timestamp_is_null()
            throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var results = service.getClassrooms("200", MemberRoles.OWNER, null);
        var resultsByList = results.collect(Collectors.toList());

        assertEquals(1, resultsByList.size());
        assertEqualsAlgorithmClass(resultsByList.get(0), MemberRoles.OWNER);
    }

    @Test
    void getClassrooms_It_should_return_three_classrooms_when_user_is_Jerry_with_student_role_and_timestamp_is_null()
            throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var results = service.getClassrooms("200", MemberRoles.STUDENT, null);
        var resultsByList = results.collect(Collectors.toList());

        assertEquals(1, resultsByList.size());
        assertEqualsCalculusClass(resultsByList.get(0), MemberRoles.STUDENT);
    }

    @Test
    void getClassrooms_It_should_return_three_classrooms_when_user_is_Jerry_with_teacher_role_and_timestamp_is_null()
            throws InterruptedException, ExecutionException, InvalidUserInformationException {
        var results = service.getClassrooms("200", MemberRoles.COLLABORATOR, null);
        var resultsByList = results.collect(Collectors.toList());

        assertEquals(1, resultsByList.size());
        assertEqualsCookingClass(resultsByList.get(0), MemberRoles.COLLABORATOR);
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

//    @ParameterizedTest
//    @ValueSource(longs = {1_584_839_542_000_000L, 1_585_012_342_000_000L, 1_585_138_304_000_000L})
//    void getClassrooms_It_should_return_three_classrooms_when_user_is_Jerry_with_null_role_and_at_around_timestamp_25th_march(Long epoch) {
//
//    }
//
//    @ParameterizedTest
//    @ValueSource(longs = {1_584_839_542_000_000L, 1_585_012_342_000_000L, 1_585_138_304_000_000L})
//    void getClassrooms_It_should_return_three_classrooms_when_user_is_Jerry_with_owner_role_and_at_around_timestamp_25th_march(Long epoch) {
//
//    }
//
//    @ParameterizedTest
//    @ValueSource(longs = {1_584_839_542_000_000L, 1_585_012_342_000_000L, 1_585_138_304_000_000L})
//    void getClassrooms_It_should_return_three_classrooms_when_user_is_Jerry_with_teacher_role_and_at_around_timestamp_25th_march(Long epoch) {
//
//    }
//
//    @ParameterizedTest
//    @ValueSource(longs = {1_584_839_542_000_000L, 1_585_012_342_000_000L, 1_585_138_304_000_000L})
//    void getClassrooms_It_should_return_three_classrooms_when_user_is_Jerry_with_student_role_and_at_around_timestamp_25th_march(Long epoch) {
//
//    }
//
//    @ParameterizedTest
//    @ValueSource(longs = {})
//    void getClassrooms_It_should_return_three_classrooms_when_user_is_Jerry_with_null_role_and_at_around_timestamp_7th_april(Long epoch) {
//
//    }
//
//    @ParameterizedTest
//    @ValueSource(longs = {})
//    void getClassrooms_It_should_return_three_classrooms_when_user_is_Jerry_with_owner_role_and_at_around_timestamp_7th_april(Long epoch) {
//
//    }
//
//    @ParameterizedTest
//    @ValueSource(longs = {})
//    void getClassrooms_It_should_return_three_classrooms_when_user_is_Jerry_with_teacher_role_and_at_around_timestamp_7th_april(Long epoch) {
//
//    }
//
//    @ParameterizedTest
//    @ValueSource(longs = {})
//    void getClassrooms_It_should_return_three_classrooms_when_user_is_Jerry_with_student_role_and_at_around_timestamp_7th_april(Long epoch) {
//
//    }
//
//    @ParameterizedTest
//    @ValueSource(longs = {})
//    void getClassrooms_It_should_return_three_classrooms_when_user_is_Jerry_with_null_role_and_at_around_timestamp_15th_april(Long epoch) {
//
//    }
//
//    @ParameterizedTest
//    @ValueSource(longs = {})
//    void getClassrooms_It_should_return_three_classrooms_when_user_is_Jerry_with_owner_role_and_at_around_timestamp_15th_april(Long epoch) {
//
//    }
//
//    @ParameterizedTest
//    @ValueSource(longs = {})
//    void getClassrooms_It_should_return_three_classrooms_when_user_is_Jerry_with_teacher_role_and_at_around_timestamp_15th_april(Long epoch) {
//
//    }
//
//    @ParameterizedTest
//    @ValueSource(longs = {})
//    void getClassrooms_It_should_return_three_classrooms_when_user_is_Jerry_with_student_role_and_at_around_timestamp_15th_april(Long epoch) {
//
//    }
//
//    @ParameterizedTest
//    @ValueSource(longs = {})
//    void getClassrooms_It_should_return_three_classrooms_when_user_is_Jerry_with_null_role_and_at_after_timestamp_15th_april(Long epoch) {
//
//    }
//
//    @ParameterizedTest
//    @ValueSource(longs = {})
//    void getClassrooms_It_should_return_three_classrooms_when_user_is_Jerry_with_owner_role_and_at_after_timestamp_15th_april(Long epoch) {
//
//    }
//
//    @ParameterizedTest
//    @ValueSource(longs = {})
//    void getClassrooms_It_should_return_three_classrooms_when_user_is_Jerry_with_teacher_role_and_at_after_timestamp_15th_april(Long epoch) {
//
//    }
//
//    @ParameterizedTest
//    @ValueSource(longs = {})
//    void getClassrooms_It_should_return_three_classrooms_when_user_is_Jerry_with_student_role_and_at_after_timestamp_15th_april(Long epoch) {
//
//    }
//
//    @Test
//    void getClassrooms_It_should_return_three_classrooms_when_user_is_John_with_role_and_timestamp_are_nulls()
//            throws InterruptedException, ExecutionException, InvalidUserInformationException {
//        var results = service.getClassrooms("300", null, null);
//        var resultsByList = results.collect(Collectors.toList());
//
//        assertEquals(3, resultsByList.size());
//        assertEqualsCalculusClass(resultsByList.get(2), CollaboratorRoles.STUDENT);
//        assertEqualsAlgorithmClass(resultsByList.get(1), CollaboratorRoles.TEACHER);
//        assertEqualsCookingClass(resultsByList.get(0), CollaboratorRoles.OWNER);
//    }
//
//    @ParameterizedTest
//    @ValueSource(longs = {})
//    void getClassrooms_It_should_return_three_classrooms_when_user_is_John_with_null_role_and_at_around_timestamp_25th_march(Long epoch) {
//
//    }
//
//    @ParameterizedTest
//    @ValueSource(longs = {})
//    void getClassrooms_It_should_return_three_classrooms_when_user_is_John_with_owner_role_and_at_around_timestamp_25th_march(Long epoch) {
//
//    }
//
//    @ParameterizedTest
//    @ValueSource(longs = {})
//    void getClassrooms_It_should_return_three_classrooms_when_user_is_John_with_teacher_role_and_at_around_timestamp_25th_march(Long epoch) {
//
//    }
//
//    @ParameterizedTest
//    @ValueSource(longs = {})
//    void getClassrooms_It_should_return_three_classrooms_when_user_is_John_with_student_role_and_at_around_timestamp_25th_march(Long epoch) {
//
//    }
//
//    @ParameterizedTest
//    @ValueSource(longs = {})
//    void getClassrooms_It_should_return_three_classrooms_when_user_is_John_with_null_role_and_at_around_timestamp_7th_april(Long epoch) {
//
//    }
//
//    @ParameterizedTest
//    @ValueSource(longs = {})
//    void getClassrooms_It_should_return_three_classrooms_when_user_is_John_with_owner_role_and_at_around_timestamp_7th_april(Long epoch) {
//
//    }
//
//    @ParameterizedTest
//    @ValueSource(longs = {})
//    void getClassrooms_It_should_return_three_classrooms_when_user_is_John_with_teacher_role_and_at_around_timestamp_7th_april(Long epoch) {
//
//    }
//
//    @ParameterizedTest
//    @ValueSource(longs = {})
//    void getClassrooms_It_should_return_three_classrooms_when_user_is_John_with_student_role_and_at_around_timestamp_7th_april(Long epoch) {
//
//    }
//
//    @ParameterizedTest
//    @ValueSource(longs = {})
//    void getClassrooms_It_should_return_three_classrooms_when_user_is_John_with_null_role_and_at_around_timestamp_15th_april(Long epoch) {
//
//    }
//
//    @ParameterizedTest
//    @ValueSource(longs = {})
//    void getClassrooms_It_should_return_three_classrooms_when_user_is_John_with_owner_role_and_at_around_timestamp_15th_april(Long epoch) {
//
//    }
//
//    @ParameterizedTest
//    @ValueSource(longs = {})
//    void getClassrooms_It_should_return_three_classrooms_when_user_is_John_with_teacher_role_and_at_around_timestamp_15th_april(Long epoch) {
//
//    }
//
//    @ParameterizedTest
//    @ValueSource(longs = {})
//    void getClassrooms_It_should_return_three_classrooms_when_user_is_John_with_student_role_and_at_around_timestamp_15th_april(Long epoch) {
//
//    }

    // TODO continue from here


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
        assertThrows(InvalidUserInformationException.class, () -> service.getClassrooms("", MemberRoles.OWNER, timestamp));
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
        assertThrows(InvalidUserInformationException.class, () -> service.getClassrooms("", MemberRoles.STUDENT, timestamp));
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
        assertThrows(InvalidUserInformationException.class, () -> service.getClassrooms("", MemberRoles.COLLABORATOR, timestamp));
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
        assertThrows(InvalidUserInformationException.class, () -> service.getClassrooms(null, MemberRoles.OWNER, Timestamp.ofTimeMicroseconds(epoch)));
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
        assertThrows(InvalidUserInformationException.class, () -> service.getClassrooms(null, MemberRoles.STUDENT, Timestamp.ofTimeMicroseconds(epoch)));
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
        assertThrows(InvalidUserInformationException.class, () -> service.getClassrooms(null, MemberRoles.COLLABORATOR, Timestamp.ofTimeMicroseconds(epoch)));
    }

    @TestConfiguration
    static class TestCollectionsConfig {

        private final Firestore firestore;

        @Autowired
        TestCollectionsConfig(Firestore firestore) {
            this.firestore = firestore;
        }

        @Bean("usersCollection")
        @DependsOn("firestore")
        public CollectionReference getUsersCollection() throws DatabaseFailedToInitializeException {
            return Optional.ofNullable(firestore)
                    .map(db -> db.collection("users_test"))
                    .orElseThrow(DatabaseFailedToInitializeException::new);
        }

        @Bean("classroomsCollection")
        @DependsOn("firestore")
        public CollectionReference getClassroomsCollection() throws DatabaseFailedToInitializeException {
            return Optional.ofNullable(firestore)
                    .map(db -> db.collection("classrooms_test"))
                    .orElseThrow(DatabaseFailedToInitializeException::new);
        }

        @Bean("membersCollection")
        @DependsOn("firestore")
        public CollectionReference getMembersCollection() throws DatabaseFailedToInitializeException {
            return Optional.ofNullable(firestore)
                    .map(db -> db.collection("members_test"))
                    .orElseThrow(DatabaseFailedToInitializeException::new);
        }

    }
}
