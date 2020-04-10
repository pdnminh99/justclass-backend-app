package com.projecta.eleven.justclassbackend.user;

import com.google.cloud.Timestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("api/v1/friend")
public class FriendController {

    private final IMinifiedUserOperations userService;

    @Autowired
    public FriendController(@Qualifier("defaultUserService") IMinifiedUserOperations userService) {
        this.userService = userService;
    }

    @GetMapping("{localId}")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Timestamp> getFriendsLocalIds(
            @PathVariable String localId,
            @Nullable
            @RequestParam(value = "count", required = false) Integer count,
            @Nullable
            @RequestParam(value = "sortByMostRecentAccess", required = false) Boolean sortByMostRecentAccess) {
        return userService.getLocalIdsOfFriends(localId, count, sortByMostRecentAccess);
    }

//    @GetMapping("{localId}/all")
//    @ResponseStatus(HttpStatus.OK)
//    public Iterable<MinifiedUser> getFriends(
//            @PathVariable String localId,
//            @Nullable
//            @RequestParam(value = "count", required = false) Integer count,
//            @Nullable
//            @RequestParam(value = "sortByField", required = false) String sortByField,
//            @Nullable
//            @RequestParam(value = "isAscending", required = false) Boolean isAscending) {
//        return null;
//    }

    @PostMapping("map")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<MinifiedUser>> getFriendsFromMap(
            @Nullable
            @RequestBody Map<String, Timestamp> requestMap,
            @Nullable
            @RequestParam(value = "sortByField", required = false) String sortByField,
            @Nullable
            @RequestParam(value = "isAscending", required = false) Boolean isAscending) throws ExecutionException, InterruptedException {
        return Optional.ofNullable(userService.getUsers(requestMap, sortByField, isAscending))
                .map(this::handleResponseUsersNotEmpty)
                .orElseGet(this::handleResponseUsersEmpty);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<MinifiedUser>> getFriendsFromLocalIds(
            @Nullable
            @RequestBody String[] localIds,
            @Nullable
            @RequestParam(value = "sortByField", required = false) String sortByField,
            @Nullable
            @RequestParam(value = "isAscending", required = false) Boolean isAscending)
            throws ExecutionException, InterruptedException {
        return Optional.ofNullable(userService.getUsers(localIds, sortByField, isAscending))
                .map(this::handleResponseUsersNotEmpty)
                .orElseGet(this::handleResponseUsersEmpty);
    }

    private ResponseEntity<List<MinifiedUser>> handleResponseUsersNotEmpty(List<MinifiedUser> responseUsers) {
        return responseUsers.isEmpty() ?
                handleResponseUsersEmpty() :
                ResponseEntity.ok(responseUsers);
    }

    private ResponseEntity<List<MinifiedUser>> handleResponseUsersEmpty() {
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(Collections.emptyList());
    }

}
