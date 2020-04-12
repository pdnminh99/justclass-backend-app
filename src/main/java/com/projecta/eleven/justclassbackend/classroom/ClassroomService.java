package com.projecta.eleven.justclassbackend.classroom;

import com.google.cloud.Timestamp;
import com.projecta.eleven.justclassbackend.user.IUserOperations;
import com.projecta.eleven.justclassbackend.user.InvalidUserInformationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class ClassroomService implements IClassroomOperationsService {

    private final IClassroomRepository repository;

    @Autowired
    public ClassroomService(IClassroomRepository repository,
                            @Qualifier("defaultUserService") IUserOperations userService) {
        this.repository = repository;
    }

    @Override
    public Stream<Classroom> get(String hostId, Boolean joinedClassesOnly, Timestamp lastRequest) throws InvalidUserInformationException {
        if (Objects.isNull(hostId)) {
            throw new InvalidUserInformationException("LocalId of current logged in user is required to retrieve classrooms", new NullPointerException("LocalId is null."));
        }
        return Stream.empty();
    }

    @Override
    public Optional<Classroom> create(ClassroomRequestBody classroom, String localId)
            throws InvalidUserInformationException, InvalidClassroomInformationException {
        if (Objects.isNull(localId)) {
            throw new InvalidUserInformationException("LocalId of current logged in user is required to create new classroom", new NullPointerException("LocalId is null."));
        }
        if (Objects.isNull(classroom)) {
            throw new InvalidClassroomInformationException("Classroom should have at least theme or title field.", new NullPointerException("classroom title or theme is missing."));
        }
        return Optional.empty();
    }

    @Override
    public Optional<Classroom> update(ClassroomRequestBody classroom, String localId) {
        return Optional.empty();
    }

    @Override
    public Optional<Boolean> delete(String localId, String classroomId) {
        return Optional.empty();
    }
}
