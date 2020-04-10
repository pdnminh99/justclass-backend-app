package com.projecta.eleven.justclassbackend.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("api/v1/user")
public class UserController {

    private final IUserOperations userService;

    @Autowired
    public UserController(@Qualifier("defaultUserService") IUserOperations userService) {
        this.userService = userService;
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

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<String> handleTypeMismatch() {
        return ResponseEntity.badRequest().body("Request param 'autoUpdate' must be a boolean.");
    }

    @ExceptionHandler({InvalidUserInformationException.class})
    public String handleInvalidUserInfo(InvalidUserInformationException e) {
        return e.getMessage();
    }
}
