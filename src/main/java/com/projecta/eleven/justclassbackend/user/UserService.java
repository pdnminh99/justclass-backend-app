package com.projecta.eleven.justclassbackend.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Service("defaultUserService")
public class UserService extends AbstractUserService {

    private final IUserRepository repository;

    @Autowired
    public UserService(IUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<User> assignUser(UserRequestBody user) throws ExecutionException, InterruptedException, InvalidUserInformationException {
        if (!verifyValidStringField(user.getLocalId())) {
            throw new InvalidUserInformationException("LocalId field not found");
        }
        var existingUser = repository.getUser(user);
        if (existingUser.isPresent()) {
            return existingUser;
        }
        if (verifyValidStringField(user.getEmail()) && (verifyValidStringField(user.getFirstName()) ||
                verifyValidStringField(user.getLastName()) ||
                verifyValidStringField(user.getFullName()) ||
                verifyValidStringField(user.getDisplayName()))
        ) {
            return repository.createUser(user);
        }
        throw new InvalidUserInformationException("At least one of the name fields (firstName, lastName, fullName or displayName) and email field must not be null.", new NullPointerException("Email field is null."));
    }

    private boolean verifyValidStringField(String value) {
        return Objects.nonNull(value) && value.trim().length() != 0;
    }

    @Override
    public Optional<MinifiedUser> getMinifiedUser(String localId) {
        return Optional.empty();
    }

    @Override
    public Iterable<MinifiedUser> getMinifiedUsers(String keyword) {
        return null;
    }

    @Override
    public Iterable<MinifiedUser> getMinifiedUsers(Iterable<String> localIds) {
        return null;
    }
}
