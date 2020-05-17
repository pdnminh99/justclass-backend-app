package com.projecta.eleven.justclassbackend.note;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.google.common.collect.Maps;
import com.projecta.eleven.justclassbackend.user.MinifiedUser;
import com.projecta.eleven.justclassbackend.utils.MapSerializable;

import java.util.HashMap;
import java.util.List;

public class BasicNote implements MapSerializable {

    private String Id;

    private MinifiedUser author;

    private String authorId;

    private DocumentReference authorReference;

    private String content;

    private Timestamp createAt;

    private Integer commentsCount;

    private List<String> links;

    private String classroomId;

    private DocumentReference classroomReference;

    public BasicNote(
            String Id,
            MinifiedUser author,
            String authorId,
            DocumentReference authorReference,
            String content,
            Timestamp createAt,
            Integer commentsCount,
            String classroomId,
            DocumentReference classroomReference,
            List<String> links
    ) {
        this.Id = Id;
        this.author = author;
        this.authorId = authorId;
        this.authorReference = authorReference;
        this.content = content;
        this.createAt = createAt;
        this.commentsCount = commentsCount;
        this.classroomId = classroomId;
        this.classroomReference = classroomReference;
        this.links = links;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public MinifiedUser getAuthor() {
        return author;
    }

    public void setAuthor(MinifiedUser author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Timestamp createAt) {
        this.createAt = createAt;
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

    @Override
    public HashMap<String, Object> toMap(boolean isTimestampInMilliseconds) {
        HashMap<String, Object> map = Maps.newHashMap();

        if (getAuthor() != null) {
            ifFieldNotNullThenPutToMap("author", getAuthor().toMap(), map);
        }
        ifFieldNotNullThenPutToMap("authorId", getAuthorId(), map);
        ifFieldNotNullThenPutToMap("authorReference", getAuthorReference(), map);
        ifFieldNotNullThenPutToMap("content", getContent(), map);
        if (getCreateAt() != null) {
            ifFieldNotNullThenPutToMap("createAt", isTimestampInMilliseconds ?
                    getCreateAt().toDate().getTime() :
                    getCreateAt(), map);
        }
        ifFieldNotNullThenPutToMap("commentsCount", getCommentsCount(), map);
        ifFieldNotNullThenPutToMap("classroomId", getClassroomId(), map);
        ifFieldNotNullThenPutToMap("classroomReference", getClassroomReference(), map);
        ifFieldNotNullThenPutToMap("links", getLinks(), map);
        return map;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }
}
