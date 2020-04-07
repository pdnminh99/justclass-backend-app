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
        if (Objects.isNull(user.getLocalId())) {
            throw new InvalidUserInformationException("LocalId field not found");
        }
        var existingUser = repository.getUser(user);
        if (existingUser.isPresent()) {
            return existingUser;
        }
        var firstName = Optional.ofNullable(user.getFirstName());
        var lastName = Optional.ofNullable(user.getLastName());
        var fullName = Optional.ofNullable(user.getFullName());
        var displayName = Optional.ofNullable(user.getDisplayName());
        var email = Optional.ofNullable(user.getEmail());
        if (email.isEmpty() || email.get().length() == 0) {
            throw new InvalidUserInformationException("User must have an email address.", new NullPointerException("Email field is null."));
        }
        if ((firstName.isEmpty() || firstName.get().trim().length() == 0)
                && (lastName.isEmpty() || lastName.get().trim().length() == 0)
                && fullName.isEmpty() && displayName.isEmpty()) {
            throw new InvalidUserInformationException("You must specify user name.", new NullPointerException("firstName, lastName and fullName is null."));
        }
        return repository.createUser(user);
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
