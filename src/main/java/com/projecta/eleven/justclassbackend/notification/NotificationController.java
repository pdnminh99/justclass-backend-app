package com.projecta.eleven.justclassbackend.notification;

import com.google.cloud.Timestamp;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
            @RequestParam(value = "isMicrosecondsAccuracy", defaultValue = "false") boolean isMicrosecondsAccuracy,
            @RequestParam(value = "pageSize", defaultValue = "0") int pageSize,
            @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(value = "excludeDeleted", defaultValue = "true") boolean excludeDeleted) throws ExecutionException, InterruptedException {
        Timestamp lastRefreshAt = null;

        if (Objects.nonNull(lastRefresh)) {
            long epochTime = isMicrosecondsAccuracy ?
                    lastRefresh :
                    lastRefresh * 1000;
            lastRefreshAt = Timestamp.ofTimeMicroseconds(epochTime);
        }
        return ResponseEntity.ok(
                service.get(localId, pageSize, pageNumber, lastRefreshAt, excludeDeleted)
                        .collect(Collectors.toList()));
    }

    @GetMapping("new/{localId}")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> getNewNotificationsCount(
            @PathVariable String localId,
            @Nullable
            @RequestParam Long lastRefresh,
            @RequestParam(defaultValue = "false") boolean isMicrosecondsAccuracy) throws ExecutionException, InterruptedException {
        Map<String, Object> responseMap = Maps.newHashMap();
        Timestamp lastRefreshAt = null;

        if (Objects.nonNull(lastRefresh)) {
            long epochTime = isMicrosecondsAccuracy ?
                    lastRefresh :
                    lastRefresh * 1000;
            lastRefreshAt = Timestamp.ofTimeMicroseconds(epochTime);
        }
        responseMap.put("count", service.getNotificationsCount(localId, lastRefreshAt));
        responseMap.put("localId", localId);
        return responseMap;
    }

}
