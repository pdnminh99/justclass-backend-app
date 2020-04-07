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

//    @GetMapping("{userId}")
//    public ResponseEntity<MinifiedUser> getMinifiedUser(@PathVariable String userId) {
//
//    }

    @PostMapping
    public ResponseEntity<User> assignUser(@RequestBody UserRequestBody requestUser) throws InvalidUserInformationException, ExecutionException, InterruptedException {
        userService.assignUser(requestUser)
                .ifPresent(this::setCurrentUser);
        return Optional.ofNullable(currentUser).isPresent() ?
                ResponseEntity.ok(currentUser) :
                ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
    }

    @ExceptionHandler({ExecutionException.class, InterruptedException.class})
    public ResponseEntity<String> handleFirestoreException() {
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                .body("Fail to create new user. Please try again.");
    }

    @ExceptionHandler({InvalidUserInformationException.class})
    public ResponseEntity<String> handleInvalidUserResponseBody() {
        return ResponseEntity.badRequest().body("LocalId and Email should not be null. Or at least one of user's name (firstName, lastName or displayName) must not be null.");
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
