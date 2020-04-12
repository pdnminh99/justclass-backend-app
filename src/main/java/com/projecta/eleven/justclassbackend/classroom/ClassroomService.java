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
    public Stream<UserViewClassroom> get(String hostId, Boolean joinedClassesOnly, Timestamp lastRequest) throws InvalidUserInformationException {
        if (Objects.isNull(hostId)) {
            throw new InvalidUserInformationException(
                    "LocalId of current logged in user is required to retrieve classrooms",
                    new NullPointerException("LocalId is null."));
        }
        return Stream.empty();
    }

    @Override
    public Optional<UserViewClassroom> create(ClassroomRequestBody classroomRequestBody, String localId)
            throws InvalidUserInformationException, InvalidClassroomInformationException, ExecutionException, InterruptedException {
        validateCreateRequestInput(classroomRequestBody, localId);
        var userReference = userService.getUserReference(localId);
        validateUserReferenceExistence(userReference, localId);
        var now = Timestamp.now();
        var classroomMap = classroomRequestBody.toMap();
        classroomMap.put("createdTimestamp", now);

        var createdClassroomReference = repository.createClassroom(classroomMap);
        var collaboratorMap = (new Collaborator(null, createdClassroomReference, userReference, CollaboratorRoles.OWNER))
                .toMap();
        collaboratorMap.remove("collaboratorId");
        repository.createCollaborator(collaboratorMap);
        return Optional.of(
                classroomRequestBody.toClassroom(
                        createdClassroomReference.getId(),
                        now
                ).toUserViewClassroom(CollaboratorRoles.OWNER)
        );
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
    public Optional<UserViewClassroom> update(ClassroomRequestBody classroom, String localId) {
        return Optional.empty();
    }

    @Override
    public Optional<Boolean> delete(String localId, String classroomId) {
        return Optional.empty();
    }
}
