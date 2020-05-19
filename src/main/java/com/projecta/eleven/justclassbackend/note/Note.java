package com.projecta.eleven.justclassbackend.note;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.common.collect.Maps;
import com.projecta.eleven.justclassbackend.classroom.MinifiedMember;
import com.projecta.eleven.justclassbackend.file.BasicFile;
import com.projecta.eleven.justclassbackend.utils.MapSerializable;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Note implements MapSerializable {

    private String Id;

    private MinifiedMember author;

    private String authorId;

    private DocumentReference authorReference;

    private String content;

    private Timestamp createdAt;

    private Integer commentsCount;

    private List<String> links;

    private String classroomId;

    private DocumentReference classroomReference;

    private List<DocumentReference> attachmentReferences;

    private List<BasicFile> attachments;

    private Timestamp deletedAt;

    public Note(
            String Id,
            MinifiedMember author,
            String authorId,
            DocumentReference authorReference,
            String content,
            Timestamp createdAt,
            Integer commentsCount,
            String classroomId,
            DocumentReference classroomReference,
            List<String> links,
            List<DocumentReference> attachmentReferences,
            List<BasicFile> attachments,
            Timestamp deletedAt
    ) {
        this.Id = Id;
        this.author = author;
        this.authorId = authorId;
        this.authorReference = authorReference;
        this.content = content;
        this.createdAt = createdAt;
        this.commentsCount = commentsCount;
        this.classroomId = classroomId;
        this.classroomReference = classroomReference;
        this.links = links;
        this.attachmentReferences = attachmentReferences;
        this.attachments = attachments;
        this.deletedAt = deletedAt;
    }

    public Note(DocumentSnapshot snapshot) {
        this.Id = snapshot.getId();
        this.authorId = snapshot.getString("authorId");
        this.authorReference = snapshot.get("authorReference", DocumentReference.class);
        this.classroomId = snapshot.getString("classroomId");
        this.classroomReference = snapshot.get("classroomReference", DocumentReference.class);
        this.commentsCount = snapshot.get("commentsCount", Integer.class);
        this.content = snapshot.getString("content");
        this.deletedAt = snapshot.getTimestamp("deletedAt");
    }

    public static NoteBuilder newBuilder() {
        return new NoteBuilder();
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public MinifiedMember getAuthor() {
        return author;
    }

    public void setAuthor(MinifiedMember author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(Integer commentsCount) {
        this.commentsCount = commentsCount;
    }

    public String getClassroomId() {
        return classroomId;
    }

    public void setClassroomId(String classroomId) {
        this.classroomId = classroomId;
    }

    public DocumentReference getClassroomReference() {
        return classroomReference;
    }

    public void setClassroomReference(DocumentReference classroomReference) {
        this.classroomReference = classroomReference;
    }

    public DocumentReference getAuthorReference() {
        return authorReference;
    }

    public void setAuthorReference(DocumentReference authorReference) {
        this.authorReference = authorReference;
    }

    public List<String> getLinks() {
        return links;
    }

    public void setLinks(List<String> links) {
        this.links = links;
    }

    public List<BasicFile> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<BasicFile> attachments) {
        this.attachments = attachments;
    }

    public List<DocumentReference> getAttachmentReferences() {
        return attachmentReferences;
    }

    public void setAttachmentReferences(List<DocumentReference> attachmentReferences) {
        this.attachmentReferences = attachmentReferences;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public Timestamp getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Timestamp deletedAt) {
        this.deletedAt = deletedAt;
    }

    @Override
    public HashMap<String, Object> toMap(boolean isTimestampInMilliseconds) {
        HashMap<String, Object> map = Maps.newHashMap();

        if (getAuthor() != null) {
            ifFieldNotNullThenPutToMap("author", getAuthor().toMap(isTimestampInMilliseconds), map);
        }
        ifFieldNotNullThenPutToMap("authorId", getAuthorId(), map);
        ifFieldNotNullThenPutToMap("authorReference", getAuthorReference(), map);
        ifFieldNotNullThenPutToMap("content", getContent(), map);
        if (getCreatedAt() != null) {
            ifFieldNotNullThenPutToMap("createdAt", isTimestampInMilliseconds ?
                    getCreatedAt().toDate().getTime() :
                    getCreatedAt(), map);
        }
        ifFieldNotNullThenPutToMap("commentsCount", getCommentsCount(), map);
        ifFieldNotNullThenPutToMap("classroomId", getClassroomId(), map);
        ifFieldNotNullThenPutToMap("classroomReference", getClassroomReference(), map);
        ifFieldNotNullThenPutToMap("links", getLinks(), map);

        if (getAttachmentReferences() != null && getAttachmentReferences().size() > 0) {
            ifFieldNotNullThenPutToMap("attachmentReferences", getAttachmentReferences(), map);
        }

        if (getAttachments() != null && getAttachments().size() > 0) {
            List<HashMap<String, Object>> files = getAttachments()
                    .stream()
                    .map(a -> a.toMap(isTimestampInMilliseconds))
                    .collect(Collectors.toList());
            map.put("attachments", files);
        }
        map.put("deletedAt", getDeletedAt() != null && isTimestampInMilliseconds ?
                getDeletedAt().toDate().getTime() :
                getDeletedAt());
        return map;
    }
}
