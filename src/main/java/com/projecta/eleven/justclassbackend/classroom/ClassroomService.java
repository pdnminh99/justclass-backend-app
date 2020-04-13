package com.projecta.eleven.justclassbackend.classroom;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.projecta.eleven.justclassbackend.user.IUserOperations;
import com.projecta.eleven.justclassbackend.user.InvalidUserInformationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

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
        var classroomInstance = classroomRequestBody.toClassroom(now, NotePermissions.VIEW_COMMENT_POST);
        var classroomMap = classroomInstance.toMap();

        // Since ClassroomRequestBody constructor also include these two fields,
        // we must exclude them to prevent side effects.
        classroomMap.remove("classroomId");
        classroomMap.remove("role");

        var createdClassroomReference = repository.createClassroom(classroomMap);
        classroomInstance.setClassroomId(createdClassroomReference.getId());

        var collaboratorId = classroomInstance.getClassroomId() + localId;
        var collaboratorMap = (new Collaborator(
                null,
                createdClassroomReference,
                userReference,
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
    public Optional<Classroom> update(Classroom classroom, String localId) {
        // TODO: check if user has permission to edit this classroom.
        System.err.println("----------------------------");
        System.err.println(classroom.getClassroomId());
        System.err.println(classroom.getTitle());
        System.err.println(classroom.getSubject());
        System.err.println(classroom.getDescription());
        System.err.println(classroom.getRoom());
        System.err.println(classroom.getPublicCode());
        System.err.println(classroom.getNotePermission());
        return Optional.empty();
    }

    @Override
    public Optional<Boolean> delete(String localId, String classroomId) {
        return Optional.empty();
    }
}
