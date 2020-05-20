package com.projecta.eleven.justclassbackend.file;

import com.google.api.core.ApiFutures;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteBatch;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Repository
class FileRepository {

    private final CollectionReference filesCollection;

    private final boolean isDeploymentEnvironment = Boolean.parseBoolean(System.getenv("envi"));

    private final Storage storage;

    private final Firestore firestore;

    private WriteBatch batch;

    private Map<String, BasicFile> fileMap;

    private Map<String, DocumentReference> fileReferencesMap;

//    private List<WriteResult> writeResults;

    @Autowired
    public FileRepository(
            @Qualifier("filesCollection") CollectionReference filesCollection,
            Firestore firestore,
            Storage storage) {
        this.firestore = firestore;
        this.filesCollection = filesCollection;
        this.storage = storage;
    }

    public List<BasicFile> getFiles() {
        return Lists.newArrayList(fileMap.values());
    }

    public List<DocumentReference> getFileReferences() {
        return Lists.newArrayList(fileReferencesMap.values());
    }

    private String getStorageDirectory(String blobId) {
        return isDeploymentEnvironment ?
                "v1/" + blobId :
                "dev/" + blobId;
    }

    public boolean isBatchActive() {
        return batch != null;
    }

    public void resetBatch() {
        batch = firestore.batch();
    }

    public DocumentReference add(BasicFile f) {
        if (!isBatchActive()) {
            resetBatch();
        }
        var map = f.toMap();
        map.remove("fileId");

        String nextId = filesCollection.document().getId();
        f.setFileId(nextId);
        batch.set(filesCollection.document(nextId), map);
        return filesCollection.document(nextId);
    }

    public void commit() throws ExecutionException, InterruptedException {
        if (isBatchActive()) {
            batch.commit();
            batch = null;
        }
        if (fileReferencesMap != null && !fileReferencesMap.isEmpty()) {
            ApiFutures.allAsList(
                    fileReferencesMap
                            .values()
                            .stream()
                            .map(DocumentReference::get)
                            .collect(Collectors.toList()))
                    .get()
                    .stream()
                    .map(BasicFile::new)
                    .forEach(file -> fileMap.put(file.getFileId(), file));
        }
    }

    public void flush() {
        fileReferencesMap = null;
        fileMap = null;
    }

    public void store(String directory, MultipartFile file) throws IOException {
        BlobId blobId = BlobId.of("justclass-da0b0.appspot.com", getStorageDirectory(directory));
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(file.getContentType())
                .build();

        storage.create(blobInfo, file.getInputStream());
    }

    public void get(String fileId) {
        if (fileMap == null) {
            fileReferencesMap = Maps.newHashMap();
            fileMap = Maps.newHashMap();
        }
        fileReferencesMap.put(fileId, filesCollection.document(fileId));
    }
}
