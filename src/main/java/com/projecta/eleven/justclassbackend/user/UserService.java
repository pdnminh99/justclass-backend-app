package com.projecta.eleven.justclassbackend.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public Optional<User> assignUser(UserResponseBody user) throws ExecutionException, InterruptedException {
        var existingUser = repository.getUser(user);
        if (existingUser.isPresent()) {
            return existingUser;
        }
        return repository.createUser(user);
    }
}
