package com.projecta.eleven.justclassbackend.note;

import com.google.cloud.Timestamp;
import com.google.common.collect.Lists;
import com.projecta.eleven.justclassbackend.classroom.InvalidClassroomInformationException;
import com.projecta.eleven.justclassbackend.user.InvalidUserInformationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/note")
public class NoteController {

    private final NoteService service;

    @Autowired
    public NoteController(NoteService service) {
        this.service = service;
    }

    @GetMapping(value = "{classroomId}", produces = "application/json;charset=utf-8")
    @ResponseStatus(HttpStatus.OK)
    public List<HashMap<String, Object>> get(
            @PathVariable("classroomId") String classroomId,
            @RequestParam(value = "pageSize", defaultValue = "0") int pageSize,
            @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
            @Nullable
            @RequestParam("lastRefresh") Long lastRefresh,
            @RequestParam(value = "isMicrosecondsAccuracy", defaultValue = "false") boolean isMicrosecondsAccuracy,
            @RequestParam(value = "excludeDeleted", defaultValue = "true") boolean excludeDeleted
    ) throws ExecutionException, InterruptedException {
        Timestamp lastRefreshAt = null;

        if (Objects.nonNull(lastRefresh)) {
            long epochTime = isMicrosecondsAccuracy ?
                    lastRefresh :
                    lastRefresh * 1000;
            lastRefreshAt = Timestamp.ofTimeMicroseconds(epochTime);
        }
        return service.get(classroomId, pageSize, pageNumber, lastRefreshAt, excludeDeleted)
                .map(note -> note.toMap(true))
                .collect(Collectors.toList());
    }

    @PatchMapping(value = "{localId}/{noteId}", produces = "application/json;charset=utf-8")
    public HashMap<String, Object> edit(
            @PathVariable String localId,
            @PathVariable String noteId,
            @Nullable
            @RequestParam String content,
            @Nullable
            @RequestParam List<String> deletedAttachments,
            @Nullable
            @RequestParam List<MultipartFile> attachments
    ) throws InterruptedException, ExecutionException, InvalidUserInformationException, IOException {
        return service.edit(localId, noteId, content, deletedAttachments, attachments);
    }

    @PostMapping(value = "{localId}/{classroomId}", produces = "application/json;charset=utf-8")
    public ResponseEntity<HashMap<String, Object>> create(
            @PathVariable("localId") String localId,
            @PathVariable("classroomId") String classroomId,
            @Nullable
            @RequestParam("content") String content,
            @Nullable
            @RequestBody List<MultipartFile> attachments
    ) throws ExecutionException, InterruptedException, InvalidUserInformationException, IOException {
        return service.create(localId, classroomId, content, attachments)
                .map(m -> m.toMap(true))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @DeleteMapping("{localId}/{noteId}")
    public void delete(
            @PathVariable("localId") String localId,
            @PathVariable("noteId") String noteId
    ) throws ExecutionException, InterruptedException {
        service.delete(localId, noteId);
    }

    @GetMapping(value = "comments/{localId}/{noteId}", produces = "application/json;charset=utf-8")
    public List<Map<String, Object>> getComments(
            @PathVariable String localId,
            @PathVariable String noteId,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(defaultValue = "0") int pageNumber
    ) {
        // TODO implement this.
        return Lists.newArrayList();
    }

    @PostMapping(value = "comments/{localId}/{noteId}", produces = "application/json;charset=utf-8")
    public List<Map<String, Object>> createComment(
            @PathVariable String localId,
            @PathVariable String noteId,
            @RequestBody String content) {
        // TODO implement this.
        return Lists.newArrayList();
    }

    @ExceptionHandler(InvalidClassroomInformationException.class)
    public ResponseEntity<String> handleInvalidClassroomInfo(InvalidClassroomInformationException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(InvalidUserInformationException.class)
    public ResponseEntity<String> handleInvalidUserInfo(InvalidUserInformationException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleInvalidUserInfo(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public String handleArgumentTypeMismatchException() {
        return "Request parameter is not valid.";
    }

    @ExceptionHandler(IllegalAccessError.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public String handleIllegalAccessError(IllegalAccessError exception) {
        return exception.getMessage();
    }

}
