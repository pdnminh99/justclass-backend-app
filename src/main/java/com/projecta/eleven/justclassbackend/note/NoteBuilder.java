package com.projecta.eleven.justclassbackend.note;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.projecta.eleven.justclassbackend.classroom.MinifiedMember;
import com.projecta.eleven.justclassbackend.file.BasicFile;

import java.util.List;

class NoteBuilder {

    private String noteId;

    private String authorId;

    private MinifiedMember author;

    private DocumentReference authorReference;

    private String content;

    private Timestamp createdAt;

    private int commentsCount = 0;

    private List<String> links;

    private String classroomId;

    private DocumentReference classroomReference;

    private List<BasicFile> attachments;

    private List<DocumentReference> attachmentReferences;

    private Timestamp deletedAt;

//    private Note note = new Note();

    public NoteBuilder setNoteId(String id) {
        this.noteId = id;
        return this;
    }

    public NoteBuilder setAuthorId(String authorId) {
        this.authorId = authorId;
        return this;
    }

    public NoteBuilder setAuthor(MinifiedMember author) {
        this.author = author;
        if (author != null && author.getLocalId() != null && authorId == null) {
            authorId = author.getLocalId();
        }
        return this;
    }

    public NoteBuilder setAuthorReference(DocumentReference authorReference) {
        this.authorReference = authorReference;
        if (authorReference != null && authorId == null) {
            authorId = authorReference.getId();
        }
        return this;
    }

    public NoteBuilder setContent(String content) {
        this.content = content;
        return this;
    }

    public NoteBuilder setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public NoteBuilder setCommentsCount(int commentsCount) {
        if (commentsCount >= 0) {
            this.commentsCount = commentsCount;
        }
        return this;
    }

    public NoteBuilder setLinks(List<String> links) {
        this.links = links;
        return this;
    }

    public NoteBuilder setClassroomId(String classroomId) {
        this.classroomId = classroomId;
        return this;
    }

    public NoteBuilder setClassroomReference(DocumentReference classroomReference) {
        this.classroomReference = classroomReference;
        if (classroomReference != null && classroomId == null) {
            classroomId = classroomReference.getId();
        }
        return this;
    }

    public NoteBuilder setAttachments(List<BasicFile> attachments) {
        this.attachments = attachments;
        return this;
    }

    public NoteBuilder setAttachmentReferences(List<DocumentReference> attachmentReferences) {
        this.attachmentReferences = attachmentReferences;
        return this;
    }

    public NoteBuilder setDeletedAt(Timestamp deletedAt) {
        this.deletedAt = deletedAt;
        return this;
    }

    public Note build() {
        return new Note(
                noteId,
                author,
                authorId,
                authorReference,
                content,
                createdAt,
                commentsCount,
                classroomId,
                classroomReference,
                links,
                attachmentReferences,
                attachments,
                deletedAt);
    }
}
