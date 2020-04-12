package com.projecta.eleven.justclassbackend.classroom;

import com.google.cloud.Timestamp;
import com.projecta.eleven.justclassbackend.user.InvalidUserInformationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            @RequestParam("joinedOnly") Boolean joinedClassesOnly,
            @RequestParam("lastRequest") Timestamp lastRequest)
            throws InvalidUserInformationException {
        return service.get(localId, joinedClassesOnly, lastRequest)
                .map(Classroom::toMap)
                .collect(Collectors.toList());
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
                .map(this::handleCreateNotEmpty)
                .orElseGet(this::handleCreateOrUpdateEmpty);
    }

    private ResponseEntity<HashMap<String, Object>> handleCreateNotEmpty(Classroom createdClassroom) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createdClassroom.toMap());
    }

    private ResponseEntity<HashMap<String, Object>> handleCreateOrUpdateEmpty() {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
    }

    @PatchMapping("{localId}")
    public ResponseEntity<HashMap<String, Object>> update(@PathVariable("localId") String localId,
                                                          @RequestBody Classroom newClassroomVersion) {
        return service.update(newClassroomVersion, localId)
                .map(Classroom::toMap)
                .map(ResponseEntity::ok)
                .orElseGet(this::handleCreateOrUpdateEmpty);
    }

    @DeleteMapping("{localId}/{classroomId}")
    public ResponseEntity<Boolean> delete(@PathVariable("localId") String localId,
                                          @PathVariable("classroomId") String classroomId) {
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
}
