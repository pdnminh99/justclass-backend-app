package com.projecta.eleven.justclassbackend.file;

import com.google.cloud.firestore.CollectionReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
class FileRepository {

    private final CollectionReference filesCollection;

//    private final Bucket storageBucket;

    @Autowired
    public FileRepository(@Qualifier("filesCollection") CollectionReference filesCollection) {
        this.filesCollection = filesCollection;
//        this.storageBucket = storageBucket;
    }
}
