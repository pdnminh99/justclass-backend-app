package com.projecta.eleven.justclassbackend.user;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

public interface IMinifiedUserOperations {
    Optional<MinifiedUser> getUser(String localId);

    Iterable<MinifiedUser> getUsers(String keyword);

    Iterable<MinifiedUser> getUsers(Iterable<String> localIds) throws ExecutionException, InterruptedException;

    Iterable<MinifiedUser> getFriends(String hostId) throws ExecutionException, InterruptedException;
}
