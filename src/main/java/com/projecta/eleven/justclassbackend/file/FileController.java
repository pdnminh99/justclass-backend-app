package com.projecta.eleven.justclassbackend.file;

import com.google.cloud.ReadChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
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
    public ResponseEntity<StreamingResponseBody> download(@PathVariable String fileId) throws ExecutionException, InterruptedException, FileNotFoundException {
        ReadChannel reader = service.downloadFile(fileId);
        if (service.getFiles().size() == 0) {
            throw new FileNotFoundException("File not found.");
        }
        BasicFile file = service.getFiles().get(0);
        InputStream stream = Channels.newInputStream(reader);
        service.flush();

        StreamingResponseBody responseBody = outputStream -> {
            int bytesCount = 0;
            byte[] data = new byte[1024];
            while ((bytesCount = stream.read(data, 0, data.length)) != -1) {
                outputStream.write(data, 0, bytesCount);
            }
            stream.close();
        };
        return ResponseEntity.ok()
                .header("Content-Type", file.getType() + "; name=\"" + file.getName() + "\"")
                .header("Content-Disposition", "inline; filename=\"" + file.getName() + "\"")
                .header("Content-Length", String.valueOf(file.getSize()))
                .body(responseBody);
    }

    @PostMapping
    public List<Map<String, Object>> upload(
            @RequestBody List<MultipartFile> files,
            @Nullable
            @RequestParam("content") String content
    ) {
        return files.stream().map(file -> {
            Map<String, Object> attachment = new HashMap<>();

            attachment.put("originalName", file.getOriginalFilename());
            attachment.put("name", file.getName());
            attachment.put("type", file.getContentType());
            attachment.put("size", file.getSize());

            return attachment;
        }).collect(Collectors.toList());
    }

    @ExceptionHandler(FileNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleFileNotFound() {
        return "File not found!";
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public ResponseEntity<String> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException exception) {
        return ResponseEntity.badRequest().body(exception.getMessage());
    }
}
