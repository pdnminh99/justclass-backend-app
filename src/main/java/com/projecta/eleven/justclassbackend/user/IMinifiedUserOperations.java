package com.projecta.eleven.justclassbackend.user;

import com.google.cloud.Timestamp;

import java.util.*;
import java.util.concurrent.ExecutionException;

public interface IMinifiedUserOperations {
    Optional<MinifiedUser> getUser(String localId);

    List<MinifiedUser> getUsers(String keyword);

    List<MinifiedUser> getUsers(Iterable<String> localIds, String sortByField, Boolean isAscending)
            throws ExecutionException, InterruptedException;

    default List<MinifiedUser> getUsers(String[] localIds, String sortByField, Boolean isAscending)
            throws ExecutionException, InterruptedException {
        return getUsers(Arrays.asList(localIds), sortByField, isAscending);
    }

    default List<MinifiedUser> getUsers(Map<String, Timestamp> map, String sortByField, Boolean isAscending)
            throws ExecutionException, InterruptedException {
        return getUsers(new ArrayList<>(map.keySet()), sortByField, isAscending);
    }

//    List<MinifiedUser> getFriends(String hostId) throws ExecutionException, InterruptedException;

    Map<String, Timestamp> getLocalIdsOfFriends(String localId, Integer count, Boolean sortByMostRecentAccess);
}
