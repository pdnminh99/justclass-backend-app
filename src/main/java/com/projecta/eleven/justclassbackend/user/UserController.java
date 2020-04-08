package com.projecta.eleven.justclassbackend.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.util.NestedServletException;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("api/v1/user")
public class UserController {

    private final AbstractUserService userService;

    @Autowired
    public UserController(@Qualifier("defaultUserService") AbstractUserService userService) {
        this.userService = userService;
    }

    @GetMapping("{hostId}")
    public ResponseEntity<Iterable<MinifiedUser>> getFriends(@PathVariable String hostId)
            throws ExecutionException, InterruptedException {
        return Optional.ofNullable(userService.getFriends(hostId))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound()
                        .build());
    }

    @PostMapping
    public ResponseEntity<User> assignUser(@RequestBody UserRequestBody requestUser,
                                           @Nullable
                                           @RequestParam(value = "autoUpdate", required = false) Boolean autoUpdate)
            throws InvalidUserInformationException, ExecutionException, InterruptedException {
        return userService.assignUser(requestUser, autoUpdate)
                .map(this::constructAssignUserResponse)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                        .build());
    }

    private ResponseEntity<User> constructAssignUserResponse(User user) {
        return user.isNewUser() ?
                ResponseEntity.status(HttpStatus.CREATED).body(user) :
                ResponseEntity.ok(user);
    }

    @ExceptionHandler({ExecutionException.class, InterruptedException.class, NestedServletException.class})
    public ResponseEntity<String> handleFirestoreException() {
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                .body("Fail to create new user due to Server or Database errors. Please try again.");
    }

    @ExceptionHandler({InvalidUserInformationException.class})
    public ResponseEntity<String> handleInvalidUserResponseBody(InvalidUserInformationException exception) {
        return ResponseEntity.badRequest().body(exception.getMessage());
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ResponseEntity<String> handleHttpMessageNotReadable() {
        return ResponseEntity.badRequest().body("JSON string is not valid.");
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<String> handleTypeMismatch() {
        return ResponseEntity.badRequest().body("Request param 'autoUpdate' must be a boolean.");
    }
}
