package com.projecta.eleven.justclassbackend.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("api/v1/user")
public class UserController {

    private final UserRepository repository;

    @Autowired
    public UserController(UserRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public ResponseEntity<List<MinifiedUser>> getAllUsers() throws ExecutionException, InterruptedException {
        List<MinifiedUser> minifiedUsers = repository.getUsers();
        return ResponseEntity.ok(minifiedUsers);
    }

    @ExceptionHandler(value = {ExecutionException.class, InterruptedException.class})
    public ResponseEntity<String> handleException() {
        return ResponseEntity.ok("It's OK");
    }
}
