package com.projecta.eleven.justclassbackend.user;

import com.google.cloud.Timestamp;
import com.projecta.eleven.justclassbackend.classroom.InvalidClassroomInformationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/user")
public class UserController {

    private final IUserOperations userService;

    @Autowired
    public UserController(IUserOperations userService) {
        this.userService = userService;
    }

    @GetMapping("{localId}")
    @ResponseStatus(HttpStatus.OK)
    public List<MinifiedUser> getConnectedUsers(
            @PathVariable String localId,
            @Nullable
            @RequestParam(value = "lastRequest", required = false) String lastRequestString,
            @Nullable
            @RequestParam(value = "isMicrosecondsAccuracy", required = false) Boolean isMicrosecondsAccuracy)
            throws ExecutionException, InterruptedException {
        Timestamp lastRequestTimestamp = null;

        if (Objects.nonNull(lastRequestString)) {
            long epochTime = isMicrosecondsAccuracy != null && isMicrosecondsAccuracy ?
                    Long.parseLong(lastRequestString) :
                    Long.parseLong(lastRequestString) * 1000;
            lastRequestTimestamp = Timestamp.ofTimeMicroseconds(epochTime);
        }
        return userService.getFriendsOfUser(localId, lastRequestTimestamp)
                .collect(Collectors.toList());
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

    @ExceptionHandler({DateTimeParseException.class})
    public ResponseEntity<String> handleDatetimeParsingFailure() {
        return ResponseEntity
                .status(HttpStatus.NOT_ACCEPTABLE)
                .body("You must use epoch time with milli or micro-seconds of accuracy format.");
    }

    @ExceptionHandler({InvalidClassroomInformationException.class})
    public ResponseEntity<String> handleInvalidClassroomInfo(InvalidClassroomInformationException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler({InvalidUserInformationException.class})
    public ResponseEntity<String> handleInvalidUserInfo(InvalidUserInformationException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public String handleArgumentTypeMismatchException() {
        return "Request parameter is not valid.";
    }
}
