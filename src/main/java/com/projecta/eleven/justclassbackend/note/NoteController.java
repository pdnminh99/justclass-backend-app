package com.projecta.eleven.justclassbackend.note;

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

    @GetMapping(value = "{localId}/{classroomId}", produces = "application/json;charset=utf-8")
    @ResponseStatus(HttpStatus.OK)
    public List<BasicNote> get(
            @PathVariable("localId") String localId,
            @PathVariable("classroomId") String classroomId,
            @RequestParam(value = "pageSize", defaultValue = "20") int pageSize,
            @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber
    ) {
        return service.get(localId, classroomId, pageSize, pageNumber)
                .collect(Collectors.toList());
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
    ) {
        // TODO implement this.
    }

    @GetMapping(value = "comments/{localId}/{noteId}", produces = "application/json;charset=utf-8")
    public List<Map<String, Object>> getComments(
            @PathVariable("localId") String localId,
            @PathVariable("noteId") String noteId,
            @RequestParam(value = "pageSize", defaultValue = "20") int pageSize,
            @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber
    ) {
        // TODO implement this.
        return Lists.newArrayList();
    }

    @PostMapping("comments/{localId}/{noteId}")
    public List<Map<String, Object>> createComment(
            @PathVariable("localId") String localId,
            @PathVariable("noteId") String noteId,
            @RequestBody String content) {
        // TODO implement this.
        return Lists.newArrayList();
    }

    @ExceptionHandler({InvalidClassroomInformationException.class})
    public ResponseEntity<String> handleInvalidClassroomInfo(InvalidClassroomInformationException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler({InvalidUserInformationException.class})
    public ResponseEntity<String> handleInvalidUserInfo(InvalidUserInformationException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<String> handleInvalidUserInfo(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public String handleArgumentTypeMismatchException() {
        return "Request parameter is not valid.";
    }

}
