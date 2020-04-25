package com.projecta.eleven.justclassbackend.classroom;

import com.google.cloud.Timestamp;
import com.projecta.eleven.justclassbackend.user.InvalidUserInformationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/classroom")
public class ClassroomController {

    private final IClassroomOperationsService service;

    @Autowired
    public ClassroomController(IClassroomOperationsService service) {
        this.service = service;
    }

    @GetMapping(value = "{localId}", produces = "application/json;charset=utf-8")
    @ResponseStatus(HttpStatus.OK)
    public Iterable<HashMap<String, Object>> getClassrooms(
            @PathVariable("localId") String localId,
            @Nullable
            @RequestParam(value = "role", required = false) MemberRoles role,
            @Nullable
            @RequestParam(value = "lastRequest", required = false) String lastRequest,
            @Nullable
            @RequestParam(value = "isMicrosecondsAccuracy", required = false) Boolean isMicrosecondsAccuracy)
            throws InvalidUserInformationException, ExecutionException, InterruptedException {
        Timestamp lastRequestTimestamp = null;
        if (lastRequest != null) {
            long epochTime = isMicrosecondsAccuracy != null && isMicrosecondsAccuracy ?
                    Long.parseLong(lastRequest) :
                    Long.parseLong(lastRequest) * 1000;
            lastRequestTimestamp = Timestamp.ofTimeMicroseconds(epochTime);
        }
        return service.getClassrooms(localId, role, lastRequestTimestamp)
                .map(MinifiedClassroom::toMap)
                .collect(Collectors.toList());
    }

    @GetMapping(value = "{localId}/{classroomId}", produces = "application/json;charset=utf-8")
    public ResponseEntity<HashMap<String, Object>> getClassroom(@PathVariable("localId") String localId,
                                                                @PathVariable("classroomId") String classroomId)
            throws InvalidUserInformationException, ExecutionException, InvalidClassroomInformationException, InterruptedException {
        return service.get(localId, classroomId)
                .map(this::handleRetrieveNotEmpty)
                .orElseGet(this::handleResponseEmpty);
    }

    private ResponseEntity<HashMap<String, Object>> handleRetrieveNotEmpty(Classroom classroom) {
        return ResponseEntity.ok(classroom.toMap());
    }

    @PutMapping(value = "{localId}", produces = "application/json;charset=utf-8")
    public ResponseEntity<HashMap<String, Object>> inviteMembers(@PathVariable("localId") String localId,
                                                                 @RequestBody Iterable<HashMap<String, String>> invitations) {
        return ResponseEntity.ok(new HashMap<String, Object>());
    }

    @PutMapping(value = "{localId}/{publicCode}", produces = "application/json;charset=utf-8")
    public ResponseEntity<HashMap<String, Object>> joinClassroom(@PathVariable("localId") String localId,
                                                                 @PathVariable("publicCode") String publicCode) {
        return ResponseEntity.ok(new HashMap<String, Object>());
    }

    @PostMapping(value = "{localId}", produces = "application/json;charset=utf-8")
    public ResponseEntity<HashMap<String, Object>> create(@RequestBody ClassroomRequestBody request,
                                                          @PathVariable String localId)
            throws InvalidUserInformationException, InvalidClassroomInformationException, ExecutionException, InterruptedException {
        return service.create(request, localId)
                .map(this::handleCreateNotEmpty)
                .orElseGet(this::handleResponseEmpty);
    }

    private ResponseEntity<HashMap<String, Object>> handleCreateNotEmpty(Classroom createdClassroom) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createdClassroom.toMap());
    }

    private ResponseEntity<HashMap<String, Object>> handleResponseEmpty() {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
    }


    // TODO why not allow null, new public code param.
    @PatchMapping(value = "{localId}", produces = "application/json;charset=utf-8")
    public ResponseEntity<HashMap<String, Object>> update(
            @PathVariable("localId") String localId,
            @Nullable
            @RequestParam(value = "requestNewPublicCode", required = false) Boolean requestNewPublicCode,
            @RequestBody Classroom newClassroomVersion)
            throws InvalidUserInformationException, ExecutionException, InvalidClassroomInformationException, InterruptedException {
        return service.update(newClassroomVersion, localId, requestNewPublicCode)
                .map(Classroom::toMap)
                .map(ResponseEntity::ok)
                .orElseGet(this::handleResponseEmpty);
    }

    @DeleteMapping("{localId}/{classroomId}")
    public ResponseEntity<Boolean> delete(@PathVariable("localId") String localId,
                                          @PathVariable("classroomId") String classroomId)
            throws InvalidUserInformationException, ExecutionException, InvalidClassroomInformationException, InterruptedException {
        return service.delete(localId, classroomId)
                .map(this::handleDeleteStateNotEmpty)
                .orElseGet(this::handleDeleteStateEmpty);
    }

    private ResponseEntity<Boolean> handleDeleteStateNotEmpty(boolean state) {
        return state ?
                ResponseEntity.ok(true) :
                ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(false);
    }

    private ResponseEntity<Boolean> handleDeleteStateEmpty() {
        return ResponseEntity.badRequest().build();
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
