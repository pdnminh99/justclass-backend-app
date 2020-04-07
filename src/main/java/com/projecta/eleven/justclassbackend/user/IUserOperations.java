package com.projecta.eleven.justclassbackend.user;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

public interface IUserOperations {
    Optional<User> assignUser(UserResponseBody user) throws ExecutionException, InterruptedException;
}
