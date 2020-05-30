package com.projecta.eleven.justclassbackend.file;

import com.google.cloud.ReadChannel;
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
        flush();
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

    public void storeAll(List<MultipartFile> attachments, String authorId, String classroomId) throws IOException, ExecutionException, InterruptedException {
        Timestamp now = Timestamp.now();

        files = attachments.stream()
                .map(attachment -> new BasicFile(null, attachment.getOriginalFilename(), attachment.getContentType(), attachment.getSize(), authorId, classroomId, now))
                .collect(Collectors.toList());

        filesReferences = files.stream()
                .map(repository::add)
                .collect(Collectors.toList());
        repository.commit();

        for (var index = 0; index < files.size(); index++) {
            repository.store(files.get(index).getFileId(), attachments.get(index));
        }
    }

    public ReadChannel downloadFile(String fileId) throws ExecutionException, InterruptedException {
        repository.get(fileId);
        commit();
        return repository.getBlob(fileId);
    }

    public void addFileQuery(String fileId) {
        repository.get(fileId);
    }

    public void commit() throws ExecutionException, InterruptedException {
        repository.commit();
        files = repository.getFiles();
        filesReferences = repository.getFileReferences();
        repository.flush();
    }

    public void delete(String id) {
        repository.delete(id);
    }

    public void deleteByClassroom(String classroomId) throws ExecutionException, InterruptedException {
        repository.getByClassroomId(classroomId);
        repository.deleteBlobs();

        repository.commit();
        repository.flush();
    }
}
