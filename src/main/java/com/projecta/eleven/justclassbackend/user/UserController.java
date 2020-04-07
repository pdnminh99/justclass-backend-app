package com.projecta.eleven.justclassbackend.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("api/v1/user")
public class UserController {

    private final AbstractUserService userService;

    private User currentUser;

    @Autowired
    public UserController(@Qualifier("defaultUserService") AbstractUserService userService) {
        this.userService = userService;
    }

    private void setCurrentUser(User user) {
        currentUser = user;
    }

    @PostMapping
    public ResponseEntity<User> assignUser(@RequestBody UserResponseBody requestUser) throws ExecutionException, InterruptedException {
        userService.assignUser(requestUser)
                .ifPresent(this::setCurrentUser);
        return Optional.ofNullable(currentUser).isPresent() ?
                ResponseEntity.ok(currentUser) :
                ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
    }

    @ExceptionHandler({ExecutionException.class, InterruptedException.class})
    public ResponseEntity<String> handleException() {
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                .body("Fail to create new user. Please try again.");
    }

//    @GetMapping
//    public ResponseEntity<List<MinifiedUser>> getAllUsers() throws ExecutionException, InterruptedException {
//        List<MinifiedUser> minifiedUsers = repository.getUsers();
//        return ResponseEntity.ok(minifiedUsers);
//    }
//
//    @ExceptionHandler(value = {ExecutionException.class, InterruptedException.class})
//    public ResponseEntity<String> handleException() {
//        return ResponseEntity.ok("It's OK");
//    }
}
