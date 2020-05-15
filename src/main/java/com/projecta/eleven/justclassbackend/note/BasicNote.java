package com.projecta.eleven.justclassbackend.note;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.projecta.eleven.justclassbackend.user.MinifiedUser;
import com.projecta.eleven.justclassbackend.utils.MapSerializable;

import java.util.HashMap;

public class BasicNote implements MapSerializable {

    private String Id;

    private MinifiedUser author;

    @JsonIgnore
    private DocumentReference authorMembershipReference;

    private String content;

    private Timestamp createAt;

    private Integer commentsCount;

    private Integer likesCount;

    private String classroomId;

    @JsonIgnore
    private DocumentReference classroomReference;

    public BasicNote(
            String Id,
            MinifiedUser author,
            DocumentReference authorMembershipReference,
            String content,
            Timestamp createAt,
            Integer commentsCount,
            Integer likesCount,
            String classroomId,
            DocumentReference classroomReference
    ) {
        this.Id = Id;
        this.author = author;
        this.authorMembershipReference = authorMembershipReference;
        this.content = content;
        this.createAt = createAt;
        this.commentsCount = commentsCount;
        this.likesCount = likesCount;
        this.classroomId = classroomId;
        this.classroomReference = classroomReference;
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

    public DocumentReference getAuthorMembershipReference() {
        return authorMembershipReference;
    }

    public void setAuthorMembershipReference(DocumentReference authorMembershipReference) {
        this.authorMembershipReference = authorMembershipReference;
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

    public Integer getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(Integer likesCount) {
        this.likesCount = likesCount;
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

    @Override
    public HashMap<String, Object> toMap(boolean isTimestampInMilliseconds) {
        var map = new HashMap<String, Object>();

        if (getAuthor() != null) {
            ifFieldNotNullThenPutToMap("author", getAuthor().toMap(), map);
        }
        ifFieldNotNullThenPutToMap("authorMembershipReference", getAuthorMembershipReference(), map);
        ifFieldNotNullThenPutToMap("content", getContent(), map);
        if (getCreateAt() != null) {
            ifFieldNotNullThenPutToMap("createAt", isTimestampInMilliseconds ?
                    getCreateAt().toDate().getTime() :
                    getCreateAt(), map);
        }
        ifFieldNotNullThenPutToMap("commentsCount", getCommentsCount(), map);
        ifFieldNotNullThenPutToMap("likesCount", getLikesCount(), map);
        ifFieldNotNullThenPutToMap("classroomId", getClassroomId(), map);
        ifFieldNotNullThenPutToMap("classroomReference", getClassroomReference(), map);

        return map;
    }
}
