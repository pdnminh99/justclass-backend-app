package com.projecta.eleven.justclassbackend.Controllers;

import com.projecta.eleven.justclassbackend.Models.User;
import com.projecta.eleven.justclassbackend.Repositories.FirestoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("user")
public class UserController {

    private final FirestoreRepository repository;

    @Autowired
    public UserController(FirestoreRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() throws ExecutionException, InterruptedException {
        List<User> users = repository.getUsers();
        return ResponseEntity.ok(users);
    }

    @ExceptionHandler(value = { ExecutionException.class, InterruptedException.class })
    public ResponseEntity<String> handleException() {
        return ResponseEntity.ok("It's OK");
    }
}
