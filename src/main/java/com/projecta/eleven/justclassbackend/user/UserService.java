package com.projecta.eleven.justclassbackend.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("defaultUserService")
public class UserService extends AbstractUserService {

    private final IUserRepository repository;

    @Autowired
    public UserService(IUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public User assignUsers(UserResponseBody user) {
        return null;
    }
}
