package com.projecta.eleven.justclassbackend.file;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        // TODO implement this.
    }

    @PostMapping
    public List<Map<String, Object>> upload(
            @RequestBody List<MultipartFile> files,
            @RequestParam("content") String content,
            @Nullable
            @RequestParam("attachToPost") String attachToPost
    ) {
        System.out.println(content);
        return files.stream().map(file -> {
            Map<String, Object> attachment = new HashMap<>();

            attachment.put("originalName", file.getOriginalFilename());
            attachment.put("name", file.getName());
            attachment.put("type", file.getContentType());
            attachment.put("size", file.getSize());

            return attachment;
        }).collect(Collectors.toList());
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<String> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException exception) {
        return ResponseEntity.badRequest().body(exception.getMessage());
    }
}
