package com.projecta.eleven.justclassbackend.user;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

interface IUserRepository {
    Optional<User> createUser(UserRequestBody user) throws ExecutionException, InterruptedException;

    Iterable<MinifiedUser> getUsers(Iterable<String> localIds);

    Optional<User> getUser(String localId) throws ExecutionException, InterruptedException;

    default Optional<User> getUser(MinifiedUser sampleUser) throws ExecutionException, InterruptedException {
        return getUser(sampleUser.getLocalId());
    }
//    User deleteUser(MinifiedUser user);
//
//    User deleteUser(String userId);
//
//    User getUser(String user);
//
//    User getUser(MinifiedUser user);
//
//    User updateUser(MinifiedUser user);
}
