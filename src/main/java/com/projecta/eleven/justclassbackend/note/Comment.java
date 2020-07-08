package com.projecta.eleven.justclassbackend.note;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.Exclude;
import com.projecta.eleven.justclassbackend.user.MinifiedUser;

public class Comment {

    @DocumentId
    private String commentId;

    private String content;

    @Exclude
    private MinifiedUser author;

    @JsonIgnore
    private String classroomId;

    @JsonIgnore
    private String noteId;

    private Timestamp createdAt;

    public Comment() {
    }

    public Comment(
            String commentId,
            String noteId,
            String classroomId,
            String content,
            MinifiedUser author,
            Timestamp createdAt) {
        this.commentId = commentId;
        this.noteId = noteId;
        this.classroomId = classroomId;
        this.content = content;
        this.author = author;
        this.createdAt = createdAt;
    }

    @JsonIgnore
    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    @JsonIgnore
    public String getAuthorId() {
        return author == null ? null : author.getLocalId();
    }

    public void setAuthorId(String authorId) {
        author = new MinifiedUser(authorId, null, null, null);
    }

    @Exclude
    public MinifiedUser getAuthor() {
        return author;
    }

    public void setAuthor(MinifiedUser author) {
        this.author = author;
    }

    @JsonIgnore
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Exclude
    @JsonGetter("createdAt")
    public Long getCreatedAtByEpoch() {
        return createdAt == null ? null : createdAt.toDate().getTime();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getClassroomId() {
        return classroomId;
    }

    public void setClassroomId(String classroomId) {
        this.classroomId = classroomId;
    }
}
