package com.projecta.eleven.justclassbackend.user;

import java.util.Optional;

public interface IUserRepository {
    Optional<User> createUser(UserResponseBody user);

    Iterable<MinifiedUser> getUsers(Iterable<String> localIds);

    Optional<MinifiedUser> getUser(String localId);

    default Optional<MinifiedUser> getUser(MinifiedUser sampleUser) {
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
