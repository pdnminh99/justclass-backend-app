package com.projecta.eleven.justclassbackend.user;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

public interface IUserOperations {
    Optional<User> assignUser(UserRequestBody user) throws ExecutionException, InterruptedException, InvalidUserInformationException;
}
