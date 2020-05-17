package com.projecta.eleven.justclassbackend.file;

import com.google.cloud.firestore.*;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Repository
class FileRepository {

    private final CollectionReference filesCollection;

    private final CollectionReference fileReferencesCollection;

    private final Storage storage;

    private final Firestore firestore;

    private WriteBatch batch;

    private List<WriteResult> writeResults;

    @Autowired
    public FileRepository(
            @Qualifier("filesCollection") CollectionReference filesCollection,
            @Qualifier("fileReferencesCollection") CollectionReference fileReferencesCollection,
            Firestore firestore,
            Storage storage) {
        this.firestore = firestore;
        this.filesCollection = filesCollection;
        this.fileReferencesCollection = fileReferencesCollection;
        this.storage = storage;
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

    public void commit() {
        if (isBatchActive()) {
            batch.commit();
            batch = null;
        }
    }

    public void store(String directory, MultipartFile file) throws IOException {
        BlobId blobId = BlobId.of("justclass-da0b0.appspot.com", directory);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(file.getContentType())
                .build();

        storage.create(blobInfo, file.getInputStream());
    }
}
