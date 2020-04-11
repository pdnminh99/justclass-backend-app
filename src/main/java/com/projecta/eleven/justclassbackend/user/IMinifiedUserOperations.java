package com.projecta.eleven.justclassbackend.user;

import com.google.cloud.Timestamp;

import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

public interface IMinifiedUserOperations {
    Stream<MinifiedUser> getFriendsOfUser(String localId, Timestamp lastTimeRequest)
            throws ExecutionException, InterruptedException;
}
