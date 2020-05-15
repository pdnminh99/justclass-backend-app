package com.projecta.eleven.justclassbackend.note;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.projecta.eleven.justclassbackend.file.BasicFile;
import com.projecta.eleven.justclassbackend.user.MinifiedUser;

import java.util.List;

public class MaterialNote extends BasicNote {

    private List<BasicFile> attachments;

    public MaterialNote(
            String Id,
            MinifiedUser author,
            DocumentReference authorReference,
            String content,
            Timestamp createAt,
            Integer commentsCount,
            String classroomId,
            DocumentReference classroomReference,
            List<String> links,
            NoteType type) {
        super(Id, author, authorReference, content, createAt, commentsCount, classroomId, classroomReference, links, type);
    }

    public List<BasicFile> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<BasicFile> attachments) {
        this.attachments = attachments;
    }
}
