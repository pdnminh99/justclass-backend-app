package com.projecta.eleven.justclassbackend.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/notification")
public class NotificationController {

    private final NotificationService service;

    @Autowired
    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @GetMapping(value = "{localId}", produces = "application/json;charset=utf-8")
    public ResponseEntity<List<HashMap<String, Object>>> get(
            @PathVariable String localId,
            @Nullable
            @RequestParam("count") Integer count) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(
                service.get(localId, count)
                        .collect(Collectors.toList())
        );
    }

//    @DeleteMapping("{localId}")
//    public void delete(@PathVariable String localId, @RequestBody List<String> notifications) {
//
//    }

}
