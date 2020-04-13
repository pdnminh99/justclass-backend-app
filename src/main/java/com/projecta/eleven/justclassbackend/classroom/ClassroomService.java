package com.projecta.eleven.justclassbackend.classroom;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.projecta.eleven.justclassbackend.user.IUserOperations;
import com.projecta.eleven.justclassbackend.user.InvalidUserInformationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

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
    public Stream<Classroom> get(String hostId, Boolean joinedClassesOnly, Timestamp lastRequest) throws InvalidUserInformationException {
        if (Objects.isNull(hostId)) {
            throw new InvalidUserInformationException(
                    "LocalId of current logged in user is required to retrieve classrooms",
                    new NullPointerException("LocalId is null."));
        }
        return Stream.empty();
    }

    @Override
    public Optional<Classroom> create(ClassroomRequestBody classroomRequestBody, String localId)
            throws InvalidUserInformationException, InvalidClassroomInformationException, ExecutionException, InterruptedException {
        validateCreateRequestInput(classroomRequestBody, localId);
        var userReference = userService.getUserReference(localId);
        validateUserReferenceExistence(userReference, localId);
        var now = Timestamp.now();

        // TODO: generate public code and pass to `toClassroom` method.
        var classroomInstance = classroomRequestBody.toClassroom(
                now, now, NotePermissions.VIEW_COMMENT_POST);
        var classroomMap = classroomInstance.toMap();

        // Since ClassroomRequestBody constructor also include these three fields,
        // we must exclude them to prevent side effects.
        classroomMap.remove("classroomId");
        classroomMap.remove("role");
        classroomMap.remove("lastAccessTimestamp");

        var createdClassroomReference = repository.createClassroom(classroomMap);
        classroomInstance.setClassroomId(createdClassroomReference.getId());

        var collaboratorId = classroomInstance.getClassroomId() + localId;
        var collaboratorMap = (new Collaborator(
                null,
                createdClassroomReference,
                userReference,
                now,
                now,
                CollaboratorRoles.OWNER)).toMap();
        repository.createCollaborator(collaboratorMap, collaboratorId);
        classroomInstance.setRole(CollaboratorRoles.OWNER);
        return Optional.of(classroomInstance);
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

    private void validateUserReferenceExistence(DocumentReference userReference, String localId)
            throws ExecutionException, InterruptedException, InvalidUserInformationException {
        if (Objects.isNull(userReference) || !userReference.get().get().exists()) {
            throw new InvalidUserInformationException("User with localID [" + localId + "] not exist.");
        }
    }

    @Override
    public Optional<Classroom> update(Classroom classroom, String localId)
            throws InvalidClassroomInformationException, InvalidUserInformationException, ExecutionException, InterruptedException {
        validateClassroomUpdateRequestInput(classroom, localId);
        var classroomId = classroom.getClassroomId();
        var collaboratorSnapshot = repository
                .getCollaborator(classroomId, localId)
                .get()
                .get();

        if (!collaboratorSnapshot.exists()) {
            throw new IllegalArgumentException("User " + localId + " do not have permissions to edit classroom [" + classroomId + "] info.");
        }
        var collaboratorRole = (new Collaborator(collaboratorSnapshot))
                .getRole();

        // Only OWNER and TEACHER have permissions to edit class.
        if (Objects.isNull(collaboratorRole) || collaboratorRole == CollaboratorRoles.STUDENT) {
            throw new IllegalArgumentException("User " + localId + " do not have permissions to edit classroom [" + classroomId + "] info.");
        }

        var oldClassroomSnapshot = repository.getClassroom(classroomId)
                .get()
                .get();
        if (!oldClassroomSnapshot.exists()) {
            throw new InvalidClassroomInformationException("Classroom [" + classroomId + "] does not exist. Use POST method if you want to create new classroom.");
        }

        var oldClassroomInstance = new Classroom(oldClassroomSnapshot);
        var now = Timestamp.now();

        if (containChangesAfterCompareAndApplyUpdates(oldClassroomInstance, classroom)) {
            System.err.println("Changes found.");

            var classroomMap = classroom.toMap();
            classroomMap.remove("classroomId");
            if (classroomMap.isEmpty()) {
                throw new IllegalArgumentException("There is nothing to update. If you want to retrieve full data of classroom. Try the `GET` method.");
            }
            // No need to exclude `publicCode` and `createdTimestamp` from `classroomMap`, since these fields are marked as @JsonIgnore.
            repository.updateClassroom(classroomMap, classroomId);

            // Update `lastAccessTimestamp` of collaborator.
            var collaboratorMap = new HashMap<String, Object>();
            collaboratorMap.put("lastAccessTimestamp", now);

            collaboratorSnapshot.getReference()
                    .update(collaboratorMap);
        }
        oldClassroomInstance.setRole(collaboratorRole);
        oldClassroomInstance.setLastAccessTimestamp(now);
        return Optional.of(oldClassroomInstance);
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

    private boolean containChangesAfterCompareAndApplyUpdates(Classroom oldVersion, Classroom newVersion) {
        var containChanges = false;

        if (newVersion.getTitle() != null && !newVersion.getTitle().equals(oldVersion.getTitle())) {
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
        return containChanges;
    }

    @Override
    public Optional<Boolean> delete(String localId, String classroomId) {
        return Optional.empty();
    }
}
