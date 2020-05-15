package com.projecta.eleven.justclassbackend.file;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController()
@RequestMapping("api/v1/file")
public class FileController {

    private final FileService service;

    @Autowired
    public FileController(FileService service) {
        this.service = service;
    }

    @GetMapping("{fileId}")
    public void download(
            @PathVariable("fileId") String fileId
    ) {

    }

    @PostMapping
    public void upload(
            @RequestBody MultipartFile[] files,
            @Nullable
            @RequestParam("attachToPost") String attachToPost
    ) {
        for (MultipartFile file : files) {
            var name = file.getOriginalFilename();
            var type = file.getContentType();
            var size = file.getSize();

            System.out.println("Name: " + name + " | Type: " + type + " | Size: " + size + ".\n------");
        }
        System.err.println("--------------");
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<String> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException exception) {
        return ResponseEntity.badRequest().body(exception.getMessage());
    }
}
