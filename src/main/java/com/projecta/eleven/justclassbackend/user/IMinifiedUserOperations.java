package com.projecta.eleven.justclassbackend.user;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public interface IMinifiedUserOperations {
    Optional<MinifiedUser> getUser(String localId);

    List<MinifiedUser> getUsers(String keyword);

    List<MinifiedUser> getUsers(Iterable<String> localIds) throws ExecutionException, InterruptedException;

    List<MinifiedUser> getFriends(String hostId) throws ExecutionException, InterruptedException;
}
