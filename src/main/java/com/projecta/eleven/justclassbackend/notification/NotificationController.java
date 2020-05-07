package com.projecta.eleven.justclassbackend.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/notification")
public class NotificationController {

    private final NotificationService service;

    @Autowired
    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @GetMapping(value = "{localId}", produces = "application/json;charset=utf-8")
    public ResponseEntity<List<Notification>> get(@PathVariable String localId) {
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{localId}")
    public void delete(@PathVariable String localId, @RequestBody List<String> notifications) {

    }

}
