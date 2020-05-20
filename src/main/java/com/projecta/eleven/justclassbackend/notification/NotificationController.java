package com.projecta.eleven.justclassbackend.notification;

import com.google.cloud.Timestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
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
            @RequestParam("lastRefresh") Long lastRefresh,
            @Nullable
            @RequestParam("isMicrosecondsAccuracy") Boolean isMicrosecondsAccuracy,
            @RequestParam(value = "pageSize", defaultValue = "20") int pageSize,
            @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber) throws ExecutionException, InterruptedException {
        Timestamp lastRefreshAt = null;

        if (Objects.nonNull(lastRefresh)) {
            long epochTime = isMicrosecondsAccuracy != null && isMicrosecondsAccuracy ?
                    lastRefresh :
                    lastRefresh * 1000;
            lastRefreshAt = Timestamp.ofTimeMicroseconds(epochTime);
        }
        return ResponseEntity.ok(
                service.get(localId, pageSize, pageNumber, lastRefreshAt)
                        .collect(Collectors.toList())
        );
    }

}
