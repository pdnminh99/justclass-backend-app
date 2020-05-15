package com.projecta.eleven.justclassbackend.file;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileService {
    private final FileRepository repository;

    @Autowired
    public FileService(FileRepository repository) {
        this.repository = repository;
    }
}
