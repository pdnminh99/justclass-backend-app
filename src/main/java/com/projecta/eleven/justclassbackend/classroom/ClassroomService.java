package com.projecta.eleven.justclassbackend.classroom;

import com.google.api.core.ApiFutures;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.common.collect.Lists;
import com.projecta.eleven.justclassbackend.user.IUserOperations;
import com.projecta.eleven.justclassbackend.user.InvalidUserInformationException;
import com.projecta.eleven.justclassbackend.user.MinifiedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.CRC32;

@Service
public class ClassroomService implements IClassroomOperationsService {

    private final IClassroomRepository repository;

    private final IUserOperations userService;

    @Autowired
    public ClassroomService(IClassroomRepository repository,
                            @Qualifier("defaultUserService") IUserOperations userService) {
        this.repository = repository;
        this.userService = userService;
    }

    @Override
    public Stream<MinifiedClassroom> getClassrooms(String hostId, MemberRoles role, Timestamp lastRequest)
            throws InvalidUserInformationException, ExecutionException, InterruptedException {
        if (Objects.isNull(hostId) || hostId.trim().length() == 0) {
            throw new InvalidUserInformationException(
                    "LocalId of current logged in user is required to retrieve classrooms",
                    new NullPointerException("LocalId is null."));
        }

        var members = repository.getMembersByUser(hostId, role)
                .map(Member::new)
                .collect(Collectors.toList());

        if (members.isEmpty()) {
            return Stream.empty();
        }

        var classrooms = ApiFutures.allAsList(
                members
                        .parallelStream()
                        .map(Member::getClassroomReference)
                        .map(DocumentReference::get)
                        .collect(Collectors.toList()))
                .get()
                .parallelStream()
                .map(MinifiedClassroom::new)
                .collect(Collectors.toList());

        if (lastRequest != null) {
            var index = 0;

            while (index < classrooms.size()) {
                if (classrooms.get(index).getLastEdit().compareTo(lastRequest) < 0) {
                    classrooms.remove(index);
                    members.remove(index);
                    continue;
                }
                index++;
            }
        }

        // TODO what if you don't find any owner of a classroom?
        var ownersReferences = ApiFutures.allAsList(
                members
                        .parallelStream()
                        .map(Member::getClassroomId)
                        .map(c -> repository.getMembers(c, MemberRoles.OWNER))
                        .collect(Collectors.toList()))
                .get()
                .parallelStream()
                .map(QuerySnapshot::getDocuments)
                .map(c -> c.get(0))
                .map(Member::new)
                .map(Member::getUserReference)
                .map(DocumentReference::get)
                .collect(Collectors.toList());

        var owners = ApiFutures.allAsList(ownersReferences)
                .get()
                .parallelStream()
                .map(MinifiedUser::new)
                .collect(Collectors.toList());

        var studentsCount = getMembersCount(
                members.parallelStream().map(Member::getClassroomId), MemberRoles.STUDENT);

        var collaboratorsCount = getMembersCount(
                members.parallelStream().map(Member::getClassroomId), MemberRoles.COLLABORATOR);

        for (int index = 0; index < classrooms.size(); index++) {
            var currentClassroom = classrooms.get(index);
            currentClassroom.setRole(members.get(index).getRole());
            currentClassroom.setLastAccess(members.get(index).getLastAccess());
            currentClassroom.setOwner(owners.get(index));
            currentClassroom.setCollaboratorsCount(collaboratorsCount.get(index));
            currentClassroom.setStudentsCount(studentsCount.get(index));
            classrooms.set(index, currentClassroom);
        }
        return classrooms.stream();
    }

    private List<Integer> getMembersCount(Stream<String> classroomIdStream, MemberRoles role)
            throws ExecutionException, InterruptedException {
        return ApiFutures.allAsList(
                classroomIdStream
                        .map(id -> repository.getMembers(id, role))
                        .collect(Collectors.toList()))
                .get()
                .parallelStream()
                .map(QuerySnapshot::size)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Classroom> get(String localId, String classroomId)
            throws InvalidUserInformationException, InvalidClassroomInformationException, ExecutionException, InterruptedException {
        validateRetrieveOrDeleteRequestInput(localId, classroomId);
        DocumentSnapshot memberSnapshot = repository.getMember(classroomId, localId)
                .get()
                .get();
        if (!memberSnapshot.exists()) {
            throw new InvalidClassroomInformationException("Classroom does not exist, or user does not have permission to perform `GET` task.");
        }
        var now = Timestamp.now();
        var member = new Member(memberSnapshot);
        var classroom = new Classroom(member
                .getClassroomReference()
                .get()
                .get());

        // Only get the metadata.
        var queries = Lists.newArrayList(
                repository.getMembers(classroomId, MemberRoles.COLLABORATOR),
                repository.getMembers(classroomId, MemberRoles.STUDENT)
        );
        if (member.getRole() == MemberRoles.OWNER) {
            var owner = new MinifiedUser(member.getUserReference().get().get());
            classroom.setOwner(owner);
        } else {
            queries.add(repository.getMembers(classroomId, MemberRoles.OWNER));
        }
        var querySnapshots = ApiFutures.allAsList(queries)
                .get();

        // Parse queries'result.
        classroom.setCollaboratorsCount(querySnapshots.get(0).size());
        classroom.setStudentsCount(querySnapshots.get(1).size());
        if (querySnapshots.size() == 3) {
            var owner = new MinifiedUser(querySnapshots.get(2).getDocuments().get(0));
            classroom.setOwner(owner);
        }
        classroom.setLastAccess(now);
        classroom.setRole(member.getRole());

        // Update collaborators info.
        var memberUpdateMap = new HashMap<String, Object>();
        memberUpdateMap.put("lastAccess", now);
        memberSnapshot.getReference()
                .update(memberUpdateMap);

        return Optional.of(classroom);
    }

    @Override
    public Optional<Classroom> create(ClassroomRequestBody classroomRequestBody, String localId)
            throws InvalidUserInformationException, InvalidClassroomInformationException, ExecutionException, InterruptedException {
        validateCreateRequestInput(classroomRequestBody, localId);
        DocumentReference userReference = verifyUserExistenceOrElseThrow(localId);

        var now = Timestamp.now();
        var classroom = classroomRequestBody.toClassroom(
                now, NotePermissions.VIEW_COMMENT_POST, generateNewPublicCode());
        classroom.setLastEdit(now);

        DocumentReference classroomReference = createClassroomDocument(classroom);
        createMemberDocument(classroomReference, userReference, now);

        classroom.setClassroomId(classroomReference.getId());
        classroom.setRole(MemberRoles.OWNER);
        classroom.setLastAccess(now);
        return Optional.of(classroom);
    }

    private String generateNewPublicCode() throws ExecutionException, InterruptedException {
        var isCorrect = false;
        var crc = new CRC32();
        String newPublicCode = "";
        long currentMillis;

        while (!isCorrect) {
            currentMillis = System.currentTimeMillis();
            crc.update(longToBytes(currentMillis));
            newPublicCode = Long.toHexString(crc.getValue());
            isCorrect = !repository.isPublicCodeAlreadyExist(newPublicCode);
        }

        return newPublicCode;
    }

    private byte[] longToBytes(long x) {
        var buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }

    private DocumentReference verifyUserExistenceOrElseThrow(String localId) throws InvalidUserInformationException, ExecutionException, InterruptedException {
        DocumentReference ref = userService.getUserReference(localId);

        if (!ref.get().get().exists()) {
            throw new InvalidUserInformationException("User with localID [" + localId + "] does not exist.");
        }
        return ref;
    }

    private void createMemberDocument(DocumentReference classroomReference, DocumentReference userReference, Timestamp createdTimestamp) throws ExecutionException, InterruptedException {
        var member = new Member(
                classroomReference.getId() + "_" + userReference.getId(),
                classroomReference,
                userReference,
                createdTimestamp,
                createdTimestamp,
                MemberRoles.OWNER);

        repository.createMember(member);
    }

    private DocumentReference createClassroomDocument(Classroom classroom) throws InterruptedException, ExecutionException, InvalidClassroomInformationException {
        // Since ClassroomRequestBody constructor also include these four fields,
        // we must exclude them to prevent side effects.
        classroom.setClassroomId(null);
        classroom.setRole(null);
        classroom.setLastAccess(null);

        return repository.createClassroom(classroom);
    }

    private void validateCreateRequestInput(ClassroomRequestBody classroomRequestBody, String localId)
            throws InvalidUserInformationException, InvalidClassroomInformationException {
        if (Objects.isNull(localId)) {
            throw new InvalidUserInformationException(
                    "LocalId of current logged in user is required to create new classroomRequestBody",
                    new NullPointerException("LocalId is null."));
        }
        if (Objects.isNull(classroomRequestBody)) {
            throw new InvalidClassroomInformationException(
                    "Classroom should have at least theme or title field.",
                    new NullPointerException("classroomRequestBody title or theme is missing."));
        }
        var title = classroomRequestBody.getTitle();
        if (Objects.isNull(title) || title.trim().length() == 0) {
            throw new InvalidClassroomInformationException("Classroom title must not null or empty.");
        }
        if (Objects.isNull(classroomRequestBody.getTheme())) {
            throw new InvalidClassroomInformationException(
                    "Classroom theme for " + title + " must not null.",
                    new NullPointerException("Theme for classroomRequestBody " + title + " is null."));
        }
    }

    @Override
    public Optional<Classroom> update(Classroom classroom, String localId, Boolean requestNewPublicCode)
            throws InvalidClassroomInformationException, InvalidUserInformationException, ExecutionException, InterruptedException {
        validateClassroomUpdateRequestInput(classroom, localId);
        var classroomId = classroom.getClassroomId();
        DocumentSnapshot memberSnapshot = repository
                .getMember(classroomId, localId)
                .get()
                .get();

        if (!memberSnapshot.exists()) {
            throw new IllegalArgumentException("User " + localId + " is not part of classroom [" + classroomId + "]. Or this classroom does not exist.");
        }
        var member = new Member(memberSnapshot);

        // Only OWNER have permissions to edit class.
        if (Objects.isNull(member.getRole()) || member.getRole() != MemberRoles.OWNER) {
            throw new IllegalArgumentException("User " + localId + " does not have permission to edit classroom [" + classroomId + "] info.");
        }

        var originalClassroomSnapshot = repository.getClassroom(classroomId)
                .get()
                .get();
        if (!originalClassroomSnapshot.exists()) {
            throw new InvalidClassroomInformationException("Classroom [" + classroomId + "] does not exist. Use POST method if you want to create new classroom.");
        }

        var originalClassroom = new Classroom(originalClassroomSnapshot);
        boolean shouldUpdateLastAccess = classroom.getTitle() != null && classroom.getTitle().trim().length() != 0 &&
                !originalClassroom.getTitle().equals(classroom.getTitle()) ||
                classroom.getSubject() != null && !originalClassroom.getSubject().equals(classroom.getSubject()) ||
                classroom.getTheme() != null && !originalClassroom.getTheme().equals(classroom.getTheme());

        if (containChangesAfterCompareAndApplyUpdates(originalClassroom, classroom, requestNewPublicCode)) {
            var classroomMap = classroom.toMap();
            classroomMap.remove("classroomId");
            if (requestNewPublicCode != null && !requestNewPublicCode) {
                classroomMap.put("publicCode", null);
            }
            // No need to exclude `publicCode` and `createdTimestamp` from `classroomMap`, since these fields are marked as @JsonIgnore.
            repository.updateClassroom(classroomMap, classroomId);
        }

        var now = Timestamp.now();
        // Update `lastAccess` of collaborators.
        if (shouldUpdateLastAccess) {
            var classroomUpdateMap = new HashMap<String, Object>();
            classroomUpdateMap.put("lastEdit", now);
            member.getClassroomReference().update(classroomUpdateMap);
        }

        originalClassroom.setRole(member.getRole());
        originalClassroom.setLastAccess(now);
        originalClassroom.setLastEdit(now);
        return Optional.of(originalClassroom);
    }

    private void validateClassroomUpdateRequestInput(Classroom classroom, String localId) throws InvalidClassroomInformationException, InvalidUserInformationException {
        if (Objects.isNull(classroom) || Objects.isNull(classroom.getClassroomId()) || classroom.getClassroomId().trim().length() == 0) {
            throw new InvalidClassroomInformationException("ClassroomId must be included to execute the edit task.",
                    new NullPointerException("Classroom or classroomId is null."));
        }
        if (classroom.getTitle() != null && classroom.getTitle().trim().length() == 0) {
            throw new InvalidClassroomInformationException("Cannot update classroom title of empty.");
        }
        if (Objects.isNull(localId) || localId.trim().length() == 0) {
            throw new InvalidUserInformationException("LocalId of current logged in user must include to execute the edit task.",
                    new NullPointerException("LocalId is null or empty."));
        }
    }

    private boolean containChangesAfterCompareAndApplyUpdates(Classroom oldVersion, Classroom newVersion, Boolean requireNewPublicCode) throws ExecutionException, InterruptedException {
        var containChanges = false;

        if (newVersion.getTitle() != null && newVersion.getTitle().trim().length() != 0 && !newVersion.getTitle().equals(oldVersion.getTitle())) {
            oldVersion.setTitle(newVersion.getTitle());
            containChanges = true;
        }
        if (newVersion.getStudentsNotePermission() != null && newVersion.getStudentsNotePermission() != oldVersion.getStudentsNotePermission()) {
            oldVersion.setStudentsNotePermission(newVersion.getStudentsNotePermission());
            containChanges = true;
        }
        if (newVersion.getDescription() != null && !newVersion.getDescription().equals(oldVersion.getDescription())) {
            oldVersion.setDescription(newVersion.getDescription());
            containChanges = true;
        }
        if (newVersion.getRoom() != null && !newVersion.getRoom().equals(oldVersion.getRoom())) {
            oldVersion.setRoom(newVersion.getRoom());
            containChanges = true;
        }
        if (newVersion.getSection() != null && !newVersion.getSection().equals(oldVersion.getSection())) {
            oldVersion.setSection(newVersion.getSection());
            containChanges = true;
        }
        if (newVersion.getSubject() != null && !newVersion.getSubject().equals(oldVersion.getSubject())) {
            oldVersion.setSubject(newVersion.getSubject());
            containChanges = true;
        }
        if (newVersion.getTheme() != null && !newVersion.getTheme().equals(oldVersion.getTheme())) {
            oldVersion.setTheme(newVersion.getTheme());
            containChanges = true;
        }
        if (requireNewPublicCode != null) {
            if (requireNewPublicCode) {
                var newPublicCode = generateNewPublicCode();
                newVersion.setPublicCode(newPublicCode);
                oldVersion.setPublicCode(newPublicCode);
            } else {
                newVersion.setPublicCode(null);
                oldVersion.setPublicCode(null);
            }
            containChanges = true;
        }
        return containChanges;
    }

    @Override
    public Optional<Boolean> delete(String localId, String classroomId)
            throws InvalidUserInformationException, InvalidClassroomInformationException, ExecutionException, InterruptedException {
        validateRetrieveOrDeleteRequestInput(localId, classroomId);
        // Check if user has permission to perform delete task.
        var memberSnapshot = repository.getMember(classroomId, localId)
                .get()
                .get();
        if (!memberSnapshot.exists()) {
            throw new InvalidClassroomInformationException("Classroom does not exist, or user does not have permission to perform `DELETE` task.");
        }
        var member = new Member(memberSnapshot);
        if (member.getRole() != MemberRoles.OWNER) {
            throw new InvalidUserInformationException("User with Id [" + localId + "] does not have permission to delete classroom [" + classroomId + "].");
        }
        // No need to check classroomReference for
        var collaboratorsByClassroom = repository.getMembersByClassroom(classroomId)
                .map(DocumentReference::delete)
                .collect(Collectors.toList());

        collaboratorsByClassroom.add(
                Objects.requireNonNull(memberSnapshot
                        .get("classroomReference", DocumentReference.class))
                        .delete());
        ApiFutures.allAsList(collaboratorsByClassroom);
        return Optional.of(true);
    }

    private void validateRetrieveOrDeleteRequestInput(String localId, String classroomId)
            throws InvalidUserInformationException, InvalidClassroomInformationException {
        if (Objects.isNull(localId) ||
                localId.trim().length() == 0) {
            throw new InvalidUserInformationException("LocalId must be included to perform this task.");
        }
        if (Objects.isNull(classroomId) ||
                classroomId.trim().length() == 0) {
            throw new InvalidClassroomInformationException("ClassroomId must be included to perform this task.");
        }
    }
}
