package com.projecta.eleven.justclassbackend.note;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/note")
public class NoteController {

    private final NoteService service;

    @Autowired
    public NoteController(NoteService service) {
        this.service = service;
    }

    @GetMapping("{localId}/{classroomId}")
    public ResponseEntity<List<BasicNote>> get(
            @PathVariable("localId") String localId,
            @PathVariable("classroomId") String classroomId,
            @RequestParam(value = "pageSize", defaultValue = "20") int pageSize,
            @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber
    ) {
        return ResponseEntity.ok().build();
    }
}
