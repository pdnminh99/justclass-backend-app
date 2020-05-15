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
            DocumentReference authorMembershipReference,
            String content,
            Timestamp createAt,
            Integer commentsCount,
            Integer likesCount,
            String classroomId,
            DocumentReference classroomReference) {
        super(Id, author, authorMembershipReference, content, createAt, commentsCount, likesCount, classroomId, classroomReference);
    }

    public List<BasicFile> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<BasicFile> attachments) {
        this.attachments = attachments;
    }
}
