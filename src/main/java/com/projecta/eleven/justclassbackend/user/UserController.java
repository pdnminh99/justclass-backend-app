package com.projecta.eleven.justclassbackend.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/user")
public class UserController {

    private final AbstractUserService userService;

    @Autowired
    public UserController(AbstractUserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<String> assignUser(@RequestBody UserResponseBody user) {
        return ResponseEntity.ok(userService.assignUsers(user).getLocalId());
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
