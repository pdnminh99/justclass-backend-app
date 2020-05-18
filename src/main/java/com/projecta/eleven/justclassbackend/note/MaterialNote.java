package com.projecta.eleven.justclassbackend.note;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.projecta.eleven.justclassbackend.file.BasicFile;
import com.projecta.eleven.justclassbackend.user.MinifiedUser;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class MaterialNote extends BasicNote {

    private List<DocumentReference> attachmentReferences;

    private List<BasicFile> attachments;

    public MaterialNote(
            String Id,
            MinifiedUser author,
            String authorId,
            DocumentReference authorReference,
            String content,
            Timestamp createAt,
            Integer commentsCount,
            String classroomId,
            DocumentReference classroomReference,
            List<String> links,
            List<DocumentReference> attachmentReferences,
            List<BasicFile> attachments) {
        super(Id, author, authorId, authorReference, content, createAt, commentsCount, classroomId, classroomReference, links);
        this.attachmentReferences = attachmentReferences;
        this.attachments = attachments;
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

    @Override
    public HashMap<String, Object> toMap(boolean isTimestampInMilliseconds) {
        var map = super.toMap(isTimestampInMilliseconds);

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
        return map;
    }
}
