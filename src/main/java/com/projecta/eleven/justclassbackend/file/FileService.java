package com.projecta.eleven.justclassbackend.file;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class FileService {

    private final FileRepository repository;

    private List<BasicFile> files;

    private List<DocumentReference> filesReferences;

    @Autowired
    public FileService(FileRepository repository) {
        this.repository = repository;
        files = Lists.newArrayList();
        filesReferences = Lists.newArrayList();
    }

    public List<DocumentReference> getFilesReferences() {
        return filesReferences;
    }

    public List<BasicFile> getFiles() {
        return files;
    }

    public void flush() {
        filesReferences = Lists.newArrayList();
        files = Lists.newArrayList();
    }

    public void storeAll(List<MultipartFile> attachments, String noteId) throws ExecutionException, InterruptedException, IOException {
        Timestamp now = Timestamp.now();

        files = attachments.stream()
                .map(attachment -> new BasicFile(null, attachment.getOriginalFilename(), attachment.getContentType(), attachment.getSize(), now))
                .collect(Collectors.toList());

        filesReferences = files.stream()
                .map(repository::add)
                .collect(Collectors.toList());
        repository.commit();

        for (var index = 0; index < files.size(); index++) {
            repository.store(files.get(index).getFileId(), attachments.get(index));
        }
    }
}
