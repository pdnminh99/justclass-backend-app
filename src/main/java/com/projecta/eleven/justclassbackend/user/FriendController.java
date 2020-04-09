package com.projecta.eleven.justclassbackend.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("api/v1/friend")
public class FriendController {

    private final AbstractUserService userService;

    @Autowired
    public FriendController(@Qualifier("defaultUserService") AbstractUserService userService) {
        this.userService = userService;
    }

    @GetMapping("{localId}")
    @ResponseStatus(HttpStatus.OK)
    public String[] getFriendsLocalIds(@PathVariable String localId,
                                       @Nullable
                                       @RequestParam("count") Integer count) {
        return null;
    }

    @GetMapping("{localId}/all")
    @ResponseStatus(HttpStatus.OK)
    public Iterable<MinifiedUser> getFriends(@PathVariable String localId,
                                             @Nullable
                                             @RequestParam("count") Integer count,
                                             @Nullable
                                             @RequestParam("sortByField") String sortByField,
                                             @Nullable
                                             @RequestParam("isAscending") Boolean isAscending) {
        return null;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public Iterable<MinifiedUser> getFriends(@RequestBody String[] localIds,
                                             @Nullable
                                             @RequestParam("count") Integer count,
                                             @Nullable
                                             @RequestParam("sortByField") String sortByField,
                                             @Nullable
                                             @RequestParam("isAscending") Boolean isAscending) {
        return null;
    }

}
