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

    @GetMapping("{localId}")
    @ResponseStatus(HttpStatus.OK)
    public Iterable<HashMap<String, Object>> getClassrooms(
            @PathVariable("localId") String localId,
            @Nullable
            @RequestParam(value = "role", required = false) CollaboratorRoles role,
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

    @GetMapping("{localId}/{classroomId}")
    public ResponseEntity<HashMap<String, Object>> getClassroom(@PathVariable("localId") String localId,
                                                                @PathVariable("classroomId") String classroomId)
            throws InvalidUserInformationException, ExecutionException, InvalidClassroomInformationException, InterruptedException {
        return service.get(localId, classroomId)
                .map(this::handleCreateOrRetrieveNotEmpty)
                .orElseGet(this::handleResponseEmpty);
    }

//    @GetMapping("{hostId}/{guestId}/{classroomId}")
//    public ResponseEntity<Boolean> invite(@PathVariable("hostId") String hostId,
//                                          @PathVariable("guestId") String guestId,
//                                          @PathVariable("classroomId") String classroomId) {
//        return ResponseEntity.ok(null);
//    }

    @PostMapping("{localId}")
    public ResponseEntity<HashMap<String, Object>> create(@RequestBody ClassroomRequestBody request,
                                                          @PathVariable String localId)
            throws InvalidUserInformationException, InvalidClassroomInformationException, ExecutionException, InterruptedException {
        return service.create(request, localId)
                .map(this::handleCreateOrRetrieveNotEmpty)
                .orElseGet(this::handleResponseEmpty);
    }

    private ResponseEntity<HashMap<String, Object>> handleCreateOrRetrieveNotEmpty(Classroom createdClassroom) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createdClassroom.toMap());
    }

    private ResponseEntity<HashMap<String, Object>> handleResponseEmpty() {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
    }

    @PatchMapping("{localId}")
    public ResponseEntity<HashMap<String, Object>> update(@PathVariable("localId") String localId,
                                                          @RequestBody Classroom newClassroomVersion)
            throws InvalidUserInformationException, ExecutionException, InvalidClassroomInformationException, InterruptedException {
        return service.update(newClassroomVersion, localId)
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
