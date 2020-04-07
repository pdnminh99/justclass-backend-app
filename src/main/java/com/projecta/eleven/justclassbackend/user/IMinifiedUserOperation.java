package com.projecta.eleven.justclassbackend.user;

import java.util.Optional;

public interface IMinifiedUserOperation {
    Optional<MinifiedUser> getMinifiedUser(String localId);

    Iterable<MinifiedUser> getMinifiedUsers(String keyword);

    Iterable<MinifiedUser> getMinifiedUsers(Iterable<String> localIds);
}
